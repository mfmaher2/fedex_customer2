package datastax.com.cam_analytics.DataElements

import datastax.com.cam_analytics.JsonHelper
import org.apache.spark.sql.cassandra.DataFrameWriterWrapper
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{DataFrame, SaveMode}

case class AccountBalanceResults(accountID: String, accountBalance:Float)

object DataHelpers {

  def writeDataFrame(df: DataFrame, keyspace: String, table: String): Unit = {
    df.write.cassandraFormat(table, keyspace)
      .mode(SaveMode.Append)
      .save();
  }

  /*
Important to execute the API call on spark worker(s), not the driver
If execute on the driver will not take advantage of Spark's parallel processing effectively.
Online reference:
https://medium.com/geekculture/how-to-execute-a-rest-api-call-on-apache-spark-the-right-way-in-python-4367f2740e78

Create UDF for executing REST API call, will be executed on worker(s) as intended
 */
  val executeRestApi = (accountID: Int) => {
    val receivedData = scala.io.Source.fromURL("http://localhost:8080/accountBalance?accountNum=" + accountID.toString).mkString
    val apiReturn = JsonHelper.fromJSON[AccountBalanceResults](receivedData)
    apiReturn.accountBalance.toString
  }

  val executeRestApiUDF = udf(executeRestApi, StringType)
}
