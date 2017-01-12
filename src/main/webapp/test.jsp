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


	<div id="test1"></div>
	<div id="test2"></div>

	<table id="t01" border="1" style="width: 100%">
		<tr>
			<td>Jill</td>
			<td>Smith</td>
			<td>50</td>
		</tr>
		<tr>
			<td>Eve</td>
			<td>Jackson</td>
			<td>94</td>
		</tr>
	</table>

</body>

<%commPage.PrintEndPage(); %>
