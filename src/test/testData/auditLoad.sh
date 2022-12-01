#!/bin/bash
#DSBULK_PATH=/Users/michaeldownie/DSE/dsbulk-1.8.0/bin/dsbulk
DSBULK_PATH=/Users/michaeldownie/DSE/dsbulk-1.10.0/bin/dsbulk
KEYSPACE=cam_audit_history_l1_ks
TABLE=audit_history_v1
#DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/audit.csv
#DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/audit_from_bulk.csv
#DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/audit_from_bulk_mod.csv
#DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/audit_from_bulk_v10.csv
DATA_FILE=/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/audit_from_bulk_v10_mod.csv
#This version works and loads data correctly.  Able to replace /" with ' in export file

$DSBULK_PATH load -k $KEYSPACE -t $TABLE -url $DATA_FILE -m '0=account_number, 1=last_update_tmstp, 2=transaction_id, 3=app_id, 4=audit_details, 5=request_action, 6=request_type, 7=source, 8=user_id' -delim ',' --schema.allowMissingFields true
#$DSBULK_PATH load -k $KEYSPACE -t $TABLE -url $DATA_FILE  -delim ',' --schema.allowMissingFields true

#/Users/michaeldownie/DSE/dsbulk-1.8.0/bin/dsbulk unload -k cam_audit_history_l1_ks -t audit_history_v1 -url audit_bulk_out.csv