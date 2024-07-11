/**
 * MonetDB result set encoding.
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
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package databasedumper;

import com.opencsv.ResultSetHelperService;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Helper to adjust how database query results are encoded to CSV format.
 * @author leonhelwerda
 */
public class ResultSetMonetHelper extends ResultSetHelperService {
    public ResultSetMonetHelper() {
        this.setDateFormat("yyyy-MM-dd");
        this.setDateTimeFormat("yyyy-MM-dd HH:mm:ss.SSS000");        
    }
    
    @Override
    public String[] getColumnValues(ResultSet rs, boolean trim, String dateFormatString, String timeFormatString)
            throws SQLException, IOException {
        ResultSetMetaData metadata = rs.getMetaData();
        String[] valueArray = new String[metadata.getColumnCount()];
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
           valueArray[i-1] = getColumnValue(rs, metadata.getColumnType(i), i,
                 trim, dateFormatString, timeFormatString);
        }
        return valueArray;   
    }
    
    private  String getColumnValue(ResultSet rs, int colType, int colIndex, boolean trim, String dateFormatString, String timestampFormatString)
         throws SQLException {

        String value = "";

        switch (colType) {
           case Types.BIT:
           case Types.JAVA_OBJECT:
              value = Objects.toString(rs.getObject(colIndex), "");
              break;
           case Types.BOOLEAN:
              value = Objects.toString(rs.getBoolean(colIndex));
              break;
           case Types.BIGINT:
              value = Objects.toString(rs.getLong(colIndex));
              break;
           case Types.DECIMAL:
           case Types.REAL:
           case Types.NUMERIC:
              value = Objects.toString(rs.getBigDecimal(colIndex), "");
              break;
           case Types.DOUBLE:
              value = Objects.toString(rs.getDouble(colIndex));
              break;
           case Types.FLOAT:
              value = Objects.toString(rs.getFloat(colIndex));
              break;
           case Types.INTEGER:
           case Types.TINYINT:
           case Types.SMALLINT:
              value = Objects.toString(rs.getInt(colIndex));
              break;
           case Types.DATE:
              java.sql.Date date = rs.getDate(colIndex);
              if (date != null) {
                 SimpleDateFormat df = new SimpleDateFormat(dateFormatString);
                 value = df.format(date);
              }
              break;
           case Types.TIME:
              value = Objects.toString(rs.getTime(colIndex), "");
              break;
           case Types.TIMESTAMP:
              value = handleTimestamp(rs.getTimestamp(colIndex), timestampFormatString);
              break;
           case Types.NVARCHAR: // todo : use rs.getNString
           case Types.NCHAR: // todo : use rs.getNString
           case Types.LONGNVARCHAR: // todo : use rs.getNString
           case Types.LONGVARCHAR:
           case Types.VARCHAR:
           case Types.CHAR:
           case Types.NCLOB: // todo : use rs.getNClob
           case Types.CLOB:
              String columnValue = rs.getString(colIndex);
              if (trim && columnValue != null) {
                 value = columnValue.trim();
              } else {
                 value = columnValue;
              }
              break;
           default:
              value = "";
        }

        // Make NULL values distinguishable
        // The monetdb-import script import_tables.sh recognizes these
        if (rs.wasNull() || value == null) {
            value = "\7NUL\7";
        }

        return value;
    }
    
}
