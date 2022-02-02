package datastax.com.schemaElements;

public enum Keyspaces {

    ACCOUNT_KS("account_ks"),
    ACCOUNT_CONTACT_KS("account_contact_ks"),
    ASSOC_ACCOUNT_KS("assoc_account_ks"),
    APPLY_DISCOUNT_KS("apply_discount_ks"),
    LINE_OF_BUSINESS_KS("line_of_business_ks"),
    COMMENT_KS("comment_ks"),
    GROUP_KS("group_ks"),
    AUDIT_HISTORY_KS("audit_history_ks"),
    TIME_EVENT_KS("time_event_ks"),
    CENTRALIZED_VIEW_KS("centralized_view_ks"),
    PAYMENT_INFO_KS("payment_info_ks"),
    DYNAMIC_PROFILE_KS("dynamic_profile_ks"),
    SEARCH_KS("search_ks"),
    CUSTOMER("customer_test");

    private String keyspaceName;
    Keyspaces(String ksName) { this.keyspaceName = ksName;}
    public String keyspaceName() { return keyspaceName;}
}
