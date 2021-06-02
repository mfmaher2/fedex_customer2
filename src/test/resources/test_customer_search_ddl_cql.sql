USE customer;
CREATE SEARCH INDEX IF NOT EXISTS ON cust_acct_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON account_contact;
CREATE SEARCH INDEX IF NOT EXISTS ON payment_info_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON assoc_accounts_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON national_account_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON apply_discount_detail_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON time_event_v1;

--Temporary search index for test functionality
CREATE SEARCH INDEX IF NOT EXISTS ON contact;


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

ALTER SEARCH INDEX SCHEMA ON cust_acct_v1
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
