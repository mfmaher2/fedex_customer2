USE customer;

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
