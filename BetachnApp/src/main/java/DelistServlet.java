import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DelistServlet")
public class DelistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get the houseId from the request
        String houseId = request.getParameter("houseId");
        
        // Get the session object
        HttpSession session = request.getSession(false);  // Use false to not create a new session if one doesn't exist
        String phoneNum = null;
        
        if (session != null) {
            // Retrieve the phone number from the session
            phoneNum = (String) session.getAttribute("phoneNumber");
        }

        if (houseId == null || phoneNum == null) {
            response.getWriter().write("failure");
            return;
        }

        // Database connection
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/betoch", "root", "abenezer33")) {
            // Query to update the house status to 'Unavailable' based on both phoneNumber and houseId
            String query = "UPDATE houses SET `status` = 'Unavailable' WHERE phoneNumber = ? AND id = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, phoneNum);  // Set the phone number from session
                preparedStatement.setString(2, houseId);  // Set the houseId from the request

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    // Successful delist
                    response.getWriter().write("success");
                } else {
                    // No house updated, possibly invalid houseId or phoneNumber
                    response.getWriter().write("failure");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("error");
        }
    }
}
