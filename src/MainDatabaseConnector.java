import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainDatabaseConnector {
    public static void main(String[] args) {
        try {
            // Attempt to establish a connection to the database
            Connection con = DatabaseConnector.getConnection();

            if (con != null) {
                System.out.println("Connected to the database!");

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM question"); // Execute query
                // Check if there is a row to move to
                if (rs.next()) {
                    String name = rs.getString("question_text"); // Retrieve name from db
                    System.out.println(name);
                } else {
                    System.out.println("No rows found in the result set.");
                }

                // Close the connection when done
                DatabaseConnector.closeConnection(con);
                System.out.println("Connection closed.");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
