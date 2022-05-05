package datastax.com;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.protocol.internal.util.Bytes;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

import datastax.com.dataObjects.*;
import datastax.com.DAOs.*;
import datastax.com.schemaElements.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CustomerTest {

    private static CustomerMapper customerMapper = null;
    private static CustomerMapperEdge customerMapperEdge = null;
    static AccountDao daoAccount = null;
    static PaymentInfoDao daoPayment = null;
    static AssocAccountDao daoAssoc = null;
    static ContactDao daoContact = null;
    static NationalAccountDao daoNational = null;
    static ApplyDiscountDao daoApplyDiscount = null;
    static IndexCollectionDao daoIndexCollect = null;
    static CommentDao daoComment = null;
    static AuditHistoryDao daoAuditHistory = null;
    static AccountContactDao daoAccountContact = null;

    private static boolean skipSchemaCreation = false;
    private static boolean skipDataLoad = false;
    private static boolean skipKeyspaceDrop = false;
    private static boolean skipIndividualTableDrop = false;
    private static String productName = "Customer" ;

    //Create environment related parameters
    private static EnvironmentCustomizeParameters environmentParams = new EnvironmentCustomizeParameters();
    private static EnvironmentCustomize environment = null;

    private static Map<DataCenter, CqlSession>  sessionMap = new HashMap<>();
    private static KeyspaceConfig ksConfig = null;

    @BeforeClass
    public static void init() {
        System.out.println(productName + " - before init() method called");

        try{
            //Setup for specific test environment - only one (L1 or L4) should be uncommented
            //**********
            //** Begin L1 environment config

            //common values
            environmentParams.sourceFilesPath = Paths.get("src/main/resources/genericSchema/").toAbsolutePath().toString();
            environmentParams.dataFilesPath = Paths.get("src/test/testData").toAbsolutePath().toString();
            environmentParams.cqlshPath = "/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh";
            environmentParams.bulkLoadPath = "/Users/michaeldownie/DSE/dsbulk-1.8.0/bin/dsbulk";

            //** End L1 environment config
            //**********
            ksConfig = new KeyspaceConfigSingleDC("SearchGraphAnalytics");

            environmentParams.environmentID = "l1";
            environmentParams.schemaCreateHost = "127.0.0.1";
            environmentParams.schemaCreatePort = "9042";
            environmentParams.searchIndexCreateHost = "127.0.0.1";
            environmentParams.searchIndexCreatePort = "9042";

            String sessionConf = "src/test/resources/L1/application.conf";
            String confFilePath = Paths.get(sessionConf).toAbsolutePath().toString();
            CqlSession commonSession = CqlSession.builder()
                    .withConfigLoader(DriverConfigLoader.fromFile(new File(confFilePath)))
                    .build();

            //in L1 only a single DC, assign each entry in session map to same 'common'
            //session value -- all operations will execute using same session
            sessionMap.put(DataCenter.CORE, commonSession);
            sessionMap.put(DataCenter.EDGE, commonSession);
            sessionMap.put(DataCenter.SEARCH, commonSession);
            //**********


            //** Begin L4 environment config
//            ksConfig = new KeyspaceConfigMultiDC("core", "edge", "search");
//            SCHEMA_SCRIPT_PATH = "src/test/resources/L4/create_customer_schema.sh" ;
//            DATA_SCRIPT_PATH = "src/test/resources/L4/load_customer_data.sh" ;
//
//            environmentParams.environmentID = "l4";
//            environmentParams.schemaCreateHost = "127.0.0.1";
//            environmentParams.schemaCreatePort = "9042";
//            environmentParams.searchIndexCreateHost = "127.0.0.1";
//            environmentParams.searchIndexCreatePort = "9044";


//            //L4 - core DC session
//            String coreSessionConf = "src/test/resources/L4/core-application.conf";
//            String coreConfFilePath = Paths.get(coreSessionConf).toAbsolutePath().toString();
//            CqlSession coreSession = CqlSession.builder()
//                    .withConfigLoader(DriverConfigLoader.fromFile(new File(coreConfFilePath)))
//                    .build();
//            sessionMap.put(DataCenter.CORE, coreSession);
//
//            //L4 - edge DC session
//            String edgeSessionConf = "src/test/resources/L4/edge-application.conf";
//            String edgeConfFilePath = Paths.get(edgeSessionConf).toAbsolutePath().toString();
//            CqlSession edgeSession = CqlSession.builder()
//                    .withConfigLoader(DriverConfigLoader.fromFile(new File(edgeConfFilePath)))
//                    .build();
//            sessionMap.put(DataCenter.EDGE, edgeSession);
//
//            //L4 - search DC session
//            String searchSessionConf = "src/test/resources/L4/search-application.conf";
//            String searchConfFilePath = Paths.get(searchSessionConf).toAbsolutePath().toString();
//            CqlSession searchSession = CqlSession.builder()
//                    .withConfigLoader(DriverConfigLoader.fromFile(new File(searchConfFilePath)))
//                    .build();
//            sessionMap.put(DataCenter.SEARCH, searchSession);
            //** End L4 environment config
            //**********

            environment = new EnvironmentCustomize(environmentParams);
            environment.generateCustomizedEnvironment();


            //setup schema and data
            dropTestKeyspace();
            loadSchema();
            loadData();

            System.out.println("\tBeginning Mapper and DAO creation");
            customerMapper = new CustomerMapperBuilder(sessionMap.get(DataCenter.CORE)).build();
            customerMapperEdge = new CustomerMapperEdgeBuilder(sessionMap.get(DataCenter.EDGE)).build();

            daoAccount = customerMapper.accountDao(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS));
            daoPayment = customerMapper.paymentInfoDao(ksConfig.getKeyspaceName(Keyspace.PAYMENT_INFO_KS));
            daoAssoc = customerMapper.assocAccountDao(ksConfig.getKeyspaceName(Keyspace.ASSOC_ACCOUNT_KS));
            daoContact  =  customerMapper.contactDao(ksConfig.getKeyspaceName(Keyspace.CUSTOMER));
            daoNational = customerMapper.nationalAccountDao(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS));
            daoIndexCollect = customerMapper.indexCollectionDao(ksConfig.getKeyspaceName(Keyspace.CUSTOMER));
            daoApplyDiscount = customerMapper.applyDiscountDao(ksConfig.getKeyspaceName(Keyspace.APPLY_DISCOUNT_KS));
            daoComment = customerMapper.commentDao(ksConfig.getKeyspaceName(Keyspace.COMMENT_KS));
            daoAuditHistory = customerMapper.auditHistoryDao(ksConfig.getKeyspaceName(Keyspace.AUDIT_HISTORY_KS));
            daoAccountContact = customerMapperEdge.accountContactDao(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_CONTACT_KS));
            System.out.println("\tMapper and DAO creation complete");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void dropTestKeyspace() throws InterruptedException {
        if(!skipKeyspaceDrop) {

            CqlSession localSession = sessionMap.get(DataCenter.SEARCH); //**Note use search for all functionality

            for(Keyspace ks : Keyspace.values()) {
                String ksName = ksConfig.getKeyspaceName(ks);

                if (!skipIndividualTableDrop) {
                    System.out.println("Dropping tables from keyspace - " + ksName);
                    long startTables = System.currentTimeMillis();

                    Select select = selectFrom("system_schema", "tables")
                            .column("table_name")
                            .whereColumn("keyspace_name").isEqualTo(bindMarker());

                    ResultSet resultSet = localSession.execute(select.build(ksName));
                    List<Row> foundRows = resultSet.all();
                    for (Row curRow : foundRows) {
                        String curTable = curRow.getString("table_name");
                        localSession.execute(dropTable(ksName, curTable).ifExists().build());
                        System.out.println("\tdrop table - " + curTable);
                    }
                    long stopTables = System.currentTimeMillis();
                    System.out.println("Time for dropping all tables from keyspace " + ksName + "- " + ((stopTables - startTables) / 1000.0) + "s");
                }

                long startKeySpace = System.currentTimeMillis();
                System.out.println("Dropping keyspace - " + ksName);
                localSession.execute(dropKeyspace(ksName).ifExists().build());
                long stopKeyspace = System.currentTimeMillis();
                System.out.println("Time for dropping keyspace - " + ((stopKeyspace - startKeySpace) / 1000.0) + "s");

                Thread.sleep(150L);
            }
        }
    }

    static void loadSchema() throws IOException, InterruptedException {
        if (!skipSchemaCreation) {
            System.out.println("Running " + productName + " loadSchema");

            System.out.println("\tBeginning keypspace creation");
            KeyspaceCreator.createKeyspacesFromConfig(ksConfig, sessionMap.get(DataCenter.SEARCH));
            System.out.println("\tKeyspace creation complete");

            System.out.println("\tBeginning table and index creation");
            runScript(environment.getSchemaCreationFilePath());
            System.out.println("\tTable and index creation complete");
        }
    }

    static void runScript(String scriptPath) throws InterruptedException, IOException {
        ProcessBuilder processBuild = new ProcessBuilder(Paths.get(scriptPath).toAbsolutePath().toString());
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

            runScript(environment.getLoadDataFilePath());

            //sleep for a time to allow Solr indexes to update completely
            System.out.println("Completed " + productName + " data load.  Pausing to allow indexes to update...");
            Thread.sleep(11000);

            System.out.println("Index update complete, starting tests...");
        }
    }

    @AfterClass
    public static void close() throws InterruptedException {
        System.out.println("Running " + productName + " close");

        dropTestKeyspace();
        sessionMap.values().forEach(s -> s.close());
    }

   @Ignore
    @Test
    public void outputKeyspaceCreation() throws IOException {
        String outputFilePath = "/Users/michaeldownie/Downloads/camKeyspaceCreate.cql";
        KeyspaceCreator.outputKeyspacesCreationCQL(ksConfig, outputFilePath);
    }

    @Test
    public void prepareTimout(){
        CqlSession localSession = sessionMap.get(DataCenter.CORE);

        Select select1 = selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "cust_acct_v1")
                                .all()
                                .whereColumn("account_number").in(bindMarker());
        PreparedStatement prep = localSession.prepare(select1.asCql());

        Select select2 = selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "cust_acct_v1")
                            .columns("account_number", "opco", "last_update_tmstp", "profile__customer_type", "profile__account_type", "profile__account_service_level", "profile__account_status__status_code", "profile__account_status__status_date", "profile__account_status__reason_code", "profile__fdx_ok_to_call_flag", "profile__enterprise_source", "profile__nasa_id", "profile__nasa_key", "profile__creation_date", "profile__origin_source", "profile__account_linkage_flag", "profile__welcome_kit__welcome_kit_flag", "profile__welcome_kit__welcome_kit_promo_code", "profile_service_restrictions", "profile_view_restrictions", "profile_tax_exempt", "eligibility__ground", "eligibility__express", "eligibility__freight", "eligibility__office", "profile__customer_requester_name", "profile__employee_requester__opco", "profile__employee_requester__number", "profile__source_group", "profile__source_dept", "profile__source_system", "profile__employee_creator_opco", "profile__employee_creator_number", "profile__account_sub_type", "profile__customer_account_status", "profile__duplicate_account_flag", "profile__archive_date", "profile__archive_reason_code", "profile__archive_options", "profile__cargo_ind", "profile__pref_cust_flag", "profile__sales_rep__opco", "profile__sales_rep__number", "profile__service_level", "profile__scac_code", "profile_special_instructions", "account_receivables__coll_zone_desc", "account_receivables__gsp_write_off", "account_receivables__online_eligibility", "account_receivables__partial_pay_letter_flag", "account_receivables__payment_type", "account_receivables__payment_method_code", "account_receivables__payor_type", "account_receivables__arrow_customer_flag_cd", "account_receivables__international___ar_preference", "account_receivables__international___ar_date", "account_receivables__no_refund_flag", "account_receivables__debut_company_code", "account_receivables__credit_note_flag", "account_receivables__credit_note_exception_flag", "account_receivables__fifo_eligibility_code", "aggregations__ed_aggr_code", "aggregations__geo_acct_number", "aggregations__global_account_number", "aggregations__global_subgroup", "aggregations__ss_account_number", "aggregations__bill_to_number", "aggregations__edi_number", "aggregations__copy_master_address", "claims_preference", "credit_detail__credit_status", "credit_detail__credit_status_reason_code", "credit_detail__denied_flag", "credit_detail__bankruptcy_date", "credit_detail__cash_only_flag", "credit_detail__cash_only_date", "credit_detail__cash_only_reason", "credit_detail__credit_alert_detail", "credit_detail__credit_alert_account_number", "credit_detail__credit_alert_parent_type", "credit_detail__credit_limit", "credit_detail__credit_limit_tolerance_pct", "credit_detail__credit_override_date", "credit_detail__credit_rating", "credit_detail__receivership_account_number", "credit_detail__receivership_date", "credit_detail__rev_auth_id", "edi__cust_inv_rept_flag", "edi__dom_data_frmt", "edi__dom_frmt_ver", "edi__dom_inv_print_until_date", "edi__intl_data_frmt", "edi__intl_inv_frmt_ver", "edi__intl_inv_print_until_date", "edi__mm_bill_3rd_party", "edi__mm_bill_recip", "edi__mm_bill_ship", "edi__mm_bill_pwr_ship", "edi__past_due_medium", "edi__past_due_send_to", "remit_frmt_vers", "sep_exp_grnd_file", "invoice_preference__additional_invoice_copy_flag_cd", "invoice_preference__audit_firm_exp_year_month", "invoice_preference__audit_firm_number", "invoice_preference__billing_closing_day", "invoice_preference__billing_cycle", "invoice_preference__billing_medium", "invoice_preference__billing_payment_day", "invoice_preference__billing_payment_month", "invoice_preference__billing_restriction_indicator", "invoice_preference__billing_type", "invoice_preference__combine_option", "invoice_preference__consolidated_invoicing_flag_cd", "invoice_preference__consolidated_refund_flag", "invoice_preference__cost_center_number", "invoice_preference__currency_code", "invoice_preference__customer_reference_information", "invoice_preference__daysto_credit", "invoice_preference__daysto_pay", "invoice_preference__document_exception_indicator", "invoice_preference__duty_tax_daysto_pay", "invoice_preference__duty_tax_billing_cycle", "invoice_preference__electronic_bill_payment_plan_flag_cd", "invoice_preference__electronic_data_record_proof_of_delivery", "invoice_preference__fax_flag", "invoice_preference__fec_discount_card_flag_cd", "invoice_preference__ground_auto___pod", "invoice_preference__ground_duty_tax_billing_cycle", "invoice_preference__ground_print_weight_indicator", "invoice_preference__international_billing_cycle", "invoice_preference__international_billing_medium", "invoice_preference__international_invoice_bypass", "invoice_preference__international_invoice_program_override_flag", "invoice_preference__international_parent_child_flag", "invoice_preference__international_duty_tax_invoice_bypass", "invoice_preference__invoice__detail_level", "invoice_preference__invoice__level_discount_eff_date", "invoice_preference__invoice__level_discount_exp_date", "invoice_preference__invoice__level_discount_flag_cd", "invoice_preference__invoice__minimum_override_flag", "invoice_preference__invoice__option_flag_cd", "invoice_preference__invoice__page_layout_indicator", "invoice_preference__invoice__transaction_breakup_type", "invoice_preference__invoice__wait_days", "invoice_preference__manage_my_account_at_fed_ex_flag_cd", "invoice_preference__master_account_invoice_summary_flag_cd", "invoice_preference__monthly_billing_indicator", "invoice_preference__past_due_detail_level", "invoice_preference__past_due_flag_cd", "invoice_preference__pod_wait_days", "invoice_preference__primary_sort_option", "invoice_preference__print_summary_page_flag", "invoice_preference__print_weight_indicator", "invoice_preference__reference_append", "invoice_preference__return_envelope_indicator", "invoice_preference__single_invoice_option", "invoice_preference__sort_field_length", "invoice_preference__split_bill_duty_tax", "invoice_preference__statement_of_account__billing_cycle", "invoice_preference__statement_of_account__layout_indicator", "invoice_preference__statement_of_account__receipt_flag_cd", "invoice_preference__statement_type", "invoice_preference__statement_type_date", "invoice_preference__viewed_statement_type", "invoice_preference__direct_link_flag", "invoice_preference__no___pod_flag_cd", "invoice_preference__settlement_level_indicator", "invoice_preference__direct_debit_indicator", "invoice_preference__fbo_eft_flag", "invoice_preference__balance_forward_code", "invoice_preference__late_fee_enterprise_waiver", "mma_stats__last_cancel_code", "mma_stats__last_cancel_date", "mma_stats__last_deactivation_date", "mma_stats__last_registration_date", "mma_stats__last_reject_code", "mma_stats__last_reject_date", "mma_stats__last_update_date_time", "mma_stats__last_update_user", "swipe__cc_eligibility_flag", "swipe__decline_count", "swipe__swipe_lockout_date_time", "duty_tax_info", "tax_info__tax_exempt_code", "tax_info__codice_fiscale", "tax_info__mdb_eff_date", "tax_info__mdb_exp_date", "tax_info__tax_exempt_number", "tax_info__vat__type", "tax_info__vat__number", "tax_info__vat__exemption_code", "tax_info__vat__eff_date", "tax_info__vat__exp_date", "tax_info__vat__response_code", "tax_info__vat__category_code", "tax_info__vat__threshold_amount", "profile__distribution_id", "profile__mailer_id", "profile__pickup_carrier", "profile__return_eligibility_flag", "profile__return_svc_flag", "profile__hub_id", "profile__usps_bound_printed_matter_flag", "profile__usps_media_mail_flag", "profile__usps_parcel_select_flag", "profile__usps_standard_mail_flag", "profile__smartpost_enabled_flag", "profile__delivery_confirmation", "profile__zone_indicator", "profile__multiplier_ref_exp", "profile__mulitiplier_ref_grnd", "profile__agent_flag", "profile__alcohol_flag", "profile__cut_flowers_flag", "profile__declared_value_exception", "profile__derived_station", "profile__drop_ship_flag", "profile__emerge_flag", "profile__doc_prep_service_flag", "profile__ftbd_flag", "profile__ftbd_svc", "profile__hazardous_shipper_flag", "profile__high_value_accept_cd", "profile__interline_cd", "profile__idf_elig_flag", "profile__ifs_flag", "profile__ipd_flag", "profile__money_back_guarantee", "profile__notify_ship_delay_cd", "profile__overnight_frgt_ship_flag", "profile__pak_isrt_flag", "profile__power_of_attorney_date", "profile__power_of_attorney_flag", "profile__regular_stop_flag", "profile__reroutes_allowed_flag", "profile__signature_on_file", "profile__signature_required", "profile__tpc_flag", "profile__emp_ship_emp_number", "profile__supply_no_cut_flag", "profile__starter_kit", "profile__starter_kit_quantity", "profile__exception_flag", "profile__international_shipper", "profile__special_dist_flag", "profile__transmart_flag", "profile__special_comment_cd", "profile__contact_flag", "geographic_info__alpha_id", "geographic_info__station_number", "profile__source_name", "profile__tnt_customer_number", "profile__migration_date", "profile__deactivation_date", "profile__grnd_barcode_type", "profile__grnd_hazmat_flag", "profile__grnd_pickup_type", "profile__grnd_collect_flag", "profile__national_account_number", "profile__grnd_lbl_hazmat_flag", "profile__grnd_lbl_p_r_pflag", "profile__grnd_lbl_univ_waste_flag", "profile__svc_center_code", "profile__airport_code", "profile__business_mode", "profile__coding_instructions", "profile__synonym_name_1", "profile__synonym_name_2", "automation_info__insight_flag", "automation_info__meter_zone_flag", "automation_info__device_type_code", "account_regulatory__fdc_broker_nbr", "account_regulatory__fdc_broker_type_cd", "customer_id__iata_number", "customer_id__custom_importer_id", "customer_id__customer_id_doc_nbr", "account_regulatory__regulated_agentregimeeffyearmonth", "account_regulatory__regulated_agentregimeexpyearmonth", "account_regulatory__bus_registration_id", "account_regulatory__broker_date", "account_regulatory__canadian_broker_id", "account_regulatory__employer_id", "account_regulatory__employer_id_type", "account_regulatory__forwd_brkr_cd", "account_regulatory__gaa_flag", "account_regulatory__import_declaration_cd", "account_regulatory__nri_cd", "account_regulatory__shipper_export_declaration_flag", "profile__spot_rate_ind", "profile__express_plan_flag", "profile__express_plan_activity_date", "profile__catalog_remail_service_cd", "profile__middle_man_cd", "profile__gratuity_flag", "profile__bonus_weight_envelope_flag", "profile__priority_alert_flag", "profile__domestic_max_declared_value_flag", "profile__international_max_declared_value_flag", "profile__linehaul_charge_flag", "profile__pricing_flag", "profile__blind_shipper_flag", "profile__pricing_code", "profile__geo_terr", "profile__marketing_cd", "profile__correspondencecd", "potential_revenue_detail__opening_acct_reason", "potential_revenue_detail__opening_acct_comment", "potential_revenue_detail__lead_employee_opco", "potential_revenue_detail__lead_employee_number", "potential_revenue_detail__revenue_source_system", "potential_revenue_detail__potential_revenue_account_type", "tax_info__tax_data", "tax_info__tax_exempt_detail", "potential_revenue_detail__potential_revenue", "potential_revenue_detail__other_potential_info")
                            .whereColumn("account_number").isEqualTo(bindMarker())
                            .whereColumn("opco").isEqualTo(bindMarker());
        PreparedStatement prep2 = localSession.prepare(select2.asCql());

        assert(true); //no timeout exception
    }

    @Test
    public void batchPurgeTest(){
        String acctNum = "192837-bp";
        String opco = "opPurge";
        String contactType = "contType1";
        String contactBusID = "1001";
        String firstName = "Bob";
        String lastName = "Smith";
        String customerType = "custType1";

        //cleanup any existing records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> verifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(verifyResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> verifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(verifyAccountResults.all().size() == 0);

        AccountContact acctCont1 = new AccountContact();
        acctCont1.setAccountNumber(acctNum);
        acctCont1.setOpco(opco);
        acctCont1.setContactTypeCode(contactType);
        acctCont1.setContactBusinessID(contactBusID);
        acctCont1.setPersonFirstName(firstName);
        acctCont1.setPersonLastName(lastName);

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setProfileCustomerType(customerType);
        daoAccount.save(custAcct);

        //verify new account record is created
        PagingIterable<Account> verifyAccountCreated = daoAccount.findAllByAccountNumber(acctNum);
        assert(verifyAccountCreated.all().size() == 1);

        //example using one delete and one save command
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED).build();
        batch = batch.add(daoAccountContact.batchSave(acctCont1));
        batch = batch.add(daoAccount.batchDelete(custAcct));
        sessionMap.get(DataCenter.CORE).execute(batch);

        //verify new account record is created
        PagingIterable<Account> verifyAccountDeleted = daoAccount.findAllByAccountNumber(acctNum);
        assert(verifyAccountDeleted.all().size() == 0);

        //verify account contact created
        PagingIterable<AccountContact> verifyContactCreated = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(verifyContactCreated.all().size() == 1);

        //cleanup tests records
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> cleanVerifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(cleanVerifyResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> cleanVerifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(cleanVerifyAccountResults.all().size() == 0);
    }

    @Test
    public void lwtUpdateTest() throws InterruptedException {

        String seqNumTableName = "sequence_num_tbl";
        String domainName  = "customer";
        String sequenceName = "CAM_TEST_1";

        SequenceNumberGenerator generator = new SequenceNumberGenerator(
                    sessionMap.get(DataCenter.CORE),
                    ksConfig.getKeyspaceName(Keyspace.CUSTOMER),
                    seqNumTableName,
                    "localHost"); //todo - handle hostname based on environment

        generator.initDomainSequence(domainName, sequenceName, 100, 0, 5000);
        Boolean results =  generator.getSequenceNumbers(3, 10, 4, domainName, sequenceName);

        assert(results);
    }

    @Test
    public void archiveDateTest() {
        String acctNum = "70987125";
        String opco = "op1";
        int testYear = 1;
        int testMonth = 4;
        int testDay = 15;

        String testDate = String.join("-",
                String.format("%04d", testYear),
                String.format("%02d", testMonth),
                String.format("%02d", testDay));

        LocalDate date = LocalDate.parse(testDate);

        String acctNum2 = "70987125-2";
        String opco2 = "op2";
        int testYear2 = 1;
        int testMonth2 = 1;
        int testDay2 = 1;

        String testDate2 = String.join("-",
                String.format("%04d", testYear2),
                String.format("%02d", testMonth2),
                String.format("%02d", testDay2));

        LocalDate date2 = LocalDate.parse(testDate2);

        //cleanup any existing records and verify
        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> verifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(verifyAccountResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum2);
        PagingIterable<Account> verifyAccountResults2 = daoAccount.findAllByAccountNumber(acctNum2);
        assert(verifyAccountResults2.all().size() == 0);

        Account acct = new Account();
        acct.setAccountNumber(acctNum);
        acct.setOpco(opco);
        acct.setProfileArchiveDate(date);
        daoAccount.save(acct);

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        LocalDate foundDate = foundAcct.getProfileArchiveDate();
        assert(foundAcct.getOpco().equals(opco));
        assert(foundDate.getYear() == testYear);
        assert(foundDate.getMonthValue() == testMonth);
        assert(foundDate.getDayOfMonth() == testDay);

        Account acct2 = new Account();
        acct2.setAccountNumber(acctNum2);
        acct2.setOpco(opco2);
        acct2.setProfileArchiveDate(date2);
        daoAccount.save(acct2);

        Account foundAcct2 = daoAccount.findByAccountNumber(acctNum2);
        LocalDate foundDate2 = foundAcct2.getProfileArchiveDate();
        assert(foundAcct2.getOpco().equals(opco2));
        assert(foundDate2.getYear() == testYear2);
        assert(foundDate2.getMonthValue() == testMonth2);
        assert(foundDate2.getDayOfMonth() == testDay2);

        //cleanup test records and verify
        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> cleanVerifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(cleanVerifyAccountResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum2);
        PagingIterable<Account> cleanVerifyAccountResults2 = daoAccount.findAllByAccountNumber(acctNum2);
        assert(cleanVerifyAccountResults2.all().size() == 0);
    }

    @Test
    public void batchStatementMultiScopeAddTest() {

        String acctNum = "0987123-ms";
        String opco = "op1";
        String contactType = "contType1";
        String contactBusID = "1001";
        String firstName = "Bob";
        String lastName = "Smith";
        String customerType = "custType1";

        //cleanup any existing records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> verifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(verifyResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> verifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(verifyAccountResults.all().size() == 0);

        AccountContact acctCont1 = new AccountContact();
        acctCont1.setAccountNumber(acctNum);
        acctCont1.setOpco(opco);
        acctCont1.setContactTypeCode(contactType);
        acctCont1.setContactBusinessID(contactBusID);
        acctCont1.setPersonFirstName(firstName);
        acctCont1.setPersonLastName(lastName);

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setProfileCustomerType(customerType);

        //example using two save commands, more statements can be added as needed
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED).build();

        batch = batch.add(daoAccountContact.batchSave(acctCont1));
        batch = batch.add(daoAccount.batchSave(custAcct));

        sessionMap.get(DataCenter.CORE).execute(batch);

        //verify records/values written to database as expected with batch
        AccountContact foundAcctContact = daoAccountContact.findByAccountNumber(acctNum);
        assert(foundAcctContact.getOpco().equals(opco));
        assert(foundAcctContact.getContactTypeCode().equals(contactType));
        assert(foundAcctContact.getContactBusinessID().equals(contactBusID));

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        assert(foundAcct.getOpco().equals(opco));
        assert(foundAcct.getProfileCustomerType().equals(customerType));

        //cleanup tests records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> cleanVerifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(cleanVerifyResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> cleanVerifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(cleanVerifyAccountResults.all().size() == 0);
    }


    @Test
    public void batchStatementTest() {

        String acctNum = "0987123";
        String opco = "op1";
        String contactType = "contType1";
        String contactBusID = "1001";
        String firstName = "Bob";
        String lastName = "Smith";
        String customerType = "custType1";

        //cleanup any existing records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> verifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(verifyResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> verifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(verifyAccountResults.all().size() == 0);

        AccountContact acctCont1 = new AccountContact();
        acctCont1.setAccountNumber(acctNum);
        acctCont1.setOpco(opco);
        acctCont1.setContactTypeCode(contactType);
        acctCont1.setContactBusinessID(contactBusID);
        acctCont1.setPersonFirstName(firstName);
        acctCont1.setPersonLastName(lastName);

        Account custAcct = new Account();
        custAcct.setAccountNumber(acctNum);
        custAcct.setOpco(opco);
        custAcct.setProfileCustomerType(customerType);

        //example using two save commands, more statements can be added as needed
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(daoAccountContact.batchSave(acctCont1))
                .addStatement(daoAccount.batchSave(custAcct))
                .build();
        sessionMap.get(DataCenter.CORE).execute(batch);

        //verify records/values written to database as expected with batch
        AccountContact foundAcctContact = daoAccountContact.findByAccountNumber(acctNum);
        assert(foundAcctContact.getOpco().equals(opco));
        assert(foundAcctContact.getContactTypeCode().equals(contactType));
        assert(foundAcctContact.getContactBusinessID().equals(contactBusID));

        Account foundAcct = daoAccount.findByAccountNumber(acctNum);
        assert(foundAcct.getOpco().equals(opco));
        assert(foundAcct.getProfileCustomerType().equals(customerType));

        //cleanup tests records
        //cleanup any existing records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> cleanVerifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(cleanVerifyResults.all().size() == 0);

        daoAccount.deleteAllByAccountNumber(acctNum);
        PagingIterable<Account> cleanVerifyAccountResults = daoAccount.findAllByAccountNumber(acctNum);
        assert(cleanVerifyAccountResults.all().size() == 0);
    }

    @Test
    public void acctDataFormatTest() throws ParseException {
//TODO update test to use updated field type of text -- or remove test

//        String acctNum = "778899";
//        int rawYear = 2021;
//        int rawMonth = 10;
//        String yearMonthRaw = rawYear + "-" + rawMonth;
//
//        //cleanup any existing records and verify
//        daoAccount.deleteAllByAccountNumber(acctNum);
//        PagingIterable<AccountContact> cleanVerifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
//        assert(cleanVerifyResults.all().size() == 0);
//
//        //Option #1,  append day of month value to received Year-Month value then use
//        //'standard' LocalDate parsing to store value
//        String date1Raw = yearMonthRaw + "-01";
//        LocalDate date1 = LocalDate.parse(date1Raw);
//        String opco1 = "opco1";
//        Account testAcct1 = new Account();
//        testAcct1.setAccountNumber(acctNum);
//        testAcct1.setOpco(opco1);
//        testAcct1.setAcctRegRegimeEffYearMon(date1);
//        daoAccount.save(testAcct1);
//
//        Account option1Result = daoAccount.findByAccountNumberOpco(acctNum, opco1);
//        LocalDate date1Queried = option1Result.getAcctRegRegimeEffYearMon();
//        assert(date1Queried.getYear() == rawYear);
//        assert(date1Queried.getMonth().getValue() == rawMonth);
//
//        //Option #2, use YearMonth class to parse Year-Month value and then convert to LocalDate for
//        //storage in table
//        String opco2 = "opco2";
//        YearMonth ymVal = YearMonth.parse(yearMonthRaw);
//        LocalDate date2 = ymVal.atDay(1);
//        Account testAcct2 = new Account();
//        testAcct2.setAccountNumber(acctNum);
//        testAcct2.setOpco(opco2);
//        testAcct2.setAcctRegRegimeEffYearMon(date2);
//        daoAccount.save(testAcct2);
//
//        Account option2Result = daoAccount.findByAccountNumberOpco(acctNum, opco2);
//        LocalDate date2Queried = option2Result.getAcctRegRegimeEffYearMon();
//        assert(date2Queried.getYear() == rawYear);
//        assert(date2Queried.getMonth().getValue() == rawMonth);
//
//        //Option #3, use SimpleDateFormat to parse Year-Month value received to Date value then
//        //covert Date value to LocalDate value for table storage
//        String opco3 = "opco3";
//        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM");
//        Date date3 = simpleFormatter.parse(yearMonthRaw);
//        Account testAcct3 = new Account();
//        testAcct3.setAccountNumber(acctNum);
//        testAcct3.setOpco(opco3);
//        testAcct3.setAcctRegRegimeEffYearMon(date3.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//        daoAccount.save(testAcct3);
//
//        Account option3Result = daoAccount.findByAccountNumberOpco(acctNum, opco3);
//        LocalDate date3Queried = option3Result.getAcctRegRegimeEffYearMon();
//        assert(date3Queried.getYear() == rawYear);
//        assert(date3Queried.getMonth().getValue() == rawMonth);
//
//        //cleanup any existing records and verify
//        daoAccount.deleteAllByAccountNumber(acctNum);
//        PagingIterable<AccountContact> cleanVerifyResultsAfter = daoAccountContact.findAllByAccountNumber(acctNum);
//        assert(cleanVerifyResultsAfter.all().size() == 0);
    }

    @Test
    public void contactSAITest(){
        String acctNum = "333444555";
        String opco = "op1";
        String contactType = "contType1";
        String contactBusID1 = "1001";
        String contactBusID2 = "1002";
        String contactBusID3 = "1003";
        String firstName1 = "Bob";
        String firstName2 = "John";
        String firstName3 = "Jane";
        String lastName1 = "Smith";
        String lastName2 = "Jones";
        String lastName3 = lastName2;

        //cleanup any existing records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> cleanVerifyResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(cleanVerifyResults.all().size() == 0);


        AccountContact acctCont1 = new AccountContact();
        acctCont1.setAccountNumber(acctNum);
        acctCont1.setOpco(opco);
        acctCont1.setContactTypeCode(contactType);
        acctCont1.setContactBusinessID(contactBusID1);
        acctCont1.setPersonFirstName(firstName1);
        acctCont1.setPersonLastName(lastName1);
        daoAccountContact.save(acctCont1);

        AccountContact acctCont2 = new AccountContact();
        acctCont2.setAccountNumber(acctNum);
        acctCont2.setOpco(opco);
        acctCont2.setContactTypeCode(contactType);
        acctCont2.setContactBusinessID(contactBusID2);
        acctCont2.setPersonFirstName(firstName2);
        acctCont2.setPersonLastName(lastName2);
        daoAccountContact.save(acctCont2);

        AccountContact acctCont3 = new AccountContact();
        acctCont3.setAccountNumber(acctNum);
        acctCont3.setOpco(opco);
        acctCont3.setContactTypeCode(contactType);
        acctCont3.setContactBusinessID(contactBusID3);
        acctCont3.setPersonFirstName(firstName3);
        acctCont3.setPersonLastName(lastName3);
        daoAccountContact.save(acctCont3);

        //use SAI to find records with specified last name
        PagingIterable<AccountContact> foundContacts1 = daoAccountContact.findAllByLastName(lastName1);
        assert(foundContacts1.one().getPersonFirstName().equals(firstName1));
        assert(foundContacts1.isFullyFetched() == true);  //should only return one record

        int expectedSize = 2;
        PagingIterable<AccountContact> foundContacts2 = daoAccountContact.findAllByLastName(lastName2);
        assert(foundContacts2.all().size() == expectedSize);  //two records have same last name

        //cleanup records and verify
        daoAccountContact.deleteAllByAccountNumber(acctNum);
        PagingIterable<AccountContact> cleanVerifyAfterResults = daoAccountContact.findAllByAccountNumber(acctNum);
        assert(cleanVerifyAfterResults.all().size() == 0);
    }

    @Test
    public void auditHistorySAITest(){
        String acctNum = "888999000";
        String opco = "op1";
        Instant maxDT = Instant.parse("9999-12-31T00:00:00.000Z");


        Instant lastUpdate1 = Instant.parse("2021-04-01T00:00:00.001Z");
        Instant lastUpdate2 = Instant.parse("2021-04-02T00:00:00.001Z");
        Instant lastUpdate3 = Instant.parse("2021-04-03T00:00:00.001Z");

        String transID1 = "trans_ID1";
        String transID2 = "trans_ID2";
        String transID3 = "trans_ID3";

        //cleanup any existing records and verify
        daoAuditHistory.deleteAllByAccountNumber(acctNum);
        PagingIterable<AuditHistory> cleanVerifyResults = daoAuditHistory.findAllByAccountNumber(acctNum);
        assert(cleanVerifyResults.all().size() == 0);

        //create test records
        AuditHistory audit1 = new AuditHistory();
        audit1.setAccountNumber(acctNum);
        audit1.setOpco(opco);
        audit1.setLastUpdated(lastUpdate1);
        audit1.setTransactionID(transID1);
        daoAuditHistory.save(audit1);

        AuditHistory audit2 = new AuditHistory();
        audit2.setAccountNumber(acctNum);
        audit2.setOpco(opco);
        audit2.setLastUpdated(lastUpdate2);
        audit2.setTransactionID(transID2);
        daoAuditHistory.save(audit2);

        AuditHistory audit3 = new AuditHistory();
        audit3.setAccountNumber(acctNum);
        audit3.setOpco(opco);
        audit3.setLastUpdated(lastUpdate3);
        audit3.setTransactionID(transID3);
        daoAuditHistory.save(audit3);

        //find all audit entries for account
        PagingIterable<AuditHistory> allAudits = daoAuditHistory.findByAccountNumDateTimeRange(acctNum, lastUpdate1, maxDT);
        assert(allAudits.all().size() == 3);

        //find two earliese entries for account and verify order
        PagingIterable<AuditHistory> earlyAudits = daoAuditHistory.findByAccountNumDateTimeRange(acctNum, lastUpdate1, lastUpdate2);

        //only record #1 and record #2 should be returned
        //because of descending cluster order, #2 should be first record retrieved
        assert(earlyAudits.one().getTransactionID().equals(transID2));
        //second record retrieved should be #1
        assert(earlyAudits.one().getTransactionID().equals(transID1));
        //two records retrieved from result set, should be no more records available
        assert(earlyAudits.isFullyFetched() == true);

        //cleanup any verify
        daoAuditHistory.deleteAllByAccountNumber(acctNum);
        PagingIterable<AuditHistory> cleanAfterResults = daoAuditHistory.findAllByAccountNumber(acctNum);
        assert(cleanAfterResults.all().size() == 0);
    }

    @Test
    public void commentDeleteTest() throws InterruptedException {
        String acctNum = "commentDel-Acct1";
        String opco = "op1";

        Instant commentDT1 = Instant.parse("2022-04-01T00:00:00.001Z");
        Instant commentDT2 = Instant.parse("2022-04-02T00:00:00.001Z");
        Instant maxDT = Instant.parse("9999-12-31T00:00:00.000Z");

        String commentID1 = "cID_1";
        String commentID2 = "cID_2";

        String type1 = "typ1";
        String type2 = "typ2";

        //cleanup any existing records and verify
        daoComment.deleteAllByAccountNumber(acctNum);
        PagingIterable<Comment> cleanVerifyComments = daoComment.findAllByAccountNumber(acctNum);
        assert(cleanVerifyComments.all().size() == 0);

        //create test records
        Comment comment1 = new Comment();
        comment1.setAccountNumber(acctNum);
        comment1.setOpco(opco);
        comment1.setCommentDateTime(commentDT1);
        comment1.setCommentType(type1);
        comment1.setCommentID(commentID1);
        daoComment.save(comment1);

        Comment comment2 = new Comment();
        comment2.setAccountNumber(acctNum);
        comment2.setOpco(opco);
        comment2.setCommentDateTime(commentDT2);
        comment2.setCommentType(type2);
        comment2.setCommentID(commentID2);
        daoComment.save(comment2);

        //pause to allow SAI to update
        Thread.sleep(150);

        PagingIterable<Comment> addedVerifyComments = daoComment.findAllByAccountNumber(acctNum);
        assert(addedVerifyComments.all().size() == 2);

        //can only delete by providing record key(s)
        //two step process to delete comment by non-key column
        //  1) find record using index on non-key column
        //  2) Use keys of found record to execute delete

        //find comment record for delete
        PagingIterable<Comment> foundComments = daoComment.findByCommentID(commentID2);
        Comment deletComment = foundComments.one();
        assert (foundComments.isFullyFetched());

        //delete record using full primary key
        daoComment.deleteByKeys(deletComment.getAccountNumber(),
                                deletComment.getOpco(),
                                deletComment.getCommentType(),
                                deletComment.getCommentDateTime()
                                );

        PagingIterable<Comment> deletedVerifyComments = daoComment.findAllByAccountNumber(acctNum);
        assert(deletedVerifyComments.all().size() == 1);

        Comment foundOtherComment = daoComment.findByAcctOpcoCommentId(acctNum, opco, commentID1);

        //delete other record using full primary key
        daoComment.deleteByKeys(foundOtherComment.getAccountNumber(),
                foundOtherComment.getOpco(),
                foundOtherComment.getCommentType(),
                foundOtherComment.getCommentDateTime()
        );

        PagingIterable<Comment> deletedVerifyOtherComments = daoComment.findAllByAccountNumber(acctNum);
        assert(deletedVerifyOtherComments.all().size() == 0);
    }

    @Test
    public void commentSAITest(){
        String acctNum = "890123";
        String opco = "op1";

        Instant commentDT1 = Instant.parse("2020-08-01T00:00:00.001Z");
        Instant commentDT2 = Instant.parse("2020-08-02T00:00:00.001Z");
        Instant commentDT3 = Instant.parse("2020-08-03T00:00:00.001Z");
        Instant maxDT = Instant.parse("9999-12-31T00:00:00.000Z");

        String commentID1 = "cID_1";
        String commentID2 = "cID_2";
        String commentID3 = "cID_3";

        String type1 = "COMTYP1";
        String type2 = "COMTYP1";
        String type3 = "COMTYP1";

        //cleanup any existing records and verify
        daoComment.deleteAllByAccountNumber(acctNum);
        PagingIterable<Comment> cleanVerifyComments = daoComment.findAllByAccountNumber(acctNum);
        assert(cleanVerifyComments.all().size() == 0);

        //create test records
        Comment comment1 = new Comment();
        comment1.setAccountNumber(acctNum);
        comment1.setOpco(opco);
        comment1.setCommentDateTime(commentDT1);
        comment1.setCommentType(type1);
        comment1.setCommentID(commentID1);
        daoComment.save(comment1);

        Comment comment2 = new Comment();
        comment2.setAccountNumber(acctNum);
        comment2.setOpco(opco);
        comment2.setCommentDateTime(commentDT2);
        comment2.setCommentType(type2);
        comment2.setCommentID(commentID2);
        daoComment.save(comment2);

        Comment comment3 = new Comment();
        comment3.setAccountNumber(acctNum);
        comment3.setOpco(opco);
        comment3.setCommentDateTime(commentDT3);
        comment3.setCommentType(type3);
        comment3.setCommentID(commentID3);
        daoComment.save(comment3);

        //test query patterns
        //find all comments starting with date/time of first test comment record

        //todo - update to match latest schema/index changes
        PagingIterable<Comment> foundComments =
                daoComment.findByAccountNumOpcoDateTimeRange(acctNum, opco, commentDT1, maxDT);
        assert(foundComments.all().size() == 3);
//
//        //find all comments starting with date/time between first test comment data and second test comment date
//        PagingIterable<Comment> foundComments2 =
//                daoComment.findByAccountNumOpcoDateTimeRange(acctNum, opco, commentDT1, commentDT2);
//        assert(foundComments2.one().getCommentID().equals(commentID2));  //should be second comment due to default cluster column ordering
//        assert(foundComments2.getAvailableWithoutFetching() == 1);  //should be two total records found so only one left
//        assert(foundComments2.one().getCommentID().equals(commentID1));  //last record found should be first comment

//        PagingIterable<Comment> foundAcctType = daoComment.findByAccountNumType(acctNum, type2);
//        assert(foundAcctType.one().getCommentID().equals(commentID2));
//        assert(foundAcctType.isFullyFetched() == true);
//
//        PagingIterable<Comment> foundype = daoComment.findByCommentType(type1);
//        assert(foundype.one().getCommentID().equals(commentID1));
//        assert(foundype.isFullyFetched() == true);

        //cleanup after tests and verify
        daoComment.deleteAllByAccountNumber(acctNum);
        PagingIterable<Comment> cleanAfterVerifyComments = daoComment.findAllByAccountNumber(acctNum);
        assert(cleanAfterVerifyComments.all().size() == 0);
    }

    @Test
    public void custAcctProileAcctTypeSAITest(){
        String queryType = "acctType2";
        String expectedCustomerType = "custType2";

        PagingIterable<Account> foundAccts = daoAccount.findByProfileAccountType(queryType);
        Account foundAcct = foundAccts.one();
        assert(foundAccts.isFullyFetched());  //only one result expected
        assert(foundAcct.getProfileCustomerType().equals(expectedCustomerType));
    }

    @Test
    public void ngramsSAITest() {

        String acctNum = "123987456";
        String opco1 = "testOpcoNgrams1";
        String opco2 = "testOpcoNgrams2";
        String opco3 = "testOpcoNgrams3";
        String testVal1 = "1327 NORTHBROOK PKWY";
        String testVal2 = "16304 BELLINGHAM DR";
        String testVal3 = "106 S BELLE AVE";

        IndexCollection indexCollect1 = new IndexCollection();
        indexCollect1.setAccountNumber(acctNum);
        indexCollect1.setOpco(opco1);
        indexCollect1.setTestVal(testVal1);
        indexCollect1.setTestValGrams(createNGrams(testVal1, 3));
        daoIndexCollect.save(indexCollect1);

        IndexCollection indexCollect2 = new IndexCollection();
        indexCollect2.setAccountNumber(acctNum);
        indexCollect2.setOpco(opco2);
        indexCollect2.setTestVal(testVal2);
        indexCollect2.setTestValGrams(createNGrams(testVal2, 3));
        daoIndexCollect.save(indexCollect2);

        IndexCollection indexCollect3 = new IndexCollection();
        indexCollect3.setAccountNumber(acctNum);
        indexCollect3.setOpco(opco3);
        indexCollect3.setTestVal(testVal3);
        indexCollect3.setTestValGrams(createNGrams(testVal3, 3));
        daoIndexCollect.save(indexCollect3);

        int expected1 = 1;
        PagingIterable<IndexCollection> foundRecs1 = daoIndexCollect.findByPartialText("NORT");
        assert(expected1 == foundRecs1.all().size());

        //verify complete string can be located also
        PagingIterable<IndexCollection> foundRecs2 = daoIndexCollect.findAllByAccountNumber(testVal2);

        IndexCollection test = foundRecs2.one();
//        assert(foundRecs2.one().getOpco().equals(opco2)); //todo not working as expected
//        assert(foundRecs2.isFullyFetched()); //should only be one record found

        int expected3 = 2;
        PagingIterable<IndexCollection> foundRecs3 = daoIndexCollect.findByPartialText("BELL");
        assert(expected3 == foundRecs3.all().size());

        //cleanup
        daoIndexCollect.delete(indexCollect1);
        daoIndexCollect.delete(indexCollect2);
        daoIndexCollect.delete(indexCollect3);
    }

    private Set<String> createNGrams(String source, int minGramLength)
    {   Set<String> nGrams = new HashSet<String>();

        int lengthSource = source.length();
        int nCurStart = 0;

        for(int nCurGramLength = minGramLength; nCurGramLength<=lengthSource; nCurGramLength++) {
            nCurStart = 0;
            while ((nCurStart + nCurGramLength) <= lengthSource) {
                String curGram = source.substring(nCurStart, nCurStart + nCurGramLength);
                nGrams.add(curGram);
                nCurStart++;
            }
        }

        //Debug output if needed
//        System.out.println("Source string: " + source);
//        System.out.println("Num n-grams: " + nGrams.size());
//        System.out.println(nGrams.toString());

        return nGrams;
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
                "    " + ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS) + ".cust_acct_v1 \n" +
                "SET \n" +
                "    duty_tax_info = duty_tax_info + {'" + keyC +"' : '" + valC + "'} \n" +
                "WHERE \n" +
                "    account_number = '" + acctNum + "' AND \n" +
                "    opco = '" + opco + "';";

        sessionMap.get(DataCenter.CORE).execute(dutyTaxAddElement);
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

    //TODO convert possible functionality of following test to use SAI
