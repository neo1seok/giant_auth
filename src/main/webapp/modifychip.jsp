<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="giant_auth.comm.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="giant_auth.Admin.*"%>

<%
	String title = "GIANT2 칩 정보 입력";
	CommPageInfo commPage = new CommPageInfo(request);
	String css = commPage.getCssInfo();
	String navi = commPage.getMenuInfo();
	String jsonsample = commPage.getProtocol();
	String jsonStringMapKeyType= commPage.getMapKeyType();
	String callServeraddess = commPage.getCallServeraddess();
	String jssrc = commPage.getJsSources();
	String defmeta = commPage.getDefMeta();
	title = commPage.getTitle();
	
//	AdminHandler adminHandler = new AdminHandler();
//	adminHandler.Open();
	//String jsonString = adminHandler.queryProuductInfo();
	
	String jsonString ="";
	
	//adminHandler.Close();

	pageContext.setAttribute("jsonString", jsonString);
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
	var strjson = '<%=jsonString%>';
	
	console.log(strjson);
	console.log(strjsonMapKeyType);

	mapValue = JSON.parse(strjson);
	
	var strjsonMapKeyType ='<%=jsonStringMapKeyType%>'
	mapKeyType = JSON.parse(strjsonMapKeyType);
	
	UpdateProductDropInfo();
	//UpdateDropBox();
	console.log('load page <%=title%>');
	leaveChange();
}

  
	function myFunction() {

		var frm = document.input;
		frm.prd_name.value = "test";

	}

	function leaveChange() {

		var frm = document.input;
		var prd_name = frm.prd_no.value;
		var mapKey = mapValue[prd_name];
		
		frm.prd_name.value = mapKey["prd_name"];
		frm.key_type.value = mapKeyType[mapKey["key_type"]];
		
		var keytype = mapKey["key_type"];
		
		frm.key_value.disabled = true;
		
		console.log(keytype);
		
		if(keytype =="chip_key"){
			console.log("if in");
			frm.key_value.disabled = false;
		}
		

		var txt = '{ "id":"outsider", "sex":"male" }';

		var obj = JSON.parse(txt);

		//document.getElementById("message").innerHTML = obj["sex"];

	}
	function confirm(item) {
		$("#result").text("");
		var x = item.prd_name.value;
		var y = item.prd_no.value;
		
		
		var strSn = item.sn.value;
		var strKeyValue = item.key_value.value;
		
		
		
		
		
		console.log(strSn);
		
		if(!unitCheckConfirm("sn",strSn,9)){
			
			return false;
		}
		
		if(!item.key_value.disabled){
			console.log("item.key_value.disabled is false");
			if(!unitCheckConfirm("key_value",strKeyValue,32)){
				
				return false;
			}
			
		}

		return true;

	}
	function unitCheckConfirm(title_,value_,size_){
		var msg = "";
		if (value_ == "") {
			
			msg = title_+" 필드가  비었습니다.";
			alert(msg);
			$("#result").text(msg);
			return false;
		}
		
		if(!CheckHexStr(value_, size_)){
			msg = title_+" 필드는 "+size_+ " 자리의 hex string 이어야 합니다.";
			alert(msg);
			$("#result").text(msg);
			return false;
			
		}
		return true;
		
	}
	var myVar;
	function viewcount(item,countid){
		console.log("viewcount");
		clearTimeout(myVar);
		myVar = setTimeout(doupdate, 500)
		
	
		
		
	}
	function doupdate(){
		console.log("doupdate");
		var frm = document.input;
		var strsn = frm.sn.value;
		var strkeyval =frm.key_value.value;
		
		console.log(strsn );
		console.log(strkeyval);
		
		$("#SN_COUNT").text(strsn.length/2);
		$("#KEY_VALUE_COUNT").text(strkeyval.length/2);
		
		clearTimeout(myVar);
		
	}
</script>

<body onload="onloadpage()">
	<h1><%=title%></h1>
	<div id="menu">
		<%=navi%>
	</div>
	<form id="input" name="input" action="Admin" method="post" onsubmit="return confirm(this)">

		<input type="hidden" name="cmd" value="UPDATE_CHIP">

		<table class="board">
			<tr>
				<td>제품번호</td>
				<td><select id=prd_no name="prd_no"
					onchange="leaveChange()">

				</select></td>
				<td><div id="message"></div></td>

			</tr>
			<tr>
				<td>제품이름</td>
				<td><input type="text" id="prd_name" name="prd_name"
					readonly size="50"></td>

			</tr>
			<tr>
				<td>키인증 종류</td>
				<td><input type="text" id="key_type" name="key_type"
					readonly size="15"></td>

			</tr>


			<tr>
				<td>SN</td>
				<td><input type="text" name="sn" id="sn" size="50" onkeydown="viewcount()" onchange="viewcount()" ><div id="SN_COUNT"></div></td>
				

			</tr>


			<tr>
				<td>키값</td>

				<td><textarea name="key_value" id="key_value" cols="60" rows="2" onkeydown="viewcount()" onchange="viewcount()"></textarea><div id="KEY_VALUE_COUNT"></div></td>
			</tr>

		</table>

		<input type="submit" value="등록">
	</form>
	
	<div id="result"></div>

</body>
</html>

