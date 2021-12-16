USE customer;

--CREATE SEARCH INDEX IF NOT EXISTS ON payment_info_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (exp_date_month) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (exp_date_year) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (profile_type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (profile_name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (auto_sched_term) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (auto_sched_thresh_amt) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (cc_seq) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (days_to_debit) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (eft_alias_name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (eft_seq) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (eft_type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON payment_info_v1 (threshhold_amount) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON national_account_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON national_account_v1 (national_account_detail__national_account_company_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON national_account_v1 (national_account_detail__national_account_nbr) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON national_account_v1 (national_account_detail__national_priority_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON national_account_v1 (national_account_detail__membership_exp_date_time) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON national_account_v1 (national_account_detail__membership_eff_date_time) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON time_event_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON time_event_v1 (create_time) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON time_event_v1 (event_processed_time) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON centralized_view_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON centralized_view_v1 (account_status__status_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON centralized_view_v1 (account_status__status_date) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON apply_discount_detail_v1;
CREATE custom index if NOT EXISTS ON apply_discount_detail_v1 (apply_discount__expiration_date_time) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON assoc_accounts_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON assoc_accounts_v1 (associated_account__opco) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON assoc_accounts_v1 (associated_account__number) USING 'StorageAttachedIndex';

--customer accounts search index creation
--CREATE SEARCH INDEX IF NOT EXISTS ON cust_acct_v1
--    WITH COLUMNS
--       account_number,
--       opco,
--       profile__archive_reason_code,
--       profile__customer_account_status,
--       profile__account_type,
--       invoice_preference__billing_restriction_indicator,
--       profile__interline_cd,
--
--       --included for 'other searches' and/or adHoc queries
--       aggregations__bill_to_number,
--       aggregations__edi_number,
--       account_regulatory__bus_registration_id,
--       tax_info__tax_data,
--       customer_id__customer_id_doc_nbr,
--       duty_tax_info,
--       tax_info__codice_fiscale,
--       aggregations__ed_aggr_code,
--
--       --included  for adHoc queries
--       invoice_preference__billing_cycle,
--       invoice_preference__billing_medium,
--       invoice_preference__currency_code,
--       invoice_preference__electronic_bill_payment_plan_flag_cd,
--       invoice_preference__international_billing_cycle,
--       invoice_preference__international_billing_medium,
--       invoice_preference__settlement_level_indicator,
--       invoice_preference__direct_debit_indicator,
--       eligibility__ground,
--       eligibility__express,
--       eligibility__freight,
--       eligibility__office,
--       credit_detail__credit_status,
--       credit_detail__credit_status_reason_code,
--       credit_detail__cash_only_reason,
--       credit_detail__credit_rating,
--       account_receivables__payment_type,
--       geographic_info__alpha_id,
--       geographic_info__station_number
--
--;
--
--ALTER SEARCH INDEX SCHEMA ON cust_acct_v1
--   ADD types.fieldType [
--      @name='textNorm1' ,
--      @class='org.apache.solr.schema.TextField',
--      @omitNorms='true',
--      @sortMissingLast='true']
--   WITH
--    $${
--        "analyzer": [ {
--            "type": "index",
--            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
--            "filter": [
--                { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
--                { "class": "solr.UpperCaseFilterFactory" }
--            ]
--        },
--        {
--            "type": "query",
--            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
--            "filter": [
--                 { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
--                 { "class": "solr.UpperCaseFilterFactory" }
--             ]
--        }]
--    }$$;
--
--ALTER SEARCH INDEX SCHEMA ON cust_acct_v1 SET FIELD[@name='profile__archive_reason_code'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON cust_acct_v1 SET FIELD[@name='profile__customer_account_status'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON cust_acct_v1 SET FIELD[@name='profile__account_type'] @type='textNorm1';
--
--RELOAD SEARCH INDEX ON cust_acct_v1;
--REBUILD SEARCH INDEX ON cust_acct_v1;

--customer accounts SAI index creation
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (profile__archive_reason_code) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (profile__customer_account_status) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (profile__account_type) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__billing_restriction_indicator) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (profile__interline_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (aggregations__bill_to_number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (aggregations__edi_number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (account_regulatory__bus_registration_id) USING 'StorageAttachedIndex';

--UDT, must match entire entry, not possible to search for individual element content with SAI
--CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (tax_info__tax_data) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (customer_id__customer_id_doc_nbr) USING 'StorageAttachedIndex';

-- map: duty_tax_info_country_code -> duty_tax_info_duty_tax_number  --todo - create values index and example query test
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (duty_tax_info) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (tax_info__codice_fiscale) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (aggregations__ed_aggr_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__billing_cycle) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__billing_medium) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__currency_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__electronic_bill_payment_plan_flag_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__international_billing_cycle) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__international_billing_medium) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__settlement_level_indicator) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (invoice_preference__direct_debit_indicator) USING 'StorageAttachedIndex';
--Boolean types not supported by SAI
--CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (eligibility__ground) USING 'StorageAttachedIndex';
--CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (eligibility__express) USING 'StorageAttachedIndex';
--CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (eligibility__freight) USING 'StorageAttachedIndex';
--CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (eligibility__office) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (credit_detail__credit_status) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (credit_detail__credit_status_reason_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (credit_detail__cash_only_reason) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (credit_detail__credit_rating) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (account_receivables__payment_type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (geographic_info__alpha_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (geographic_info__station_number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cust_acct_v1 (aggregations__ed_aggr_code) USING 'StorageAttachedIndex';

--account contact search index creation
--CREATE SEARCH INDEX IF NOT EXISTS ON account_contact
--    WITH COLUMNS
--       account_number,
--       opco,
--       contact_document_id,
--       contact_type_code,
--       contact_business_id,
--       company_name,
--       person__first_name,
--       person__last_name,
--       person__middle_name,
--       address__street_line,
--       address__additional_line1,
--       address__geo_political_subdivision1,
--       address__geo_political_subdivision2,
--       address__geo_political_subdivision3,
--       address__postal_code,
--       email,
--       address__country_code,
--       tele_com
--    ;
--
--ALTER SEARCH INDEX SCHEMA ON account_contact
--   ADD types.fieldType [
--      @name='textNorm1' ,
--      @class='org.apache.solr.schema.TextField',
--      @omitNorms='true',
--      @sortMissingLast='true']
--   WITH
--    $${
--        "analyzer": [ {
--            "type": "index",
--            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
--            "filter": [
--                { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
--                { "class": "solr.UpperCaseFilterFactory" }
--            ]
--        },
--        {
--            "type": "query",
--            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
--            "filter": [
--                 { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
--                 { "class": "solr.UpperCaseFilterFactory" }
--             ]
--        }]
--    }$$;
--
----Is this type used in system ??
--ALTER SEARCH INDEX SCHEMA ON account_contact
--   ADD types.fieldType [
--      @name='textNorm2' ,
--      @class='org.apache.solr.schema.TextField',
--      @omitNorms='true',
--      @sortMissingLast='true']
--   WITH
--    $${
--        "analyzer": [ {
--            "type": "index",
--            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
--            "filter": [
--                { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9\p{Punct}\p{Space}]", "replacement"="", "replace"="all" },
--                { "class": "solr.UpperCaseFilterFactory" }
--            ]
--        },
--        {
--            "type": "query",
--            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
--            "filter": [
--                 { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
--                 { "class": "solr.UpperCaseFilterFactory" }
--             ]
--        }]
--    }$$;
--
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='person__first_name'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='person__last_name'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='person__middle_name'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__street_line'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__additional_line1'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__geo_political_subdivision1'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__geo_political_subdivision2'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__geo_political_subdivision3'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__postal_code'] @type='textNorm1';
--ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='email'] @type='textNorm1';
--
--ALTER SEARCH INDEX SCHEMA ON account_contact
--    ADD fields.field[ @name='nameLine',
--                      @type='textNorm1',
--                      @multiValued='true',
--                      @docValues='true'];
--
--ALTER SEARCH INDEX SCHEMA ON account_contact ADD copyField[@source='company_name', @dest='nameLine'];
--ALTER SEARCH INDEX SCHEMA ON account_contact ADD copyField[@source='person__first_name', @dest='nameLine'];
--ALTER SEARCH INDEX SCHEMA ON account_contact ADD copyField[@source='person__last_name', @dest='nameLine'];
--
--RELOAD SEARCH INDEX ON account_contact;
--REBUILD SEARCH INDEX ON account_contact;


CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (contact_type_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (contact_business_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (company_name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (person__first_name) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (person__last_name) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (person__middle_name) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__street_line) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__additional_line1) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__geo_political_subdivision1) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__geo_political_subdivision2) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__geo_political_subdivision3) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__postal_code) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (email) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (address__country_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON account_contact (VALUES(name_line)) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};


--CREATE SEARCH INDEX IF NOT EXISTS ON comment_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON comment_v1 (comment__type) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON audit_history_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON audit_history_v1 (last_update_tmstp) USING 'StorageAttachedIndex';

--CREATE SEARCH INDEX IF NOT EXISTS ON group_info_v1;
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (group_id__code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (group_id__number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (group_id_detail__requester) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (group_id_detail__name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (group_id_detail__master_account) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (effective_date_time) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON group_info_v1 (expiration_date_time) USING 'StorageAttachedIndex';

CREATE SEARCH INDEX IF NOT EXISTS ON cam_search_v1;
ALTER SEARCH INDEX SCHEMA ON cam_search_v1
   ADD types.fieldType [
      @name='textNorm1' ,
      @class='org.apache.solr.schema.TextField',
      @omitNorms='true',
      @sortMissingLast='true']
   WITH
    $${
        "analyzer": [ {
            "type": "index",
            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
            "filter": [
                { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
                { "class": "solr.UpperCaseFilterFactory" }
            ]
        },
        {
            "type": "query",
            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
            "filter": [
                 { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9]", "replacement"="", "replace"="all" },
                 { "class": "solr.UpperCaseFilterFactory" }
             ]
        }]
    }$$;

ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='profile__airport_code'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='profile__synonym_name_1'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='profile__synonym_name_2'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='person__first_name'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='person__last_name'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='person__middle_name'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='address__street_line'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='address__additional_line1'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='address__geo_political_subdivision1'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='address__geo_political_subdivision2'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='address__geo_political_subdivision3'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='address__postal_code'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 SET FIELD[@name='email'] @type='textNorm1';

ALTER SEARCH INDEX SCHEMA ON cam_search_v1
    ADD fields.field[ @name='nameLine',
                      @type='textNorm1',
                      @multiValued='true',
                      @docValues='true'];

ALTER SEARCH INDEX SCHEMA ON cam_search_v1 ADD copyField[@source='company_name', @dest='nameLine'];
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 ADD copyField[@source='person__first_name', @dest='nameLine'];
ALTER SEARCH INDEX SCHEMA ON cam_search_v1 ADD copyField[@source='person__last_name', @dest='nameLine'];

RELOAD SEARCH INDEX ON cam_search_v1;
REBUILD SEARCH INDEX ON cam_search_v1;
