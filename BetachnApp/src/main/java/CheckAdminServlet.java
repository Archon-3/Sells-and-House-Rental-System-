import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/CheckAdminServlet")
public class CheckAdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // Debug logs
        System.out.println("CheckAdminServlet called.");

        // Get session
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Session does not exist.");
            out.write("{\"isAdmin\": false}");
            return;
        }

        String phoneNum = (String) session.getAttribute("phoneNumber");
        System.out.println("Session phone number: " + phoneNum);

        // Admin verification
        if ("+251941991245".equals(phoneNum)) {
            System.out.println("User is admin.");
            out.write("{\"isAdmin\": true}");
        } else {
            System.out.println("User is not admin.");
            out.write("{\"isAdmin\": false}");
        }
    }
}
