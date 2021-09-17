package datastax.com.dataObjects;

import java.time.Instant;

public class ApplyDiscountHelper {

    static String opocParam = "__opco";
    static String discountFlag = "__discoungFlag";
    static String effectiveDT = "__effectiveDT";
    static String expirationDT = "__expireDT";

    static String defaultParam = "*";

    static String solrQuery =
            "    {" +
                    "        \"q\": \"opco:" + opocParam + " && " +
                    "              apply_discount__discount_flag:" + /*true*/ discountFlag + " && " +
                    "              apply_discount__effective_date_time:[" /*2018-11-01T00:00:00.001Z*/ + effectiveDT + " TO *] && " +
                    "              apply_discount__expiration_date_time:[* TO " /*2019-01-01T00:00:00.001Z*/+ expirationDT + " ]\"," +
                    "        \"sort\": \"apply_discount__effective_date_time desc\"}";


    static public String constructSearchQuery(String opco){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, defaultParam);
        parameterQuery=  parameterQuery.replace(effectiveDT, defaultParam);
        parameterQuery=  parameterQuery.replace(expirationDT, defaultParam);

        return parameterQuery;
    }

    static public String constructSearchQuery(String opco, Boolean applyDiscount){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, applyDiscount.toString());
        parameterQuery=  parameterQuery.replace(effectiveDT, defaultParam);
        parameterQuery=  parameterQuery.replace(expirationDT, defaultParam);

        return parameterQuery;
    }

    static public String constructSearchQuery(String opco, Instant effectiveDateTime){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, defaultParam);
        parameterQuery=  parameterQuery.replace(effectiveDT, effectiveDateTime.toString());
        parameterQuery=  parameterQuery.replace(expirationDT, defaultParam);

        return parameterQuery;
    }

    static public String constructSearchQuery(String opco, Boolean applyDiscount, Instant effectiveDateTime, Instant expirationDateTime){
        String parameterQuery;

        parameterQuery = solrQuery.replace(opocParam, opco);
        parameterQuery=  parameterQuery.replace(discountFlag, applyDiscount.toString());
        parameterQuery=  parameterQuery.replace(effectiveDT, effectiveDateTime.toString());
        parameterQuery=  parameterQuery.replace(expirationDT, expirationDateTime.toString());

        return parameterQuery;
    }
}
