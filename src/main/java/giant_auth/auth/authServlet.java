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
 * Servlet implementation class NFCServlet
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
    
    private static final String KALIMAH2 = "\u0644\u064e\u0622 \u0625\u0650\u0644\u0670\u0647\u064e \u0625\u0650\u0644\u0651\u064e\u0627 \u0627\u0644\u0644\u0647\u064f \u0645\u064f\u062d\u064e\u0645\u0651\u064e\u062f\u064c \u0631\u0651\u064e\u0633\u064f\u0648\u0652\u0644\u064f \u0627\u0644\u0644\u0647\u0650";
    private static final String KALIMAH = "Å×½ºÆ®";

    protected void printGreeting (HttpServletResponse res) throws IOException {
    	res.setContentType("text/html;charset=utf-8"); //ÇÑ±Û±úÁü¹æÁö
        res.setCharacterEncoding( "utf-8" );
        PrintWriter out = res.getWriter();
        
        
          
        out.write( KALIMAH );
        out.close();
    }
    
    void doNFCAuth(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
		
		
		
		String cmd = request.getParameter("cmd");
		String jsonbase64 = request.getParameter("jsonbase64");
		String type = request.getParameter("type");

		if( cmd == null){
			out.println("<h1>NFC AUTH TEST</h1>");
			out.println("NO JSON PARAM!!!!");
			
			printGreeting(response);
			return;
		}
		if(type != null && type.equals("debug")){
			
			isDebug = true;
			nfcAuthHandler.SetDebug(isDebug);
			
		}
		if(cmd.equals("CMDTEST_COMPRESS") || cmd.equals("CMDTEST")){
			Protocol protocol = new Protocol();
			
			resjson = protocol.toJsonString() ;
			
			if(cmd.equals("CMDTEST_COMPRESS")) resjson = Util.compressURL(resjson); 
			out.println(resjson);
			
			return;
			
		}

		String jsonParam = "";

		if(jsonbase64 == null ){
			out.println("<h1>NFC AUTH TEST</h1>");
			out.println("NO JSON PARAM!!!!");
			
			printGreeting(response);
			return;
		}
		
		System.out.println(cmd);
		
		if(cmd.equals("CMDBYJSON")){
			resjson = nfcAuthHandler.doRunByCompressed(jsonbase64);
		}
		else if(cmd.equals("CMDBYJSON_ROW")){
			resjson = nfcAuthHandler.doRunByJson(jsonbase64);
			
						
		}
		else{
			out.println("<h1>NFC AUTH TEST</h1>");
			out.println("<h2>NO COMMAND</h2>");
			
		}
		
		System.out.println(jsonbase64);
		
		out.print(resjson);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			
		
		try {
			System.out.println("doGet");
			doNFCAuth(request, response);
			
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
			doNFCAuth(request, response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
