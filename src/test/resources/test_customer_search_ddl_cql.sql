USE customer;
CREATE SEARCH INDEX IF NOT EXISTS ON cust_acct_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON payment_info_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON assoc_accounts_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON national_account_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON apply_discount_detail_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON time_event_v1;

--Temporary search index for test functionality
CREATE SEARCH INDEX IF NOT EXISTS ON contact;

--customer accounts search index creation
CREATE SEARCH INDEX IF NOT EXISTS ON cust_acct_v1
    WITH COLUMNS
       account_number,
       opco,
       profile__archive_reason_code,
       profile__customer_account_status,
       profile__account_type,
       invoice_preference__billing_restriction_indicator,
       credit_detail__cash_only_reason,
       credit_detail__credit_rating,
       profile__interline_cd
;

ALTER SEARCH INDEX SCHEMA ON cust_acct_v1
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

ALTER SEARCH INDEX SCHEMA ON cust_acct_v1 SET FIELD[@name='profile__archive_reason_code'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cust_acct_v1 SET FIELD[@name='profile__customer_account_status'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cust_acct_v1 SET FIELD[@name='profile__account_type'] @type='textNorm1';

RELOAD SEARCH INDEX ON cust_acct_v1;
REBUILD SEARCH INDEX ON cust_acct_v1;

--account contact search index creation
CREATE SEARCH INDEX IF NOT EXISTS ON account_contact
    WITH COLUMNS
       account_number,
       opco,
       contact_document_id,
       contact_type_code,
       contact_business_id
       company_name,
       person__first_name,
       person__last_name,
       person__middle_name,
       address__street_line,
       address__additional_line1,
       address__geo_political_subdivision1,
       address__geo_political_subdivision2,
       address__geo_political_subdivision3,
       address__postal_code,
       email,
       address__country_code,
       tele_com
    ;

ALTER SEARCH INDEX SCHEMA ON account_contact
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

--Is this type used in system ??
ALTER SEARCH INDEX SCHEMA ON account_contact
   ADD types.fieldType [
      @name='textNorm2' ,
      @class='org.apache.solr.schema.TextField',
      @omitNorms='true',
      @sortMissingLast='true']
   WITH
    $${
        "analyzer": [ {
            "type": "index",
            "tokenizer": { "class": "solr.KeywordTokenizerFactory" },
            "filter": [
                { "class": "solr.PatternReplaceFilterFactory", "pattern":"[^a-zA-Z0-9\p{Punct}\p{Space}]", "replacement"="", "replace"="all" },
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

ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='person__first_name'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='person__last_name'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='person__middle_name'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__street_line'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__additional_line1'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__geo_political_subdivision1'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__geo_political_subdivision2'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__geo_political_subdivision3'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='address__postal_code'] @type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON account_contact SET FIELD[@name='email'] @type='textNorm1';

ALTER SEARCH INDEX SCHEMA ON account_contact
    ADD fields.field[ @name='nameLine',
                      @type='textNorm1',
                      @multiValued='true',
                      @docValues='true'];

ALTER SEARCH INDEX SCHEMA ON account_contact ADD copyField[@source='company_name', @dest='nameLine'];
ALTER SEARCH INDEX SCHEMA ON account_contact ADD copyField[@source='person__first_name', @dest='nameLine'];
ALTER SEARCH INDEX SCHEMA ON account_contact ADD copyField[@source='person__last_name', @dest='nameLine'];

RELOAD SEARCH INDEX ON account_contact;
REBUILD SEARCH INDEX ON account_contact;