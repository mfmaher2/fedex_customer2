package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static com.datastax.oss.driver.api.core.CqlSession.*;

public class CustomerTest {

    private static CqlSession session = null;
    private static CustomerContactMapper contactMapper = null;
    private static CustomerAccountMapper accountMapper = null;
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
            session = builder()
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

            contactMapper = new CustomerContactMapperBuilder(session).build();
            accountMapper = new CustomerAccountMapperBuilder(session).build();
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
    public void customerAccountMapperReadTest(){
        CustomerAccountDao daoAccount = accountMapper.customerAccountDao(keyspaceName);

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
    public void contactUdtMapperReadTest(){
        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

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

        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

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
        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

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
        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

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
        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

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
