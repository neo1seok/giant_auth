<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="giant_auth.comm.*"%>


<%@ page import="giant_auth.comm.*"%>
<%
	String title = "TEST AUTH";

	CommPageInfo commPage = new CommPageInfo(request);
	String css = commPage.getCssInfo();
	String navi = commPage.getMenuInfo();
	String jsonsample = commPage.getProtocol();
	String callServeraddess = commPage.getCallServeraddess();
	String jssrc = commPage.getJsSources();
	String defmeta = commPage.getDefMeta();
	title = commPage.getTitle();
	
	
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%=defmeta%>
<title><%=title%></title>
<%=css%>
<%=jssrc%>
</head>

<script type="text/javascript" src="http://localhost:8080/giant2Auth/util.js"></script>

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
<form action="http://localhost:8080/giant2Auth/NFC" method="post">

	<table style="width: 100%">
		<tr>
			<td>cmd:</td>
			<td><input type="text" name="cmd" value="CMDBYJSON_ROW"></td>

		</tr>
		<tr>
			<td>json</td>
			<td><textarea name="jsonbase64" cols="100" rows="20" ><%=jsonsample%></textarea>></td>
			<td>94</td>
		</tr>
	</table>

	<input type="submit" value="Submit">
</form>

</body>
</html>

