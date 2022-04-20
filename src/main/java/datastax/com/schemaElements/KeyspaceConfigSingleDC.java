package datastax.com.schemaElements;

public class KeyspaceConfigSingleDC extends KeyspaceConfig{

    String dcName;

    public KeyspaceConfigSingleDC(String datacenterName){
        super();
        this.dcName = datacenterName;

        AssignKeyspaceNames();
        AssignDataCenterMappings();
    }

    @Override
    protected void AssignKeyspaceNames(){
        keyspaceNames.put(Keyspaces.ACCOUNT_KS, "cam_account_l1_ks");
        keyspaceNames.put(Keyspaces.ACCOUNT_CONTACT_KS, "cam_account_contact_l1_ks");
        keyspaceNames.put(Keyspaces.ASSOC_ACCOUNT_KS, "cam_assoc_account_l1_ks");
        keyspaceNames.put(Keyspaces.APPLY_DISCOUNT_KS, "cam_apply_discount_l1_ks");
        keyspaceNames.put(Keyspaces.LINE_OF_BUSINESS_KS, "cam_line_of_business_l1_ks");
        keyspaceNames.put(Keyspaces.COMMENT_KS, "cam_comment_l1_ks");
        keyspaceNames.put(Keyspaces.GROUP_KS, "cam_group_l1_ks");
        keyspaceNames.put(Keyspaces.AUDIT_HISTORY_KS, "cam_audit_history_l1_ks");
        keyspaceNames.put(Keyspaces.TIME_EVENT_KS, "cam_time_event_l1_ks");
        keyspaceNames.put(Keyspaces.CENTRALIZED_VIEW_KS, "cam_centralized_view_l1_ks");
        keyspaceNames.put(Keyspaces.PAYMENT_INFO_KS, "cam_payment_info_l1_ks");
        keyspaceNames.put(Keyspaces.DYNAMIC_PROFILE_KS, "cam_dynamic_profile_l1_ks");
        keyspaceNames.put(Keyspaces.SEARCH_KS, "cam_search_l1_ks");
        keyspaceNames.put(Keyspaces.SEQUENCE_KS, "cam_sequence_l1_ks");

        //archive specific keyspace
        keyspaceNames.put(Keyspaces.ACCOUNT_ARCHIVE_KS, "cam_account_archive_l1_ks");
        keyspaceNames.put(Keyspaces.ACCOUNT_CONTACT_ARCHIVE_KS, "cam_account_contact_archive_l1_ks");
        keyspaceNames.put(Keyspaces.ASSOC_ACCOUNT_ARCHIVE_KS, "cam_assoc_account_archive_l1_ks");
        keyspaceNames.put(Keyspaces.APPLY_DISCOUNT_ARCHIVE_KS, "cam_apply_discount_archive_l1_ks");
        keyspaceNames.put(Keyspaces.LINE_OF_BUSINESS_ARCHIVE_KS, "cam_line_of_business_archive_l1_ks");
        keyspaceNames.put(Keyspaces.CENTRALIZED_VIEW_ARCHIVE_KS, "cam_centralized_view_archive_l1_ks");
        keyspaceNames.put(Keyspaces.PAYMENT_INFO_ARCHIVE_KS, "cam_payment_info_l1_archive_ks");

        //test only keyspace(s)
        keyspaceNames.put(Keyspaces.CUSTOMER, "customer_test");
    }

    @Override
    protected void AssignDataCenterMappings(){
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_CONTACT_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.ASSOC_ACCOUNT_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.APPLY_DISCOUNT_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.LINE_OF_BUSINESS_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.COMMENT_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.GROUP_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.AUDIT_HISTORY_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.TIME_EVENT_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.CENTRALIZED_VIEW_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.PAYMENT_INFO_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.DYNAMIC_PROFILE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.SEARCH_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.SEQUENCE_KS, dcName, 1);

        //archive specific keyspace
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_ARCHIVE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_CONTACT_ARCHIVE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.ASSOC_ACCOUNT_ARCHIVE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.APPLY_DISCOUNT_ARCHIVE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.LINE_OF_BUSINESS_ARCHIVE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.CENTRALIZED_VIEW_ARCHIVE_KS, dcName, 1);
        AddKeyspaceDataCenter(Keyspaces.PAYMENT_INFO_ARCHIVE_KS, dcName, 1);

        //test only keyspace(s)
        AddKeyspaceDataCenter(Keyspaces.CUSTOMER, dcName, 1);
    }
}
