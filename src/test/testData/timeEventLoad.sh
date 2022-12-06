#!/bin/bash
DSBULK_PATH=/Users/michaeldownie/DSE/dsbulk-1.10.0/bin/dsbulk
KEYSPACE=cam_time_event_l1_ks
TABLE=time_event_v1
DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/time_event.csv

$DSBULK_PATH load -k $KEYSPACE -t $TABLE -url $DATA_FILE -m '0=account_number, 1=process_time, 2=type, 3=status, 4=additional_details_items, 5=create_time, 6=event_processed_time, 7=last_update_tmstp' -delim ',' --schema.allowMissingFields true

#optional execution with automatically determined column mapping
#$DSBULK_PATH load -k $KEYSPACE -t $TABLE -url $DATA_FILE  -delim ',' --schema.allowMissingFields true


