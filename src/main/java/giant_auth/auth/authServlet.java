package giant_auth.auth;

import giant_auth.comm.Protocol;
import giant_auth.comm.Util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.neolib.Util.NeoHexString;

/**
* @FileName : authServlet.java
* @Project : giant_auth
* @Date : 2017. 1. 17.
* @�ۼ��� : neo1seok
* @���α׷� ���� :
* ������ �����ϴ� ���� Ŭ���� 
* http://address:port/giant_auth?auth ������ url�� ���� ������ �� �ִ�.
* �Ķ���Ϳ� ���� �ڼ��� ������ ���� ���� �ϱ� �ٶ� 
* 
* 
* * Servlet class to perform authentication
* Http: // address: port / giant_auth? It can be executed via url of type auth.
* Please refer to the document for details of the parameters
*  
*/


public class authServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public authServlet() {
        super();
        // TODO Auto-generated constructor stub
        
        
    }
    boolean isDebug = false;
    
    
    /**
    * @Name : doAuth
    * @�ۼ��� : 2017. 1. 17.
    * @�ۼ��� : neo1seok
    * @���� :
    * doGet�̳� doPost ��� ���� �Ǵ� �⺻�Լ� 
    * 
    * Basic functions that do both doGet and doPost
    * 
    */
    
    
    synchronized void doAuth(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	String ipAddress =  request.getRemoteAddr();
    	int port = request.getRemotePort();
    	
    	System.out.println("IP Address: "+ipAddress);
    	
    	authHandler nfcAuthHandler = new authHandler();
    	nfcAuthHandler.setAddress(ipAddress,port);
		nfcAuthHandler.InitRun();
		
		String resjson ="";
		
		
		
		
		response.setHeader("Content-Type", "text/xml; charset=UTF-8");
		
		request. setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		
		
		PrintWriter out = response.getWriter();
		
		
		
		//String cmd = request.getParameter("cmd");
		String json = request.getParameter("json");
		String type = request.getParameter("type");
		String debug = request.getParameter("debug");
		
		if(json == null){
			json = "";
		}
		if(type == null){
			type = "";
		}
		if(debug == null){
			debug = "";
		}
		if(debug != null && type.equals("true")){
			
			isDebug = true;
			nfcAuthHandler.setDebug(isDebug);
			
		}

		if(json.isEmpty() ){
			out.println("<h1>NFC AUTH TEST</h1>");
			out.println("NO JSON PARAM!!!!");
			
			//#printGreeting(response);
			return;
		}
	
		resjson = nfcAuthHandler.doRunByJson(json);
		
		
		System.out.println(json);
		nfcAuthHandler.endRun();
		out.print(resjson);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			
		
		try {
			System.out.println("doGet");
			doAuth(request, response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			System.out.println("doPost");
			doAuth(request, response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
