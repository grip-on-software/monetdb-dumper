# MonetDB dumper

This repository contains a Java application which can interact with a MonetDB
database containing a Grip on Software database in order to export a CSV 
representation of a table that can be imported again using scripts in the 
`monetdb-import` repository.

The application is mostly useful for exchange of encrypted data (it can filter 
out unencrypted data from the table before exporting) or for backups.

## Installation: Requirements and configuration

The dumper application has been tested with OpenJDK 8. In order to build the 
application, we use Ant 1.10.1+.

Before building, ensure you have create a file in the path 
`nbproject/private/config.properties` containing the following properties:

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
`ant -buildfile build.xml -propertyfile nbproject/private/config.properties`

The application is then made available in `dist/databasedumper.jar`.

## Running

Run the application as follows: `java -Ddatabasedumper.encrypted=true -jar 
dist/databasedumper.jar TABLE OUTPUT.csv.gz`. Skip the `-D` define if you want 
to export unencrypted personal data as well. Replace the `TABLE` and `OUTPUT` 
with the table name and output file path, respectively.
