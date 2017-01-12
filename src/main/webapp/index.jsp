<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="giant_auth.comm.*"%>


<%@ page import="giant_auth.comm.*"%>
<%

	String title = "GIANT2 MAIN PAGE";
	CommPageInfo commPage = new CommPageInfo(request);
	String css = commPage.getCssInfo();
	String navi = commPage.getMenuInfo();
	String jsonsample = commPage.getProtocol();
	String callServeraddess = commPage.getCallServeraddess();
	String jssrc = commPage.getJsSources();
	String defmeta = commPage.getDefMeta();
	title = commPage.getTitle();
	String systemSettings = commPage.getSystemSettingsInfo();
	
	
	
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%=defmeta%>

<title><%=title%></title>
<%=css%>
<%=jssrc%>


</head>


<script type="text/javascript">

function onloadpage(){
	console.log('load page <%=title%>');
}


</script>


<body onload="onloadpage()">
	<h1><%=title%></h1>
	<div id="menu">
		<%=navi%>
	</div>
	<p><%=systemSettings%></p>
	

</body>
</html>

