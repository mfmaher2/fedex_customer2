USE customer;
CREATE SEARCH INDEX IF NOT EXISTS ON cust_acct_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON account_contact;
CREATE SEARCH INDEX IF NOT EXISTS ON payment_info_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON assoc_accounts_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON national_account_v1;
CREATE SEARCH INDEX IF NOT EXISTS ON apply_discount_detail_v1;


--Temporary search index for test functionality
CREATE SEARCH INDEX IF NOT EXISTS ON contact;
