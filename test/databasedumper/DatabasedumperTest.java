/**
 * Unit tests for application entry point.
 * 
 * Copyright 2015 Bytecode Pty Ltd.
 * Copyright 2017-2020 ICTU
 * Copyright 2017-2022 Leiden University
 * Copyright 2017-2024 Leon Helwerda
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package databasedumper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Databasedumper class.
 * @author Leon Helwerda
 */
public class DatabasedumperTest {
    private Properties props;
    
    public DatabasedumperTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        props = System.getProperties();
        System.setProperty("databasedumper.url", "jdbc:monetdb://localhost/gros_test");
        System.setProperty("databasedumper.user", "monetdb");
        System.setProperty("databasedumper.password", "monetdb");
    }
    
    @AfterEach
    public void tearDown() {
        System.setProperties(props);
    }

    /**
     * Test the getConnection method of Databasedumper.
     * @throws SQLException If the connection fails
     */
    @Test
    public void testGetConnection() throws SQLException {
        ResourceBundle bundle = ResourceBundle.getBundle("databasedumper.database");
        Connection conn = Databasedumper.getConnection(bundle);
        assertEquals("monetdb", conn.getClientInfo("user"));
        assertEquals("monetdb", conn.getClientInfo("password"));
    }
    
    @Test
    public void testMain() throws FileNotFoundException, IOException {
        Exception exception = assertThrows(DumperException.class, () -> {
            Databasedumper.main(new String[]{"--help"});
        });
        assertTrue(exception.getMessage().startsWith("\nUsage:"));
        assertTrue(exception.getMessage().contains("databasedumper.jar"));
        
        Databasedumper.main(new String[]{"test", "build/test/test.csv.gz"});
        File output = new File("build/test/test.csv.gz");
        assertTrue(output.exists());
        try (
            FileInputStream input = new FileInputStream(output);
            GZIPInputStream gzip = new GZIPInputStream(input);
            Reader reader = new InputStreamReader(gzip, "UTF-8");
            BufferedReader buffer = new BufferedReader(reader)
        ) {
            assertEquals("false,12345,5.25,1.01,2.22,3,2024-07-10,12:00:00,2024-07-10 12:34:56.000000,TEST-42,0", buffer.readLine());
            assertEquals("true,6789,\7NUL\7,2.02,3.33,4,\7NUL\7,\7NUL\7,2024-07-10 13:57:00.000000,TEST-43,1", buffer.readLine());
            assertTrue(buffer.readLine() == null);
        }
        output.deleteOnExit();
        
        System.setProperty("databasedumper.encrypted", "1");
        Databasedumper.main(new String[]{"test", "build/test/encrypted.csv.gz"});
        File encrypted = new File("build/test/encrypted.csv.gz");
        assertTrue(encrypted.exists());
        try (
            FileInputStream input = new FileInputStream(encrypted);
            GZIPInputStream gzip = new GZIPInputStream(input);
            Reader reader = new InputStreamReader(gzip, "UTF-8");
            BufferedReader buffer = new BufferedReader(reader)
        ) {
            assertEquals("true,6789,\7NUL\7,2.02,3.33,4,\7NUL\7,\7NUL\7,2024-07-10 13:57:00.000000,TEST-43,1", buffer.readLine());
            assertTrue(buffer.readLine() == null);
        }
        encrypted.deleteOnExit();
    }
}
