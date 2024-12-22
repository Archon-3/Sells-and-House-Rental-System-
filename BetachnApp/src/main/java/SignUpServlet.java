import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class SignUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form data
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmpassword");
        String gender = request.getParameter("gender");

        // Validate password and confirm password
        if (!password.equals(confirmPassword)) {
            out.println("<h3>Passwords do not match. Please try again.</h3>");
            return;
        }

        // Database connection information
        String jdbcURL = "jdbc:mysql://localhost:3306/betoch";
        String dbUser = "root";
        String dbPassword = "abenezer33";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Load MySQL JDBC driver (only needed for older versions, newer versions don't require this)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            // SQL query to insert user
            String sql = "INSERT INTO users (first_name, last_name, email, phone, password, gender) VALUES (?, ?, ?, ?, ?, ?)";

            // Prepare statement
            statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, password); // Ideally, hash the password for security
            statement.setString(6, gender);

            // Execute the query
            int rowsInserted = statement.executeUpdate();

            // Check if registration is successful
            if (rowsInserted > 0) {
                response.sendRedirect("LoginTo.html"); // Redirect to success page
            } else {
                out.println("<h3>Registration failed. Please try again.</h3>");
            }
        } catch (SQLException | ClassNotFoundException e) {
            // Handle SQL and ClassNotFound exceptions
            e.printStackTrace();
            out.println("<h3>An error occurred. Please try again later.</h3>");
        } finally {
            // Close the resources
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
