CREATE TYPE IF NOT EXISTS cam_search_<ENV_LEVEL_ID>ks.telecom_details_type (
    telecom_method text,
    numeric_country_code text,
    alpha_country_code text,
    area_code text,
    phone_number text,
    extension text,
    pin text,
    ftc_ok_to_call_flag boolean,
    text_message_flag boolean
);

CREATE TYPE IF NOT EXISTS cam_audit_history_<ENV_LEVEL_ID>ks.history_additional_identifier_type (
    type text,
    value text
);

CREATE TYPE IF NOT EXISTS cam_audit_history_<ENV_LEVEL_ID>ks.history_entity_type (
    action text,
    stanza_name text,
    stanza text
);

CREATE TYPE IF NOT EXISTS cam_audit_history_<ENV_LEVEL_ID>ks.history_field_type (
    action text,
    stanza_name text,
    field_name text,
    previous_value text,
    new_value text
);

CREATE TYPE IF NOT EXISTS cam_audit_history_<ENV_LEVEL_ID>ks.audit_history_entry (
     history_detail__descriptive_identifier text,
     history_detail__additional_identifier__key set<frozen<history_additional_identifier_type>>,
     history_detail__entity set<frozen<history_entity_type>>,
     history_detail__field set<frozen<history_field_type>>,
);

CREATE TYPE IF NOT EXISTS cam_time_event_<ENV_LEVEL_ID>ks.time_event_additional_details_items(
    name text,
    value text
);

CREATE TABLE IF NOT EXISTS cam_audit_history_<ENV_LEVEL_ID>ks.audit_history_v1 (
     account_number text,
     opco text,                     --maps to history_detail__opco
     last_update_tmstp timestamp,
     audit_details set<frozen<audit_history_entry>>,
     request_action text,
     transaction_id text,
     app_id text,
     user_id text,
     source text,
     request_type text,
    PRIMARY KEY(account_number, last_update_tmstp, opco, transaction_id))
 WITH CLUSTERING ORDER BY(last_update_tmstp DESC, opco ASC, transaction_id ASC)
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

CREATE TABLE IF NOT EXISTS cam_time_event_<ENV_LEVEL_ID>ks.time_event_v1 (
    account_number text,
    type text,             -- possible values: MONTHLY_BILLING_INDICATOR,ACCOUNT_RESTORE
    status text,           -- possible values: FAILED,IN_PROGRESS,NOT_STARTED,SUCCESS
    create_time timestamp,
    process_time timestamp,
    event_processed_time timestamp,
    additional_details_items set<frozen<time_event_additional_details_items>>,
    last_update_tmstp timestamp,
    PRIMARY KEY(account_number, process_time, type, status))
WITH CLUSTERING ORDER BY (process_time DESC, type ASC, status ASC)
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

--Possible search use case specific table
CREATE TABLE IF NOT EXISTS cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 (
    account_number text,
    opco text,
    last_update_tmstp timestamp,
    profile__archive_reason_code text,
    profile__customer_account_status text,
    profile__account_type text,
    profile__airport_code text,
    profile__synonym_name_1 text,
    profile__synonym_name_2 text,
    profile__interline_cd text,
    invoice_preference__billing_restriction_indicator text,
    credit_detail__cash_only_reason text,
    credit_detail__credit_rating text,
    contact_document_id bigint,
    contact_type_code text,
    contact_business_id text,
    company_name text,
    person__first_name text,
    person__last_name text,
    person__middle_name text,
    address__street_line text,
    address__additional_line1 text,
    address__geo_political_subdivision1 text,
    address__geo_political_subdivision2 text,
    address__geo_political_subdivision3 text,
    address__postal_code text,
    address__country_code text,
    share_id text,
    email text,
    tele_com set<frozen<telecom_details_type>>,
    PRIMARY KEY(account_number, opco, contact_type_code, contact_business_id))
WITH CLUSTERING ORDER BY(opco ASC, contact_type_code ASC, contact_business_id ASC)
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

CREATE TABLE IF NOT EXISTS cam_operations_<ENV_LEVEL_ID>ks.processing_cache (
    transaction_id text, --todo change to UUID
    table_name text,
    table_primary_key_properties text,
    table_primary_key_values text,
    property_name text,
    property_previous_value text,
    property_previous_writetime bigint,
    property_new_value text,
   PRIMARY KEY(transaction_id, table_name, table_primary_key_values, property_name))
WITH CLUSTERING ORDER BY(table_name ASC, table_primary_key_values ASC, property_name ASC)
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

CREATE TABLE IF NOT EXISTS cam_operations_<ENV_LEVEL_ID>ks.processing_cache_object (
    transaction_id text, --todo change to UUID
    table_name text,
    table_primary_key_properties text,
    table_primary_key_values text,
    prevous_entry blob,
    new_entry blob,
   PRIMARY KEY(transaction_id, table_name, table_primary_key_values))
WITH CLUSTERING ORDER BY(table_name ASC, table_primary_key_values ASC)
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


