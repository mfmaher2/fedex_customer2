package datastax.com.cam_analytics

import datastax.com.cam_analytics.DataElements.AccountBalanceResults
import datastax.com.cam_analytics.DataElements.DataHelpers.writeDataFrame
import datastax.com.cam_analytics.DataElements.DataHelpers.executeRestApiUDF
import org.apache.spark.sql.cassandra.DataFrameReaderWrapper
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.types.{FloatType, StringType, StructField, StructType}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers


class CAMAnalyticsTest extends AnyFunSuite with Matchers with SparkTestSession {

  final val TEST_KEYSPACE = "cam_analytics_module_test_ks"

  override def beforeAll(): Unit = {
    super.beforeAll()
    DataCreator.createSchema(TEST_KEYSPACE)
  }

  override def afterAll(): Unit = {
//    DataCreator.dropSchema(TEST_KEYSPACE)
    super.afterAll()
  }

  val restApiSchema = StructType(
      Array(
        StructField( "accountID", StringType, true),
        StructField( "accountBalance", FloatType, true)
      )
    )




  test("verify successful API call ") {
    //running API verification test
    val baseURL = "http://localhost:8080/accountBalance?accountNum="
    val testAcctNum = "abc123"

    val received = scala.io.Source.fromURL(baseURL + testAcctNum).mkString
    println("Fetched from WebService:")
    println("\t" + received)

    //parse results as JSON
    val serviceCallData  = JsonHelper.fromJSON[AccountBalanceResults](received)
    println("Returned data")
    println("\t" + serviceCallData.accountID)
    println("\t" + serviceCallData.accountBalance)

    //account number passed as parameter to API should be included in returned data
    assert(serviceCallData.accountID.equals(testAcctNum))
  }

  test("Verify DataFrame operations with test schema and data") {

    println("Running Verify DataFrame operations test")

    val rowCount = 20;
    DataCreator.writeCoreData(TEST_KEYSPACE, rowCount)

    val df =spark.read
      .cassandraFormat(keyspace = TEST_KEYSPACE, table = DataCreator.BASE_TABLE)
      .load()

    assert(df.count() == rowCount)

    println("Base dataframe")
    df.collect().foreach(println)

    val dfExt = df.withColumn("s", executeRestApiUDF(col("k")))
    writeDataFrame(dfExt, TEST_KEYSPACE, DataCreator.EXT_TABLE)

    println("Read dataframe with Account Balance")
    val dfReadAcctBal =spark.read
      .cassandraFormat(keyspace = TEST_KEYSPACE, table = DataCreator.EXT_TABLE)
      .load()
    dfReadAcctBal.collect().foreach(println)

    //known values expected form API call for specified account IDs
    val expectedAccountBalances = Map(
      "1" -> 0.0f,
      "2" -> 0.0f,
      "3" -> 0.0f,
      "4" -> 44.0f,
      "5" -> 550.0f,
      "6" -> 600.0f,
      "7" -> 0.0f,
      "8" -> 8080.0f,
      "9" -> 9.0f,
      "10" -> 101.0f,
      "11" -> 0.0f,
      "12" -> 112.0f
    )

    for((acctNum, acctBal) <- expectedAccountBalances){
      assert(dfReadAcctBal.filter(col("k") === acctNum).select("s").first().getString(0).equals(acctBal.toString));
    }
  }
}
