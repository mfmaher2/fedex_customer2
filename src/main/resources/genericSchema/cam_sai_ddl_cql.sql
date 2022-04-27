--***
--SAI index definitions
--***
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (exp_date_month) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (exp_date_year) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (profile_type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (profile_name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (auto_sched_term) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (auto_sched_thresh_amt) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (cc_seq) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (days_to_debit) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (eft_alias_name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (eft_seq) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (eft_type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (threshhold_amount) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_payment_info_<ENV_LEVEL_ID>_ks.payment_info_v1 (payment_id) USING 'StorageAttachedIndex';  --previously credit_card_id

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.national_account_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.national_account_v1 (national_account_detail__national_account_company_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.national_account_v1 (national_account_detail__national_account_nbr) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.national_account_v1 (national_account_detail__national_priority_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.national_account_v1 (national_account_detail__membership_exp_date_time) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.national_account_v1 (national_account_detail__membership_eff_date_time) USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_centralized_view_<ENV_LEVEL_ID>_ks.centralized_view_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_centralized_view_<ENV_LEVEL_ID>_ks.centralized_view_v1 (account_status__status_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_centralized_view_<ENV_LEVEL_ID>_ks.centralized_view_v1 (account_status__status_date) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_centralized_view_<ENV_LEVEL_ID>_ks.centralized_view_v1 (keys(opco_description)) USING 'StorageAttachedIndex'; -- opco_code
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_centralized_view_<ENV_LEVEL_ID>_ks.centralized_view_v1 (values(opco_description)) USING 'StorageAttachedIndex'; -- opco_account_number

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_apply_discount_<ENV_LEVEL_ID>_ks.apply_discount_detail_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE custom index if NOT EXISTS ON cam_apply_discount_<ENV_LEVEL_ID>_ks.apply_discount_detail_v1 (apply_discount__expiration_date_time) USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_assoc_account_<ENV_LEVEL_ID>_ks.assoc_accounts_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_assoc_account_<ENV_LEVEL_ID>_ks.assoc_accounts_v1 (associated_account__opco) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_assoc_account_<ENV_LEVEL_ID>_ks.assoc_accounts_v1 (associated_account__number) USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (opco) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (profile__archive_reason_code) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (profile__customer_account_status) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (profile__account_type) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__billing_restriction_indicator) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (profile__interline_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (aggregations__bill_to_number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (aggregations__edi_number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (account_regulatory__bus_registration_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (customer_id__customer_id_doc_nbr) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (values(tax_info__tax_data)) USING 'StorageAttachedIndex'; --tax_id
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (keys(duty_tax_info)) USING 'StorageAttachedIndex';  --duty_tax_info_country_code
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (values(duty_tax_info)) USING 'StorageAttachedIndex'; --duty_tax_info_duty_tax_number
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (tax_info__codice_fiscale) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (aggregations__ed_aggr_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__billing_cycle) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__billing_medium) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__currency_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__electronic_bill_payment_plan_flag_cd) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__international_billing_cycle) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__international_billing_medium) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__settlement_level_indicator) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (invoice_preference__direct_debit_indicator) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (credit_detail__credit_status) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (credit_detail__credit_status_reason_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (credit_detail__cash_only_reason) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (credit_detail__credit_rating) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (account_receivables__payment_type) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (geographic_info__alpha_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_<ENV_LEVEL_ID>_ks.cust_acct_v1 (geographic_info__station_number) USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (contact_type_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (contact_business_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (contact_document_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (share_id) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (additional_email_info__email) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (company_name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (person__first_name) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (person__last_name) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (person__middle_name) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (address__street_line) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (address__additional_line1) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (address__geo_political_subdivision2) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (address__geo_political_subdivision3) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (address__postal_code) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (email) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (address__country_code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_account_contact_<ENV_LEVEL_ID>_ks.account_contact (VALUES(name_line)) USING 'StorageAttachedIndex' WITH OPTIONS = {'case_sensitive': 'false'};

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_comment_<ENV_LEVEL_ID>_ks.comment_v1 (opco) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_comment_<ENV_LEVEL_ID>_ks.comment_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_comment_<ENV_LEVEL_ID>_ks.comment_v1 (comment__comment_id) USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (opco) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (group_id__code) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (group_id__number) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (group_id_detail__requester) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (group_id_detail__name) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (group_id_detail__master_account) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (effective_date_time) USING 'StorageAttachedIndex';
CREATE CUSTOM INDEX IF NOT EXISTS ON cam_group_<ENV_LEVEL_ID>_ks.group_info_v1 (expiration_date_time) USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON cam_line_of_business_<ENV_LEVEL_ID>_ks.line_of_business_v1 (last_update_tmstp) USING 'StorageAttachedIndex';
