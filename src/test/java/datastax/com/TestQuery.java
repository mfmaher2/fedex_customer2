package datastax.com;

public class TestQuery {

    enum QueryType {
        INSERT_READ_PROP,
        INSERT_CHECK_COUNT,
        READ_PROP,
        CHECK_COUNT
    }
    public String queryID;
    public String query;
    public int expectedReturnCount;

    public String table;
    public String recordIDProp;
    public String recordIDval;
    public String checkProp;
    public String expectedResult;

    public QueryType queryType;

    public boolean numericKey;

    public TestQuery(String ID, String query, int count){
        this.queryID = ID;
        this.query = query;
        this.expectedReturnCount = count;

        queryType = QueryType.INSERT_CHECK_COUNT;
        numericKey = false;
    }
    public TestQuery(String ID, String query, String table, String recID, String recIDVal, String propVerify, String valVerify){
        this.queryID = ID;
        this.query = query;
        this.table = table;
        this.recordIDProp = recID;
        this.recordIDval = recIDVal;
        this.checkProp = propVerify;
        this.expectedResult = valVerify;

        queryType = QueryType.INSERT_READ_PROP;
        numericKey = false;
    }

    public TestQuery(String ID, String table, String recID, String recIDVal, String propVerify, String valVerify){
        this.queryID = ID;
        this.table = table;
        this.recordIDProp = recID;
        this.recordIDval = recIDVal;
        this.checkProp = propVerify;
        this.expectedResult = valVerify;

        queryType = QueryType.READ_PROP;
        numericKey = false;
    }

    //todo consolidate constuctors
    public TestQuery(String ID, String table, String recID, String recIDVal, String propVerify, String valVerify, boolean useNumericKey){
        this.queryID = ID;
        this.table = table;
        this.recordIDProp = recID;
        this.recordIDval = recIDVal;
        this.checkProp = propVerify;
        this.expectedResult = valVerify;

        queryType = QueryType.READ_PROP;
        numericKey = useNumericKey;
    }

}
