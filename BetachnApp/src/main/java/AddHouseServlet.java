import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AddHouseServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get session and retrieve phone number
        HttpSession session = request.getSession();
        String phone = (String) session.getAttribute("phoneNumber");

        if (phone == null) {
            response.getWriter().println("User not logged in. Please log in first.");
            return;
        }

        // Get form data
        String description = request.getParameter("description");
        String location = request.getParameter("location");
        String price = request.getParameter("price");
        String condition = request.getParameter("condition");
        
        Part houseImage = request.getPart("houseImage");
        String imagePath = null;
        
        if (houseImage != null) {
            String fileName = houseImage.getSubmittedFileName();
            String uploadDir = getServletContext().getRealPath("/") + "images";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // Create directory if it doesn't exist
            }
            imagePath = "images/" + fileName;
            File file = new File(uploadDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                houseImage.getInputStream().transferTo(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establish database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/betoch", "yourUsername", "yourPassword");

            // Insert data into the database
            String sql = "INSERT INTO Houses (description, location, price, condition, phoneNumber, houseImage) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, description);
            preparedStatement.setString(2, location);
            preparedStatement.setString(3, price);
            preparedStatement.setString(4, condition);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, imagePath);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                // Append house details to the appropriate HTML file
                String htmlFilePath = getServletContext().getRealPath("/") + (condition.equals("rent") ? "forRent.html" : "forBuy.html");
                try (PrintWriter writer = new PrintWriter(new FileWriter(htmlFilePath, true))) {
                    writer.println(createHouseCardHTML(imagePath, description, price, location, phone));
                }

                // Redirect to the appropriate page
                response.sendRedirect(condition.equals("rent") ? "forRent.html" : "forBuy.html");
            } else {
                response.getWriter().println("Failed to add house. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String createHouseCardHTML(String imagePath, String description, String price, String location, String phone) {
        return String.format(
            "<div class=\"house-card\" data-price=\"%s\" data-location=\"%s\">\n" +
            "  <img src=\"%s\" alt=\"House Image\" class=\"house-image\">\n" +
            "  <p>Description: %s</p>\n" +
            "  <p>Price: %s ETB</p>\n" +
            "  <p>Location: %s</p>\n" +
            "  <p>Owner's Phone: <a href=\"tel:%s\">%s</a></p>\n" +
            "</div>",
            price, location, imagePath, description, price, location, phone, phone
        );
    }
}
