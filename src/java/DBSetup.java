import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 */
public class DBSetup {
    private Boolean creationError(String tableName, SQLException exception) {
        System.out.printf("The table \"%s\" could not be created, check the output console.\n", tableName);
        exception.printStackTrace();
        return false;
    }
    
    // Attempt to create the specified table
    private Boolean createTable(Connection connection, String tableName, String sqlStatement) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlStatement);
            statement.close();
            System.out.printf("\"%s\" table successfully created!", tableName);
            return true;
        } catch (SQLException e) {
            return creationError(tableName, e);
        }
    }
    
    
    public void createTables() {
        DBConnect dbConnect = new DBConnect();
        Connection connection = dbConnect.getConnection();
        Statement statement = null;
        
        // Get metadata to see which tables to create
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Check if tables already exist before creating them
            ResultSet existingTables = metaData.getTables(null, null, "User", null);
            if (existingTables.next()) {
                String userSql = "CREATE TABLE User (" +
                    "login TEXT PRIMARY KEY NOT NULL," +
                    "password TEXT NOT NULL," +
                    "firstName TEXT NOT NULL," +
                    "lastName TEXT NOT NULL," +
                    "role VARCHAR(8) NOT NULL," +
                    "CONSTRAINT check_allowed CHECK (role in (‘admin’, ‘employee’, ‘customer’))" +
                    ");";
                
                // User table creation error
                if (!createTable(connection, "User", userSql)) {
                    return;
                }
            }
        
            existingTables = metaData.getTables(null, null, "CustomerInfo", null);
            if (existingTables.next()) {
                String customerInfoSql = "CREATE TABLE CustomerInfo (" +
                "customerLogin TEXT NOT NULL," +
                "cardNumber TEXT NOT NULL," +
                "cardExpiration DATE NOT NULL," +
                "email TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "FOREIGN KEY(customerLogin) REFERENCES User(login)" +
                ");" ;
                
                // User table creation error
                if (!createTable(connection, "CustomerInfo", customerInfoSql)) {
                    return;
                }
            } 
            
            System.out.println("All tables have been successfully created");
            connection.close();
        } catch (SQLException e) {
            System.out.println("CUnable to gather Connection MetaData! Check output console");
            e.printStackTrace();
            return;
        }
    }
    
}
