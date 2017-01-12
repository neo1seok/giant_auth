package giant_auth.test;


import giant_auth.comm.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.*;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.tomcat.util.codec.binary.Base64;



public class HelloServlet extends HttpServlet {

	Map<CMD,Runnable> map = new LinkedHashMap<CMD,Runnable>();
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/giant2";

	//  Database credentials
	static final String USER = "ictk";
	static final String PASS = "#ictk1234";
	Runnable fdsfsd;

	public HelloServlet(){
		System.out.println("HelloServlet");


		

		




	}



	

	public void doGet(HttpServletRequest req,HttpServletResponse res)

			throws ServletException,IOException {


		Connection conn = null;
		Statement stmt = null;






		req. setCharacterEncoding("utf-8");
		String cmd = req.getParameter("cmd");
		String jsonbase64 = req.getParameter("jsonbase64");

		String jsonParam = "";

		if(jsonbase64 != null){
			System.out.println(jsonbase64);
			try {
				jsonParam = Util.decompress(jsonbase64);
				System.out.println(jsonParam);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		res.setContentType("text/html;charset=UTF-8");

		PrintWriter out = res.getWriter();


		out.println("<HTML>");

		out.println("<BODY>");
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

		out.println("<h1>Hello World!!asdfafafs</h1>");
		out.println(String.format("<h2>%s</h2>",dayTime.format(date)));

		out.println("cmd:"+cmd);
		out.println("<lb/>");

		//out.println("jsonbase64:"+jsonbase64);
		out.println("<lb/>");
		//out.println("jsonbase64:"+jsonbase64);
		out.println("jsonParam:\n"+jsonParam);
		out.println("<lb/>");



		//STEP 2: Register JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			System.out.println("Conneced to database...");
			stmt = conn.createStatement();

			String sql;
			sql = "SELECT seq, cfm_uid, chp_uid, rand_number, mac, result, error, updt_date, reg_date, comment FROM giant2.confirm;";

			ResultSet rs = stmt.executeQuery(sql);
			System.out.println("executeQuery to database...");

			out.println("<table style=\"width:100%\" border=\"1\" >");


			out.println("<tr>");
			out.println("  <td>chp_uid</td>");
			out.println("  <td>rand_number</td>");
			out.println("  <td>result</td>");
			out.println("</tr>");







			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String chp_uid  = rs.getString("chp_uid");
				String rand_number = rs.getString("rand_number");
				String result = rs.getString("result");



				out.println("<tr>");
				out.println("  <td>"+chp_uid+"</td>");
				out.println("  <td>"+rand_number+"</td>");
				out.println("  <td>"+result+"</td>");
				out.println("</tr>");


				//				//Display values
				//				out.print("chp_uid: " + chp_uid);
				//				out.print(", rand_number: " + rand_number);
				//				out.println(", result: " +result);
				//				out.println("<bl/>");
			}
			//STEP 6: Clean-up environment

			out.println("</table>");

			rs.close();
			stmt.close();
			conn.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try

		//STEP 3: Open a connection






		out.println("</BODY>");

		out.println("</HTML>");

		out.close();

	}

}
