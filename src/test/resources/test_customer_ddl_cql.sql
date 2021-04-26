USE customer;

CREATE TYPE IF NOT EXISTS telecom_details_type (
    telecom_method text,
    numeric_country_code text,
    aplha_country_code text,
    area_code text,
    phone_number text,
    extension text,
    pin text,
    ftc_ok_to_call_flag boolean,
    text_message_flag boolean
);

CREATE TYPE IF NOT EXISTS social_media_type (
    type_code text,
    value text
);

CREATE TYPE IF NOT EXISTS tax_data_type (
    tax_id text,
    tax_id_desc text,
);

CREATE TYPE IF NOT EXISTS tax_exempt_code_type (
    type text,
    value text
);

CREATE TYPE IF NOT EXISTS tax_exempt_data_type (
    tax_exempt_id text,
    tax_exempt_id_desc text,
    tax_exempt_flag boolean
);

CREATE TABLE IF NOT EXISTS cust_acct_v1 (
    account_number text,
    opco text,
    last_update_timestamp timestamp,

    --enterpriseProfile
    profile__customer_type text,
    profile__account_service_level text,
    profile__account_type text,
    profile__account_status__status_code text,
    profile__account_status__status_date date,
    profile__account_status__reason_code text,
    profile__fdx_ok_to_call_flag boolean,
    profile__enterprise_source text,
    profile__nasa_id text,
    profile__nasa_key text,
    profile__creation_date date,
    profile__origin_source text,
    profile__account_linkage_flag boolean,
    profile__welcome_kit__welcome_kit_flag boolean,
    profile__welcome_kit__welcome_kit_promo_code text,

    profile__customer_request_name text,
    profile__employee_requester__opco text,
    profile__employee_requester__number text,
    profile__source_group text,
    profile__source_dept text,
    profile__source_system text,
    profile__employee_creator_opco text,
    profile__employee_creator_number text,
    profile__account_sub_type text,
    profile__customer_account_status text,
    profile__duplicate_account_flag boolean,
    profile__archive_date date,
    profile__archive_reason_code text,
    profile__archive_options text,
    profile__cargo_ind text,
    profile__pref_cust_flag boolean,
    profile__sales_rep__opco text,
    profile__sales_rep__number text,
    profile__service_level text,
    profile__scac_code text,


    --cargoAccountReceivables
    --express_account_receivables
    account_receivables__call_zone_desc text,
    account_receivables__gsp_write_off text,
    account_receivables__online_eligibility boolean,
    account_receivables__partial_pay_letter_flag boolean,
    account_receivables__payment_type text,
    account_receivables__payment_method_code text,
    account_receivables__payor_type text,
    account_receivables__arrow_customer_flag_cd text,
    account_receivables__international___ar_preference int,
    account_receivables__international___ar_date date,
    account_receivables__no_refund_flag boolean,
    account_receivables__debut_company_code text,

    --express_aggregations
    aggregations__ed_aggr_code text,
    aggregations__geo_acct_number text,
    aggregations__global_account_number text,
    aggregations__global_subgroup text,
    aggregations__ss_account_number text,
    aggregations__bill_to_number text,
    aggregations__edi_number text,
    aggregations__copy_master_address boolean,

    --express_claims_preference
    claims_preference text,

    --express_credit_detail
    --freightCreditDetail
    --officeCreditDetail
    --ukDomesticCreditCardDetail
    credit_detail__credit_status text,
    credit_detail__credit_status_reason_code text,
    credit_detail__denied_flag boolean,
    credit_detail__bankruptcy_date date,
    credit_detail__cash_only_flag boolean,
    credit_detail__cash_only_date date,
    credit_detail__cash_only_reason text,
    credit_detail__credit_alert_detail text,
    credit_detail__credit_alert_account_number text,
    credit_detail__credit_alert_parent_type text,
    credit_detail__credit_limit text,
    credit_detail__credit_limit_tolerance_pct int,
    credit_detail__override_date date,
    credit_detail__credit_rating text,
    credit_detail__receivership_account_number text,
    credit_detail__receivership_date date,
    credit_detail__rev_auth_id text,

    --express_direct_debit
    direct_debit_detail__person__first_name text,
    direct_debit_detail__person__last_name text,
    direct_debit_detail__back_account_holder_name text,
    direct_debit_detail__bank_name text,
    direct_debit_detail__address__street_line text,
    direct_debit_detail__address__additional_line1 text,
    direct_debit_detail__address__additional_line2 text,
    direct_debit_detail__address__secondary__unit1 text,
    direct_debit_detail__address__secondary__value1 text,
    direct_debit_detail__address__secondary__unit2 text,
    direct_debit_detail__address__secondary__value2 text,
    direct_debit_detail__address__secondary__unit3 text,
    direct_debit_detail__address__secondary__value3 text,
    direct_debit_detail__address__secondary__unit4 text,
    direct_debit_detail__address__secondary__value4 text,
    direct_debit_detail__address__geo_political_subdivision1 text,
    direct_debit_detail__address__geo_political_subdivision2 text,
    direct_debit_detail__address__geo_political_subdivision3 text,
    direct_debit_detail__address__postal_code text,
    direct_debit_detail__address__country_code text,
    direct_debit_detail__iban__swift_code text,
    direct_debit_detail__iban_iban text,
    direct_debit_detail__noiban__bank_code text,
    direct_debit_detail__noiban__branch_code text,
    direct_debit_detail__account_number text,
    direct_debit_detail__sort_code text,
    direct_debit_detail__mandate_id text,
    direct_debit_detail__mandate_start_date date,
    direct_debit_detail__legal_entity text,

    --express_duty_tax
    duty_tax_info_duty_tax_number text,
    duty_tax_info_country_code text,

    --express_edi
    edi__cust_inv_rept_flag boolean,
    edi__dom_data_frmt text,
    edi__dom_inv_print_until_date date,
    edi__intl_data_frmt text,
    edi__intl_inv_print_until_date date,
    edi__mm_bill_3rd_party text,
    edi__mm_bill_recip text,
    edi__mm_bill_ship text,
    edi__mm_bill_pwr_ship text,
    edi__past_due_medium text,
    edi__past_due_send_to text,
    remit_frmt_vers text,
    sep_exp_grnd_file boolean,

    --express_invoice_preference
    --freightInvoicePreference
    --officeInoivcePreference
    --techConnectInvoicePreference
    --ukDomesticInvoicePreference
    invoice_preference__additional_invoice_copy_flag_cd text,
    invoice_preference__audit_firm_exp_year_month text,
    invoice_preference__audit_firm_number text,
    invoice_preference__billing_closing_day text,
    invoice_preference__billing_cycle text,
    invoice_preference__billing_medium text,
    invoice_preference__billing_payment_day int,
    invoice_preference__billing_payment_month int,
    invoice_preference__billing_restriction_indicator text,
    invoice_preference__billing_type int,
    invoice_preference__combine_option text,
    invoice_preference__consolidated_invoice_flag_cd text,
    invoice_preference__consolidated_refund_flag boolean,
    invoice_preference__currency_code text,
    invoice_preference__customer_reference_information text,
    invoice_preference__days_to_credit int,
    invoice_preference__days_to_pay int,
    invoice_preference__documentation_exception_indicator int,
    invoice_preference__duty_tax_days_to_pay int,
    invoice_preference__duty_tax_billing_cycle text,
    invoice_preference__electronic_bill_payment_plan_flag_cd text,
    invoice_preference__electronic_data_record_proof_of_delivery boolean,
    invoice_preference__fax_flag boolean,
    invoice_preference__fec_discount_card_flag_cd text,
    invoice_preference__ground_auto___pod boolean,
    invoice_preference__ground_duty_tax_billing_cycle text,
    invoice_preference__ground_print_weight_indicator text,
    invoice_preference__international_billing_cycle text,
    invoice_preference__international_billing_medium text,
    invoice_preference__international_invoice_bypass text,
    invoice_preference__international_invoice_program_override_flag boolean,
    invoice_preference__international_parent_child_flag boolean,
    invoice_preference__international_duty_tax_invoice_bypass text,
    invoice_preference__invoice__detail_level int,
    invoice_preference__invoice__level_discount_eff_date date,
    invoice_preference__invoice__level_discount_exp_date date,
    invoice_preference__invoice__level_discount_flag_cd text,
    invoice_preference__invoice__minimum_override_flag boolean,
    invoice_preference__invoice__opiton_flag_cd text,
    invoice_preference__invoice__page_layout_indicator int,
    invoice_preference__invoice__transaction_breakup_type int,
    invoice_preference__invoice__wait_days int,
    invoice_preference__manage_my_account_at_fed_ex_flag_cd text,
    invoice_preference__master_account_invoice_summary_flag_cd text,
    invoice_preference__monthly_billing_indicator text,
    invoice_preference__past_detail_level int,
    invoice_preference__past_due_flag_cd text,
    invoice_preference__pod_wait_days int,
    invoice_preference__primary_sort_option int,
    invoice_preference__print_summary_page_flag boolean,
    invoice_preference__print_weight_indicator text,
    invoice_preference__reference_append int,
    invoice_preference__return_envelope_indicator text,
    invoice_preference__single_invoice_option text,
    invoice_preference__sort_field_length int,
    invoice_preference__split_bill_duty_tax boolean,
    invoice_preference__statement_of_account__billing_cycle text,
    invoice_preference__statement_of_account__layout_indicator int,
    invoice_preference__statement_of_account__receipt_flag_cd text,
    invoice_preference__statement_type text,
    invoice_preference__statement_type_date date,
    invoice_preference__viewed_statement_type text,
    invoice_preference__direct_link_flag boolean,
    invoice_preference__no___pod_flag_cd text,
    invoice_preference__settlement_level_indicator text,
    invoice_preference__direct_debit_indicator text,
    invoice_preference__fbo_eft_flag boolean,
    invoice_preference__balance_forward_code text,
    invoice_preference__late_fee_enterprise_waiver text,

    --expressMMA
    mma_stats__last_cancel_code text,
    mma_stats__last_cancel_date date,
    mma_stats__last_deactivation_date date,
    mma_stats__last_registration_date date,
    mma_stats__last_reject_code text,
    mma_stats__last_reject_date date,
    mma_stats__last_update_date_time timestamp,
    mma_stats__last_update_user text,
    swipe__cc_eligibility_flag boolean,
    swipe__decline_count int,
    swipe__swipe_lockout_date_time timestamp,

    --supplyChainTaxInfo
    --freightTaxInfo
    --techConnectTaxInfo
    tax_info__tax_data set<frozen<tax_data_type>>,
    tax_info__tax_exempt_detail set<frozen<tax_exempt_data_type>>,
    tax_info__tax_exempt_code map<text, text>,
    tax_info__codice_fiscale text,
    tax_info__mdb_eff_date date,
    tax_info__mdb_exp_date date,
    tax_info__tax_exempt_number int,
    tax_info__vat__type text,
    tax_info__vat__number text,
    tax_info__vat__exemption_code text,
    tax_info__vat__exeption_ref text,
    tax_info__vat__eff_date date,
    tax_info__vat__exp_date date,
    tax_info__vat__response_code int,
    tax_info__vat__category_code int,
    tax_info__vat__threshold_amount float,

    --expressElectronicPay
    addl_bank_info__abi_code text,
    addl_bank_info__addl_bank_id text,
    bank_number text,
    cab_code text,
    giro_account text,
    domicile_number text,
    alt_payment__alt_payment_type text,
    alt_payment__billing_agreement_id text,
    alt_payment__billing_agreement_date date,
    alt_payment__client_id text,
    alt_payment__auto_sched_term text,
    alt_payment__auto_sched_thresh_amt text,


    -- ***** OPERATIONS STREAM  *****
    -- smart_post_operations_profile
    profile__distribution_id int,
    profile__mailer_id int,
    profile__pickup_carrier text,
    profile__return_eligibility_flag boolean,
    profile__return_svc_flag text,
    profile__hub_id set<text>,
    profile__usps_bound_printed_matter_flag int,
    profile__usps_media_mail_flag int,
    profile__usps_parcel_select_flag int,
    profile__usps_standard_mail_flag int,
    profile__smartpost_enabled_flag boolean,
    profile__delivery_confirmation boolean,
    profile__zone_indicator text,

    -- express_operations_profile
    profile__multiplier_ref_exp text,
    profile__mulitiplier_ref_grnd text,
    profile__agent_flag text,
    profile__alcohol_flag boolean,
    profile__cut_flowers_flag boolean,
    profile__declared_value_exception boolean,
    profile__derived_station int,
    profile__drop_ship_flag boolean,
    profile__emerge_flag boolean,
    profile__doc_prep_service_flag boolean,
    profile__ftbd_flag text,
    profile__ftbd_svc map<text,text>,
    profile__hazardous_shipper_flag boolean,
    profile__high_value_accept_cd text,
    profile__interline_cd text,
    profile__idf_elig_flag text,
    profile__ifs_flag boolean,
    profile__ipd_flag text,
    profile__money_back_guarantee text,
    profile__notify_ship_delay_cd boolean,
    profile__overnight_frgt_ship_flag boolean,
    profile__pak_isrt_flag text,
    profile__power_of_attorney_date date,
    profile__power_of_attorney_flag boolean,
    profile__regular_stop_flag boolean,
    profile__reroutes_allowed_flag boolean,
    profile__signature_on_file boolean,
    profile__signature_required boolean,
    profile__tpc_flag boolean,
    profile__emp_ship_emp_number text,
    profile__supply_no_cut_flag text,
    profile__starter_kit int,
    profile__starter_kit_quantity int,
    profile__exception_flag boolean,
    profile__international_shipper text,
    profile__special_dist_flag text,
    profile__transmart_flag boolean,
    profile__special_comment_cd text,
    profile__contact_flag boolean,

    -- express_geographic_info
    geographic_info__alpha_id text,
    geographic_info__station_number text,

    -- tnt_operations_profile
    profile__source_name text,
    profile__tnt_customer_number int,
    profile__migration_date date,
    profile__deactivation_date date,

    -- ground_operations_profile
    profile__grnd_barcode_type text,
    profile__grnd_hazmat_flag boolean,
    profile__grnd_pickup_type text,
    profile__grnd_collect_flag boolean,
    profile__national_account_number text,
    profile__grnd_lbl_hazmat_flag boolean,
    profile__grnd_lbl_p_r_pFlag boolean,
    profile__grnd_lbl_univ_waste_flag boolean,

    -- freight_operations_profile
    profile__svc_center_code text,

    -- cargo_operations_profile
    profile__airport_code text,
    profile__business_mode text,
    profile__coding_instructions text,
    profile__synonym_name_1 text,
    profile__synonym_name_2 text,

    -- ***** AUTOMATION STREAM  *****
    -- expressAutomationInfo
    automation_info__insight_flag boolean,
    automation_info__meter_zone_flag boolean,
    automation_info__device_type_code text,

    -- ***** CLEARANCE STREAM  *****
    -- cargoRegulatory
    account_regulatory__fdc_broker_nbr text,
    account_regulatory__fdc_broker_type_cd text,

    -- expressConsignor
    consignor__contactDocumentId bigint,  -- WILL BE TIED TO A CONTACT STANZA
    consignor__natureOfBusiness text,
    consignor__signedDate date,
    consignor__vatNumber text,
    consignor__accountConsignor boolean,
    consignor__name text,

    -- expressCustomerId
    customer_id__iata_number text,
    customer_id__custom_importer_id text,
    customer_id__customer_id_doc_nbr text,

    -- expressRegulatory AND freightRegulatory
    account_regulatory__regulated_agentRegimeEffYearMonth date,  -- QUESTION . FORMAT IS YEAR/MONTH , is date type correct.
    account_regulatory__regulated_agentRegimeExpYearMonth date,
    account_regulatory__bus_registration_id text,
    account_regulatory__broker_date date,
    account_regulatory__canadian_broker_id text,
    account_regulatory__employer_id text,  -- ## expressRegulatory AND freightRegulatory
    account_regulatory__employer_id_type text,
    account_regulatory__forwd_brkr_cd text,
    account_regulatory__gaa_flag boolean,
    account_regulatory__import_declaration_cd set<text>,  -- QUESTION . This is an array of 3.
    account_regulatory__nri_cd text,
    account_regulatory__shipper_export_declaration_flag boolean,

    -- ***** PRICING STREAM  *****
    -- expressPricingProfile
    profile__spot_rate_ind boolean,
    profile__express_plan_flag text,
    profile__express_plan_activity_date date,
    profile__catalog_remail_service_cd text,
    profile__middle_man_cd text,
    profile__gratuity_flag boolean,

    -- expressPricingPreference
    profile__bonus_weight_envelope_flag text,
    profile__priority_alert_flag text,
    profile__domestic_max_declared_value_flag text,
    profile__international_max_declared_value_flag text,
    profile__linehaul_charge_flag text,

    -- freightPricingProfile
    profile__pricing_flag boolean,
    profile__blind_shipper_flag boolean,
    profile__pricing_code text,

    -- nationalAccount   -- QUESTION . We need another table...... ?
    PRIMARY KEY(account_number, opco))
