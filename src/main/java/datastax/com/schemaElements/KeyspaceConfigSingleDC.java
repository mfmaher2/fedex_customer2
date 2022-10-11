package datastax.com.schemaElements;

public class KeyspaceConfigSingleDC extends KeyspaceConfig{

    String datacenterName;

    public KeyspaceConfigSingleDC(String dcName){
        super();
        this.datacenterName = dcName;

       init();
    }

    @Override
    protected void AssignKeyspaceNames(){
        keyspaceNames.put(Keyspace.ACCOUNT_KS, "cam_account_l1_ks");
        keyspaceNames.put(Keyspace.ACCOUNT_CONTACT_KS, "cam_account_contact_l1_ks");
        keyspaceNames.put(Keyspace.ASSOC_ACCOUNT_KS, "cam_assoc_account_l1_ks");
        keyspaceNames.put(Keyspace.APPLY_DISCOUNT_KS, "cam_apply_discount_l1_ks");
        keyspaceNames.put(Keyspace.LINE_OF_BUSINESS_KS, "cam_line_of_business_l1_ks");
        keyspaceNames.put(Keyspace.COMMENT_KS, "cam_comment_l1_ks");
        keyspaceNames.put(Keyspace.GROUP_KS, "cam_group_l1_ks");
        keyspaceNames.put(Keyspace.AUDIT_HISTORY_KS, "cam_audit_history_l1_ks");
        keyspaceNames.put(Keyspace.TIME_EVENT_KS, "cam_time_event_l1_ks");
        keyspaceNames.put(Keyspace.CENTRALIZED_VIEW_KS, "cam_centralized_view_l1_ks");
        keyspaceNames.put(Keyspace.PAYMENT_INFO_KS, "cam_payment_info_l1_ks");
        keyspaceNames.put(Keyspace.DYNAMIC_PROFILE_KS, "cam_dynamic_profile_l1_ks");
        keyspaceNames.put(Keyspace.SEARCH_KS, "cam_search_l1_ks");
        keyspaceNames.put(Keyspace.SEQUENCE_KS, "cam_sequence_l1_ks");
        keyspaceNames.put(Keyspace.CAM_OPERATIONS_KS, "cam_operations_l1_ks");

        //archive specific keyspace
        keyspaceNames.put(Keyspace.ACCOUNT_ARCHIVE_KS, "cam_account_archive_l1_ks");
        keyspaceNames.put(Keyspace.ACCOUNT_CONTACT_ARCHIVE_KS, "cam_account_contact_archive_l1_ks");
        keyspaceNames.put(Keyspace.ASSOC_ACCOUNT_ARCHIVE_KS, "cam_assoc_account_archive_l1_ks");
        keyspaceNames.put(Keyspace.APPLY_DISCOUNT_ARCHIVE_KS, "cam_apply_discount_archive_l1_ks");
        keyspaceNames.put(Keyspace.LINE_OF_BUSINESS_ARCHIVE_KS, "cam_line_of_business_archive_l1_ks");
        keyspaceNames.put(Keyspace.CENTRALIZED_VIEW_ARCHIVE_KS, "cam_centralized_view_archive_l1_ks");
        keyspaceNames.put(Keyspace.PAYMENT_INFO_ARCHIVE_KS, "cam_payment_info_l1_archive_ks");

        //test only keyspace(s)
        keyspaceNames.put(Keyspace.CUSTOMER, "customer_test");
    }

    @Override
    protected void AssignDataCenterMappings(){
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_CONTACT_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.ASSOC_ACCOUNT_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.APPLY_DISCOUNT_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.LINE_OF_BUSINESS_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.COMMENT_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.GROUP_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.AUDIT_HISTORY_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.TIME_EVENT_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.CENTRALIZED_VIEW_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.PAYMENT_INFO_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.DYNAMIC_PROFILE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.SEARCH_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.SEQUENCE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.CAM_OPERATIONS_KS, datacenterName, 1);

        //archive specific keyspace
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_ARCHIVE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_CONTACT_ARCHIVE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.ASSOC_ACCOUNT_ARCHIVE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.APPLY_DISCOUNT_ARCHIVE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.LINE_OF_BUSINESS_ARCHIVE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.CENTRALIZED_VIEW_ARCHIVE_KS, datacenterName, 1);
        AddKeyspaceDataCenter(Keyspace.PAYMENT_INFO_ARCHIVE_KS, datacenterName, 1);

        //test only keyspace(s)
        AddKeyspaceDataCenter(Keyspace.CUSTOMER, datacenterName, 1);
    }
}
