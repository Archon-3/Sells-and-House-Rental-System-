import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form data
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        // Database connection information
        String jdbcURL = "jdbc:mysql://localhost:3306/betoch";
        String dbUser = "root";
        String dbPassword = "abenezer33";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            // SQL query to check user credentials
            String sql = "SELECT * FROM users WHERE phone = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, phone);
            statement.setString(2, password);

            // Execute the query
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // If credentials are valid, create a session and store the user's phone number
                HttpSession session = request.getSession();
                session.setAttribute("phoneNumber", phone); // Store phone number in session
                session.setMaxInactiveInterval(60*60*72);

                // Redirect to the home page
                response.sendRedirect("home.html");
            } else {
                // Login failed, redirect back to login page with error
                response.sendRedirect("LoginTo.html?error=1");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            out.println("<h3>An error occurred. Please try again later.</h3>");
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

