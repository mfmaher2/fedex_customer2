package datastax.com.schemaElements;

public enum Keyspace {
    ACCOUNT_KS,
    ACCOUNT_CONTACT_KS,
    ASSOC_ACCOUNT_KS,
    APPLY_DISCOUNT_KS,
    LINE_OF_BUSINESS_KS,
    COMMENT_KS,
    GROUP_KS,
    AUDIT_HISTORY_KS,
    TIME_EVENT_KS,
    CENTRALIZED_VIEW_KS,
    PAYMENT_INFO_KS,
    DYNAMIC_PROFILE_KS,
    SEARCH_KS,
    SEQUENCE_KS,
    CAM_OPERATIONS,

    //archive specific keyspace
    ACCOUNT_ARCHIVE_KS,
    ACCOUNT_CONTACT_ARCHIVE_KS,
    ASSOC_ACCOUNT_ARCHIVE_KS,
    APPLY_DISCOUNT_ARCHIVE_KS,
    LINE_OF_BUSINESS_ARCHIVE_KS,
    CENTRALIZED_VIEW_ARCHIVE_KS,
    PAYMENT_INFO_ARCHIVE_KS,

    //test only keyspace(s)
    CUSTOMER
}
