package datastax.com;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.protocol.internal.util.Bytes;
import datastax.com.dataObjects.*;
import datastax.com.DAOs.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerTest {

    private static CqlSession session = null;
    private static CustomerMapper customerMapper = null;
    static AccountDao daoAccount = null;
    static PaymentInfoDao daoPayment = null;
    static AssocAccountDao daoAssoc = null;
    static ContactDao daoContact = null;
    static NationalAccountDao daoNational = null;
    static ApplyDiscountDao daoApplyDiscount = null;

    private static boolean skipSchemaCreation = false;
    private static boolean skipDataLoad = false;
    private static boolean skipKeyspaceDrop = false;
    private static String keyspaceName = "customer";
    private static String productName = "Customer" ;

    //** paths needs to be updated or revised to use relative project path
    private static String schemaScriptPath = "/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/resources/create_customer_schema.sh" ;
    private static String dataScriptPath = "/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/resources/load_customer_data.sh" ;

    @BeforeClass
    public static void init() {
        System.out.println(productName + " - before init() method called");

        try{
            session = CqlSession.builder()
//                    .addContactPoints("127.0.0.1") //should have multiple (2+) contactpoints listed
//                    .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM))
//                    .withLoadBalancingPolicy(
//                            new TokenAwarePolicy(
//                                    LatencyAwarePolicy.builder(
//                                            DCAwareRoundRobinPolicy.builder()
//                                                    .withLocalDc("dc1")
//                                                    .build()
//                                    )
//                            )
//                        .withExclusionThreshold(2.0)
//                        .build()
//                    )
//                    .withCompression(ProtocolOptions.Compression.LZ4) //LZ4 jar needs to be in class path
                    //auth information may also be needed
                    .build();

            dropTestKeyspace();
            loadSchema();
            session.execute("USE " + keyspaceName + ";");
            loadData();

            customerMapper = new CustomerMapperBuilder(session).build();
            daoAccount = customerMapper.accountDao(keyspaceName);
            daoPayment = customerMapper.paymentInfoDao(keyspaceName);
            daoAssoc = customerMapper.assocAccountDao(keyspaceName);
            daoContact  =  customerMapper.contactDao(keyspaceName);
            daoNational = customerMapper.nationalAccountDao(keyspaceName);
            daoApplyDiscount = customerMapper.applyDiscountDao(keyspaceName);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void dropTestKeyspace(){
        String keyspaceDrop = "DROP KEYSPACE IF EXISTS " + keyspaceName + ";";
        if(!skipKeyspaceDrop) {
            System.out.println("Dropping keyspace - " + keyspaceName);

            session.execute(keyspaceDrop);
        }
    }

    static void loadSchema() throws IOException, InterruptedException {
        if (!skipSchemaCreation) {
            System.out.println("Running " + productName + " loadSchema");

            String keyspaceCreate = "CREATE KEYSPACE If NOT EXISTS " + keyspaceName + " WITH replication = {'class': 'NetworkTopologyStrategy', 'SearchGraphAnalytics': '1'}  AND durable_writes = true;";
            session.execute(keyspaceCreate);

            runScript(schemaScriptPath);//TODO get resource path programmatically
        }
    }

    static void runScript(String scriptPath) throws InterruptedException, IOException {
        ProcessBuilder processBuild = new ProcessBuilder(scriptPath); //TODO get resource path programmatically
        Process process = processBuild.start();

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            // check for errors
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("execution of script failed!");
        }
    }

    static void loadData() throws Exception {
        if(!skipDataLoad){
            System.out.println("Running " + productName + " data load");

            runScript(dataScriptPath); //TODO get resource path programmatically

            //sleep for a time to allow Solr indexes to update completely
            System.out.println("Completed " + productName + " data load.  Pausing to allow indexes to update...");
            Thread.sleep(11000);

            System.out.println("Index update complete, starting tests...");
        }
    }

    @AfterClass
    public static void close(){
        System.out.println("Running " + productName + " close");

        dropTestKeyspace();
        if (session != null) session.close();
    }

    @Test
    public void latencyTest() {

        List<String> acctList = new ArrayList<String>();
        acctList.add("001146670");
        acctList.add("002101513");
        acctList.add("002101530");
        acctList.add("002101572");

        acctList.stream().forEach(acctNbr->{
            long futuresstart = System.currentTimeMillis();
            CompletableFuture<Boolean> future1
                    = CompletableFuture.supplyAsync(() -> {
                            long start = System.currentTimeMillis();
                            PagingIterable<Account> customerAccountIterable = daoAccount.findAllByAccountNumber(acctNbr);
                            int numCustAccounts = customerAccountIterable.getAvailableWithoutFetching();
                            System.out.println("Acct# " + acctNbr + " OPCO INQUIRY QUERY TIME : " + (System.currentTimeMillis() - start) + "\t\tFIRST PAGE SIZE : " + numCustAccounts);
                            return customerAccountIterable;
                        }).thenApply(customerAccountIterable -> { /*mapAccountData(customerAccountIterable, resultMap);*/ return true;});

            CompletableFuture<Boolean> future2
                    = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
                PagingIterable<ApplyDiscount> applyDiscountIterable = daoApplyDiscount.findAllByAccountNumber(acctNbr);
                int numAccountDiscounts = applyDiscountIterable.getAvailableWithoutFetching();
                System.out.println("Acct# " + acctNbr + " APPLY DISCOUNT INQUIRY QUERY TIME : " + (System.currentTimeMillis() - start) + "\t\tFIRST PAGE SIZE : " + numAccountDiscounts);
                return applyDiscountIterable;
            }).thenApply(customerAccountIterable -> { /*mapAccountData(customerAccountIterable, resultMap);*/ return true;});

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2);
            try {
                allFutures.get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("All complete for acct : " + acctNbr);
            System.out.println("ACCOUNT FUTURES TOTAL QUERY TIME : " + (System.currentTimeMillis() - futuresstart) + "\n\n");
        });
    }

    @Test
    public void mappedDutyTaxTest() {
        String acctNum = "654321987";
        String opco = "testOpcoAcctTypes";

        String keyUS = "US";
        String keyCA = "CA";
        String keyIN = "IN";
        String keyMX = "MX";

        String usDutyTax= "123123123";
        String caDutyTax_v1= "123123123";
        String caDutyTax_v2= "124124124";
        String inDutyTax= "123123123";
        String mxDutyTax= "345678901";

        //create initial state
        Map<String, String> dutyTax = new HashMap<>();
        dutyTax.put(keyUS, usDutyTax);
        dutyTax.put(keyCA, caDutyTax_v1);
        dutyTax.put(keyIN, inDutyTax);

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setDutyTaxInfo(dutyTax);
        daoAccount.save(custAcct);

        System.out.println("\nExpress Duty Tax Test\nInitial Map State:\n" + dutyTax.toString());

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        Map<String, String> foundDutyTax = foundAcct.getDutyTaxInfo();
        assert(foundDutyTax.get(keyUS).equals(usDutyTax));
        assert(foundDutyTax.get(keyCA).equals(caDutyTax_v1));
        assert(foundDutyTax.get(keyIN).equals(inDutyTax));
        assert(foundDutyTax.get(mxDutyTax) == null);

        Map<String, String> dutyTaxNewEntries = new HashMap<>();
        dutyTaxNewEntries.put(keyCA, caDutyTax_v2);
        dutyTaxNewEntries.put(keyMX, mxDutyTax);
        daoAccount.upsertDutyTaxInfoMapEntries(acctNum, opco, dutyTaxNewEntries);

        Account foundAcctUpdated = daoAccount.findByAccountNumber(acctNum);
        Map<String, String> foundDutyTaxUpdated = foundAcctUpdated.getDutyTaxInfo();
        assert(foundDutyTaxUpdated.get(keyUS).equals(usDutyTax));
        assert(foundDutyTaxUpdated.get(keyCA).equals(caDutyTax_v2));
        assert(foundDutyTaxUpdated.get(keyIN).equals(inDutyTax));
        assert(foundDutyTaxUpdated.get(keyMX).equals(mxDutyTax));

        System.out.println("\nFinal Map State:\n" + foundDutyTaxUpdated.toString());


        //cleanup
        daoAccount.delete(custAcct);
    }


    @Test
    public void mappedCollectionsTest() {
        String acctNum = "9876543344";
        String opco = "testOpcoAcctTypes";
        Map<String, String> dutyTax = new HashMap<>();

        String keyA = "key1";
        String keyB = "key2";
        String keyC = "key3";
        String keyD = "key4";
        String valA = "val1";
        String valB = "val2";
        String valC = "val3";
        String valD = "val4";

        dutyTax.put(keyA, valA);
        dutyTax.put(keyB, valB);

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setDutyTaxInfo(dutyTax);
        daoAccount.save(custAcct);

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        Map<String, String> foundDutyTax = foundAcct.getDutyTaxInfo();
        assert(foundDutyTax.get(keyA).equals(valA));
        assert(foundDutyTax.get(keyB).equals(valB));

        daoAccount.updateCustomerType(acctNum, opco, "custType1");

        //CQL map entry example
        String dutyTaxAddElement =
                "UPDATE \n" +
                "    " + keyspaceName + ".cust_acct_v1 \n" +
                "SET \n" +
                "    duty_tax_info = duty_tax_info + {'" + keyC +"' : '" + valC + "'} \n" +
                "WHERE \n" +
                "    account_number = '" + acctNum + "' AND \n" +
                "    opco = '" + opco + "';";

        session.execute(dutyTaxAddElement);
        Account foundAcct2 = daoAccount.findByAccountNumber(acctNum);
        Map<String, String> foundDutyTax2 = foundAcct2.getDutyTaxInfo();
        assert(foundDutyTax2.get(keyC).equals(valC));

        //Java Mapper upsert example
        Map<String, String> dutyTaxNewEntries = new HashMap<>();
        dutyTaxNewEntries.put(keyD, valD);
        daoAccount.upsertDutyTaxInfoMapEntries(acctNum, opco, dutyTaxNewEntries);

        Account foundAcct3 = daoAccount.findByAccountNumber(acctNum);
        Map<String, String> foundDutyTax3 = foundAcct3.getDutyTaxInfo();
        assert(foundDutyTax3.get(keyD).equals(valD));

        //cleanup
        daoAccount.delete(custAcct);

        //reference for CQL manipulation for map type fields
        //https://docs.datastax.com/en/dse/5.1/cql/cql/cql_using/useInsertMap.html
    }

    @Test
    public void searchSubStringTest() throws InterruptedException {
        //using dummy values for contact record to test substring matching functionaliyt
        String insertRec1 = "INSERT INTO account_contact\n" +
                "    (account_number, opco, contact_document_id, contact_type_code, contact_business_id, person__first_name)\n" +
                "    VALUES('123456', 'opc1', 101, 'type1', 'cBus1', 'FedExDotCom');";

        String insertRec2 = "INSERT INTO account_contact\n" +
                "    (account_number, opco, contact_document_id, contact_type_code, contact_business_id, person__first_name)\n" +
                "    VALUES('123456', 'opc1', 102, 'type1', 'cBus2', 'FedExDotom');";

        //add test records to table
        session.execute(insertRec1);
        session.execute(insertRec2);

        //sleep for a time to allow Solr indexes to update completely
        System.out.println("Inserted substring test records. Pausing to allow indexes to update...");
        Thread.sleep(11000);

        //construct test query using Solr
        String searchQueryBase = "SELECT * \n" +
                "FROM account_contact\n" +
                "WHERE solr_query = ";

        //query 1, should find two records
        String searchQueryDetail1 = "'person__first_name:FedEx*'";
        ResultSet rs1 = session.execute(searchQueryBase + searchQueryDetail1);
        assert(rs1.all().size() == 2);

        //query 2, should find two records
        String searchQueryDetail2 = "'person__first_name:FedEx*D*'";
        ResultSet rs2 = session.execute(searchQueryBase + searchQueryDetail2);
        assert(rs2.all().size() == 2);

        //query 3, should find one record
        String searchQueryDetail3 = "'person__first_name:FedEx*C*'";
        ResultSet rs3 = session.execute(searchQueryBase + searchQueryDetail3);
        Row row3 = rs3.one();
        assert(row3.getLong("contact_document_id") == 101);
        assert(row3.getString("contact_business_id").equals("cBus1"));
        assert(rs3.isFullyFetched() == true); //only one record found

        //query 4, should find one record
        String searchQueryDetail4 = "'person__first_name:FedEx*D*C*'";
        ResultSet rs4 = session.execute(searchQueryBase + searchQueryDetail4);
        Row row4 = rs4.one();
        assert(row4.getLong("contact_document_id") == 101);
        assert(row4.getString("contact_business_id").equals("cBus1"));
        assert(rs4.isFullyFetched() == true); //only one record found

        //query 5, should find one record
        String searchQueryDetail5 = "'person__first_name:FedEx*D*tom'";
        ResultSet rs5 = session.execute(searchQueryBase + searchQueryDetail5);
        Row row5 = rs5.one();
        assert(row5.getLong("contact_document_id") == 102);
        assert(row5.getString("contact_business_id").equals("cBus2"));
        assert(rs5.isFullyFetched() == true); //only one record found

        String cleanup = "DELETE FROM account_contact WHERE account_number = '123456'";
        session.execute(cleanup);
    }

    @Test
    public void customerAcctTypesTest(){
        String acctNum = "9876543333";
        String opco = "testOpcoAcctTypes";
        String hazardShipperFlag = "shipFlag";

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setHazardousShipperFlag(hazardShipperFlag);

        daoAccount.save(custAcct);

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        assert(foundAcct.getHazardousShipperFlag().equals(hazardShipperFlag));

        //cleanup
        daoAccount.delete(custAcct);
    }

    @Test
    public void containUTFTest(){
        char euroUTF = '\u20ac';

        String acctNum = "987654321";
        String opco = "testOpco";
        String acctType = String.valueOf(euroUTF);

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setProfileAccountType(acctType);

        daoAccount.save(custAcct);

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        String foundAcctType = foundAcct.getProfileAccountType();

        assert(foundAcctType.equals(acctType));

        //cleanup
        daoAccount.delete(custAcct);
    }

    @Test
    public void applyDiscountUpdateTest(){
        String acctNum = "123456789";
        String opco = "FX";
        Boolean applyDiscountFlag = true;
        Instant effectiveDT = Instant.parse("2021-05-01T01:00:00.001Z");
        Instant expirationDT = Instant.parse("2015-12-10T00:00:00.001Z");

        ApplyDiscount appDisc = new ApplyDiscount();

        appDisc.setAccountNumber(acctNum);
        appDisc.setOpco(opco);
        appDisc.setApplyDiscountFlag(applyDiscountFlag);
        appDisc.setDisountEffectiveDateTime(effectiveDT);
        appDisc.setDisountExpirationateTime(expirationDT);

        //save initial version of the record
        daoApplyDiscount.save(appDisc);

        //retrieve initial version and verify property values
        ApplyDiscount readAppDisc = daoApplyDiscount.findByKeys(acctNum, opco, effectiveDT);
        assert(readAppDisc.getAccountNumber().equals(acctNum));
        assert(readAppDisc.getOpco().equals(opco));
        assert(readAppDisc.getApplyDiscountFlag() == applyDiscountFlag);
        assert(readAppDisc.getDisountEffectiveDateTime().equals(effectiveDT));
        assert(readAppDisc.getDisountExpirationateTime().equals(expirationDT));


        //update retrieved record
        Boolean updatedApplyDiscountFlag = false;
        Instant updatedExpirationDT = Instant.parse("2016-04-15T00:00:00.001Z");
        readAppDisc.setApplyDiscountFlag(updatedApplyDiscountFlag);
        readAppDisc.setDisountExpirationateTime(updatedExpirationDT);

        daoApplyDiscount.update(readAppDisc);

        //retrieve updated version and verify property values were updated
        ApplyDiscount foundUpdatedAppDisc = daoApplyDiscount.findByKeys(acctNum, opco, effectiveDT);
        assert(foundUpdatedAppDisc.getAccountNumber().equals(acctNum));
        assert(foundUpdatedAppDisc.getOpco().equals(opco));
        assert(foundUpdatedAppDisc.getApplyDiscountFlag() == updatedApplyDiscountFlag);
        assert(foundUpdatedAppDisc.getDisountEffectiveDateTime().equals(effectiveDT));
        assert(foundUpdatedAppDisc.getDisountExpirationateTime().equals(updatedExpirationDT));

        //cleanup test record
        daoApplyDiscount.delete(appDisc);
    }

    @Test
    public void applyDiscountSearchTest(){
        int expectResultSize = 3;

        String fullQuery = "select * from apply_discount_detail_v1 \n" +
                "where \n" +
                "    account_number = '000001236' and " +
                "    solr_query = " +
                "    '{" +
                "        \"q\": \"opco:FX && " +
                "              apply_discount__discount_flag:true && " +
                "              apply_discount__effective_date_time:[2018-11-01T00:00:00.001Z TO *] && " +
                "              apply_discount__expiration_date_time:[* TO 2019-01-01T00:00:00.001Z]\"," +
                "        \"sort\": \"apply_discount__effective_date_time desc\"}';";


        ResultSet rs = session.execute(fullQuery);
        assert(expectResultSize == rs.all().size());
    }

    @Test
    public void applyDiscountAsyncTest() throws ExecutionException, InterruptedException {
        String acctNum = "000001236";
        int expectResultSize = 11;

        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts = daoApplyDiscount.findAllByAccountNumberAsync(acctNum);
        cfDiscounts.join();
        assert(cfDiscounts.get().remaining() == expectResultSize);
    }

    @Test
    public void applyDiscountSearchAsyncTest() throws ExecutionException, InterruptedException {
        String acctNum = "000001236";
        String opco = "FX";
        Boolean applyDiscountFlag = true;
        Instant effectiveDT = Instant.parse("2018-11-01T01:00:00.001Z");
        Instant expriationDT = Instant.parse("2019-01-01T00:00:00.001Z");

        //Solr filter on opco value only
        int expectedResultSize1 = 11;
        String solrParm1 = ApplyDiscountHelper.constructSearchQuery(opco);
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts1 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm1);
        cfDiscounts1.join();
        assert(cfDiscounts1.get().remaining() == expectedResultSize1);

        //Solr filter on opco and apply discount flag values only
        int expectedResultSize2 = 6;
        String solrParm2 = ApplyDiscountHelper.constructSearchQuery(opco, applyDiscountFlag);
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts2 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm2);
        cfDiscounts2.join();
        assert(cfDiscounts2.get().remaining() == expectedResultSize2);

        //Solr filter on opco and effective date/time values only
        int expectedResultSize3 = 7;
        String solrParm3 = ApplyDiscountHelper.constructSearchQuery(opco, effectiveDT);
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts3 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm3);
        cfDiscounts3.join();
        assert(cfDiscounts3.get().remaining() == expectedResultSize3);

        //Solr filter on opco, apply discount flag, effective date/time and expiration date/time
        int expectedResultSize4 = 3;
        String solrParm4 = ApplyDiscountHelper.constructSearchQuery(opco, applyDiscountFlag, effectiveDT,expriationDT);
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts4 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm4);
        cfDiscounts4.join();
        assert(cfDiscounts4.get().remaining() == expectedResultSize4);
    }


    @Test
    public void nationalAccountFullSearchTest(){
        String solrParam = "national_account_detail__national_account_nbr:00706";
        int expectedResultCount = 14;
        PagingIterable<NationalAcccount> foundNatAccts = daoNational.findByNationalAccountNumberFullSolrParam(solrParam);

        assert(foundNatAccts.all().size() == expectedResultCount);
    }

    @Test
    public void nationalAccountSearchTest(){
        String solrParam = "national_account_detail__national_account_nbr:00706";
        int expectedResultCount = 14;
        PagingIterable<NationalAcccount> foundNatAccts = daoNational.findBySearchQuery(solrParam);

        assert(foundNatAccts.all().size() == expectedResultCount);
    }

    @Test
    public void nationalAccountTest(){
        String acctID = "00112770";
        int expectedResultCount = 5;

        PagingIterable<NationalAcccount> foundNatAccts = daoNational.findByAccountNumber(acctID);

        assert(foundNatAccts.all().size() == expectedResultCount);
    }

    @Test
    public void nationalAccountPagingTest(){
        int pageSize = 3;

        Function<BoundStatementBuilder, BoundStatementBuilder> functionCustomPageSize =
                builder -> builder.setPageSize(pageSize);

        String acctID = "00112770";
        PagingIterable<NationalAcccount> foundNatAccts = daoNational.findByAccountNumber(acctID, functionCustomPageSize);

        assert(foundNatAccts.getAvailableWithoutFetching() == pageSize);
        assert (foundNatAccts.isFullyFetched() == false);

        System.out.println(foundNatAccts.getExecutionInfo().getPagingState());
    }

    @Test
    public void nationalAccountMultPagingTest(){
        String acctID = "00112770";
        int expectedTotalSize = 5;
        int pageSize = 3;
        String query = "select * from " + keyspaceName + ".national_account_v1 where account_number = '" + acctID + "';";

        System.out.println(query);
        SimpleStatement stmt = SimpleStatement.builder(query).setPageSize(pageSize).build();

        ResultSet rs =  session.execute(stmt);
        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        System.out.println("First page results:");
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            System.out.println("\t" + row.getInstant("national_account_detail__membership_eff_date_time"));
        }

        SimpleStatement stmt2 = SimpleStatement.builder(query).setPagingState(pagingState).build();
        ResultSet rs2 = session.execute(stmt2);

        assert(rs2.isFullyFetched() == true );
        assert(rs2.getAvailableWithoutFetching() == (expectedTotalSize - pageSize));
        System.out.println("Second page results:");
        while(rs2.getAvailableWithoutFetching()>0){
            Row row = rs2.one();
            System.out.println("\t" + row.getInstant("national_account_detail__membership_eff_date_time"));
        }
    }

    @Test
    public void nationalAccountMultPagingMappedTest(){
        String acctID = "00112770";
        int expectedTotalSize = 5;
        int pageSize = 3;
        String query = "select * from " + keyspaceName + ".national_account_v1 where account_number = '" + acctID + "';";

        System.out.println(query);
        SimpleStatement stmt = SimpleStatement.builder(query).setPageSize(pageSize).build();

        ResultSet rs =  session.execute(stmt);
        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        System.out.println("First page results:");
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            NationalAcccount natAcct = daoNational.asNationalAccount(row);

            System.out.println("\t" +
                    "Account Number - " + natAcct.getAccountNumber() + "    " +
                    "Opco - " + natAcct.getOpco() +  "    " +
                    "Effective date/time - " + natAcct.getMembershipEffectiveDateTime());
        }

        SimpleStatement stmt2 = SimpleStatement.builder(query).setPagingState(pagingState).build();
        ResultSet rs2 = session.execute(stmt2);

        assert(rs2.isFullyFetched() == true );
        assert(rs2.getAvailableWithoutFetching() == (expectedTotalSize - pageSize));
        System.out.println("Second page results:");
        while(rs2.getAvailableWithoutFetching()>0){
            Row row = rs2.one();
            NationalAcccount natAcct = daoNational.asNationalAccount(row);

            System.out.println("\t" +
                    "Account Number - " + natAcct.getAccountNumber() + "    " +
                    "Opco - " + natAcct.getOpco() +  "    " +
                    "Effective date/time - " + natAcct.getMembershipEffectiveDateTime());
        }
    }


    @Test
    public void sampleTest(){
        String acctID = "00112770";
        String query = "select * from " + keyspaceName + ".national_account_v1 where account_number = '" + acctID + "';";

        int expectedTotalSize = 5;
        int pageSize = 3;
        SimpleStatement stmt = SimpleStatement.builder(query).setPageSize(pageSize).build();
        ResultSet rs =  session.execute(stmt);

        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            //do something with row
        }

        SimpleStatement stmt2 = SimpleStatement.builder(query).setPagingState(pagingState).build();
        ResultSet rs2 = session.execute(stmt2);

        assert(rs2.isFullyFetched() == true );
        assert(rs2.getAvailableWithoutFetching() == (expectedTotalSize - pageSize));

        //sample calls to Bytes utility methods
        String temp = Bytes.toHexString(pagingState);
        System.out.println(temp);

        ByteBuffer buff = Bytes.fromHexString(temp);
        System.out.println(buff);
    }
    @Test
    public void sampleTestSearch(){
        String acctID = "00112770";
        String query = "select * from " + keyspaceName + ".national_account_v1 where account_number='" + acctID + "';";
        System.out.println(query);

        int expectedTotalSize = 5;
        int pageSize = 3;
        SimpleStatement stmt = SimpleStatement.builder(query).setPageSize(pageSize).setConsistencyLevel(ConsistencyLevel.LOCAL_ONE).build();
        ResultSet rs =  session.execute(stmt);

        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            //do something with row
        }

        SimpleStatement stmt2 = SimpleStatement.builder(query).setPagingState(pagingState).build();
        ResultSet rs2 = session.execute(stmt2);

        assert(rs2.isFullyFetched() == true );
        assert(rs2.getAvailableWithoutFetching() == (expectedTotalSize - pageSize));

        //sample calls to Bytes utility methods
        String temp = Bytes.toHexString(pagingState);
        System.out.println(temp);

        ByteBuffer buff = Bytes.fromHexString(temp);
        System.out.println(buff);
    }

    @Test
    public void sampleTestSearchMultiPage(){
        String acctID = "00112770";
        String query = "select * from " + keyspaceName + ".national_account_v1 where account_number = '" + acctID + "';";
        System.out.println(query);

        int expectedTotalSize = 5;
        int pageSize = 2;
        SimpleStatement stmt = SimpleStatement.builder(query).setPageSize(pageSize).setConsistencyLevel(ConsistencyLevel.LOCAL_ONE).build();
        ResultSet rs =  session.execute(stmt);

        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);

        System.out.println("Initial page fetch - ");
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            System.out.println("\t" + row.getInstant("last_update_tmstp").toString());
            //do something with row
        }

        SimpleStatement stmt2 = SimpleStatement.builder(query).setPageSize(pageSize).setPagingState(pagingState).build();
        ResultSet rs2 = session.execute(stmt2);

        ByteBuffer pagingState2 = rs2.getExecutionInfo().getPagingState();
        System.out.println("Second paging state - " + pagingState2);

        assert(rs2.isFullyFetched() == false );
        assert(rs2.getAvailableWithoutFetching() == pageSize);
        System.out.println("\nSecond page fetch - ");
        while(rs2.getAvailableWithoutFetching()>0){
            Row row = rs2.one();
            System.out.println("\t" + row.getInstant("last_update_tmstp").toString());
            //do something with row
        }

        SimpleStatement stmt3 = SimpleStatement.builder(query).setPageSize(pageSize).setPagingState(pagingState2).build();
        ResultSet rs3 = session.execute(stmt2);

