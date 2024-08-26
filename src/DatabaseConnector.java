import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseConnector {
    static Connection conn = null;
    static String url = "jdbc:oracle:thin:@localhost:1521:orcl";
    static String user = "system";
    static String pwd = "orcl";

    // to start connection
    public static Connection getConnection() {
        try {
            conn = DriverManager.getConnection(url, user, pwd);
            if (conn != null) return conn;
            else throw new RuntimeException("Error connecting to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // to close connection
    public static void closeConnection(Connection c) throws SQLException {
        try{
            if(c!=null && !c.isClosed()){
                c.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}