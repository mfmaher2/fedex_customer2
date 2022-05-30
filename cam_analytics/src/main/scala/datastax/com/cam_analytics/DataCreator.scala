package datastax.com.cam_analytics

import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.toRDDFunctions
import org.apache.spark.sql.cassandra.DataFrameWriterWrapper
import org.apache.spark.sql.functions.{col, concat, lit}
import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

case class AccountBalanceResults(accountID: String, accountBalance:Float)

object DataCreator {

  final val DEFAULT_KEYSPACE = "ks"
  final val BASE_TABLE = "kv"
  final val EXT_TABLE = "ks_out"

  val spark = SparkSession.builder.getOrCreate()
  import spark.implicits._

  def createSchema(keyspace:String): Unit = {
    // Create keyspace and table
    CassandraConnector(spark.sparkContext).withSessionDo { session =>
      session.execute(
        """CREATE KEYSPACE IF NOT EXISTS """ + keyspace + """ WITH
          | replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }""".stripMargin)
      session.execute("""CREATE TABLE IF NOT EXISTS """ + keyspace + "." + BASE_TABLE + """ (k int, v int, PRIMARY KEY (k))""")
    }

    CassandraConnector(spark.sparkContext).withSessionDo { session =>
      session.execute("""CREATE TABLE IF NOT EXISTS """ + keyspace + "." + EXT_TABLE + """ (k int, v int, s text, PRIMARY KEY (k))""")
    }
  }

  def dropSchema(keyspace: String): Unit = {
    CassandraConnector(spark.sparkContext).withSessionDo { session =>
      session.execute("""DROP KEYSPACE IF EXISTS """ + keyspace)
    }
  }

  def writeCoreData(keyspace: String, size: Int): Unit = {
    // Write some data
    spark.range(1, (size+1))
      .map(x => (x, x))
      .rdd
      .saveToCassandra(keyspace, BASE_TABLE)
  }

  def writeExtendedData(keyspace: String, df: DataFrame): Unit ={
    println("Creating extended table with new column")
    writeDataFrame( df.withColumn("s", createExtColumn(col("k"))),
                    keyspace,
                    EXT_TABLE
                  )
//    df.withColumn("s", createExtColumn(col("k")))
//      .write.cassandraFormat(EXT_TABLE, keyspace)
//      .mode(SaveMode.Append)
//      .save();
  }

  def writeDataFrame(df: DataFrame, keyspace: String, table: String): Unit = {
   df.write.cassandraFormat(table, keyspace)
      .mode(SaveMode.Append)
      .save();
  }

  def addAccountBalanceColumn(df: DataFrame): DataFrame ={
    df.withColumn("s", createAccountBalanceCol(col("k")))
  }

  def createExtColumn(baseCol: Column): Column = {
    concat(lit("ExtVal-"), baseCol)
  }

  def createAccountBalanceCol(accountID: Column): Column= {
//    val urlAccountServiceBase = "http://localhost:8080/accountBalance?accountNum="
//    val urlFull = urlAccountServiceBase

    val received = scala.io.Source.fromURL("http://localhost:8080/accountBalance?accountNum=abc123").mkString
    println("Fetched from WebService:")
    println("\t" + received)

    //parse results as JSON
    val serviceCallData  = JsonHelper.fromJSON[AccountBalanceResults](received)
    println("Returned data")
    println("\t" + serviceCallData.accountID)
    println("\t" + serviceCallData.accountBalance)

    lit(serviceCallData.accountBalance.toString)
  }
//
//  def altAddAccountBalanceColumn(df: DataFrame): DataFrame = {
//    val dfExt = df.mapPartitions(part =>{
//      part.map(item =>{
//
//        Row.fromSeq(item.toSeq + lit("What"))
//      })
//
//    })
//
//    val newSchema = df.schema.add(StructField("s", StringType, true))
//    df.sqlContext.createDataFrame(dfExt, newSchema)
//  }
}

