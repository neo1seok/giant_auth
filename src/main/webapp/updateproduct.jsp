<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="giant_auth.comm.*"%>
<%
	String title = "GIANT2 제품 정보 입력";

	String cururl = request.getRequestURI().toString();;
	String pageName = cururl.substring(cururl.lastIndexOf("/") + 1,
			cururl.length());

	CommPageInfo commPage = new CommPageInfo(request);
	String css = commPage.getCssInfo();
	String navi = commPage.getMenuInfo();
	String jsonsample = commPage.getProtocol();

	String jsonStringMapKeyType = commPage.getMapKeyType();
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

<script type="text/javascript">

function onloadpage(){
		console.log('load page <%=title%>');
		console.log('page <%=cururl%>');
		
		
    	var strjsonMapKeyType ='<%=jsonStringMapKeyType%>'
		mapKeyType = JSON.parse(strjsonMapKeyType);

		UpdateDropBox(mapKeyType);
		keyChange(document.input.key_type);

	}

	function myFunction(item) {
		var x = item.prd_name.value;
		var y = item.prd_no.value;

		if (x == null || y == null || x == "" || y == "") {
			alert("제품이름 과 제품 번호를 입력해 주십시오.");
			return false;
		}
		return true;
	}
	function prdchange(item){
		var prd_no = item.value;
		
		document.input.prd_name.value ="PRODUCT("+prd_no+")";
	}
</script>


<body onload="onloadpage()">
	<h1><%=title%></h1>
	<div id="menu">
		<%=navi%>
	</div>

	<form name="input" action="Admin" method="post"
		onsubmit="return myFunction(this)">

		<input type="hidden" name="cmd" value="UPDATE_PRODUCT">

		<table class="board">
			<tr>
				<td>제품번호</td>
				<td><input type="text" name="prd_no" onchange="prdchange(this)" value="PRD_"></td>
			</tr>


			<tr>
				<td>제품이름</td>
				<td><input type="text" name="prd_name" value="상품명"></td>
			</tr>

			<tr>
				<td>칩인증 종류</td>
				<td><select id=key_type name="key_type"
					onchange="keyChange(this)">

				</select></td>
			</tr>


			<tr>
				<td>저장슬럿</td>
				<td><select id=slot_no name="slot_no"></select></td>

			</tr>

			<tr>
				<td>마스터/싱클키</td>
				<td><textarea name="key_value" cols="60" rows="3"></textarea></td>
			</tr>

			<tr>
				<td>설명</td>
				<td><div id="message"></div></td>

			</tr>


			<tr>
				<td>제품정보</td>

				<td><textarea name="prd_info" cols="60" rows="10"></textarea></td>
			</tr>


			<tr>
				<td>상세 제품 ULR</td>
				<td><input type="text" name="prd_url" value=""></td>
			</tr>

		</table>

		<input type="submit" value="등록">
	</form>

</body>
</html>

