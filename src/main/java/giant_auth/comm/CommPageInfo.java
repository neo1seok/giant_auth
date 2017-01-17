package giant_auth.comm;



import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.neolib.Util.NLoger;
import com.neolib.Util.NeoUtil;

public class CommPageInfo {
	
	
	
	Map<String,String> mapTitle = new LinkedHashMap<String,String>();
	HttpServletRequest request;
	HttpServletResponse response;
	
	StringBuilder sb = new StringBuilder();
	
	//String fmt = "<li><a href=\"http://localhost:8080/giant2Auth{0}\">{1}</a></li>";
	String fmtServer = "http://{0}:{1,number,#}";
	String fmt = "<li><a href=\"{0}/giant_auth{1}\">{2}</a></li>";
	//0:cur server call name with port 1: rel path 2: title
	String fmtRow = "<li><a href=\"{0}\">{1}</a></li>";
	Map<String,String> mapKeyType = new LinkedHashMap<String, String>();
	
	private String callServeraddess;
	private String title = "";
	
	public CommPageInfo(HttpServletRequest request) {
		
		
		super();
		System.out.println("CommPageInfo");
		
		
		mapKeyType.put("chip_key","칩 개별 인증");
		mapKeyType.put("mstr_key","마스터키사용");
		mapKeyType.put("one_key","상품동일키사용");
		
		
		
		this.request = request;
		
		String cururl = request.getContextPath();
		NLoger.clog(cururl);
		NLoger.clog(request.getServletPath());
		NLoger.clog(request.getRequestURI());
		NLoger.clog(request.getServerName());
		
		NLoger.clog(System.getenv("DEBUG"));
		
		
		
		callServeraddess = MessageFormat.format(fmtServer,request.getServerName(),request.getServerPort());
		NLoger.clog(callServeraddess);
		System.out.println(callServeraddess);
	}
	public CommPageInfo(HttpServletRequest request,HttpServletResponse response,String title) {

		this(request);
//		mapTitle.put("updateproduct.jsp","제품 입력");
//		mapTitle.put("updatechip.jsp","칩 정보 입력");
//		mapTitle.put("modifyproduct.jsp","제품 정보 수정");
//		
//		
//		
//		
//		mapKeyType.put("chip_key","칩 개별 인증");
//		mapKeyType.put("mstr_key","마스터키사용");
//		mapKeyType.put("one_key","상품동일키사용");
//		
//		
//		
//		this.request = request;
//		
//		String cururl = request.getContextPath();
//		NLoger.clog(cururl);
//		NLoger.clog(request.getServletPath());
//		NLoger.clog(request.getRequestURI());
//		NLoger.clog(request.getServerName());
//		
//		NLoger.clog(System.getenv("DEBUG"));
//		
//		
//		
//		callServeraddess = MessageFormat.format(fmtServer,request.getServerName(),request.getServerPort());
//		NLoger.clog(callServeraddess);
//		
		
		this.response = response;
		this.title = title;
		
	
	}
	
	void AppendLine(String line){
		sb.append(line + "\n");
	}
	public String  getCssInfo(){
		
		sb.setLength(0);
		
		
		sb.setLength(0);
		
		
		AppendLine("<link rel=\"stylesheet\" type=\"text/css\" href=\""+callServeraddess+"/giant_auth/css/giant.css\">");
		
//		AppendLine("<style type=\"text/css\">");
//		AppendLine("body {");
//		AppendLine("		    background-color: lightgreen;");
//		AppendLine("}");
//		AppendLine("input { background-color: yellow }");
//		
//		AppendLine("input[type=\"text\"], textarea {");
//		AppendLine("  background-color : #d1d1d1; ");
//		AppendLine("}");
//
//
//		AppendLine("     a{text-decoration:none; color:#000000;}");
//		AppendLine("        a:hover{color:#ff0000;}   ");
//		AppendLine("          ");
//		AppendLine(" ul{");
//		AppendLine("   list-style-type: none;");
//		AppendLine("  padding-right: 20px;");
//		AppendLine("  padding-left: 20px;");
//		AppendLine(" }");
//		AppendLine("");
//		AppendLine(" li {");
//		AppendLine("  display: inline;");
//		AppendLine("  padding:0 10px;");
//		AppendLine("  border-left:1px solid #999;");
//		  
//		AppendLine(" }");
//		AppendLine("</style>");

		

		
		
		return sb.toString();
	}
	
