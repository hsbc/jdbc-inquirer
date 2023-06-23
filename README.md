# jdbc-inquirer

The jdbc-inquirer checks connectivity via JDBC. When setting up environments, one would like to know if the connectivity to a database works.

Especially in complex network environments a simple `curl` or `wget` or `telnet` does not cut it or if one is working with a distroless image.

The app got written overnight as the team we working were struggling to prove that an app from a Kubernetes cluster could do a database (which was sitting in a different network zone).

Furthermore, as we were testing, we found different jdbc providers behaved very slightly differently, which meant our code had to deal with idiosyncrasies.  

The jdbc-inquirer could also be used to perform health checks.

## To use

```shell
#Using system properties
java -cp "target/*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" com.hsbc.engineering.JDBCInquirer
java -cp "target/*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" -DJDBC_SQL="show databases" -DRUN_PERFORMANCE_EXTRACTION_TEST='true' com.hsbc.engineering.JDBCInquirer

#Using environment variables
export JDBC_CLASS_NAME='jdbc_class_name'
export JDBC_URL='jdbc_url'
export JDBC_USER='jdbc_user'                 #optional
export JDBC_PASSWORD='jdbc_password'         #optional
export JDBC_SQL='jdbc_sql'                   #optional
export RUN_PERFORMANCE_EXTRACTION_TEST=true  #optional

java -cp [directory that contains this jar and the jdbc jars] com.hsbc.engineering.JDBCInquirer
```
## To build

Building: 
* the package - `mvn package`
* Docker image - `mvn jib:dockerBuild`

## TODO
* Docker container for this
* Add JDBC specs as test
