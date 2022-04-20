package datastax.com.schemaElements;

import java.util.HashMap;
import java.util.Map;

public class KeyspaceConfigMultiDC extends KeyspaceConfig{
    Map<DataCenter, String> datacenters = new HashMap<>();

    public KeyspaceConfigMultiDC(String coreDC, String edgeDC, String searchDC){
        super();
        this.datacenters.put(DataCenter.CORE, coreDC);
        this.datacenters.put(DataCenter.EDGE, edgeDC);
        this.datacenters.put(DataCenter.SEARCH, searchDC);

        AssignKeyspaceNames();
        AssignDataCenterMappings();
    }
    @Override
    protected void AssignKeyspaceNames(){
        keyspaceNames.put(Keyspaces.ACCOUNT_KS, "cam_account_l4_ks");
        keyspaceNames.put(Keyspaces.ACCOUNT_CONTACT_KS, "cam_account_contact_l4_ks");
        keyspaceNames.put(Keyspaces.ASSOC_ACCOUNT_KS, "cam_assoc_account_l4_ks");
        keyspaceNames.put(Keyspaces.APPLY_DISCOUNT_KS, "cam_apply_discount_l4_ks");
        keyspaceNames.put(Keyspaces.LINE_OF_BUSINESS_KS, "cam_line_of_business_l4_ks");
        keyspaceNames.put(Keyspaces.COMMENT_KS, "cam_comment_l4_ks");
        keyspaceNames.put(Keyspaces.GROUP_KS, "cam_group_l4_ks");
        keyspaceNames.put(Keyspaces.AUDIT_HISTORY_KS, "cam_audit_history_l4_ks");
        keyspaceNames.put(Keyspaces.TIME_EVENT_KS, "cam_time_event_l4_ks");
        keyspaceNames.put(Keyspaces.CENTRALIZED_VIEW_KS, "cam_centralized_view_l4_ks");
        keyspaceNames.put(Keyspaces.PAYMENT_INFO_KS, "cam_payment_info_l4_ks");
        keyspaceNames.put(Keyspaces.DYNAMIC_PROFILE_KS, "cam_dynamic_profile_l4_ks");
        keyspaceNames.put(Keyspaces.SEARCH_KS, "cam_search_l4_ks");
        keyspaceNames.put(Keyspaces.SEQUENCE_KS, "cam_sequence_l4_ks");

        //archive specific keyspace
        keyspaceNames.put(Keyspaces.ACCOUNT_ARCHIVE_KS, "cam_account_archive_l4_ks");
        keyspaceNames.put(Keyspaces.ACCOUNT_CONTACT_ARCHIVE_KS, "cam_account_contact_archive_l4_ks");
        keyspaceNames.put(Keyspaces.ASSOC_ACCOUNT_ARCHIVE_KS, "cam_assoc_account_archive_l4_ks");
        keyspaceNames.put(Keyspaces.APPLY_DISCOUNT_ARCHIVE_KS, "cam_apply_discount_archive_l4_ks");
        keyspaceNames.put(Keyspaces.LINE_OF_BUSINESS_ARCHIVE_KS, "cam_line_of_business_archive_l4_ks");
        keyspaceNames.put(Keyspaces.CENTRALIZED_VIEW_ARCHIVE_KS, "cam_centralized_view_archive_l4_ks");
        keyspaceNames.put(Keyspaces.PAYMENT_INFO_ARCHIVE_KS, "cam_payment_info_l4_archive_ks");

        //test only keyspace(s)
        keyspaceNames.put(Keyspaces.CUSTOMER, "customer_test");
    }

    @Override
    protected void AssignDataCenterMappings(){
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_CONTACT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.ASSOC_ACCOUNT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.APPLY_DISCOUNT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.LINE_OF_BUSINESS_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.COMMENT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.GROUP_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.AUDIT_HISTORY_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.TIME_EVENT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.CENTRALIZED_VIEW_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.PAYMENT_INFO_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.SEQUENCE_KS, datacenters.get(DataCenter.CORE), 1);

        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_CONTACT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspaces.ASSOC_ACCOUNT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspaces.APPLY_DISCOUNT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspaces.LINE_OF_BUSINESS_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspaces.PAYMENT_INFO_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspaces.DYNAMIC_PROFILE_KS, datacenters.get(DataCenter.EDGE), 1);

        AddKeyspaceDataCenter(Keyspaces.SEARCH_KS, datacenters.get(DataCenter.SEARCH), 1);

        //archive specific keyspace
        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_CONTACT_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.ASSOC_ACCOUNT_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.APPLY_DISCOUNT_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.LINE_OF_BUSINESS_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.CENTRALIZED_VIEW_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspaces.PAYMENT_INFO_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);

        AddKeyspaceDataCenter(Keyspaces.ACCOUNT_ARCHIVE_KS, datacenters.get(DataCenter.EDGE), 1);

        //test only keyspace(s)
        AddKeyspaceDataCenter(Keyspaces.CUSTOMER, datacenters.get(DataCenter.CORE), 1);
    }
}