WITH CLUSTERING ORDER BY (opco ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';


CREATE TABLE IF NOT EXISTS express_apply_discount_detail (
    account_number text,
    apply_discount__discount_flag boolean,
    apply_discount__effective_date_time timestamp,
    apply_discount__expiration_date_time timestamp,
    PRIMARY KEY(account_number, apply_discount__effective_date_time, apply_discount__expiration_date_time))
WITH CLUSTERING ORDER BY(apply_discount__effective_date_time DESC, apply_discount__expiration_date_time DESC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS payment_info_v1 (
    account_number text,
    opco text,
    record_type_cd text,  --account, express electronic, etc.
    record_key text,
    record_seq int,

    --express_credit_card
    --freightCreditCard
    --officeCreditCard
    --recipientServicesCreditCard
    --ukDomesticCreditCard
    type text,
    credit_card_id text,
    exp_date_month int,
    exp_date_year int,
    order_of_usage int,
    profile_type text,
    profile_name text,
    auto_sched_thresh_amt text,
    additional_credit_card_info__address__street_line text,
    additional_credit_card_info__address__additional_line1 text,
    additional_credit_card_info__address__additional_line2 text,
    additional_credit_card_info__address__geo_political_subdivision1 text,
    additional_credit_card_info__address__geo_political_subdivision2 text,
    additional_credit_card_info__address__geo_political_subdivision3 text,
    additional_credit_card_info__address__postal_code text,
    additional_credit_card_info__address__country_code text,
    additional_credit_card_info__holder_company text,
    additional_credit_card_info__holder_person__first_name text,
    additional_credit_card_info__holder_person__last_name text,
    additional_credit_card_info__holder_person__middle_name text,
    additional_credit_card_info__holder_person__prefix text,
    additional_credit_card_info__holder_person__suffix text,
    additional_credit_card_info__holder_person__title text,
    additional_credit_card_info__holder_person__gender text,
    additional_credit_card_info__holder_email text,
    additional_credit_card_info__holder_phone__numeric_country_code text,
    additional_credit_card_info__holder_phone__alpha_country_code text,
    additional_credit_card_info__holder_phone__area_code text,
    additional_credit_card_info__holder_phone__phone_number text,
    additional_credit_card_info__holder_phone__extension text,
    additional_credit_card_info__holder_phone__ftc_ok_to_call_flag boolean,
    last_authentication_date date,


    --eftBankInfo
    authorization__person__first_name text,
    authorization__person__last_name text,
    authorization__person__middle_name text,
    authorization__person__prefix text,
    authorization__person__suffix text,
    authorization__person__title text,
    authorization__company_name text,
    authorization__address__street_line text,
    authorization__address__additional_line1 text,
    authorization__address__additional_line2 text,
    authorization__address__secondary__unit1 text,
    authorization__address__secondary__value1 text,
    authorization__address__secondary__unit2 text,
    authorization__address__secondary__value2 text,
    authorization__address__secondary__unit3 text,
    authorization__address__secondary__value3 text,
    authorization__address__secondary__unit4 text,
    authorization__address__secondary__value4 text,
    authorization__address__geo_political_subdivision1 text,
    authorization__address__geo_political_subdivision2 text,
    authorization__address__geo_political_subdivision3 text,
    authorization__address__postal_code text,
    authorization__address__country_code text,
    authorization__phone__tele_com_method text,
    authorization__phone__numeric_country_code text,
    authorization__phone__alpha_country_code text,
    authorization__phone__area_code text,
    authorization__phone__extension text,
    authorization__phone__pin text,
    authorization__phone__ftc_ok_to_call_flag boolean,
    authorization__phone__text_message_flag boolean,
    bank__account text,
    bank__address__street_line text,
    bank__address__additional_line1 text,
    bank__address__additional_line2 text,
    bank__address__secondary__unit1 text,
    bank__address__secondary__value1 text,
    bank__address__secondary__unit2 text,
    bank__address__secondary__value2 text,
    bank__address__secondary__unit3 text,
    bank__address__secondary__value3 text,
    bank__address__secondary__unit4 text,
    bank__address__secondary__value4 text,
    bank__address__geo_political_subdivision1 text,
    bank__address__geo_political_subdivision2 text,
    bank__address__geo_political_subdivision3 text,
    bank__address__postal_code text,
    bank__address__country_code text,
    bank__bank_name text,
    bank__routing_number text,
    days_to_debit int,
    eft_alias_name text,
    eft_seq int,
    eft_type text,
    name_on_account text,
    threshhold_amount text,

    --amexCheckout
    credit_card__type text,
    credit_card__credit_card_id text,
    credit_card__exp_date_month int,
    credit_card__exp_date_year int,
    -- profile_name text,
    auto_sched_term text,

    -- auto_sched_thresh_amt text,
    cc_seq text,
    fpan__first_six_digits text,
    fpan__last_four_digits text,
    fpan__exp_date_month int,
    fpan__exp_date_year int,

    PRIMARY KEY(account_number, opco, record_type_cd, record_key, record_seq))
WITH CLUSTERING ORDER BY(opco ASC, record_type_cd ASC, record_key ASC, record_seq ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS account_contact (
    account_type text,
    account_type__account_number text,
    account_type__opco text,
    contact_type_code text,
    contact_document_id bigint,
    contact_business_id text,
    customer_account_refrence text,
    share_id text,
    print_name_on_bill_flag boolean,
    responsible_party_flag boolean,
    PRIMARY KEY(account_type__account_number, contact_type_code, account_type__opco, contact_business_id))
WITH CLUSTERING ORDER BY(contact_type_code ASC, account_type__opco ASC, contact_business_id ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS contact (
    contact_document_id bigint, --added field for key
    person__first_name text,
    person__last_name text,
    person__middle_name text,
    person__prefix text,
    person__suffix text,
    person__title text,
    person__gender text,
    company_name text,
    job_department text,
    address__street_line text,
    address__additional_line1 text,
    address__additional_line2 text,
    address__secondary__unit1 text,
    address__secondary__value1 text,
    address__secondary__unit2 text,
    address__secondary__value2 text,
    address__secondary__unit3 text,
    address__secondary__value3 text,
    address__secondary__unit4 text,
    address__secondary__value4 text,
    address__geo_political_subdivision1 text,
    address__geo_political_subdivision2 text,
    address__geo_political_subdivision3 text,
    address__postal_code text,
    address__country_code text,
    address__override__reason_code text,
    address__override__problem_flag text,
    address__usps_carrier_route_id text,  -- does not follow standard capitalization pattern
    address__usps_delivery_point_code text, -- does not follow standard capitalization pattern
    address__usps_check_digit text, -- does not follow standard capitalization pattern
    tele_com set<frozen<telecom_details_type>>,
    pager_use text,
    email text,
    html_use text,
    email_marketing_flag text,
    language text,
    written_marketing_method_type text,
    store_id text,
    division text,
    attention_to text,
    contact_preference_flag text,
    additional_email_info__email text,  --these could also be a set of UDTs
    additional_email_info__html_use text,
    additional_email_info__email_marketing_flag text,
    additional_email_info_2_email text,
    additional_email_info2__html_use text,
    additional_email_info2__email_marketing_flag text,
    social_media set<frozen<social_media_type>>,
    PRIMARY KEY(contact_document_id))
WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS assoc_accounts_v1 (
    account_number text,
    associated_account__opco text,
    associated_account__number text,
    PRIMARY KEY(account_number, associated_account__opco, associated_account__number))
WITH CLUSTERING ORDER BY(associated_account__opco ASC, associated_account__number ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS national_account_v1 (
    account_type__acount_id__number text,
    account_type__acount_id__opco text,
    national_account_detail__national_account_company_cd text,
    national_account_detail__national_account_nbr text,
    national_account_detail__national_priority_cd text,
    national_account_detail__national_subgroup_nbr text,
    national_account_detail__membership_exp_date_time timestamp,
    national_account_detail__membership_eff_date_time timestamp,
    last_update_tmstp timestamp,
     PRIMARY KEY(account_type__acount_id__number, account_type__acount_id__opco, national_account_detail__national_account_nbr, national_account_detail__national_subgroup_nbr, national_account_detail__national_priority_cd, national_account_detail__membership_eff_date_time))
 WITH CLUSTERING ORDER BY(account_type__acount_id__opco ASC, national_account_detail__national_account_nbr ASC, national_account_detail__national_subgroup_nbr ASC, national_account_detail__national_priority_cd ASC, national_account_detail__membership_eff_date_time DESC)
     AND bloom_filter_fp_chance = 0.01
     AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     AND comment = ''
     AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
     AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     AND crc_check_chance = 1.0
     AND dclocal_read_repair_chance = 0.0
     AND default_time_to_live = 0
     AND gc_grace_seconds = 864000
     AND max_index_interval = 2048
     AND memtable_flush_period_in_ms = 0
     AND min_index_interval = 128
     AND read_repair_chance = 0.0
     AND speculative_retry = '99PERCENTILE';
