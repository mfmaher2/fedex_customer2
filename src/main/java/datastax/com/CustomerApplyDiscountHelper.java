package datastax.com;

import java.time.Instant;

public class CustomerApplyDiscountHelper {

    static String opocParam = "__opco";
    static String discountFlag = "__discoungFlag";
    static String effectiveDT = "__effectiveDT";
    static String expirationDT = "__expireDT";

    static String defaultParam = "*";

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
                    "              apply_discount__discount_flag:" + /*true*/ discountFlag + " && " +
                    "              apply_discount__effective_date_time:[" /*2018-11-01T00:00:00.001Z*/ + effectiveDT + " TO *] && " +
                    "              apply_discount__expiration_date_time:[* TO " /*2019-01-01T00:00:00.001Z*/+ expirationDT + " ]\"," +
                    "        \"sort\": \"apply_discount__effective_date_time desc\"}";


    static String constructSearchQuery(String opco){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, defaultParam);
        parameterQuery=  parameterQuery.replace(effectiveDT, defaultParam);
        parameterQuery=  parameterQuery.replace(expirationDT, defaultParam);

        return parameterQuery;

//        return solrQuery.replace(opocParam, opco);
    }

    static String constructSearchQuery(String opco, Boolean applyDiscount){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, applyDiscount.toString());
        parameterQuery=  parameterQuery.replace(effectiveDT, defaultParam);
        parameterQuery=  parameterQuery.replace(expirationDT, defaultParam);

        return parameterQuery;
//        return solrQuery.replace(opocParam, opco);
    }

    static String constructSearchQuery(String opco, Instant effectiveDateTime){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, defaultParam);
        parameterQuery=  parameterQuery.replace(effectiveDT, effectiveDateTime.toString());
        parameterQuery=  parameterQuery.replace(expirationDT, defaultParam);

        return parameterQuery;
    }

    static String constructSearchQuery(String opco, Boolean applyDiscount, Instant effectiveDateTime, Instant expirationDateTime){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, applyDiscount.toString());
        parameterQuery=  parameterQuery.replace(effectiveDT, effectiveDateTime.toString());
        parameterQuery=  parameterQuery.replace(expirationDT, expirationDateTime.toString());

        return parameterQuery;
    }

}
