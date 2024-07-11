/**
 * Unit tests for MonetDB result set encoding.
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for the ResultSetMonetHelper class.
 * @author Leon Helwerda
 */
public class ResultSetMonetHelperTest {
    private Connection connection;
    private ResultSetMonetHelper helper;
    
    public ResultSetMonetHelperTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "monetdb");
        connectionProps.put("password", "monetdb");
        String url = "jdbc:monetdb://localhost:50000/gros_test";

        try {
            connection = DriverManager.getConnection(url, connectionProps);
        } catch (SQLException ex) {
            connection = null;
        }
        
        helper = new ResultSetMonetHelper();
    }
    
    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * Test the getColumnValues method.
     * @param key Column key
     * @throws SQLException If the query fails
     * @throws IOException If the helper fails
     */
    @ParameterizedTest
    @ValueSource(strings={"TEST-42", "TEST-43"})
    public void testGetColumnValues(String key) throws SQLException, IOException {
        assumeTrue(connection != null, "Connection available");
        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM gros.test WHERE key = '" + key + "'")
        ) {
            assertTrue(rs.next());
            // 1-indexed
            assertEquals(key, rs.getString(10));
            String[] columns = helper.getColumnValues(rs, false, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss.SSS000");
            assertEquals(11, columns.length);
            assertEquals(key, columns[9]);
            assertFalse(rs.next());
        }
    }
}
