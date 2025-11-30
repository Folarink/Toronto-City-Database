import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SQLDatabaseConnection {
    // Connect to Uranium database and import SQL files
    public static void main(String[] args) {
        String masterConnectionUrl =
                "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                        + "database=master;"
                        + "user=folarink;"
                        + "password=7966826;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";

        String dataConnectionUrl =
                "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                        + "database=Toronto data;"
                        + "user=folarink;"
                        + "password=7966826;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";

        String sqlPath = "c:\\Users\\Khadijat\\OneDrive - University of Manitoba\\Documents\\COMP 3000 LEVEL\\COMP 3380\\Group Project\\Present Cleaned Data\\SQL FILE";
        
        String[] sqlFiles = {
            "amenities.sql",
            "neighbourhoods.sql",
            "hosts.sql",
            "listings.sql",
            "listings_have_amenities.sql",
            "reviews.sql",
            "guests.sql",
            "crimes.sql",
            "criminals.sql",
            "attractions.sql",
            "police_stations.sql",
            "guest_book_listings.sql",
            "guest_visit_attractions.sql"
        };

        try {
            // First, try to create the database if it doesn't exist
            System.out.println("Attempting to create 'Toronto data' database...");
            try (Connection masterConnection = DriverManager.getConnection(masterConnectionUrl)) {
                try (Statement masterStatement = masterConnection.createStatement()) {
                    masterStatement.execute("IF NOT EXISTS(SELECT * FROM sys.databases WHERE name='Toronto data') CREATE DATABASE [Toronto data]");
                    System.out.println("✓ Database 'Toronto data' is ready!");
                }
            } catch (SQLException e) {
                System.out.println("Database note: " + e.getMessage());
            }

            // Now connect to the Toronto data database and import files
            System.out.println("\nConnecting to 'Toronto data' database to import files...");
            try (Connection connection = DriverManager.getConnection(dataConnectionUrl)) {
                System.out.println("✓ Connected to Toronto data database successfully!");
                
                try (Statement statement = connection.createStatement()) {
                    for (String sqlFile : sqlFiles) {
                        String filePath = sqlPath + "\\" + sqlFile;
                        System.out.println("\nImporting " + sqlFile + "...");
                        
                        try {
                            String sql = new String(Files.readAllBytes(Paths.get(filePath)));
                            statement.execute(sql);
                            System.out.println("✓ " + sqlFile + " imported successfully!");
                        } catch (Exception e) {
                            System.err.println("✗ Error importing " + sqlFile + ": " + e.getMessage());
                        }
                    }
                }
                System.out.println("\n✓ All files imported!");
            }
            
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error: " + e.getMessage());
        }
    }
}
