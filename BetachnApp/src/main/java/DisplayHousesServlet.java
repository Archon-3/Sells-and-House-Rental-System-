import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DisplayHousesServlet")
public class DisplayHousesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String condition = request.getParameter("condition");
        if (condition == null || (!condition.equals("rent") && !condition.equals("sell"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid condition");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Start the HTML structure for the house list
        out.println("<section class='house-list'>");

        // Database connection
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/betoch", "root", "abenezer33")) {
            String query = "SELECT description, location, price, `condition`, phoneNumber, houseImage, status FROM houses WHERE `condition` = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, condition);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    // Iterate over results and display each house as a card
                    while (resultSet.next()) {
                        String description = resultSet.getString("description");
                        String location = resultSet.getString("location");
                        String price = resultSet.getString("price");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String houseImage = resultSet.getString("houseImage"); // Full image URL
                        String status = resultSet.getString("status");
                        
                        out.println("<div class='house-card-container'>");
                        out.println("<div class='state'>" + status + "</div>");
                        out.println("<div class='house-card' data-price='" + price + "' data-location='" + location + "'>");
                        out.println("<img src='" + houseImage + "' alt='House Image'>");
                        out.println("<p>Description: " + description + "</p>");
                        out.println("<p>Price: " + price + " ETB</p>");
                        out.println("<p>Location: " + location + "</p>");
                        out.println("<p>The Owner's Phone: <a href='tel:" + phoneNumber + "'>" + phoneNumber + "</a></p>");
                        out.println("</div>");
                        out.println("</div>");

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Database error: " + e.getMessage() + "</p>");
        }

        // Close HTML structure
        out.println("</section>");
    }
}
