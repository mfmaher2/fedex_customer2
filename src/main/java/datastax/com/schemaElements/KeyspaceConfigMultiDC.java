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

        init();
    }
    @Override
    protected void AssignKeyspaceNames(){
        keyspaceNames.put(Keyspace.ACCOUNT_KS, "cam_account_ks");
        keyspaceNames.put(Keyspace.ACCOUNT_CONTACT_KS, "cam_account_contact_ks");
        keyspaceNames.put(Keyspace.ASSOC_ACCOUNT_KS, "cam_assoc_account_ks");
        keyspaceNames.put(Keyspace.APPLY_DISCOUNT_KS, "cam_apply_discount_ks");
        keyspaceNames.put(Keyspace.LINE_OF_BUSINESS_KS, "cam_line_of_business_ks");
        keyspaceNames.put(Keyspace.COMMENT_KS, "cam_comment_ks");
        keyspaceNames.put(Keyspace.GROUP_KS, "cam_group_ks");
        keyspaceNames.put(Keyspace.AUDIT_HISTORY_KS, "cam_audit_history_ks");
        keyspaceNames.put(Keyspace.TIME_EVENT_KS, "cam_time_event_ks");
        keyspaceNames.put(Keyspace.CENTRALIZED_VIEW_KS, "cam_centralized_view_ks");
        keyspaceNames.put(Keyspace.PAYMENT_INFO_KS, "cam_payment_info_ks");
        keyspaceNames.put(Keyspace.DYNAMIC_PROFILE_KS, "cam_dynamic_profile_ks");
        keyspaceNames.put(Keyspace.SEARCH_KS, "cam_search_ks");
        keyspaceNames.put(Keyspace.SEQUENCE_KS, "cam_sequence_ks");

        //archive specific keyspace
        keyspaceNames.put(Keyspace.ACCOUNT_ARCHIVE_KS, "cam_account_archive_ks");
        keyspaceNames.put(Keyspace.ACCOUNT_CONTACT_ARCHIVE_KS, "cam_account_contact_archive_ks");
        keyspaceNames.put(Keyspace.ASSOC_ACCOUNT_ARCHIVE_KS, "cam_assoc_account_archive_ks");
        keyspaceNames.put(Keyspace.APPLY_DISCOUNT_ARCHIVE_KS, "cam_apply_discount_archive_ks");
        keyspaceNames.put(Keyspace.LINE_OF_BUSINESS_ARCHIVE_KS, "cam_line_of_business_archive_ks");
        keyspaceNames.put(Keyspace.CENTRALIZED_VIEW_ARCHIVE_KS, "cam_centralized_view_archive_ks");
        keyspaceNames.put(Keyspace.PAYMENT_INFO_ARCHIVE_KS, "cam_payment_info_archive_ks");
        keyspaceNames.put(Keyspace.SYSTEM_OPERATIONS_KS, "system_operations_ks");

        //test only keyspace(s)
        keyspaceNames.put(Keyspace.CUSTOMER, "customer_test");
    }

    @Override
    protected void AssignDataCenterMappings(){
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_CONTACT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.ASSOC_ACCOUNT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.APPLY_DISCOUNT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.LINE_OF_BUSINESS_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.COMMENT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.GROUP_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.AUDIT_HISTORY_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.TIME_EVENT_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.CENTRALIZED_VIEW_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.PAYMENT_INFO_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.SEQUENCE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.SEARCH_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.SYSTEM_OPERATIONS_KS, datacenters.get(DataCenter.CORE), 1);

        AddKeyspaceDataCenter(Keyspace.ACCOUNT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_CONTACT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspace.ASSOC_ACCOUNT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspace.APPLY_DISCOUNT_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspace.LINE_OF_BUSINESS_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspace.PAYMENT_INFO_KS, datacenters.get(DataCenter.EDGE), 1);
        AddKeyspaceDataCenter(Keyspace.DYNAMIC_PROFILE_KS, datacenters.get(DataCenter.EDGE), 1);

        AddKeyspaceDataCenter(Keyspace.SEARCH_KS, datacenters.get(DataCenter.SEARCH), 1);
        AddKeyspaceDataCenter(Keyspace.SYSTEM_OPERATIONS_KS, datacenters.get(DataCenter.SEARCH), 1);

        //archive specific keyspace
        AddKeyspaceDataCenter(Keyspace.ACCOUNT_CONTACT_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.ASSOC_ACCOUNT_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.APPLY_DISCOUNT_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.LINE_OF_BUSINESS_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.CENTRALIZED_VIEW_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.PAYMENT_INFO_ARCHIVE_KS, datacenters.get(DataCenter.CORE), 1);

        AddKeyspaceDataCenter(Keyspace.ACCOUNT_ARCHIVE_KS, datacenters.get(DataCenter.EDGE), 1);

        //test only keyspace(s)
        AddKeyspaceDataCenter(Keyspace.CUSTOMER, datacenters.get(DataCenter.CORE), 1);
        AddKeyspaceDataCenter(Keyspace.CUSTOMER, datacenters.get(DataCenter.SEARCH), 1);
    }
}
