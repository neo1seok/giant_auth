package giant_auth.Admin;


import giant_auth.comm.GiantDbHanlingMySQL;
import giant_auth.comm.Protocol;
import giant_auth.comm.TABLE_NAMES;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.EnumUtils;

import com.neolib.db.IDbHanling;


/**
* @FileName : adminServlet.java
* @Project : giant_auth
* @Date : 2017. 1. 17.
* @작성자 : neo1seok
* @프로그램 설명 :
* 인증에 필요한 값을 간단하게 수정할 수 있는 서블릿 클래스 
* http://address:port/giant_auth?admin 형태의 url을 통해 실행할 수 있다.
* 파라메터에 대한 자세한 설명은 문서 참조 하길 바람 
*/


public class adminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public adminServlet() {
        super();
        
        System.out.println("AdminServlet");;
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		//out.println("<h1>TEST POST</H2>");
		try {
			String ret = Process(request,response);
			out.println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("<a href=\"javascript:history.back()\">Go Back</a>");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		//out.println("<h1>TEST POST</H2>");
		
		try {
			String ret = Process(request,response);
			out.println(ret);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("<a href=\"javascript:history.back()\">Go Back</a>");
	}
	
	
	
	/**
	* @Name : Process
	* @작성일 : 2017. 1. 17.
	* @작성자 : neo1seok
	* @설명 :
	* doGet이나 doPost 모두 실행 되는 기본함수 synchronized 를 통해 비동기화를 시킬 수 있다.
	* 
	*  Basic functions that do both doGet and doPost
	*  
	*/
	
	
	synchronized protected String Process(HttpServletRequest request, HttpServletResponse response) throws Exception{
		adminHandler adminHanlder = new adminHandler();
		adminHanlder.Open();
		PrintWriter out = response.getWriter();
		
		Protocol protocol = new Protocol();
		ArrayList<String> fdsf = Collections.list(request.getParameterNames());
		if(fdsf.size() ==0){
			out.println("<h1>ADMIN PAGE</h1>");
			out.println("NO PARAM!!!!");
			return "";
			
		}
		String cmd = "";
		for(String tmp :Collections.list(request.getParameterNames()) ){
			
			String value = request.getParameter(tmp);
			System.out.println(tmp+":"+value);
			if(tmp.equals("cmd")){
				protocol.cmd = value;
				cmd = value;
				continue;
				
			}
			protocol.params.put(tmp, value);
			//System.out.println(tmp+":"+value);
		}
		

		
		
		if( !EnumUtils.isValidEnum(ADMIN_CMD.class, cmd) ){
			out.println("<h1>ADMIN PAGE</h1>");
			out.println("NO COMMAND or NOT VALID!!!!");
			return "";
		}
		
		
		
		
		Protocol ret = adminHanlder.doProcess(protocol);
		
		
		

		


		
		
		
		
		return ret.toJsonString();
		
	}

}
