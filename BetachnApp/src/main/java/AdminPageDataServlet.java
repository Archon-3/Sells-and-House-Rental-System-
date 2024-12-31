import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/AdminPageDataServlet")
public class AdminPageDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
      
        PrintWriter out = response.getWriter();
        StringBuilder jsonResponse = new StringBuilder("{");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/betoch", "root", "abenezer33")) {
            // Count all houses
            String allHousesQuery = "SELECT COUNT(*) AS count FROM houses";
            int allHousesCount = getCount(connection, allHousesQuery);
            jsonResponse.append("\"allHouses\":").append(allHousesCount).append(",");

            // Count houses for rent
            String rentQuery = "SELECT COUNT(*) AS count FROM houses WHERE `condition` = 'rent'";
            int rentCount = getCount(connection, rentQuery);
            jsonResponse.append("\"rent\":").append(rentCount).append(",");

            // Count houses for sell
            String sellQuery = "SELECT COUNT(*) AS count FROM houses WHERE `condition` = 'sell'";
            int sellCount = getCount(connection, sellQuery);
            jsonResponse.append("\"sell\":").append(sellCount).append(",");

            // Count available houses
            String availableQuery = "SELECT COUNT(*) AS count FROM houses WHERE status = 'available'";
            int availableCount = getCount(connection, availableQuery);
            jsonResponse.append("\"available\":").append(availableCount).append(",");

            // Count unavailable houses
            String unavailableQuery = "SELECT COUNT(*) AS count FROM houses WHERE status = 'unavailable'";
            int unavailableCount = getCount(connection, unavailableQuery);
            jsonResponse.append("\"unavailable\":").append(unavailableCount).append(",");

            // Count all users
            String usersQuery = "SELECT COUNT(*) AS count FROM users";
            int usersCount = getCount(connection, usersQuery);
            jsonResponse.append("\"users\":").append(usersCount);

        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.append("\"error\":\"Database error: ").append(e.getMessage()).append("\"");
        }

        // Remove the last comma if there were no errors, ensuring valid JSON
        if (jsonResponse.charAt(jsonResponse.length() - 1) == ',') {
            jsonResponse.deleteCharAt(jsonResponse.length() - 1);
        }

        jsonResponse.append("}");

   
        out.print(jsonResponse.toString());
    }

    private int getCount(Connection connection, String query) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        }
        return 0;
    }
}
