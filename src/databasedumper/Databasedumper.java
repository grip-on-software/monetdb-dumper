/**
 * Application entry point.
 * 
 * Copyright 2015 Bytecode Pty Ltd.
 * Copyright 2017-2020 ICTU
 * Copyright 2017-2022 Leiden University
 * Copyright 2017-2023 Leon Helwerda
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package databasedumper;

import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Main entry point for the database dumper application.
 * @author Leon Helwerda
 */
public class Databasedumper {
    private final static String VERSION = "0.0.3";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String usage = "\nUsage: java -jar databasedumper.jar <table> <file>";
        if (args.length <= 1 || "--help".equals(args[0])) {
            throw new DumperException(usage + "\nMonetDB dumper " + VERSION);
        }
        ResourceBundle bundle = ResourceBundle.getBundle("databasedumper.database");
        
        String table = args[0];
        String fileName = args[1];
        String encrypted = System.getProperty("databasedumper.encrypted");
        try (
            FileOutputStream output = new FileOutputStream(fileName, false);
            GZIPOutputStream gzip = new GZIPOutputStream(output);
            Writer writer = new OutputStreamWriter(gzip, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',', '"', '\\', "\n");
            Connection conn = getConnection(bundle)
        ) {
            ResultSetHelper resultService = new ResultSetMonetHelper();
            csv.setResultService(resultService);
            
            String schema = bundle.getString("schema");
            String sql = "SELECT * FROM " + schema + "." + table;
            if (encrypted != null && !encrypted.isEmpty()) {
                try (ResultSet mrs = conn.getMetaData().getColumns("", schema, table, "encryption")) {
                    if (mrs.next()) {
                        sql += " WHERE encryption <> 0";
                    }
                }
            }
            Logger.getLogger(Databasedumper.class.getName()).log(Level.INFO, "Executing query {0}", sql);
            try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
            ) {
                csv.writeAll(rs, false, false, false);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Databasedumper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnection(ResourceBundle bundle) throws SQLException {
        String url = bundle.getString("url").trim();
        String user = bundle.getString("user").trim();
        String password = bundle.getString("password").trim();

        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);

        return DriverManager.getConnection(url, connectionProps);
    }

}
