package datastax.com.cam_analytics

import com.datastax.spark.connector._
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.cassandra._

/**
 * Hello world!
 *
 */
object CAMAnalyticsApp extends App {
  println( "Hello World from CAM Analytics Module!" )
  val spark = SparkSession.builder
    .appName("CAM Analytics Module v0.1")
    .enableHiveSupport()
    .getOrCreate()

  import spark.implicits._

  val defaultTableSize = 10;

  DataCreator.createSchema(DataCreator.DEFAULT_KEYSPACE)
  DataCreator.writeCoreData(DataCreator.DEFAULT_KEYSPACE, defaultTableSize)

  // Read data as RDD
  val rdd = spark.sparkContext
    .cassandraTable(keyspace = DataCreator.DEFAULT_KEYSPACE, table = DataCreator.BASE_TABLE)

  println("Core data read as RDD")
  rdd.collect()
    .foreach(println)

  // Read data as DataSet (DataFrame)
  val dataset = spark.read
    .cassandraFormat(keyspace = DataCreator.DEFAULT_KEYSPACE, table = DataCreator.BASE_TABLE)
    .load()

  println("Core data read as DataSet (DataFrame)")
  dataset.collect()
    .foreach(println)

  //Add column to core data and write to separate table
  DataCreator.writeExtendedData(DataCreator.DEFAULT_KEYSPACE, dataset)

  println("Read extended table as DataSet (DataFrame)")
  val datasetExt = spark.read
    .cassandraFormat(keyspace = DataCreator.DEFAULT_KEYSPACE, table = DataCreator.EXT_TABLE)
    .load()

  datasetExt.collect()
    .foreach(println)

  spark.stop()
  sys.exit(0)
}
