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
		var testval = 2;

		console.log(testval);
		CheckHexStr('AF12', testval);
		var res = "INVALID";
		
		if(CheckHexStr('AF12', 2)){
			res = "VALID";
		}
		$("#test1").text(res);

	}

	$(document).ready(function() {
		//$("#test1").text("hello");
	});
</script>




<body onload="onloadpage()">
	<%commPage.PrintBodyStart(); %>




<form method='post' action='LoginServlet'>
<input type='hidden' name='option' value = 'join'/>
<table>
<tr>
	<td>아이디</td>
	<td><input type='text' name='id' tabindex='1'/></td>
</tr>
<tr>
	<td>비밀번호</td>
	<td><input type='password' name='pw' tabindex='2'/></td>
</tr>
<tr>
	<td>비밀번호확인</td>
	<td><input type='password' name='pw_confirm' /></td>
</tr>
<tr>
	<td>이름</td>
	<td><input type='text' name='name' /></td>
</tr>
<tr>
	<td>이메일</td>
	<td><input type='text' name='email' size="50"/></td>
</tr>

</table>
	<input type="submit" value="등록">
</form>


</body>

<%commPage.PrintEndPage(); %>