	public String  getMenuInfo(){
		String isDebug = System.getenv("ISDEBUG");
		String isAuthTest = System.getenv("ISAUTHTEST");
		
		sb.setLength(0);
		AppendLine("<ul>");
		
		AppendLine(MessageFormat.format(fmt,callServeraddess, "","MAIN 화면"));
		
		for(Entry<String, String> tmp : mapTitle.entrySet()){
			AppendLine(MessageFormat.format(fmt,callServeraddess, "/"+tmp.getKey(),tmp.getValue()));
		}
		
		//AppendLine(MessageFormat.format(fmt,callServeraddess, "/updatechip.jsp","칩 정보 업데이트"));
		
		
		if(NeoUtil.isSafeEqual(isDebug, "YES"))
			AppendLine(MessageFormat.format(fmt,callServeraddess, "/testauth.jsp","AUTH TEST"));
		
		
		if(NeoUtil.isSafeEqual(isAuthTest, "YES"))
			AppendLine(MessageFormat.format(fmt,callServeraddess, "/test.jsp","TEST"));
		
		AppendLine(MessageFormat.format(fmtRow, "javascript:history.back()","Go Back"));
		
		AppendLine("</ul>");

		return sb.toString();
	}

	public String  getProtocol(){
		Protocol prot = new Protocol();
		prot.cmd = "REQ_SESSION";
		

		return prot.toJsonString();
	}
	public String getTitle(){
	
		return title;
		
		//return pageName;
		
	}
	public String getMapKeyType(){
		
		
		Gson gson = new Gson();
		return gson.toJson(mapKeyType);
		
	}
	public String getCallServeraddess() {
		return callServeraddess+"/giant_auth";
	}
	public String getAbsAddress() {
		return callServeraddess+"/giant_auth";
	}
	public String getJsSources() {
		sb.setLength(0);
		AppendLine("<script type=\"text/javascript\" src=\""+ callServeraddess +"/giant_auth/js/util.js\"></script>");
		AppendLine("<script type=\"text/javascript\" src=\""+ callServeraddess +"/giant_auth/js/jquery-3.0.0.min.js\"></script>");
		
		return sb.toString();
	}
	
	public String getDefMeta() {
		sb.setLength(0);
		AppendLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		AppendLine("<meta name=\"viewport\" content=\"user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, width=device-width, height=device-height\">");
		
		return sb.toString();
		
	}
	
	public static String dbname;
	public static  String address;
	public  static String id;
	public  static String passwd;
	public  static int port;
	
	public String getSystemSettingsInfo(){
		sb.setLength(0);
		AppendLine("<table style='width:100%'>");
		AppendLine("<tr>");
		AppendLine("<th>항목</th><th>값</th>");
		AppendLine("</tr>");
		AppendLine("<tr>");
		AppendLine("<th>buildDate</th><th>"+SystemSettings.buildDate+"</th>");
		AppendLine("</tr>");
		AppendLine("<tr>");
		AppendLine("<th>address</th><th>"+SystemSettings.address+"</th>");
		AppendLine("</tr>");
		AppendLine("<tr>");
		AppendLine("<th>dbname</th><th>"+SystemSettings.dbname+"</th>");
		AppendLine("</tr>");
		AppendLine("<tr>");
		AppendLine("<th>id</th><th>"+SystemSettings.id+"</th>");
		AppendLine("</tr>");
		AppendLine("</table>");
		
	

		
		
		return sb.toString();
	}
	public void PrintPage(String script){
		
		if(this.response == null) return ;
		try {
			PrintWriter out = response.getWriter();
			out.println(script);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void PrintInitPage(){
		
		
		String defMeta = getDefMeta();
		String css = getCssInfo();
		String jssrc = getJsSources();
		sb.setLength(0);
		AppendLine("<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
		AppendLine("<head>");
		AppendLine(defMeta);
		AppendLine("<title>" + title+"</title>");
		AppendLine(css);
		AppendLine(jssrc);
		AppendLine("</head>");
		
				
		
		PrintPage(sb.toString());

	}
	public void PrintBodyStart(){
		NLoger.clog("PrintBodyStart");
		
		
		String navi = getMenuInfo();
		sb.setLength(0);
		AppendLine("<h1>"+title+"</h1>");
		
		AppendLine("<div id='menu'>");
		AppendLine(navi);
			
		AppendLine("</div>");
		
		PrintPage(sb.toString());
			
		
	}

	public void PrintEndPage(){
		sb.setLength(0);
		AppendLine("</html>");
		PrintPage(sb.toString());

	}
	

	

}
