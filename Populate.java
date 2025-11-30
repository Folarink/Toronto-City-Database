import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Populate {
    private String connectionUrl;
    private int totalTables = 13;
    private int completedTables = 0;

    public static void main(String[] args) {
        Populate populate = new Populate();
        populate.loadConfigAndPopulate();
    }

    public void loadConfigAndPopulate() {
        Properties prop = new Properties();
        String fileName = "auth.cfg";

        try {
            FileInputStream configFile = new FileInputStream(fileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config file.");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error reading config file.");
            System.exit(1);
        }

        String username = prop.getProperty("username");
        String password = prop.getProperty("password");

        if (username == null || password == null) {
            System.out.println("Username or password not provided.");
            System.exit(1);
        }

        connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + username + ";"
                + "password=" + password + ";"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";

        System.out.println("===========================================");
        System.out.println("Tables are being populated, might take a while...");
        System.out.println("===========================================\n");

        // Populate in order respecting foreign key dependencies
        // executeSQL("neighbourhoods", "sql_files/neighbourhoods.sql");
        // executeSQL("hosts", "sql_files/hosts.sql");
        // executeSQL("guests", "sql_files/guests.sql");
        // executeSQL("amenities", "sql_files/amenities.sql");
        // executeSQL("attractions", "sql_files/attractions.sql");
        // executeSQL("police_stations", "sql_files/police_stations.sql");
        // executeSQL("criminals", "sql_files/criminals.sql");
        // executeSQL("listings", "sql_files/listings.sql");
        executeSQL("reviews", "sql_files/reviews.sql");
        // executeSQL("crimes", "sql_files/crimes.sql");
        // executeSQL("listings_have_amenities", "sql_files/listings_have_amenities.sql");
        // executeSQL("guest_visit_attractions", "sql_files/guest_visit_attractions.sql");
        // executeSQL("guest_book_listings", "sql_files/guest_book_listings.sql");

        System.out.println("\n===========================================");
        System.out.println("ALL TABLES POPULATED SUCCESSFULLY!");
        System.out.println("===========================================");
    }

    private void updateProgress(String tableName) {
        completedTables++;
        System.out.println("✓ " + tableName + " table populated successfully. [" + completedTables + "/" + totalTables + "]");
    }

    /**
     * Executes SQL INSERT statements from a file
     * Handles batch execution for better performance
     */
    public void executeSQL(String tableName, String sqlFile) {
        System.out.println("Still populating " + tableName + " table...");
        
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(sqlFile));
             Statement statement = connection.createStatement()) {

            String line;
            StringBuilder sqlBuilder = new StringBuilder();
            int batchCount = 0;
            int totalInserts = 0;

            while ((line = br.readLine()) != null) {
                // Skip empty lines and comments
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                    continue;
                }

                // Add line to current SQL statement
                sqlBuilder.append(line).append(" ");

                // Check if statement is complete (ends with semicolon)
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    
                    // Remove the trailing semicolon
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }

                    // Only execute INSERT statements
                    if (sql.toUpperCase().startsWith("INSERT")) {
                        statement.addBatch(sql);
                        batchCount++;
                        totalInserts++;

                        // Execute batch every 1000 statements for better performance
                        if (batchCount >= 1000) {
                            statement.executeBatch();
                            statement.clearBatch();
                            batchCount = 0;
                            System.out.println("  ... " + totalInserts + " rows processed");
                        }
                    }

                    // Reset for next statement
                    sqlBuilder = new StringBuilder();
                }
            }

            // Execute any remaining statements in the batch
            if (batchCount > 0) {
                statement.executeBatch();
                System.out.println("  ... " + totalInserts + " rows processed");
            }

            updateProgress(tableName);

        } catch (SQLException e) {
            System.out.println("SQL Error in " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("SQL file not found: " + sqlFile);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading SQL file: " + sqlFile);
            e.printStackTrace();
        }
    }
    /**
     * Alternative method for executing entire SQL file at once (faster but less feedback)
     * Use this if your SQL files are well-formed and you don't need progress updates
     */
    public void executeSQLFileDirect(String tableName, String sqlFile) {
        System.out.println("Still populating " + tableName + " table...");
        
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            // Read entire file
            while ((line = br.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }

            String allSQL = sqlBuilder.toString();
            
            // Split by semicolons and execute each statement
            String[] statements = allSQL.split(";");
            
            try (Statement statement = connection.createStatement()) {
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty() && !sql.startsWith("--") && sql.toUpperCase().startsWith("INSERT")) {
                        statement.execute(sql);
                    }
                }
            }

            updateProgress(tableName);

        } catch (SQLException e) {
            System.out.println("SQL Error in " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("SQL file not found: " + sqlFile);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading SQL file: " + sqlFile);
            e.printStackTrace();
        }
    }
}