import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;


public class ConnectionManager {
    private static String url = "@sqlUrl";  //Url de la DB
    private static String driverName = "@sqlUrl";  //Driver a Usar
    private static String username = "@user";  // User de la DB 
    private static String password = "@password"; // Password de la DB
    private static Connection con;
    private static String urlstring;

    public static Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(urlstring, username, password);
            } catch (SQLException ex) {
            		System.out.println("Failed to create The DB connection");
            		//Log.error("Failed to create The DB connection");
            }
        } catch (ClassNotFoundException ex) {
        		System.out.println("Failed to create The DB connection");
        		//	Log.error("Driver not Defined");
        }
        return con;
    }
}