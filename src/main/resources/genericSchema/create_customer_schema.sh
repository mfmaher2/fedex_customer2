#Parameters
# 1 - path to cqlsh
# 2 - host address
# 3 - host port number
# 4 - folder path for schema creation scripts
#** auth info possibly needed
$1 $2 $3 -e"SOURCE '$4/test_customer_ddl_cql.sql';"
$1 $2 $3 -e"SOURCE '$4/test_customer_search_ddl_cql.sql';"

#testing schemas
$1 $2 $3 -e"SOURCE '$4/test_only_schema_cql.sql';"
$1 $2 $3 -e"SOURCE '$4/test_seq_num_cql.sql';"

