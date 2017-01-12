<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="giant_auth.comm.*"%>
<%

	String title = "NO TITLE";
	
	CommPageInfo commPage = new CommPageInfo(request,response,"test");
	
	String css = commPage.getCssInfo();
	String navi = commPage.getMenuInfo();
	String jsonsample = commPage.getProtocol();
	title = commPage.getTitle();
	
	
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=title%></title>
</head>

<script type="text/javascript" src="http://localhost:8080/giant2Auth/util.js"></script>

<script type="text/javascript">

function onloadpage(){
	console.log('load page <%=title%>');
}


</script>
<%=css%>


<body onload="onloadpage()">
	<h1><%=title%></h1>
	<div id="menu">
		<%=navi%>
	</div>

</body>
</html>

