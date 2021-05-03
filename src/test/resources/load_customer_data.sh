/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"COPY customer.contact (contact_document_id, additional_email_info2__email_marketing_flag, additional_email_info2__html_use, additional_email_info_2_email, additional_email_info__email, additional_email_info__email_marketing_flag, additional_email_info__html_use, address__additional_line1, address__additional_line2, address__country_code, address__geo_political_subdivision1, address__geo_political_subdivision2, address__geo_political_subdivision3, address__override__problem_flag, address__override__reason_code, address__postal_code, address__secondary__unit1, address__secondary__value1, address__secondary__unit2, address__secondary__value2, address__secondary__unit3, address__secondary__value3, address__secondary__unit4, address__secondary__value4, address__street_line, address__usps_carrier_route_id, address__usps_check_digit, address__usps_delivery_point_code, attention_to, company_name, contact_preference_flag, division, email, email_marketing_flag, html_use, job_department, language, pager_use, person__first_name, person__gender, person__last_name, person__middle_name, person__prefix, person__suffix, person__title, social_media, store_id, tele_com, written_marketing_method_type) FROM '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/contactData.csv' WITH HEADER=TRUE;"
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"COPY customer.cust_acct_v1 (account_number, opco, profile__customer_type, profile__account_type) FROM '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/customerAccountData.csv' WITH HEADER=TRUE;"
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"COPY customer.payment_info_v1 (account_number, opco, record_type_cd, record_key, record_seq, credit_card_id) FROM '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/paymentInfoData.csv' WITH HEADER=TRUE;"
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"COPY customer.assoc_accounts_v1 (account_number, associated_account__opco, associated_account__number) FROM '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/assocAcctData.csv' WITH HEADER=TRUE;"
/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"COPY customer.national_account_v1 (opco, account_number, national_account_detail__membership_eff_date_time, national_account_detail__national_account_nbr, national_account_detail__national_subgroup_nbr, national_account_detail__national_priority_cd, national_account_detail__membership_exp_date_time, last_update_tmstp, national_account_detail__national_account_company_cd) FROM '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/nationAcctData.csv' WITH HEADER=TRUE;"

# /Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh 127.0.0.1 -e"COPY customer.enterprise_profile FROM '/Users/michaeldownie/Documents/DataStax/Projects/FedEx/code/fedex_customer/src/test/testData/entProfile.csv' WITH HEADER=TRUE;"