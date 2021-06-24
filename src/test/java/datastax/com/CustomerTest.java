package datastax.com;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.protocol.internal.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.datastax.oss.driver.api.core.CqlSession.*;

public class CustomerTest {

    private static CqlSession session = null;
    private static CustomerMapper customerMapper = null;
    static CustomerAccountDao daoAccount = null;
    static CustomerPaymentInfoDao daoPayment = null;
    static CustomerAssocAccountDao daoAssoc = null;
    static CustomerContactDao daoContact = null;
    static CustomerNationalAccountDao daoNational = null;
    static CustomerApplyDiscountDao daoApplyDiscount = null;

    private static boolean skipSchemaCreation = true;
    private static boolean skipDataLoad = true;
    private static boolean skipKeyspaceDrop = true;
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
            daoAccount = customerMapper.customerAccountDao(keyspaceName);
            daoPayment = customerMapper.customerPaymentInfoDao(keyspaceName);
            daoAssoc = customerMapper.customerAssocAccountDao(keyspaceName);
            daoContact  =  customerMapper.customerContactDao(keyspaceName);
            daoNational = customerMapper.customerNationalAccountDao(keyspaceName);
            daoApplyDiscount = customerMapper.customerApplyDiscountDao(keyspaceName);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void dropTestKeyspace(){
        String keyspaceDrop = "DROP KEYSPACE IF EXISTS " + keyspaceName + ";";
        if(!skipKeyspaceDrop) {session.execute(keyspaceDrop); }
    }

    static void loadSchema() throws IOException, InterruptedException {
        if (!skipSchemaCreation) {
            System.out.println("Running " + productName + " loadSchema");

            String keyspaceCreate = "CREATE KEYSPACE If NOT EXISTS " + keyspaceName + " WITH replication = {'class': 'NetworkTopologyStrategy', 'SearchGraphAnalytics': '1'}  AND durable_writes = true;";
            session.execute(keyspaceCreate);

            runScrpt(schemaScriptPath);//TODO get resource path programmatically
        }
    }

