import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)

@WebServlet("/AddHouseServlet")
public class AddHouseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get session and retrieve phone number
        HttpSession session = request.getSession();
        String phone = (String) session.getAttribute("phoneNumber");

        if (phone == null) {
            response.sendRedirect("login.html");  // Redirect to login page if not logged in
            return;
        }

        // Get form data
        String description = request.getParameter("description");
        String location = request.getParameter("location");
        String price = request.getParameter("price");
        String condition = request.getParameter("condition");

        // Handle file upload
        Part houseImage = request.getPart("houseImage");
        String fullImagePath = null;

        if (houseImage != null && houseImage.getSize() > 0) {
            String fileName = houseImage.getSubmittedFileName();
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

            // Check for valid image types
            if ("jpg".equals(fileExtension) || "jpeg".equals(fileExtension) || "png".equals(fileExtension)) {
                // Set the upload directory
                String uploadDir = Paths.get(getServletContext().getRealPath("/"), "images").toString();
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs(); // Create directory if it doesn't exist
                }

                // Create a unique image path to avoid collision
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                File savedFile = new File(uploadDir, uniqueFileName);

                // Save the uploaded file to the server
                try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                    houseImage.getInputStream().transferTo(fos);
                }

                // Generate full URL of the saved image
                String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                fullImagePath = appUrl + "/images/" + uniqueFileName;

                System.out.println("Full image URL: " + fullImagePath);  // Debugging message
            } else {
                response.getWriter().println("Invalid file type. Only JPG, JPEG, and PNG are allowed.");
                return;
            }
        } else {
            response.getWriter().println("No file uploaded or file is too large.");
            return;
        }

        // Database connection and insertion
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/betoch", "root", "abenezer33")) {
            // Insert house data into the database
            String sql = "INSERT INTO houses (description, location, price, `condition`, phoneNumber, houseImage) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, description);
                preparedStatement.setString(2, location);
                preparedStatement.setString(3, price);
                preparedStatement.setString(4, condition);
                preparedStatement.setString(5, phone);  // Use phone number from session
                preparedStatement.setString(6, fullImagePath);  // Store full image URL in the database

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    response.sendRedirect(condition.equals("rent") ? "forRent.html" : "forBuy.html");
                } else {
                    response.getWriter().println("Failed to add house. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }
    }
}
