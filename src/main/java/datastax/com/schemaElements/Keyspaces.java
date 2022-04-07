package datastax.com.schemaElements;

public enum Keyspaces {

    ACCOUNT_KS("cam_account_l1_ks"),
    ACCOUNT_CONTACT_KS("cam_account_contact_l1_ks"),
    ASSOC_ACCOUNT_KS("cam_assoc_account_l1_ks"),
    APPLY_DISCOUNT_KS("cam_apply_discount_l1_ks"),
    LINE_OF_BUSINESS_KS("cam_line_of_business_l1_ks"),
    COMMENT_KS("cam_comment_l1_ks"),
    GROUP_KS("cam_group_l1_ks"),
    AUDIT_HISTORY_KS("cam_audit_history_l1_ks"),
    TIME_EVENT_KS("cam_time_event_l1_ks"),
    CENTRALIZED_VIEW_KS("cam_centralized_view_l1_ks"),
    PAYMENT_INFO_KS("cam_payment_info_l1_ks"),
    DYNAMIC_PROFILE_KS("cam_dynamic_profile_l1_ks"),
    SEARCH_KS("cam_search_l1_ks"),
    SEQUENCE_KS("cam_sequence_l1_ks"),

    //archive specific keyspace
    ACCOUNT_ARCHIVE_KS("cam_account_archive_l1_ks"),
    ACCOUNT_CONTACT_ARCHIVE_KS("cam_account_contact_archive_l1_ks"),
    ASSOC_ACCOUNT_ARCHIVE_KS("cam_assoc_account_archive_l1_ks"),
    APPLY_DISCOUNT_ARCHIVE_KS("cam_apply_discount_archive_l1_ks"),
    LINE_OF_BUSINESS_ARCHIVE_KS("cam_line_of_business_archive_l1_ks"),
    CENTRALIZED_VIEW_ARCHIVE_KS("cam_centralized_view_archive_l1_ks"),
    PAYMENT_INFO_ARCHIVE_KS("cam_payment_info_l1_archive_ks"),

    //test only keyspace(s)
    CUSTOMER("customer_test");

    private String keyspaceName;
    Keyspaces(String ksName) { this.keyspaceName = ksName;}
    public String keyspaceName() { return keyspaceName;}

    //todo - create mechanism to define keyspace relication settings per datacenter
}
