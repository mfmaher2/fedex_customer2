package datastax.com.cam_analytics

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, Suite}

trait SparkTestSession extends BeforeAndAfterAll{
  self: Suite =>
  @transient private var _sparkSession: SparkSession = _
  protected def config: SparkConf = {
    // Additional spark properties, http://spark.apache.org/docs/latest/configuration.html,
    def appName = { "spark-CAM-testing-" + this.getClass.getName }
    def appID: String = { appName + math.floor(math.random * 100).toLong.toHexString }

    new SparkConf()
      .setMaster("local[2]")
      .setAppName(appName)
      .set("spark.ui.enabled", "false")
      .set("spark.app.id", appID)
      .set("spark.driver.host", "localhost")
      .set("spark.sql.shuffle.partitions", "1")
      .set("spark.default.parallelism", "1")
      .set("spark.driver.memory", "2g")
  }

  @transient val spark: SparkSession = {
    if (_sparkSession != null) {
      _sparkSession
    } else {
      _sparkSession = SparkSession.builder().config(config).getOrCreate()
      _sparkSession
    }
  }

  @transient val cassandraConnector: CassandraConnector = {
    CassandraConnector(_sparkSession.sparkContext)
  }

//  @transient val sc: SparkContext = {
//    if (_sparkSession != null) {
//      _sparkSession.sparkContext
//    } else {
//      null
//    }
//  }

  override def beforeAll(): Unit = {
    val s = spark //initialize the SparkSession
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    try {
      if(_sparkSession != null) {
        stop(_sparkSession.sparkContext);
        _sparkSession.stop();
        _sparkSession=null;}
    } finally {
      super.afterAll()
    }
  }

  def stop(sc: SparkContext) {
    Option(sc).foreach { context =>
      context.stop()
    }
  }
}