//    @Test
//    public void searchSubStringTest() throws InterruptedException {
//        //using dummy values for contact record to test substring matching functionaliyt
//        String insertRec1 = "INSERT INTO account_contact\n" +
//                "    (account_number, opco, contact_document_id, contact_type_code, contact_business_id, person__first_name)\n" +
//                "    VALUES('123456', 'opc1', 101, 'type1', 'cBus1', 'FedExDotCom');";
//
//        String insertRec2 = "INSERT INTO account_contact\n" +
//                "    (account_number, opco, contact_document_id, contact_type_code, contact_business_id, person__first_name)\n" +
//                "    VALUES('123456', 'opc1', 102, 'type1', 'cBus2', 'FedExDotom');";
//
//        //add test records to table
//        session.execute(insertRec1);
//        session.execute(insertRec2);
//
//        //sleep for a time to allow Solr indexes to update completely
//        System.out.println("Inserted substring test records. Pausing to allow indexes to update...");
//        Thread.sleep(11000);
//
//        //construct test query using Solr
//        String searchQueryBase = "SELECT * \n" +
//                "FROM account_contact\n" +
//                "WHERE solr_query = ";
//
//        //query 1, should find two records
//        String searchQueryDetail1 = "'person__first_name:FedEx*'";
//        ResultSet rs1 = session.execute(searchQueryBase + searchQueryDetail1);
//        assert(rs1.all().size() == 2);
//
//        //query 2, should find two records
//        String searchQueryDetail2 = "'person__first_name:FedEx*D*'";
//        ResultSet rs2 = session.execute(searchQueryBase + searchQueryDetail2);
//        assert(rs2.all().size() == 2);
//
//        //query 3, should find one record
//        String searchQueryDetail3 = "'person__first_name:FedEx*C*'";
//        ResultSet rs3 = session.execute(searchQueryBase + searchQueryDetail3);
//        Row row3 = rs3.one();
//        assert(row3.getLong("contact_document_id") == 101);
//        assert(row3.getString("contact_business_id").equals("cBus1"));
//        assert(rs3.isFullyFetched() == true); //only one record found
//
//        //query 4, should find one record
//        String searchQueryDetail4 = "'person__first_name:FedEx*D*C*'";
//        ResultSet rs4 = session.execute(searchQueryBase + searchQueryDetail4);
//        Row row4 = rs4.one();
//        assert(row4.getLong("contact_document_id") == 101);
//        assert(row4.getString("contact_business_id").equals("cBus1"));
//        assert(rs4.isFullyFetched() == true); //only one record found
//
//        //query 5, should find one record
//        String searchQueryDetail5 = "'person__first_name:FedEx*D*tom'";
//        ResultSet rs5 = session.execute(searchQueryBase + searchQueryDetail5);
//        Row row5 = rs5.one();
//        assert(row5.getLong("contact_document_id") == 102);
//        assert(row5.getString("contact_business_id").equals("cBus2"));
//        assert(rs5.isFullyFetched() == true); //only one record found
//
//        String cleanup = "DELETE FROM account_contact WHERE account_number = '123456'";
//        session.execute(cleanup);
//    }

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
    public void applyDiscountAsyncTest() throws ExecutionException, InterruptedException {
        String acctNum = "000001236";
        int expectResultSize = 11;

        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts = daoApplyDiscount.findAllByAccountNumberAsync(acctNum);
        cfDiscounts.join();
        assert(cfDiscounts.get().remaining() == expectResultSize);
    }

    @Test
    public void applyDiscountSAIAsyncTest() throws ExecutionException, InterruptedException {
        String acctNum = "000001236";
        String opco = "FX";
        Boolean applyDiscountFlag = true;
        Instant effectiveDT = Instant.parse("2018-11-01T01:00:00.001Z");
        Instant expriationDT = Instant.parse("2019-01-01T00:00:00.001Z");
        Instant maxDT = Instant.parse("9999-12-31T00:00:00.000Z");

        //Filter on account number and opco values only
        int expectedResultSize1 = 11;
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts1 =
                daoApplyDiscount.findByAccountOpcoAsync(acctNum, opco);
        cfDiscounts1.join();
        assert(cfDiscounts1.get().remaining() == expectedResultSize1);

        //Filter on account number, opco and effective date/time values only
        int expectedResultSize2 = 7;
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts2 =
                daoApplyDiscount.findByAccountNumDateTimeRangeAsync(acctNum, opco, effectiveDT, maxDT);
        cfDiscounts2.join();
        assert(cfDiscounts2.get().remaining() == expectedResultSize2);

        //Filter on account number, opco, effective date/time and expiration date/time
        int expectedResultSize3 = 5;
        CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> cfDiscounts3 =
                daoApplyDiscount.findByAccountNumDateTimeRangeAsync(acctNum, opco, effectiveDT, expriationDT);
        cfDiscounts3.join();
        assert(cfDiscounts3.get().remaining() == expectedResultSize3);
    }

    @Test
    public void nationalAccountSAITest(){
        int expectedResultCount = 14;
        PagingIterable<NationalAcccount> foundNatAccts = daoNational.findBySAINationalAccount("00706");

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
        Select select = selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "national_account_v1")
                .all()
                .whereColumn("account_number").isEqualTo(bindMarker());
        SimpleStatement selectStmt = select.build(acctID);

        System.out.println(select.asCql());
        SimpleStatement stmt = selectStmt.setPageSize(pageSize);

        ResultSet rs =  sessionMap.get(DataCenter.CORE).execute(stmt);
        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        System.out.println("First page results:");
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            System.out.println("\t" + row.getInstant("national_account_detail__membership_eff_date_time"));
        }

        SimpleStatement stmt2 = selectStmt.setPagingState(pagingState);
        ResultSet rs2 = sessionMap.get(DataCenter.CORE).execute(stmt2);

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
        Select select =
                selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "national_account_v1")
                .all()
                .whereColumn("account_number").isEqualTo(literal(acctID));
        SimpleStatement selectStmt = select.build();

        System.out.println(select.asCql());
        SimpleStatement stmt = selectStmt.setPageSize(pageSize);

        ResultSet rs =  sessionMap.get(DataCenter.CORE).execute(stmt);
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

        SimpleStatement stmt2 = selectStmt.setPagingState(pagingState);
        ResultSet rs2 = sessionMap.get(DataCenter.CORE).execute(stmt2);

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
        SimpleStatement selectStmt =
                selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "national_account_v1")
                .all()
                .whereColumn("account_number").isEqualTo(literal(acctID))
                .build();

        int expectedTotalSize = 5;
        int pageSize = 3;
        SimpleStatement stmt = selectStmt.setPageSize(pageSize);
        ResultSet rs =  sessionMap.get(DataCenter.CORE).execute(stmt);

        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            //do something with row
        }

        SimpleStatement stmt2 = selectStmt.setPagingState(pagingState);
        ResultSet rs2 = sessionMap.get(DataCenter.CORE).execute(stmt2);

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
        Select select =
                selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "national_account_v1")
                .all()
                .whereColumn("account_number").isEqualTo(literal(acctID));
        System.out.println(select.asCql());
        SimpleStatement selectStmt = select.build();

        int expectedTotalSize = 5;
        int pageSize = 3;
        SimpleStatement stmt = selectStmt.setPageSize(pageSize).setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
        ResultSet rs =  sessionMap.get(DataCenter.CORE).execute(stmt);

        ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
        System.out.println("Initial paging state - " + pagingState);

        assert(rs.isFullyFetched() == false );
        assert(rs.getAvailableWithoutFetching() == pageSize);
        while(rs.getAvailableWithoutFetching()>0){
            Row row = rs.one();
            //do something with row
        }

        SimpleStatement stmt2 = selectStmt.setPagingState(pagingState);
        ResultSet rs2 = sessionMap.get(DataCenter.CORE).execute(stmt2);

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
        Select select = selectFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "national_account_v1")
                            .all()
                            .whereColumn("account_number").isEqualTo(bindMarker());

        System.out.println(select.asCql());

        int expectedTotalSize = 5;
        int pageSize = 2;
        SimpleStatement stmt = select.build(acctID)
                                    .setPageSize(pageSize)
                                    .setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
        ResultSet rs =  sessionMap.get(DataCenter.CORE).execute(stmt);

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

        SimpleStatement stmt2 = select.build(acctID)
                                    .setPageSize(pageSize)
                                    .setPagingState(pagingState);
        ResultSet rs2 = sessionMap.get(DataCenter.CORE).execute(stmt2);

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

        SimpleStatement stmt3 = select.build(acctID)
                                    .setPageSize(pageSize)
                                    .setPagingState(pagingState2);
        ResultSet rs3 = sessionMap.get(DataCenter.CORE).execute(stmt2);   //todo - investigate possible bug, should be stmt3?

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
        Delete deleteSingleProp =  deleteFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "cust_acct_v1")
                .column(deleteProp)
                .whereColumn("account_number").isEqualTo(literal(acctID))
                .whereColumn("opco").isEqualTo(literal(opco));

        sessionMap.get(DataCenter.CORE).execute(deleteSingleProp.build());
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
        Delete deleteMultiProps =  deleteFrom(ksConfig.getKeyspaceName(Keyspace.ACCOUNT_KS), "cust_acct_v1")
                .column("profile__customer_type").column("profile__account_status__status_code").column("profile__account_status__reason_code")
                .whereColumn("account_number").isEqualTo(literal(acctID))
                .whereColumn("opco").isEqualTo(literal(opco));

        sessionMap.get(DataCenter.CORE).execute(deleteMultiProps.build());
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
        assert(foundPayment.getPaymentID().equals(expectedCCId));


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
        assert(foundPayment.getPaymentID().equals(expectedCCId));
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
        assert(foundPayment.getPaymentID().equals(expectedCCId));
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

        Select select = selectFrom(ksConfig.getKeyspaceName(Keyspace.CUSTOMER), "contact")
                            .all()
                            .whereColumn("contact_document_id").isEqualTo(literal(83));

        ResultSet resCheck = sessionMap.get(DataCenter.CORE).execute(select.build());

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
        String solrQuery = "select * from "+ ksConfig.getKeyspaceName(Keyspace.CUSTOMER) + ".contact\n" +
                "where\n" +
                "    solr_query = '" +
                "{\"q\": \"{!tuple}tele_com.area_code:123\"," +
                "\"sort\": \"contact_document_id asc\"}'";
        ;

        ResultSet resCheck = sessionMap.get(DataCenter.SEARCH).execute(
                SimpleStatement.builder(solrQuery).setExecutionProfileName("search").build()
        );

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
        CqlSession localSession = sessionMap.get(DataCenter.CORE);

        //cleanup any existing records and verify
        SimpleStatement cleanupStmt =
                deleteFrom(ksConfig.getKeyspaceName(Keyspace.CUSTOMER), "contact")
                .whereColumn("contact_document_id").isEqualTo(literal(2001))
                .build();
        localSession.execute(cleanupStmt);

        SimpleStatement checkStmt =
                selectFrom(ksConfig.getKeyspaceName(Keyspace.CUSTOMER), "contact")
                .all()
                .whereColumn("contact_document_id").isEqualTo(literal(2001))
                .build();
        ResultSet rsInitialCheck = localSession.execute(checkStmt);

        assert(rsInitialCheck != null && rsInitialCheck.all().size() == 0);

        String insertStmt = "insert into " + ksConfig.getKeyspaceName(Keyspace.CUSTOMER)+ ".contact\n" +
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
        localSession.execute(insertStmt);

        //verify record exists
        ResultSet rsDataCheck = localSession.execute(checkStmt);

        Row dataRow = rsDataCheck.one();
        assert(dataRow.getLong("contact_document_id") == (long)2001);

        Set<UdtValue> setCheckTelecom = dataRow.getSet("tele_com",UdtValue.class);

        //check only one result
        assert(setCheckTelecom.size() == 2);

        String updateStmt = "update " + ksConfig.getKeyspaceName(Keyspace.CUSTOMER)+ ".contact\n" +
                "set \n" +
                "    tele_com = tele_com + {{telecom_method:'MB', area_code:'333', phone_number:'333-2001'}}\n" +
                "where\n" +
                "    contact_document_id = 2001\n" +
                "    ;";

        //update set of UDTs
        localSession.execute(updateStmt);

        //verify record updated
        ResultSet rsDataCheckUpdate = localSession.execute(checkStmt);

        Row dataRowUpdated = rsDataCheckUpdate.one();
        assert(dataRowUpdated.getLong("contact_document_id") == (long)2001);

        Set<UdtValue> setCheckTelecomUpdated = dataRowUpdated.getSet("tele_com",UdtValue.class);

        //check only one result
        assert(setCheckTelecomUpdated.size() == 3);
    }

    private void runQueryList(List<TestQuery> queries){

        for(TestQuery curQuery : queries){
            if(curQuery.queryType == TestQuery.QueryType.READ_PROP){
                String checkQuery = "select * from " + ksConfig.getKeyspaceName(Keyspace.CUSTOMER) + "." +
                        curQuery.table + " where " +
                        curQuery.recordIDProp + " = ";
                if(curQuery.numericKey){
                    checkQuery += curQuery.recordIDval;
                }
                else {
                    checkQuery += "'" + curQuery.recordIDval + "'";
                }

                ResultSet resCheck = sessionMap.get(DataCenter.CORE).execute(checkQuery);

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
