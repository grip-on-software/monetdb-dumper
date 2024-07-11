# MonetDB dumper

This repository contains a Java application which can interact with a MonetDB
database containing a Grip on Software database in order to export a CSV 
representation of a table that can be imported again using scripts in the 
[monetdb-import](https://github.com/grip-on-software/monetdb-import) 
repository.

The application is mostly useful for exchange of encrypted data (it can filter 
out unencrypted data from the table before exporting) or for backups.

## Installation: Requirements and configuration

The dumper application has been tested with Semeru OpenJDK 21. In order to 
build the application, we use Ant 1.10.14+ with the JDK (a package with 
`javac`). Make sure your `JAVA_HOME` environment variable points to the correct 
JDK directory if you have multiple possible installations.

Before building, ensure you have create a file in the path 
`nbproject/private/config.properties`, possibly by copying the 
`config.properties.example` file to there and editing it, containing the 
following properties:

```
databasedumper.url=jdbc:monetdb://MONETDB_HOST/gros
databasedumper.user=MONETDB_USER
databasedumper.password=MONETDB_PASSWORD
databasedumper.schema=gros
```

Replace the values to properly connect to the MonetDB database. Take care to 
use a proper JDBC-MonetDB URL for the `databasedumper.url` property, including 
the correct port to connect to the database and the name of the database 
(`gros` by default).

Now run the following command in order to build the MonetDB dumper application: 
```
ant -buildfile build.xml -propertyfile nbproject/private/config.properties
```

The application is then made available in `dist/databasedumper.jar`.

## Running

Run the application as follows:

```
java -Ddatabasedumper.encrypted=true -jar dist/databasedumper.jar TABLE OUTPUT
```

Skip the `-D` define if you want to export unencrypted personal data as well. 
It is also possible to override some of the properties defined in the 
`config.properties` file included during the build using defines 
(`databasedumper.url`, `databasedumper.user` and `databasedumper.password`).
Replace the `TABLE` and `OUTPUT` with the table name and output file path, 
respectively. The output file will be a CSV file compressed with GZip, so 
typically the extension of the file is `.csv.gz`.

## Testing

Tests can be performed during the build using:

```
ant -buildfile build.xml -propertyfile nbproject/private/config.properties test
```

Note that the tests are essentially integration tests, which requires 
a database to be set up beforehand, otherwise it will skip or fail tests.

- A MonetDB database instance should be running on `localhost` on the default 
  port (`50000`).
- A database with the name `gros_test` should be created on the instance.
- The database should have a `gros` schema and a table called `test` with two 
  rows and columns of various types in it, specifically as can be imported from 
  the `create-test-data.sql` file.

Test output should indicate the successful, failed and skipped tests. Once the 
test is complete, test result and coverage information is made available in 
`build/test`, with JUnit XML files in `junit/junit.xml` in that directory and 
JaCoCo coverage XML in `jacoco.xml` and HTML reports in `jacoco/index.html`.

## License

The MonetDB importer is licensed under the Apache 2.0 License. Dependency 
libraries are included in object form (some libraries are only used in tests) 
and have the following licenses:

- CopyLibs: Part of NetBeans, distributed under Apache 2.0 License
- [commons-lang](https://github.com/apache/commons-lang): Apache 2.0 License
- [monetdb-jdbc](https://github.com/MonetDB/monetdb-java): MPL v2.0, available 
  from [MonetDB Java Download Area](https://www.monetdb.org/downloads/Java/)
- [opencsv](https://opencsv.sourceforge.net/): Apache 2.0 License

Test libraries:

- [apiguardian](https://github.com/apiguardian-team/apiguardian): Apache 2.0 
  License
- [jacoco](https://github.com/jacoco/jacoco) (agent and ant task): EPL v2.0
- [junit5](https://github.com/junit-team/junit5): EPL v2.0
- [opentest4j](https://github.com/ota4j-team/opentest4j): Apache 2.0 License