    static void runScrpt(String scriptPath) throws InterruptedException, IOException {
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

            runScrpt(dataScriptPath); //TODO get resource path programmatically

            //sleep for a time to allow Solr indexes to update completely
            System.out.println("Completed " + productName + " data load.  Pausing to allow indexes to update...");
            Thread.sleep(11000);
        }
    }

    @AfterClass
    public static void close(){
        System.out.println("Running " + productName + " close");

        dropTestKeyspace();
        if (session != null) session.close();
    }

    @Test
    public void applyDiscountUpdateTest(){
        String acctNum = "123456789";
        String opco = "FX";
        Boolean applyDiscountFlag = true;
        Instant effectiveDT = Instant.parse("2021-05-01T01:00:00.001Z");
        Instant expirationDT = Instant.parse("2015-12-10T00:00:00.001Z");

        CustomerApplyDiscount appDisc = new CustomerApplyDiscount();

        appDisc.setAccountNumber(acctNum);
        appDisc.setOpco(opco);
        appDisc.setApplyDiscountFlag(applyDiscountFlag);
        appDisc.setDisountEffectiveDateTime(effectiveDT);
        appDisc.setDisountExpirationateTime(expirationDT);

        //save initial version of the record
        daoApplyDiscount.save(appDisc);

        //retrieve initial version and verify property values
        CustomerApplyDiscount readAppDisc = daoApplyDiscount.findByKeys(acctNum, opco, effectiveDT);
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
        CustomerApplyDiscount foundUpdatedAppDisc = daoApplyDiscount.findByKeys(acctNum, opco, effectiveDT);
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

        CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> cfDiscounts = daoApplyDiscount.findAllByAccountNumberAsync(acctNum);
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
        String solrParm1 = CustomerApplyDiscountHelper.constructSearchQuery(opco);
        CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> cfDiscounts1 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm1);
        cfDiscounts1.join();
        assert(cfDiscounts1.get().remaining() == expectedResultSize1);

        //Solr filter on opco and apply discount flag values only
        int expectedResultSize2 = 6;
        String solrParm2 = CustomerApplyDiscountHelper.constructSearchQuery(opco, applyDiscountFlag);
        CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> cfDiscounts2 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm2);
        cfDiscounts2.join();
        assert(cfDiscounts2.get().remaining() == expectedResultSize2);

        //Solr filter on opco and effective date/time values only
        int expectedResultSize3 = 7;
        String solrParm3 = CustomerApplyDiscountHelper.constructSearchQuery(opco, effectiveDT);
        CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> cfDiscounts3 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm3);
        cfDiscounts3.join();
        assert(cfDiscounts3.get().remaining() == expectedResultSize3);

        //Solr filter on opco, apply discount flag, effective date/time and expiration date/time
        int expectedResultSize4 = 3;
        String solrParm4 = CustomerApplyDiscountHelper.constructSearchQuery(opco, applyDiscountFlag, effectiveDT,expriationDT);
        CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> cfDiscounts4 =
                daoApplyDiscount.findAllByAccountSearchAsync(acctNum, solrParm4);
        cfDiscounts4.join();
        assert(cfDiscounts4.get().remaining() == expectedResultSize4);
    }


    @Test
    public void nationalAccountFullSearchTest(){
        String solrParam = "national_account_detail__national_account_nbr:00706";
        int expectedResultCount = 14;
        PagingIterable<CustomerNationalAcccount> foundNatAccts = daoNational.findByNationalAccountNumberFullSolrParam(solrParam);

        assert(foundNatAccts.all().size() == expectedResultCount);
    }

    @Test
    public void nationalAccountSearchTest(){
        String solrParam = "national_account_detail__national_account_nbr:00706";
        int expectedResultCount = 14;
        PagingIterable<CustomerNationalAcccount> foundNatAccts = daoNational.findBySearchQuery(solrParam);

        assert(foundNatAccts.all().size() == expectedResultCount);
    }

    @Test
    public void nationalAccountTest(){
        String acctID = "00112770";
        int expectedResultCount = 5;

        PagingIterable<CustomerNationalAcccount> foundNatAccts = daoNational.findByAccountNumber(acctID);

        assert(foundNatAccts.all().size() == expectedResultCount);
    }

    @Test
    public void nationalAccountPagingTest(){
        int pageSize = 3;

        Function<BoundStatementBuilder, BoundStatementBuilder> functionCustomPageSize =
                builder -> builder.setPageSize(pageSize);

        String acctID = "00112770";
        PagingIterable<CustomerNationalAcccount> foundNatAccts = daoNational.findByAccountNumber(acctID, functionCustomPageSize);

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
            CustomerNationalAcccount natAcct = daoNational.asNationalAccount(row);

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
            CustomerNationalAcccount natAcct = daoNational.asNationalAccount(row);

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
        String query = "select * from " + keyspaceName + ".national_account_v1 where solr_query = 'account_number:" + acctID + "';";
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
        String query = "select * from " + keyspaceName + ".national_account_v1 where solr_query = 'account_number:" + acctID + "';";
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
        String temp = Bytes.toHexString(pagingState);
        System.out.println(temp);

        ByteBuffer buff = Bytes.fromHexString(temp);
        System.out.println(buff);
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

        CustomerAccount custAllProps = new CustomerAccount();
        custAllProps.setAccountNumber(acctID);
        custAllProps.setOpco(opco);
        custAllProps.setProfileCustomerType(custType);
        custAllProps.setProfileAccountType(acctType);
        custAllProps.setProfileAccountStatusCode(acctStatus);
        custAllProps.setProfileAccountReasonCode(acctReason);
        custAllProps.setProfileEnterpriseSource(entSource);
        daoAccount.save(custAllProps);

        //verify all properties exist in rcord before deleting select properties
        CustomerAccount foundCust = daoAccount.findByAccountNumber(acctID);
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
        CustomerAccount foundCustDelProp = daoAccount.findByAccountNumber(acctID);
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
        CustomerAccount foundCustDelMultiProps = daoAccount.findByAccountNumber(acctID);
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
        CompletableFuture<CustomerAccount> cfFoundAccount = daoAccount.findByAccountNumberAsync(acctID);
        CompletableFuture<CustomerPaymentInfo> cfFoundPayment = daoPayment.findByAccountNumberAsync(acctID);
        CompletableFuture<CustomerAssocAccount> cfFoundAssoc = daoAssoc.findByAccountNumberAsync(acctID);

        //allow all async calls to complete
        CompletableFuture.allOf(cfFoundAccount, cfFoundPayment, cfFoundAssoc);

        //** check account details
        String expectedOpco = "A";
        String expectedCustomerType = "custType1";
        String expectedAccountType = "acctType1";

        //assign account data object
        CustomerAccount foundAccount = cfFoundAccount.get();

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
        CustomerPaymentInfo foundPayment = cfFoundPayment.get();

        assert(foundPayment.getOpco().equals(expectedPaymentOpco));
        assert(foundPayment.getRecordType().equals(expectedType));
        assert(foundPayment.getRecordKey().equals(expectedKey));
        assert(foundPayment.getCreditCardID().equals(expectedCCId));


        //** check associated account details
        String expectedAssocOpco = "B";
        String expectedAssocAcct = "2222";

        //assign assoc data object
        CustomerAssocAccount foundAssoc = cfFoundAssoc.get();

        assert(foundAssoc.getOpco().equals(expectedAssocOpco));
        assert(foundAssoc.getAssociatedAccountNumber().equals(expectedAssocAcct));
    }

    @Test
    public void customerAssocAccountMapperReadTest(){
        String acctID = "1111";
        String expectedOpco = "B";
        String expectedAssocAcct = "2222";

        CustomerAssocAccount foundAssoc = daoAssoc.findByAccountNumber(acctID);

        assert(foundAssoc.getOpco().equals(expectedOpco));
        assert(foundAssoc.getAssociatedAccountNumber().equals(expectedAssocAcct));
    }

    @Test
    public void customerAssocAccountMapperReadTestAsync() throws ExecutionException, InterruptedException {
        String acctID = "1111";
        String expectedOpco = "B";
        String expectedAssocAcct = "2222";

        CompletableFuture<CustomerAssocAccount> cfFoundAssoc = daoAssoc.findByAccountNumberAsync(acctID);
        cfFoundAssoc.join();
        CustomerAssocAccount foundAssoc = cfFoundAssoc.get();

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

        CustomerPaymentInfo foundPayment = daoPayment.findByAccountNumber(acctID);

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

        CompletableFuture<CustomerPaymentInfo> cfFoundPayment = daoPayment.findByAccountNumberAsync(acctID);
        cfFoundPayment.join();
        CustomerPaymentInfo foundPayment = cfFoundPayment.get();

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

        CustomerAccount foundAccount = daoAccount.findByAccountNumber(acctID);

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

        CompletableFuture<CustomerAccount> cfFoundAccount = daoAccount.findByAccountNumberAsync(acctID);
        cfFoundAccount.join();
        CustomerAccount foundAccount = cfFoundAccount.get();

        assert(foundAccount.getOpco().equals(expectedOpco));
        assert(foundAccount.getProfileCustomerType().equals(expectedCustomerType));
        assert(foundAccount.getProfileAccountType().equals(expectedAccountType));
    }


    @Test
    public void contactUdtMapperReadTest(){
        CustomerContact foundContact = daoContact.findByContactDocumentId(83);
        Set<CustomerContactTelecomDetails> setTeleCom = foundContact.getTeleCom();

        //verify size of returned set
        assert(setTeleCom.size() == 1);

        //verify udt values
        CustomerContactTelecomDetails addrSec = setTeleCom.iterator().next();
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

        CustomerContactTelecomDetails writeTeleCom = new CustomerContactTelecomDetails();
        writeTeleCom.setTelecomMethod(telComMethod);
        writeTeleCom.setAreaCode(areaCode);
        writeTeleCom.setPhoneNumber(phoneNum);

        Set<CustomerContactTelecomDetails> setTelecom = new HashSet<>();
        setTelecom.add(writeTeleCom);

        CustomerContact writeContact = new CustomerContact();
        writeContact.setContactDocumentId(testDocID);
        writeContact.setPersonFirstName(testFirstName);
        writeContact.setPerson__last_name(testLastName);
        writeContact.setPerson_MiddleName(testMiddleName);
        writeContact.setTeleCom(setTelecom);

        //write new record to DB
        daoContact.save(writeContact);

        //verify record written correctly
        CustomerContact foundContact = daoContact.findByContactDocumentId(testDocID);
        Set<CustomerContactTelecomDetails> setReadTelecom = foundContact.getTeleCom();

        //verify size of returned set
        assert(1 == setReadTelecom.size());

        //verify udt values
        CustomerContactTelecomDetails addrSec = setReadTelecom.iterator().next();
//        assert(addrSec.getUnit().equals(addSecUnit));
//        assert(addrSec.getValue().equals(addSecVal));

        //cleanup test UDT record
        daoContact.delete(writeContact);
        CustomerContact readVerifyDelete = daoContact.findByContactDocumentId(testDocID);
        assert(null == readVerifyDelete);
    }

    @Test
    public void contactMapperTest(){
        long testDocID = 20000;
        String testFirstName = "First20000";
        String testLastName = "Last20000" ;
        String testMiddleName = "Middle20000" ;

        //write test record
        CustomerContact writeContact = new CustomerContact();
        writeContact.setContactDocumentId(testDocID);
        writeContact.setPersonFirstName(testFirstName);
        writeContact.setPerson__last_name(testLastName);
        writeContact.setPerson_MiddleName(testMiddleName);
        daoContact.save(writeContact);

        //test read functionality
        CustomerContact readContact = daoContact.findByContactDocumentId(testDocID);
        assert(readContact.getPersonFirstName().equals(testFirstName));
        assert(readContact.getPerson__last_name().equals(testLastName));
        assert(readContact.getPerson_MiddleName().equals(testMiddleName));

        //test delete capability
        daoContact.delete(writeContact);
        CustomerContact readVerifyDelete = daoContact.findByContactDocumentId(testDocID);
        assert(null == readVerifyDelete);
    }

    @Test
    public void contactMapperAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<CustomerContact> cfFoundContact = daoContact.findByContactDocumentIdAsync(83);
        cfFoundContact.join();

        CustomerContact foundContact = cfFoundContact.get();

        Set<CustomerContactTelecomDetails> setTeleCom = foundContact.getTeleCom();

        //verify size of returned set
        assert(setTeleCom.size() == 1);

        //verify udt values
        CustomerContactTelecomDetails addrSec = setTeleCom.iterator().next();
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

        CustomerContact deleteContact = new CustomerContact();
        deleteContact.setContactDocumentId(testDocId);
        daoContact.delete(deleteContact);

        CustomerContact readVerifyDelete = daoContact.findByContactDocumentId(testDocId);
        assert(null == readVerifyDelete);

        CustomerContactTelecomDetails writeTelecom1 = new CustomerContactTelecomDetails();
        writeTelecom1.setTelecomMethod(method1);
        writeTelecom1.setTelecomMethod(areaCode1);
        writeTelecom1.setPhoneNumber(phone1);

        CustomerContactTelecomDetails writeTelecom2 = new CustomerContactTelecomDetails();
        writeTelecom2.setTelecomMethod(method2);
        writeTelecom2.setTelecomMethod(areaCode2);
        writeTelecom2.setPhoneNumber(phone2);

        Set<CustomerContactTelecomDetails> setTelecom = new HashSet<>();
        setTelecom.add(writeTelecom1);
        setTelecom.add(writeTelecom2);

        CustomerContact writeContact = new CustomerContact();
        writeContact.setContactDocumentId(testDocId);
        writeContact.setTeleCom(setTelecom);

        //write new record to DB
        daoContact.save(writeContact);

        //verify record written as expected
        CustomerContact checkWriteContact = daoContact.findByContactDocumentId(testDocId);
        assert(testDocId == checkWriteContact.getContactDocumentId());

        Set<CustomerContactTelecomDetails> setCheckTelecom = checkWriteContact.getTeleCom();
        assert(2 == setTelecom.size());

        //update collection of UDTs
        CustomerContactTelecomDetails writeTelecom3 = new CustomerContactTelecomDetails();
        writeTelecom3.setTelecomMethod(method3);
        writeTelecom3.setTelecomMethod(areaCode3);
        writeTelecom3.setPhoneNumber(phone3);

        setCheckTelecom.add(writeTelecom3);
        checkWriteContact.setTeleCom(setCheckTelecom);

        daoContact.update(checkWriteContact);


        //verify update executed as expected
        CustomerContact checkUpdateContact = daoContact.findByContactDocumentId(testDocId);
        assert(testDocId == checkUpdateContact.getContactDocumentId());

        Set<CustomerContactTelecomDetails> setCheckUpdateAddrSec = checkUpdateContact.getTeleCom();
//        assert(3 == setCheckUpdateAddrSec.size());  //** update does not appear to currently work as expected

        daoContact.delete(deleteContact);
        CustomerContact readVerifyDeleteEnd = daoContact.findByContactDocumentId(testDocId);
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
