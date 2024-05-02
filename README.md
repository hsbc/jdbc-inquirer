# jdbc-inquirer

The **jdbc-inquirer** checks connectivity via JDBC. When setting up environments, one would like to know if the connectivity to a database works.

Especially in complex network environments a simple `curl` or `wget` or `telnet` does not cut it or if one is working with a [distroless image](https://github.com/GoogleContainerTools/distroless).

The app got written overnight as the team we working were struggling to prove that an app from a Kubernetes cluster could do a database (which was sitting in a different network zone).

Furthermore, as we were testing, we found different jdbc providers behaved very slightly differently, which meant our code had to deal with idiosyncrasies.  

The **jdbc-inquirer **could also be used to perform health checks.


## TL;DR

```shell
#Grab jdbc-inquirer jar
curl -O --url 'https://repo1.maven.org/maven2/com/hsbc/engineering/jdbc-inquirer/1.0.15/jdbc-inquirer-1.0.15.jar'


#Grab a driver jar. An example - hive jdbc
curl -O --url 'https://repo1.maven.org/maven2/org/apache/hive/hive-jdbc/3.1.3/hive-jdbc-3.1.3-standalone.jar'

#Test connection
java -cp "./*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" com.hsbc.engineering.JDBCInquirer

```

## Obtaining the jar

* Obtain the jar from [maven repo](https://repo1.maven.org/maven2/com/hsbc/engineering/jdbc-inquirer/1.0.1/jdbc-inquirer-1.0.1.jar)

OR
* set the following in your `pom.xml`

```xml
...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>3.5.0</version>
        <executions>
          <execution>
            <id>copy</id>
            <phase>validate</phase>
            <goals>
              <goal>copy</goal>
            </goals>

            <configuration>
              <artifactItems>
                <!-- Obtain the jdbc inquirer-->
                <artifactItem>
                  <groupId>com.hsbc.engineering</groupId>
                  <artifactId>jdbc-inquirer</artifactId>
                  <version>1.0.1</version>
                  <classifier>standalone</classifier>
                  <type>jar</type>
                  <overWrite>true</overWrite>
                  <outputDirectory>${project.build.directory}/</outputDirectory>
                </artifactItem>
                <!-- Obtain a jdbc driver, an example below is hive -->
                <artifactItem>
                  <groupId>org.apache.hive</groupId>
                  <artifactId>hive-jdbc</artifactId>
                  <version>3.1.3</version>
                  <classifier>standalone</classifier>
                  <type>jar</type>
                  <overWrite>true</overWrite>
                  <outputDirectory>${project.build.directory}/</outputDirectory>
                </artifactItem>
              </artifactItems>
              <overWriteReleases>true</overWriteReleases>
              <overWriteSnapshots>true</overWriteSnapshots>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

## To use

```shell
#Using system properties
java -cp "target/*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" com.hsbc.engineering.JDBCInquirer
java -cp "target/*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" -DJDBC_SQL="show databases" -DRUN_PERFORMANCE_EXTRACTION_TEST='true' com.hsbc.engineering.JDBCInquirer

#Launch the program
java -cp [directory that contains this jar and the jdbc jars] com.hsbc.engineering.JDBCInquirer
```
<br>

```shell

#Using environment variables
export JDBC_CLASS_NAME='jdbc_class_name'
export JDBC_URL='jdbc_url'
export JDBC_USER='jdbc_user'                 #optional
export JDBC_PASSWORD='jdbc_password'         #optional
export JDBC_SQL='jdbc_sql'                   #optional
export RUN_PERFORMANCE_EXTRACTION_TEST=true  #optional

#Launch the program
java -cp [directory that contains this jar and the jdbc jars] com.hsbc.engineering.JDBCInquirer
```

## Building the package

* Clone the repo
* Run `./mvnw package`

## TODO
* Docker container for this
* Add JDBC specs as test
