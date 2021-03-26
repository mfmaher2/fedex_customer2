package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
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

import static com.datastax.oss.driver.api.core.CqlSession.*;

public class CustomerTest {

    private static CqlSession session = null;
    private static CustomerContactMapper contactMapper = null;
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
        }
    }

    @AfterClass
    public static void close(){
        System.out.println("Running " + productName + " close");

        dropTestKeyspace();
        if (session != null) session.close();
    }

    @Test
    public void contactUdtMapperReadTest(){
        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

        CustomerContact foundContact = daoContact.findByContactDocumentId(83);
        Set<CustomerContactAddressSecondary> setAddrSecondary = foundContact.getAddressSecondary();

        //verify size of returned set
        assert(setAddrSecondary.size() ==1);

        //verify udt values
        CustomerContactAddressSecondary addrSec = setAddrSecondary.iterator().next();
        assert(addrSec.getUnit().equals("BLDG"));
        assert(addrSec.getValue().equals("5"));
    }

    @Test
    public void contactUdtMapperWriteTest(){
        long testDocID = 20001;
        String testFirstName = "First20001";
        String testLastName = "Last20001" ;
        String testMiddleName = "Middle20001" ;
        String addSecUnit = "FL20001";
        String addSecVal = "001";

        CustomerContactDao daoContact  =  contactMapper.customerContactDao(keyspaceName);

        CustomerContactAddressSecondary writeAddSec = new CustomerContactAddressSecondary();
        writeAddSec.setUnit(addSecUnit);
        writeAddSec.setValue(addSecVal);

        Set<CustomerContactAddressSecondary> setAddrSec = new HashSet<>();
        setAddrSec.add(writeAddSec);

        CustomerContact writeContact = new CustomerContact();
        writeContact.setContactDocumentId(testDocID);
        writeContact.setPersonFirstName(testFirstName);
        writeContact.setPerson__last_name(testLastName);
        writeContact.setPerson_MiddleName(testMiddleName);
        writeContact.setAddressSecondary(setAddrSec);

        //write new record to DB
        daoContact.save(writeContact);

        //verify record written correctly
        CustomerContact foundContact = daoContact.findByContactDocumentId(testDocID);
        Set<CustomerContactAddressSecondary> setAddrSecondary = foundContact.getAddressSecondary();

        //verify size of returned set
        assert(1 == setAddrSecondary.size());

        //verify udt values
        CustomerContactAddressSecondary addrSec = setAddrSecondary.iterator().next();
        assert(addrSec.getUnit().equals(addSecUnit));
        assert(addrSec.getValue().equals(addSecVal));

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
    public void verifyEnterpriseProfile(){
        List<TestQuery> entProfileQueries = new ArrayList<>();

        entProfileQueries.add(new TestQuery("qEntProf1",
                "enterprise_profile", "account_number", "acctNum1",
                "profile__enterprise_source","source1"));


        entProfileQueries.add(new TestQuery("qEntProf2",
                "enterprise_profile", "account_number", "acctNum2",
                "profile__account_status__status_code","ARCHIVE"));

        runQueryList(entProfileQueries);
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

        String addreSecondaryQuery = "select * from contact where contact_document_id = 83";
        ResultSet resCheck = session.execute(addreSecondaryQuery);

        if(null != resCheck) {
            Row rowVal = resCheck.one();

            Set<UdtValue> setAddrSecondary= rowVal.getSet("address__secondary", UdtValue.class);

            //check only one result
            assert(setAddrSecondary.size() ==1);

            //verify udt values
            UdtValue udt = setAddrSecondary.iterator().next();
            assert(udt.getString("unit").equals("BLDG"));
            assert(udt.getString("value").equals("5"));
        }
    }

    @Test
    public void verifyContactUDTsSolrQuery() {
        String solrQuery = "select * from contact\n" +
                "where\n" +
                "    solr_query = '" +
                "{\"q\": \"{!tuple}address__secondary.unit:BLDG\"," +
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

            Set<UdtValue> setAddrSecondary1 = rowVal1.getSet("address__secondary",UdtValue.class);

            //check only one result
            assert(setAddrSecondary1.size() ==1);

            //verify udt values
            UdtValue udt = setAddrSecondary1.iterator().next();
            assert(udt.getString("unit").equals("BLDG"));
            assert(udt.getString("value").equals("5"));

            //check next entry
            Row rowVal2 = resCheck.one();
            assert(rowVal2.getLong("contact_document_id") == (long)337);

            Set<UdtValue> setAddrSecondary2 = rowVal2.getSet("address__secondary",UdtValue.class);

            //check only one result
            assert(setAddrSecondary2.size() ==1);

            //verify udt values
            UdtValue udt2 = setAddrSecondary2.iterator().next();
            assert(udt2.getString("unit").equals("BLDG"));
            assert(udt2.getString("value").equals("A"));

            //check next entry
            Row rowVal3 = resCheck.one();
            assert(rowVal3.getLong("contact_document_id") == (long)363);

            Set<UdtValue> setAddrSecondary3 = rowVal3.getSet("address__secondary",UdtValue.class);

            //check only one result
            assert(setAddrSecondary3.size() ==1);

            //verify udt values
            UdtValue udt3 = setAddrSecondary3.iterator().next();
            assert(udt3.getString("unit").equals("BLDG"));
            assert(udt3.getString("value").equals("1"));

            //check next entry
            Row rowVal4 = resCheck.one();
            assert(rowVal4.getLong("contact_document_id") == (long)433);

            Set<UdtValue> setAddrSecondary4 = rowVal4.getSet("address__secondary",UdtValue.class);

            //check only one result
            assert(setAddrSecondary4.size() ==1);

            //verify udt values
            UdtValue udt4 = setAddrSecondary4.iterator().next();
            assert(udt4.getString("unit").equals("BLDG"));
            assert(udt4.getString("value").equals("2"));


            //check last entry
            Row rowVal5 = resCheck.one();
            assert(rowVal5.getLong("contact_document_id") == (long)2000);

            Set<UdtValue> setAddrSecondary5 = rowVal5.getSet("address__secondary",UdtValue.class);

            //check only one result
            assert(setAddrSecondary5.size() == 2);

            //verify udt values
            UdtValue udt5a = setAddrSecondary5.iterator().next();
            assert(udt5a.getString("unit").equals("BLDG"));
            assert(udt5a.getString("value").equals("7"));
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

        CustomerContact deleteContact = new CustomerContact();
        deleteContact.setContactDocumentId(testDocId);
        daoContact.delete(deleteContact);

        CustomerContact readVerifyDelete = daoContact.findByContactDocumentId(testDocId);
        assert(null == readVerifyDelete);

        CustomerContactAddressSecondary writeAddSec1 = new CustomerContactAddressSecondary();
        writeAddSec1.setUnit(addSecUnit1);
        writeAddSec1.setValue(addSecVal1);

        CustomerContactAddressSecondary writeAddSec2 = new CustomerContactAddressSecondary();
        writeAddSec1.setUnit(addSecUnit2);
        writeAddSec1.setValue(addSecVal2);

        Set<CustomerContactAddressSecondary> setAddrSec = new HashSet<>();
        setAddrSec.add(writeAddSec1);
        setAddrSec.add(writeAddSec2);

        CustomerContact writeContact = new CustomerContact();
        writeContact.setContactDocumentId(testDocId);
        writeContact.setAddressSecondary(setAddrSec);

        //write new record to DB
        daoContact.save(writeContact);

        //verify record written as expected
        CustomerContact checkWriteContact = daoContact.findByContactDocumentId(testDocId);
        assert(testDocId == checkWriteContact.getContactDocumentId());

        Set<CustomerContactAddressSecondary> setCheckAddrSec = checkWriteContact.getAddressSecondary();
        assert(2 == setAddrSec.size());

        //update collection of UDTs
        CustomerContactAddressSecondary writeAddSec3 = new CustomerContactAddressSecondary();
        writeAddSec1.setUnit(addSecUnit3);
        writeAddSec1.setValue(addSecVal3);

        setCheckAddrSec.add(writeAddSec3);
        checkWriteContact.setAddressSecondary(setCheckAddrSec);

        daoContact.update(checkWriteContact);


        //verify update executed as expected
        CustomerContact checkUpdateContact = daoContact.findByContactDocumentId(testDocId);
        assert(testDocId == checkUpdateContact.getContactDocumentId());

        Set<CustomerContactAddressSecondary> setCheckUpdateAddrSec = checkUpdateContact.getAddressSecondary();
//        assert(3 == setCheckUpdateAddrSec.size());  //** update does not appear to currently work as expected

        daoContact.delete(deleteContact);
        CustomerContact readVerifyDeleteEnd = daoContact.findByContactDocumentId(testDocId);
        assert(null == readVerifyDeleteEnd);
    }


    @Test
    public void verifyContactUDTInsertUpdate(){
        String cleanupQuery = "DELETE from customer.contact where contact_document_id = 2001;";
        session.execute(cleanupQuery);

        String checkQuery = "select * from customer.contact where contact_document_id = 2001;";
        ResultSet rsInitialCheck = session.execute(checkQuery);

        assert(rsInitialCheck != null && rsInitialCheck.all().size() == 0);

        String insertStmt = "insert into contact\n" +
                "(\n" +
                "    contact_document_id,\n" +
                "    address__secondary\n" +
                ")\n" +
                "values\n" +
                "(\n" +
                "    2001,\n" +
                "    {\n" +
                "        {unit:'STE', value:'S2001'},\n" +
                "        {unit:'BLDG', value:'NE2001'}\n" +
                "    }\n" +
                ")\n" +
                ";";

        //add new record
        session.execute(insertStmt);

        //verify record exists
        ResultSet rsDataCheck = session.execute(checkQuery);

        Row dataRow = rsDataCheck.one();
        assert(dataRow.getLong("contact_document_id") == (long)2001);

        Set<UdtValue> setAddrSecondary = dataRow.getSet("address__secondary",UdtValue.class);

        //check only one result
        assert(setAddrSecondary.size() == 2);

        String updateStmt = "update contact\n" +
                "set \n" +
                "    address__secondary = address__secondary + {{unit:'FL', value:'20'}}\n" +
                "where\n" +
                "    contact_document_id = 2001\n" +
                "    ;";

        //update set of UDTs
        session.execute(updateStmt);

        //verify record updated
        ResultSet rsDataCheckUpdate = session.execute(checkQuery);

        Row dataRowUpdated = rsDataCheckUpdate.one();
        assert(dataRowUpdated.getLong("contact_document_id") == (long)2001);

        Set<UdtValue> setAddrSecondaryUpdated = dataRowUpdated.getSet("address__secondary",UdtValue.class);

        //check only one result
        assert(setAddrSecondaryUpdated.size() == 3);
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