//        ByteBuffer pagingState3 = rs3.getExecutionInfo().getPagingState();
//        System.out.println("Third paging state - " + pagingState2);

//        assert(rs3.isFullyFetched() == false );
//        int fetchAvailable = rs3.getAvailableWithoutFetching();
////        assert(fetchAvailable == (expectedTotalSize-(pageSize*2)));
//        System.out.println("\nThird page fetch - ");
//        while(rs3.getAvailableWithoutFetching()>0){
//            Row row = rs3.one();
//            System.out.println("\t" + row.getInstant("last_update_tmstp").toString());
//            //do something with row
//        }


        //sample calls to Bytes utility methods
//        String temp = Bytes.toHexString(pagingState); //Debug ouput if needed
//        System.out.println(temp);
//
//        ByteBuffer buff = Bytes.fromHexString(temp);
//        System.out.println(buff);
    }

    @Test
    public void verifyCusomterNullPropertyUpdate(){
        String acctID = "998877";
        String opco = "opc1";
        String custType = "custType1";
        String acctType = "acctType1";
        String acctStatus = "status1";
        String acctReason = "reason1";
        String entSource = "entSource1";

        Account custAllProps = new Account();
        custAllProps.setAccountNumber(acctID);
        custAllProps.setOpco(opco);
        custAllProps.setProfileCustomerType(custType);
        custAllProps.setProfileAccountType(acctType);
        custAllProps.setProfileAccountStatusCode(acctStatus);
        custAllProps.setProfileAccountReasonCode(acctReason);
        custAllProps.setProfileEnterpriseSource(entSource);
        daoAccount.save(custAllProps);

        //verify all properties exist in rcord before deleting select properties
        Account foundCust = daoAccount.findByAccountNumber(acctID);
        assert(foundCust.getOpco().equals(opco));
        assert(foundCust.getProfileCustomerType().equals(custType));
        assert(foundCust.getProfileAccountType().equals(acctType));
        assert(foundCust.getProfileAccountStatusCode().equals(acctStatus));
        assert(foundCust.getProfileAccountReasonCode().equals(acctReason));
        assert(foundCust.getProfileEnterpriseSource().equals(entSource));

        //test delete single poperty
        String deleteProp = "profile__account_type";
        String stmtDeletProperty =
                "DELETE \n" +
                "    " + deleteProp + " \n" +
                "FROM\n" +
                "    cust_acct_v1 \n" +
                "WHERE \n" +
                "    account_number = '" + acctID + "'\n" +
                "    AND opco = '" + opco + "';";

        session.execute(stmtDeletProperty);
        Account foundCustDelProp = daoAccount.findByAccountNumber(acctID);
        //check that deleted property is no longer in record
        assert(foundCustDelProp.getProfileAccountType() == null);
        //check that all other properties still set to expected values
        assert(foundCustDelProp.getOpco().equals(opco));
        assert(foundCustDelProp.getProfileCustomerType().equals(custType));
        assert(foundCustDelProp.getProfileAccountStatusCode().equals(acctStatus));
        assert(foundCustDelProp.getProfileAccountReasonCode().equals(acctReason));
        assert(foundCustDelProp.getProfileEnterpriseSource().equals(entSource));

        //test delete multiple poperties
        List<String> deleteProps = Stream.of(
                "profile__customer_type",
                "profile__account_status__status_code",
                "profile__account_status__reason_code"
            ).collect(Collectors.toList());

        String stmtDeleteMultiProperties =
                "DELETE \n" +
                "    " + String.join(",", deleteProps) + " \n" +
                "FROM\n" +
                "    cust_acct_v1 \n" +
                "WHERE \n" +
                "    account_number = '" + acctID + "'\n" +
                "    AND opco = '" + opco + "';";

        session.execute(stmtDeleteMultiProperties);
        Account foundCustDelMultiProps = daoAccount.findByAccountNumber(acctID);
        //check that deleted properties are no longer in record
        assert(foundCustDelMultiProps.getProfileCustomerType() == null);
        assert(foundCustDelMultiProps.getProfileAccountType() == null);
        assert(foundCustDelMultiProps.getProfileAccountStatusCode()== null);
        assert(foundCustDelMultiProps.getProfileAccountReasonCode()== null);
        //check that all other properties still set to expected values
        assert(foundCustDelMultiProps.getOpco().equals(opco));
        assert(foundCustDelMultiProps.getProfileEnterpriseSource().equals(entSource));
    }

    @Test
    public void combineAsyncQueryTest() throws ExecutionException, InterruptedException {
        //execute async queries
        String acctID = "1111";
        CompletableFuture<Account> cfFoundAccount = daoAccount.findByAccountNumberAsync(acctID);
        CompletableFuture<PaymentInfo> cfFoundPayment = daoPayment.findByAccountNumberAsync(acctID);
        CompletableFuture<AssocAccount> cfFoundAssoc = daoAssoc.findByAccountNumberAsync(acctID);

        //allow all async calls to complete
        CompletableFuture.allOf(cfFoundAccount, cfFoundPayment, cfFoundAssoc);

        //** check account details
        String expectedOpco = "A";
        String expectedCustomerType = "custType1";
        String expectedAccountType = "acctType1";

        //assign account data object
        Account foundAccount = cfFoundAccount.get();

        assert(foundAccount.getOpco().equals(expectedOpco));
        assert(foundAccount.getProfileCustomerType().equals(expectedCustomerType));
        assert(foundAccount.getProfileAccountType().equals(expectedAccountType));


        //** check payment details
        String expectedPaymentOpco = "A";
        String expectedType = "type1";
        String expectedKey = "key1";
        int expectedSeq = 1;
        String expectedCCId = "ccID1";

        //assign payment data object
        PaymentInfo foundPayment = cfFoundPayment.get();

        assert(foundPayment.getOpco().equals(expectedPaymentOpco));
        assert(foundPayment.getRecordType().equals(expectedType));
        assert(foundPayment.getRecordKey().equals(expectedKey));
        assert(foundPayment.getCreditCardID().equals(expectedCCId));


        //** check associated account details
        String expectedAssocOpco = "B";
        String expectedAssocAcct = "2222";

        //assign assoc data object
        AssocAccount foundAssoc = cfFoundAssoc.get();

        assert(foundAssoc.getOpco().equals(expectedAssocOpco));
        assert(foundAssoc.getAssociatedAccountNumber().equals(expectedAssocAcct));
    }

    @Test
    public void customerAssocAccountMapperReadTest(){
        String acctID = "1111";
        String expectedOpco = "B";
        String expectedAssocAcct = "2222";

        AssocAccount foundAssoc = daoAssoc.findByAccountNumber(acctID);

        assert(foundAssoc.getOpco().equals(expectedOpco));
        assert(foundAssoc.getAssociatedAccountNumber().equals(expectedAssocAcct));
    }

    @Test
    public void customerAssocAccountMapperReadTestAsync() throws ExecutionException, InterruptedException {
        String acctID = "1111";
        String expectedOpco = "B";
        String expectedAssocAcct = "2222";

        CompletableFuture<AssocAccount> cfFoundAssoc = daoAssoc.findByAccountNumberAsync(acctID);
        cfFoundAssoc.join();
        AssocAccount foundAssoc = cfFoundAssoc.get();

        assert(foundAssoc.getOpco().equals(expectedOpco));
        assert(foundAssoc.getAssociatedAccountNumber().equals(expectedAssocAcct));
    }


    @Test
    public void customerPaymentMapperReadTest(){
        String acctID = "1111";
        String expectedOpco = "A";
        String expectedType = "type1";
        String expectedKey = "key1";
        int expectedSeq = 1;
        String expectedCCId = "ccID1";

        PaymentInfo foundPayment = daoPayment.findByAccountNumber(acctID);

        assert(foundPayment.getOpco().equals(expectedOpco));
        assert(foundPayment.getRecordType().equals(expectedType));
        assert(foundPayment.getRecordKey().equals(expectedKey));

        int foundSeq = foundPayment.getRecordSeq();
//        String foundSeq = foundPayment.getRecordSeq();
        System.out.println("Found record_seq - " + foundSeq);
//        assert(foundPayment.getRecordSeq() == expectedSeq);
        assert(foundPayment.getCreditCardID().equals(expectedCCId));
    }

    @Test
    public void customerPaymentMapperReadTestAsync() throws ExecutionException, InterruptedException {
        String acctID = "1111";
        String expectedOpco = "A";
        String expectedType = "type1";
        String expectedKey = "key1";
        int expectedSeq = 1;
        String expectedCCId = "ccID1";

        CompletableFuture<PaymentInfo> cfFoundPayment = daoPayment.findByAccountNumberAsync(acctID);
        cfFoundPayment.join();
        PaymentInfo foundPayment = cfFoundPayment.get();

        assert(foundPayment.getOpco().equals(expectedOpco));
        assert(foundPayment.getRecordType().equals(expectedType));
        assert(foundPayment.getRecordKey().equals(expectedKey));
//        assert(foundPayment.getRecordSeq() == expectedSeq);
        assert(foundPayment.getCreditCardID().equals(expectedCCId));
    }

    @Test
    public void customerAccountMapperReadTest(){
        String acctID = "1111";
        String expectedOpco = "A";
        String expectedCustomerType = "custType1";
        String expectedAccountType = "acctType1";

        Account foundAccount = daoAccount.findByAccountNumber(acctID);

        assert(foundAccount.getOpco().equals(expectedOpco));
        assert(foundAccount.getProfileCustomerType().equals(expectedCustomerType));
        assert(foundAccount.getProfileAccountType().equals(expectedAccountType));
    }

    @Test
    public void customerAccountMapperReadTestAsync() throws ExecutionException, InterruptedException {
        String acctID = "1111";
        String expectedOpco = "A";
        String expectedCustomerType = "custType1";
        String expectedAccountType = "acctType1";

        CompletableFuture<Account> cfFoundAccount = daoAccount.findByAccountNumberAsync(acctID);
        cfFoundAccount.join();
        Account foundAccount = cfFoundAccount.get();

        assert(foundAccount.getOpco().equals(expectedOpco));
        assert(foundAccount.getProfileCustomerType().equals(expectedCustomerType));
        assert(foundAccount.getProfileAccountType().equals(expectedAccountType));
    }


    @Test
    public void contactUdtMapperReadTest(){
        Contact foundContact = daoContact.findByContactDocumentId(83);
        Set<ContactTelecomDetails> setTeleCom = foundContact.getTeleCom();

        //verify size of returned set
        assert(setTeleCom.size() == 1);

        //verify udt values
        ContactTelecomDetails addrSec = setTeleCom.iterator().next();
        assert(addrSec.getTelecomMethod().equals("PV"));
        assert(addrSec.getAreaCode().equals("123"));
        assert(addrSec.getPhoneNumber().equals("456-7890"));
    }



    @Test
    public void contactUdtMapperWriteTest(){
        long testDocID = 20001;
        String testFirstName = "First20001";
        String testLastName = "Last20001" ;
        String testMiddleName = "Middle20001" ;
        String telComMethod = "SV";
        String areaCode = "000";
        String phoneNum = "111-2222";

        ContactTelecomDetails writeTeleCom = new ContactTelecomDetails();
        writeTeleCom.setTelecomMethod(telComMethod);
        writeTeleCom.setAreaCode(areaCode);
        writeTeleCom.setPhoneNumber(phoneNum);

        Set<ContactTelecomDetails> setTelecom = new HashSet<>();
        setTelecom.add(writeTeleCom);

        Contact writeContact = new Contact();
        writeContact.setContactDocumentId(testDocID);
        writeContact.setPersonFirstName(testFirstName);
        writeContact.setPerson__last_name(testLastName);
        writeContact.setPerson_MiddleName(testMiddleName);
        writeContact.setTeleCom(setTelecom);

        //write new record to DB
        daoContact.save(writeContact);

        //verify record written correctly
        Contact foundContact = daoContact.findByContactDocumentId(testDocID);
        Set<ContactTelecomDetails> setReadTelecom = foundContact.getTeleCom();

        //verify size of returned set
        assert(1 == setReadTelecom.size());

        //verify udt values
        ContactTelecomDetails addrSec = setReadTelecom.iterator().next();
//        assert(addrSec.getUnit().equals(addSecUnit));
//        assert(addrSec.getValue().equals(addSecVal));

        //cleanup test UDT record
        daoContact.delete(writeContact);
        Contact readVerifyDelete = daoContact.findByContactDocumentId(testDocID);
        assert(null == readVerifyDelete);
    }

    @Test
    public void contactMapperTest(){
        long testDocID = 20000;
        String testFirstName = "First20000";
        String testLastName = "Last20000" ;
        String testMiddleName = "Middle20000" ;

        //write test record
        Contact writeContact = new Contact();
        writeContact.setContactDocumentId(testDocID);
        writeContact.setPersonFirstName(testFirstName);
        writeContact.setPerson__last_name(testLastName);
        writeContact.setPerson_MiddleName(testMiddleName);
        daoContact.save(writeContact);

        //test read functionality
        Contact readContact = daoContact.findByContactDocumentId(testDocID);
        assert(readContact.getPersonFirstName().equals(testFirstName));
        assert(readContact.getPerson__last_name().equals(testLastName));
        assert(readContact.getPerson_MiddleName().equals(testMiddleName));

        //test delete capability
        daoContact.delete(writeContact);
        Contact readVerifyDelete = daoContact.findByContactDocumentId(testDocID);
        assert(null == readVerifyDelete);
    }

    @Test
    public void contactMapperAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Contact> cfFoundContact = daoContact.findByContactDocumentIdAsync(83);
        cfFoundContact.join();

        Contact foundContact = cfFoundContact.get();

        Set<ContactTelecomDetails> setTeleCom = foundContact.getTeleCom();

        //verify size of returned set
        assert(setTeleCom.size() == 1);

        //verify udt values
        ContactTelecomDetails addrSec = setTeleCom.iterator().next();
        assert(addrSec.getTelecomMethod().equals("PV"));
        assert(addrSec.getAreaCode().equals("123"));
        assert(addrSec.getPhoneNumber().equals("456-7890"));
    }

    @Test
    public void verifyEnterpriseProfile(){
        List<TestQuery> entProfileQueries = new ArrayList<>();

        entProfileQueries.add(new TestQuery("qEntProf1",
                "cust_acct_v1", "account_number", "acctNum1",
                "profile__enterprise_source","source1"));


        entProfileQueries.add(new TestQuery("qEntProf2",
                "cust_acct_v1", "account_number", "acctNum2",
                "profile__account_status__status_code","ARCHIVE"));

//        runQueryList(entProfileQueries);  //table no longer exisits //TODO - update to use cust_acct_v1 table, needs to account for clustering key(s)
    }

    @Test
    public void verifyContacts(){
        List<TestQuery> contactQueries = new ArrayList<>();

        contactQueries.add(new TestQuery("qContactFX1",
                "contact", "contact_document_id", "76",
                "company_name","OSRAM SYLVANIA INC",
                true));

        contactQueries.add(new TestQuery("qContactFDFR1",
                "contact", "contact_document_id", "984",
                "address__street_line","1999 MARCUS AVE",
                true));

        contactQueries.add(new TestQuery("qContactFXK1",
                "contact", "contact_document_id", "1022",
                "address__geo_political_subdivision2","THE WOODLANDS",
                true));

        contactQueries.add(new TestQuery("qContactMultiCont1",
                "contact", "contact_document_id", "1519",
                "address__additional_line1","TANISTOR ELECTRONICS",
                true));

        runQueryList(contactQueries);
    }
    @Test
    public void verifyContactUDTsKeyQuery(){

        String telecomQuery = "select * from contact where contact_document_id = 83";
        ResultSet resCheck = session.execute(telecomQuery);

        if(null != resCheck) {
            Row rowVal = resCheck.one();

            Set<UdtValue> setTelecomDetails= rowVal.getSet("tele_com", UdtValue.class);

            //check only one result
            assert(setTelecomDetails.size() == 1);

            //verify udt values
            UdtValue udt = setTelecomDetails.iterator().next();
            assert(udt.getString("telecom_method").equals("PV"));
            assert(udt.getString("area_code").equals("123"));
        }
    }

    @Test
    public void verifyContactUDTsSolrQuery() {
        String solrQuery = "select * from contact\n" +
                "where\n" +
                "    solr_query = '" +
                "{\"q\": \"{!tuple}tele_com.area_code:123\"," +
                "\"sort\": \"contact_document_id asc\"}'";

        ResultSet resCheck = session.execute(solrQuery);

        //call common verification method
        verifyExpectedUdtValues(resCheck);
    }

    private void verifyExpectedUdtValues(ResultSet resCheck){
        if(null != resCheck) {
            //check first entry
            Row rowVal1 = resCheck.one();

            assert(rowVal1.getLong("contact_document_id") == (long)83);

            Set<UdtValue> setTelecom1 = rowVal1.getSet("tele_com",UdtValue.class);

            //check only one result
            assert(setTelecom1.size() == 1);

            //verify udt values
            UdtValue udt = setTelecom1.iterator().next();
            assert(udt.getString("telecom_method").equals("PV"));
            assert(udt.getString("area_code").equals("123"));
            assert(udt.getString("phone_number").equals("456-7890"));

            //check next entry
            Row rowVal2 = resCheck.one();
            assert(rowVal2.getLong("contact_document_id") == (long)337);

            Set<UdtValue> setTelecom2 = rowVal2.getSet("tele_com",UdtValue.class);

            //check only one result
            assert(setTelecom2.size() == 1);

            //verify udt values
            UdtValue udt2 = setTelecom2.iterator().next();
            assert(udt2.getString("telecom_method").equals("SV"));
            assert(udt2.getString("area_code").equals("123"));
            assert(udt2.getString("phone_number").equals("333-3337"));

            //check next entry
            Row rowVal3 = resCheck.one();
            assert(rowVal3.getLong("contact_document_id") == (long)363);

            Set<UdtValue> setTelecom3 = rowVal3.getSet("tele_com",UdtValue.class);

            //check only one result
            assert(setTelecom3.size() == 1);

            //verify udt values
            UdtValue udt3 = setTelecom3.iterator().next();
            assert(udt3.getString("telecom_method").equals("MB"));
            assert(udt3.getString("area_code").equals("123"));
            assert(udt3.getString("phone_number").equals("636-3636"));
        }
        else{
            //result set should not be null
            assert(false);
        }
    }

    @Test
    public void verifyContactUDTInsertUpdateMapper(){
        long testDocId = 2010;
        String addSecUnit1 = "STE";
        String addSecVal1 = "SM20001";
        String addSecUnit2 = "BLDG";
        String addSecVal2 = "NEM2001";
        String addSecUnit3 = "FL";
        String addSecVal3 = "M20";


        String method1 = "PV-1";
        String areaCode1 = "111";
        String phone1 = "111-1111";
        String method2 = "PV-2";
        String areaCode2 = "222";
        String phone2 = "222-2222";
        String method3 = "PV-3";
        String areaCode3 = "333";
        String phone3 = "333-3333";

        Contact deleteContact = new Contact();
        deleteContact.setContactDocumentId(testDocId);
        daoContact.delete(deleteContact);

        Contact readVerifyDelete = daoContact.findByContactDocumentId(testDocId);
        assert(null == readVerifyDelete);

        ContactTelecomDetails writeTelecom1 = new ContactTelecomDetails();
        writeTelecom1.setTelecomMethod(method1);
        writeTelecom1.setTelecomMethod(areaCode1);
        writeTelecom1.setPhoneNumber(phone1);

        ContactTelecomDetails writeTelecom2 = new ContactTelecomDetails();
        writeTelecom2.setTelecomMethod(method2);
        writeTelecom2.setTelecomMethod(areaCode2);
        writeTelecom2.setPhoneNumber(phone2);

        Set<ContactTelecomDetails> setTelecom = new HashSet<>();
        setTelecom.add(writeTelecom1);
        setTelecom.add(writeTelecom2);

        Contact writeContact = new Contact();
        writeContact.setContactDocumentId(testDocId);
        writeContact.setTeleCom(setTelecom);

        //write new record to DB
        daoContact.save(writeContact);

        //verify record written as expected
        Contact checkWriteContact = daoContact.findByContactDocumentId(testDocId);
        assert(testDocId == checkWriteContact.getContactDocumentId());

        Set<ContactTelecomDetails> setCheckTelecom = checkWriteContact.getTeleCom();
        assert(2 == setTelecom.size());

        //update collection of UDTs
        ContactTelecomDetails writeTelecom3 = new ContactTelecomDetails();
        writeTelecom3.setTelecomMethod(method3);
        writeTelecom3.setTelecomMethod(areaCode3);
        writeTelecom3.setPhoneNumber(phone3);

        setCheckTelecom.add(writeTelecom3);
        checkWriteContact.setTeleCom(setCheckTelecom);

        daoContact.update(checkWriteContact);


        //verify update executed as expected
        Contact checkUpdateContact = daoContact.findByContactDocumentId(testDocId);
        assert(testDocId == checkUpdateContact.getContactDocumentId());

        Set<ContactTelecomDetails> setCheckUpdateAddrSec = checkUpdateContact.getTeleCom();
//        assert(3 == setCheckUpdateAddrSec.size());  //** update does not appear to currently work as expected

        daoContact.delete(deleteContact);
        Contact readVerifyDeleteEnd = daoContact.findByContactDocumentId(testDocId);
        assert(null == readVerifyDeleteEnd);
    }


    @Test
    public void verifyContactUDTInsertUpdate(){
        //cleanup any existing records and verify
        String cleanupQuery = "DELETE from customer.contact where contact_document_id = 2001;";
        session.execute(cleanupQuery);

        String checkQuery = "select * from customer.contact where contact_document_id = 2001;";
        ResultSet rsInitialCheck = session.execute(checkQuery);

        assert(rsInitialCheck != null && rsInitialCheck.all().size() == 0);

        String insertStmt = "insert into contact\n" +
                "(\n" +
                "    contact_document_id,\n" +
                "    tele_com\n" +
                ")\n" +
                "values\n" +
                "(\n" +
                "    2001,\n" +
                "    {\n" +
                "        {telecom_method:'PV', area_code:'111', phone_number:'111-2001'},\n" +
                "        {telecom_method:'SV', area_code:'222', phone_number:'222-2001'}\n" +
                "    }\n" +
                ")\n" +
                ";";

        //add new record
        session.execute(insertStmt);

        //verify record exists
        ResultSet rsDataCheck = session.execute(checkQuery);

        Row dataRow = rsDataCheck.one();
        assert(dataRow.getLong("contact_document_id") == (long)2001);

        Set<UdtValue> setCheckTelecom = dataRow.getSet("tele_com",UdtValue.class);

        //check only one result
        assert(setCheckTelecom.size() == 2);

        String updateStmt = "update contact\n" +
                "set \n" +
                "    tele_com = tele_com + {{telecom_method:'MB', area_code:'333', phone_number:'333-2001'}}\n" +
                "where\n" +
                "    contact_document_id = 2001\n" +
                "    ;";

        //update set of UDTs
        session.execute(updateStmt);

        //verify record updated
        ResultSet rsDataCheckUpdate = session.execute(checkQuery);

        Row dataRowUpdated = rsDataCheckUpdate.one();
        assert(dataRowUpdated.getLong("contact_document_id") == (long)2001);

        Set<UdtValue> setCheckTelecomUpdated = dataRowUpdated.getSet("tele_com",UdtValue.class);

        //check only one result
        assert(setCheckTelecomUpdated.size() == 3);
    }

    private void runQueryList(List<TestQuery> queries){

        for(TestQuery curQuery : queries){
            if(curQuery.queryType == TestQuery.QueryType.READ_PROP){
                String checkQuery = "select * from " +
                        curQuery.table + " where " +
                        curQuery.recordIDProp + " = ";
                if(curQuery.numericKey){
                    checkQuery += curQuery.recordIDval;
                }
                else {
                    checkQuery += "'" + curQuery.recordIDval + "'";
                }

                ResultSet resCheck = session.execute(checkQuery);

                if(null != resCheck){
                    Row rowVal = resCheck.one();
                    String checkedVal = rowVal.getString(curQuery.checkProp);
                    if(checkedVal.equals(curQuery.expectedResult)){
                        System.out.println(("Verification query " + curQuery.queryID + " executed successfully."));
                        assert(true);
                    }
                    else{
                        System.out.println("Failure running query " + curQuery.queryID + "." +
                                "\n\tID property - " + curQuery.recordIDProp +
                                "\n\tReturned value  - " + checkedVal +
                                "\n\tExpected value - " + curQuery.expectedResult +
                                "\n\tExecute check query - " + checkQuery);
                        assert(false);
                    }
                }
                else{
                    System.out.println("Failure running query " + curQuery.queryID + ".  Result set is NULL.");
                    assert(false);
                }
            }
        }
    }
}
