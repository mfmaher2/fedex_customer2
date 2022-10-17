--***
--Search index definitions
--***
CREATE SEARCH INDEX IF NOT EXISTS ON cam_audit_history_<ENV_LEVEL_ID>ks.audit_history_v1 ;
CREATE SEARCH INDEX IF NOT EXISTS ON cam_time_event_<ENV_LEVEL_ID>ks.time_event_v1 ;

CREATE SEARCH INDEX IF NOT EXISTS ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1;
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1
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

ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='profile__airport_code']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='profile__synonym_name_1']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='profile__synonym_name_2']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='person__first_name']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='person__last_name']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='person__middle_name']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='address__street_line']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='address__additional_line1']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='address__geo_political_subdivision1']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='address__geo_political_subdivision2']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='address__geo_political_subdivision3']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='address__postal_code']@type='textNorm1';
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 SET field[@name='email']@type='textNorm1';

ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1
    ADD fields.field[ @name='nameLine',
                      @type='textNorm1',
                      @multiValued='true',
                      @docValues='true'];

ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 ADD copyField[@source='company_name', @dest='nameLine'];
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 ADD copyField[@source='person__first_name', @dest='nameLine'];
ALTER SEARCH INDEX SCHEMA ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1 ADD copyField[@source='person__last_name', @dest='nameLine'];

RELOAD SEARCH INDEX ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1;
REBUILD SEARCH INDEX ON cam_search_<ENV_LEVEL_ID>ks.cam_search_v1;
