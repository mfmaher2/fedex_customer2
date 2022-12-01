NSERT INTO cam_time_event_l1_ks.time_event_v1
(
account_number,
type,
status,
create_time,
process_time,
event_processed_time,
additional_details_items,
last_update_tmstp
)
VALUES
(
'acctTimeEvtTest',
'MONTHLY_BILLING_INDICATOR',
'IN_PROGRESS',
'2022-04-01T00:00:00Z',
'2022-04-01T01:00:00Z',
'2022-04-01T02:00:00Z',
{{name:'nm1-1', value: 'v1-1'}},
'2022-04-01T03:00:00Z'
);

INSERT INTO cam_time_event_l1_ks.time_event_v1
(
account_number,
type,
status,
create_time,
process_time,
event_processed_time,
additional_details_items,
last_update_tmstp
)
VALUES
(
'acctTimeEvtTest',
'MONTHLY_BILLING_INDICATOR',
'IN_PROGRESS',
'2022-05-01T00:00:00Z',
'2022-05-01T01:00:00Z',
'2022-05-01T02:00:00Z',
{{name:'nm2-1', value: 'v2-1'}, {name:'nm2-2', value: 'v2-2'}},
'2022-05-01T03:00:00Z'
);