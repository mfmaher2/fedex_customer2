##** paths need to be updated to match current environment, also auth info possibly needed
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"SOURCE '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/resources/L4/test_customer_ddl_cql.sql';"
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"SOURCE '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/resources/L4/test_customer_search_ddl_cql.sql';"

#testing schemas
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"SOURCE '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/resources/L4/test_only_schema_cql.sql';"
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"SOURCE '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/resources/L4/test_seq_num_cql.sql';"

