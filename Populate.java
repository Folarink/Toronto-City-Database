import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Populate {
    private String connectionUrl;
    private static final int BATCH_SIZE = 1000;

    // ADD THIS MAIN METHOD BACK
    public static void main(String[] args) {
        new Populate().loadConfigAndPopulate();
    }

    public void loadConfigAndPopulate() {
        Properties prop = new Properties();
        try (FileInputStream configFile = new FileInputStream("auth.cfg")) {
            prop.load(configFile);
        } catch (IOException ex) {
            System.out.println("Error reading config file.");
            System.exit(1);
        }

        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;user=" + username + ";password=" + password
                + ";encrypt=false;trustServerCertificate=false;loginTimeout=30;";

        System.out.println("Starting database population...\n");

       // Populate tables in optimal order
        executeSQL("neighbourhoods", "sql_files/neighbourhoods.sql");
        executeSQL("hosts", "sql_files/hosts.sql");
        executeSQL("guests", "sql_files/guests.sql");
        executeSQL("amenities", "sql_files/amenities.sql");
        executeSQL("police_stations", "sql_files/police_stations.sql");
        executeSQL("attractions", "sql_files/attractions.sql");
        executeSQL("criminals", "sql_files/criminals.sql");
        executeSQL("listings", "sql_files/listings.sql");
        executeSQL("crimes", "sql_files/crimes.sql");
        executeSQL("reviews", "sql_files/reviews.sql");
        executeSQL("listings_have_amenities", "sql_files/listings_have_amenities.sql");
        executeSQL("guest_visit_attractions", "sql_files/guest_visit_attractions.sql");
        executeSQL("guest_book_listings", "sql_files/guest_book_listings.sql");

        System.out.println("\n Database population completed!");
    }

    private void executeSQL(String tableName, String sqlFile) {
        System.out.println("Processing " + tableName + " from " + sqlFile + "...");
        
        long startTime = System.currentTimeMillis();
        int totalRows = 0;
        int batchCount = 0;
        int errorCount = 0;

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {
            
            connection.setAutoCommit(false); // CRITICAL FOR PERFORMANCE!
            
            String line;
            StringBuilder sqlBuilder = new StringBuilder();
            Statement statement = connection.createStatement();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("--")) continue;
                
                sqlBuilder.append(line);
                
                // Check if we have a complete SQL statement
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    
                    // Remove the trailing semicolon for executeUpdate
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    
                    // Only process INSERT statements
                    if (sql.toUpperCase().startsWith("INSERT")) {
                        try {
                            statement.addBatch(sql);
                            batchCount++;
                            totalRows++;
                            
                            // Execute batch when we reach batch size
                            if (batchCount >= BATCH_SIZE) {
                                int[] results = statement.executeBatch();
                                statement.clearBatch();
                                batchCount = 0;
                                connection.commit();
                                
                                System.out.printf(" Batch executed: %,d rows total%n", totalRows);
                            }
                        } catch (SQLException e) {
                            errorCount++;
                            System.err.printf("Bad SQL (row %,d): %s%n", totalRows, e.getMessage());
                            // Clear the failed batch and continue
                            statement.clearBatch();
                            batchCount = 0;
                        }
                    }
                    
                    // Reset for next SQL statement
                    sqlBuilder = new StringBuilder();
                } else {
                    sqlBuilder.append(" "); // Add space between lines
                }
            }
            
            // Execute any remaining batch
            if (batchCount > 0) {
                try {
                    statement.executeBatch();
                    connection.commit();
                    System.out.printf("Final batch: %,d rows total%n", totalRows);
                } catch (SQLException e) {
                    System.err.println("Final batch failed: " + e.getMessage());
                    connection.rollback();
                }
            }
            
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            
            System.out.printf("%s: %,d rows, %,d errors, %.2f seconds (%.1f rows/sec)%n", 
                tableName, totalRows, errorCount, seconds, totalRows / seconds);
                
        } catch (Exception e) {
            System.err.println("Fatal error in " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}