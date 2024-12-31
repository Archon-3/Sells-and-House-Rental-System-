
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/homeServlet")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false); // Get the current session, if it exists
    	if (session != null && session.getAttribute("phoneNumber") != null) {
    	    // Valid session
    	    response.sendRedirect("home.html");
    	} else {
    	    // Invalid session, redirect to login page
    	    response.sendRedirect("LoginTo.html");
    	}


   
    }
}
