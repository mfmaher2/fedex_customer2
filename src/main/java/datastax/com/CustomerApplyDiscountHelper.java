package datastax.com;

public class CustomerApplyDiscountHelper {

    static String accountParam = "__acount";
    static String opocParam = "__opco";

    static String fullQuery = "select * from apply_discount_detail_v1 \n" +
            "where \n" +
            "    account_number = '000001236' and " +
            "    solr_query = " +
            "    '{" +
            "        \"q\": \"opco:FX && " +
            "              apply_discount__discount_flag:true && " +
            "              apply_discount__effective_date_time:[2018-11-01T00:00:00.001Z TO *] && " +
            "              apply_discount__expiration_date_time:[* TO 2019-01-01T00:00:00.001Z]\"," +
            "        \"sort\": \"apply_discount__effective_date_time desc\"}';";

//    static String solrQuery =
//            "    '{" +
//            "        \"q\": \"opco:" + opocParam + " && " +
//            "              apply_discount__discount_flag:true && " +
//            "              apply_discount__effective_date_time:[2018-11-01T00:00:00.001Z TO *] && " +
//            "              apply_discount__expiration_date_time:[* TO 2019-01-01T00:00:00.001Z]\"," +
//            "        \"sort\": \"apply_discount__effective_date_time desc\"}'";

    static String solrQuery =
            "    {" +
                    "        \"q\": \"opco:" + opocParam + " && " +
                    "              apply_discount__discount_flag:true && " +
                    "              apply_discount__effective_date_time:[2018-11-01T00:00:00.001Z TO *] && " +
                    "              apply_discount__expiration_date_time:[* TO 2019-01-01T00:00:00.001Z]\"," +
                    "        \"sort\": \"apply_discount__effective_date_time desc\"}";

    static String constructSearchQuery(String opco){
        return solrQuery.replace(opocParam, opco);
    }

}
