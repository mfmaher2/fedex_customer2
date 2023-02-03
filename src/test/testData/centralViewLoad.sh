#!/bin/bash
DSBULK_PATH=/Users/michaeldownie/DSE/dsbulk-1.10.0/bin/dsbulk
KEYSPACE=cam_centralized_view_l1_ks
TABLE=centralized_view_v1
DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/central_view.csv

$DSBULK_PATH load -k $KEYSPACE -t $TABLE -url $DATA_FILE -m '0=account_number, 1=account_status__status_code, 2=account_status__status_date, 3=last_update_tmstp, 4=opco_description' -delim ',' --schema.allowMissingFields true

#optional execution with automatically determined column mapping
#$DSBULK_PATH load -k $KEYSPACE -t $TABLE -url $DATA_FILE  -delim ',' --schema.allowMissingFields true

