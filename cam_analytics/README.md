## Test Execution

mvn clean && mvn package

dse spark-submit --class datastax.com.cam_analytics.CAMAnalyticsApp target/cam_analytics-1.0-SNAPSHOT.jar

*Note may need full path to .../bin/dse for spark-submit call

---
