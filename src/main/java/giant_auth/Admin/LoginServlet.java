package giant_auth.Admin;

import giant_auth.comm.Protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.neolib.Util.NLoger;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
        NLoger.debug("LoginServlet");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doComm( request,  response);
	
	}
   
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doComm( request,  response);
	}
	protected void doComm(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		String option = request.getParameter("option");
		String title = "";
		if(option.equals("login")){
			title = "LOGIN";
		}
		else if(option.equals("join")){
			title = "JOIN";
		}
		
		out.println(String.format("<h1>%s</H2>",title));
		
		try {
			String ret = "";
			
			Protocol protocol = new Protocol();
			
			for(String tmp :Collections.list(request.getParameterNames()) ){
				String value = request.getParameter(tmp);
				out.println("<p>"+tmp+":"+value+"</p>");
				
				if(tmp.equals("'option'")){
					protocol.cmd = value;
					continue;
					
				}
				protocol.params.put(tmp, value);
				System.out.println(tmp+":"+value);
			}
		
			
			out.println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("<a href=\"javascript:history.back()\">Go Back</a>");
	}

}
