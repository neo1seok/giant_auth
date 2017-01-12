<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="giant_auth.comm.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="giant_auth.Admin.*"%>

<%
	CommPageInfo commPage = new CommPageInfo(request,response,"TEST");
	String jsonsample = commPage.getProtocol();
	String callServeraddess = commPage.getCallServeraddess();
	commPage.PrintInitPage();
	

%>


<script type="text/javascript">

function onloadpage(){
	console.log('load page <%=commPage.getTitle()%>');
	
	}

	$(document).ready(function() {
		//$("#test1").text("hello");
	});
</script>




<body onload="onloadpage()">
	<%commPage.PrintBodyStart(); %>

		


<form method='post' action='LoginServlet'>
<input type='hidden' name='option' value = 'login'/>
<table>
<tr>
	<td>아이디</td>
	<td><input type='text' name='id' tabindex='1'/></td>
	<td rowspan='2'><input type='submit' tabindex='3' value='로그인' style='height:50px'/></td>
</tr>
<tr>
	<td>비밀번호</td>
	<td><input type='password' name='pw' tabindex='2'/></td>
</tr>
</table>
</form>

</body>

<%commPage.PrintEndPage(); %>


