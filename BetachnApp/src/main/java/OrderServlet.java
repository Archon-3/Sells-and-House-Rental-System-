import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/betoch";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "abenezer33";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Get the session object
    	HttpSession session = request.getSession(false);  // Use false to not create a new session if one doesn't exist
    	String phoneNum = null;
    	if (session != null) {
    	    // Retrieve the phone number from the session
    	    phoneNum = (String) session.getAttribute("phoneNumber");
    	}
    	
    	
    	String condition = phoneNum;
    	
        if (condition == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid condition");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Start the HTML structure for the house list
        out.println("<section class='house-list'>");

        // Database connection
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/betoch", "root", "abenezer33")) {
            String query = "SELECT id, description, location, price, `condition`, phoneNumber, houseImage FROM houses WHERE `phoneNumber` = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, condition);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    // Iterate over results and display each house as a card
                    while (resultSet.next()) {
                    	String house_id = resultSet.getString("id");
                        String description = resultSet.getString("description");
                        String location = resultSet.getString("location");
                        String price = resultSet.getString("price");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String houseImage = resultSet.getString("houseImage"); // Full image URL

                        out.println("<div class='house-card' data-id='" + house_id + "' data-price='" + price + "' data-location='" + location + "'>");
                        out.println("<img src='" + houseImage + "' alt='House Image'>");
                        out.println("<p>Description: " + description + "</p>");
                        out.println("<p>Price: " + price + " ETB</p>");
                        out.println("<p>Location: " + location + "</p>");
                        out.println("<p>The Owner's Phone: <a href='tel:" + phoneNumber + "'>" + phoneNumber + "</a></p>");
                        out.println("<button class='delist-button' onclick='delistHouse(" + house_id + ")'>Delist</button>"); // Add delist button
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
