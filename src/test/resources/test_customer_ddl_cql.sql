USE customer;

CREATE TYPE IF NOT EXISTS associate_account_type (
    opco text,
    assoc_account text
);
CREATE TYPE IF NOT EXISTS address_secondary_type (
    unit text,
    value text
);

CREATE TYPE IF NOT EXISTS telecom_details_type (
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

CREATE TYPE IF NOT EXISTS credit_card_type (
    type text,
    credit_card_id text,
    exp_date_month int,
    exp_date_year int,
    profile_type text,
    profile_name text,
    auto_sched_thresh_amt text
);

CREATE TYPE IF NOT EXISTS express_credit_card_type (
    type text,
    credit_card_id text,
    exp_date_month int,
    exp_date_year int,
    order_of_usage int,
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
    last_authentication_date date
)


CREATE TYPE IF NOT EXISTS eft_bank_info_type (
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
    authorization__address__secondary_unit1 text,
    authorization__address__secondary_value1 text,
    authorization__address__secondary_unit2 text,
    authorization__address__secondary_value2 text,
    authorization__address__secondary_unit3 text,
    authorization__address__secondary_value3 text,
    authorization__address__secondary_unit4 text,
    authorization__address__secondary_value4 text,
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
    bank__address__secondary_unit1 text,
    bank__address__secondary_value1 text,
    bank__address__secondary_unit2 text,
    bank__address__secondary_value2 text,
    bank__address__secondary_unit3 text,
    bank__address__secondary_value3 text,
    bank__address__secondary_unit4 text,
    bank__address__secondary_value4 text,
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
    threshhold_amount text
);

CREATE TABLE IF NOT EXISTS enterprise_tax_info (
    account_number text,
    tax_exempt boolean,
    PRIMARY KEY(account_number))
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

CREATE TABLE IF NOT EXISTS cargo_account_receivables (
    account_number text,
    account_receivables__payor_type text,
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS cargo_account_receivables (
    account_number text,
    account_receivables__call_zone_desc text,
    account_receivables__gsp_write_off text,
    account_receivables__onlineEligibility boolean,
    account_receivables__partial_pay_letter_flag boolean,
    account_receivables__payment_type text,
    account_receivables__payment_method_code text,
    account_receivables__arrow_customer_flag text,
    account_receivables__international_ar_preference int,   --does not follow standard capitalization patterns
    account_receivables__international_ar_date date,        --does not follow standard capitalization patterns
    account_receivables__no_refund_flag boolean,
    account_receivables__debut_company_code text,
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS cargo_account_receivables (
    account_number text,
    aggregations__ed_aggr_code text,
    aggregations__geo_acct_number text,
    aggregations__global_account_number text,
    aggregations__global_subgroup text,
    aggregations__ss_account_number text,
    aggregations__bill_to_number text,
    aggregations__edi_number text,
    aggregations__copy_master_address,
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS express_apply_discount_detail (
    account_number text,
    apply_discount__discount_flag boolean,
    apply_discount__effective_date_time timestamp,
    apply_discount__expiration_date_time timestamp,
    PRIMARY KEY(account_number))
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



CREATE TABLE IF NOT EXISTS express_credit_card (
    account_number text,
    credit_card set<frozen<express_credit_card_type>>,
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS express_credit_detail (
    account_number text,
    credit_detail__credit_status text,
    credit_detail__credit_status_reason_code text,
    credit_detail__denied_flag boolean,
    credit_detail__bankruptcy_date date,
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
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS express_edi (
    account_number text,
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
    PRIMARY KEY(account_number))
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

CREATE TABLE IF NOT EXISTS express_electronic_pay (
    account_number text,
    credit_card set<frozen<credit_card_type>>,
    eft_bank_info set<frozen<eft_bank_info_type>>,
    addl_bank_info__abi_code text,
    addl_bank_info__addl_bank_id text,
    bank_number text,
    cab_code text,
    giro_account text,
    domicile_number text,
    amex_checkout set<frozen<amex_checkout_type>>,
    alt_payment__alt_payment_type text,
    alt_payment__billing_agreement_id text,
    alt_payment__billing_agreement_date date,
    alt_payment__client_id text,
    alt_payment__auto_sched_term text,
    alt_payment__auto_sched_thresh_amt text,
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS express_gfbo_electronic_pay (
    account_number text,
    PRIMARY KEY(account_number))
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







CREATE TYPE IF NOT EXISTS amex_checkout_type (
    credit_card__type text,
    credit_card__credit_card_id text,
    credit_card__exp_date_month int,
    credit_card__exp_date_year int,
    profile_name text,
    auto_sched_term text,
    auto_sched_thresh_amt text,
    cc_seq text,
    fpan__first_six_digits text,
    fpan__last_four_digits text,
    fpan__exp_date_month int,
    fpan__exp_date_year int
);


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
    address__secondary set<frozen<address_secondary_type>>,
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


CREATE TABLE IF NOT EXISTS enterprise_profile (
    account_number text,
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
    PRIMARY KEY(account_number))
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

CREATE TABLE IF NOT EXISTS express_account_creation_profile_combined (
    account_number text,
    opco text,
    profile__customer_request_name text,
    profile__creation_date date,
    profile__employee_requester__opco text,
    profile__employee_requester__number text,
    profile__source_group text,
    profile__source_dept text,
    profile__source_system text,
    profile__employee_creator_opco text,
    profile__employee_creator_number text,
    PRIMARY KEY(account_number, opco))
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

CREATE TABLE IF NOT EXISTS express_account_creation_profile (
    account_number text,
    profile__customer_request_name text,
    profile__creation_date date,
    profile__employee_requester__opco text,
    profile__employee_requester__number text,
    profile__source_group text,
    profile__source_dept text,
    profile__source_system text,
    profile__employee_creator_opco text,
    profile__employee_creator_number text,
    PRIMARY KEY(account_number))
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

CREATE TABLE IF NOT EXISTS express_account_profile (
    account_number text,
    profile__account_type text,
    profile__customer_account_status text,
    profile__duplicate_account_flag boolean,
    profile__fdx_ok_to_call_flag boolean,
    profile__archive_date date,
    profile__archive_reason_code text,
    profile__archive_options text,
    profile__cargo_ind text,
    profile__pref_cust_flag boolean,
    profile__service_level text,
    PRIMARY KEY(account_number))
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


CREATE TABLE IF NOT EXISTS freight_account_creation_profile (
    account_number text,
    profile__customer_request_name text,
    profile__creation_date date,
    profile__employee_requester__opco text,
    profile__employee_requester__number text,
    profile__source_group text,
    profile__source_dept text,
    profile__source_system text,
    profile__employee_creator_opco text,
    profile__employee_creator_number text,
    PRIMARY KEY(account_number))
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

CREATE TABLE IF NOT EXISTS freight_account_profile (
    account_number text,
    profile__account_type text,
    profile_account_sub_type text,
    profile__customer_account_status text,
    profile__duplicate_account_flag boolean,
    profile__fdx_ok_to_call_flag boolean,
    profile__archive_date date,
    profile__archive_reason_code text,
    profile__sales_rep__opco text,
    profile__sales_rep__number text,
    profile__service_level text,
    profile__scac_code text,
    PRIMARY KEY(account_number))
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



--additional tables used for testing/demonstration

CREATE TABLE IF NOT EXISTS enterprise_assoc_accounts (
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

CREATE TABLE IF NOT EXISTS enterprise_assoc_accounts_udt (
    account_number text,
    assoc_accounts set<frozen<associate_account_type>>,
    PRIMARY KEY(account_number))
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


--CREATE TABLE IF NOT EXISTS enterprise_restrictions ( --?? Is this a 1:many relationship with enterprise profile?
--    account_number text,
--    restrict_type text,         --service or view
--    restrict_opco,
--    PRIMARY KEY(account_number, restrict_type, restrict_opco))
--WITH CLUSTERING ORDER BY(restrict_type ASC, restrict_opco ASC)
--    AND bloom_filter_fp_chance = 0.01
--    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
--    AND comment = ''
--    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'sstable_size_in_mb': '160', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
--    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
--    AND crc_check_chance = 1.0
--    AND dclocal_read_repair_chance = 0.0
--    AND default_time_to_live = 0
--    AND gc_grace_seconds = 864000
--    AND max_index_interval = 2048
--    AND memtable_flush_period_in_ms = 0
--    AND min_index_interval = 128
--    AND read_repair_chance = 0.0
--    AND speculative_retry = '99PERCENTILE';

