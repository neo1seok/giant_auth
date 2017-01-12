/******************************************************** 
   파일명 : common.js
*********************************************************/ 
function buttonStyleFix() {
 var tblButton = document.getElementById("tblButton");
 if(tblButton) {
  if(tblButton.childNodes && tblButton.childNodes.length) {
   for(i=0; i < tblButton.childNodes.length; i++ ) {
    if(tblButton.childNodes[i].style && tblButton.childNodes[i].style.visibility && tblButton.childNodes[i].style.visibility == 'hidden') {
      tblButton.childNodes[i].style.display = 'none';
    }
   }
  }
 }
} 

function delChar(str, ch){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열에서 특정문자를 제거한 새로운 문자열을 만든다.
    * str    : 문자열
    * ch    : 제거할 문자
    ***********************************************************************************************************
    */
 var len = str.length;
 var ret = "";
 
 //문자열에서 ch 문자를 제거한다. 예) ,  - 등등
 for (i=0; i<len; ++i)
 {
  if (str.substring(i,i+1) != ch)
   ret = ret + str.substring(i,i+1);
 }
 
 return ret;
}

function replace(str,oldChar,newChar){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열에서 특정문자를 다른 문자로 치환한 새로운 문자열을 만든다.
    * str    : 문자열
    * oldChar   : 바꾸기 전의 문자
    * newChar   : 바꿔서 넣을 문자
    ***********************************************************************************************************
    */
    var oldstr="";
    
    while(oldstr!=str){
     oldstr=str;
     str=str.replace(oldChar,newChar);
    }

 return str;
}


function lTrim(str){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열에서 왼쪽의 공백을 제거한다.
    * str    : 문자열
    ***********************************************************************************************************
    */
  var i;
  i = 0;
  while (str.substring(i,i+1) == ' ' || str.substring(i,i+1) == '　')  i = i + 1;
  return str.substring(i);
}

function rTrim(str){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열에서 오른쪽의 공백을 제거한다.
    * str    : 문자열
    ***********************************************************************************************************
    */


  var i = str.length - 1;
  while (i >= 0 && (str.substring(i,i+1) == ' ' || str.substring(i,i+1) == '　')) i = i - 1;
  return str.substring(0,i+1);
}

function trim(str){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열에서 양쪽의 공백을 제거한다.
    * str    : 문자열
    ***********************************************************************************************************
    */
    if( str == "" || str.length ==0 ) 
    {
      return str; 
    } 
    else
    {
      return(lTrim(rTrim(str)));
    }   
}

 

//오른쪽에 ch 문자 채우기

function rPadString(str, ch, len){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열을 정해진 길이만큼 오른쪽을 특정 문자로 채운다.
    * str    : 문자열
 * len    : 총길이
    ***********************************************************************************************************
    */
 var strlen = trim(str).length;
 var ret = "";
 var alen = len - strlen;
 var astr = ""; 
 
 //부족한 숫자만큼  len 크기로 ch 문자로 채우기
 for (i=0; i<alen; ++i)
 {
  astr = astr + ch;
 }
 
 ret = trim(str) + astr; //뒤에서 채우기
 return ret;
}

function lPadString(str, ch, len){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열을 정해진 길이만큼 왼쪽을 특정 문자로 채운다.
    * str    : 문자열
 * len    : 총길이
    ***********************************************************************************************************
    */
 var strlen = trim(str).length;
 var ret = "";
 var alen = len - strlen;
 var astr = ""; 
 
 
 //부족한 숫자만큼  len 크기로 ch 문자로 채우기
 for (i=0; i<alen; ++i)
 {
  astr = astr + ch;
 }
 
 ret = astr + trim(str); //앞에서 채우기
 return ret;
}

function formatComma(argStr){
    /*
    *********************************************************************************************************
    *   함수설명  : 숫자를 세자리마다 컴마를 찍은 형식으로 바꾸어 준다.
    * argStr    : argument
    ***********************************************************************************************************
    */
 if (argStr == null)  return;
 var argStr = argStr + ""; //숫자인 경우 문자열로 변환
 var rule = /[^0-9-.]/g;  // 숫자, 부호 및 소수점 이외의 데이터 제거

 argStr = getFilledCommaStr(argStr.replace(rule, ""));
 return argStr;
}

function getFilledCommaStr(argNumber) {
    /*
    *********************************************************************************************************
    *   함수설명  : 숫자에 천단위로 ','를 붙여서 반환
    * argNumber   : 숫자
    ***********************************************************************************************************
    */
        argNumber = argNumber.toString();

        if (isEmpty(argNumber))  return argNumber;

        // 숫자 항목에서 부호(-), 소수점(.) 체크
        var sourceStr = trim(argNumber);
        var signStr   = ""
        var dotStr    = "";

        if (sourceStr.substring(0, 1) == "-") {
            signStr   = "-";
            sourceStr = sourceStr.substring(1, sourceStr.length);
        }
        if (sourceStr.indexOf(".") >= 0) {
            dotStr    = sourceStr.substring(sourceStr.indexOf("."), sourceStr.length);
            sourceStr = sourceStr.substring(0, sourceStr.indexOf("."));
        }

        var sourceLen = sourceStr.length;
        var filledStr = "";
        var checkIdx  = 0;

        for (var idx = sourceLen - 1; idx >= 0; idx--) {
            if (checkIdx++ % 3 == 0 && idx != sourceLen -1) {
                filledStr = "," + filledStr;
                checkIdx = 1;
            }
            filledStr = sourceStr.substring(idx, idx + 1) + filledStr;
        }
        return signStr + filledStr + dotStr;
}

function formatDate(str,mark){
    /*
    *********************************************************************************************************
    *   함수설명  : 날짜형식으로 년,월,일 사이에 구분자를 넣어준다.
    * str       : 날짜가 YYMMDD형식으로 담겨있는 문자열
    * mark   : 년,월,일 사이에 들어갈 구분자
    ***********************************************************************************************************
    */
 if(str != "" && str.length == 8) {
  return str.substring(0,4)+mark+str.substring(4,6)+mark+str.substring(6,8);
 }  else {
  return "";
 }
}

function setToday(field){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란을 오늘날짜로 채워준다.
    *
    * field    : html에서 name으로 지정된 입력필드의 명
    *
    * 사용예
    ****************************************************************************************************
    */
 var cDate=new Date();
 var year=cDate.getYear();
 var month=(cDate.getMonth()+1).toString();
 month=month.length==1?"0"+month:month;
 var day=cDate.getDate().toString();
 day=day.length==1?"0"+day:day;
 field.value="" +year+month+day;
}

function setMonthFirstDay(field){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란을 이번달의 첫날로 채워준다..
    *
    * field    : html에서 name으로 지정된 입력필드의 명
    *
    * 사용예
    ****************************************************************************************************
    */
 var cDate=new Date();
 var year=cDate.getYear();
 var month=(cDate.getMonth()+1).toString();
 month=month.length==1?"0"+month:month;
    var day="01";
 field.value="" +year+month+day;
}

function setYearFirstDay(field){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란을 올해 1월1일로 채워준다.
    *
    * field    : html에서 name으로 지정된 입력필드의 명
    *
    * 사용예
    ****************************************************************************************************
    */

    var cDate=new Date();
    var year=cDate.getYear();
    var month="01";
    var day="01";
    field.value="" +year+month+day;
}

function setOneMonthBefore(field){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란을 오늘보다 한달전의 날짜로 채워준다..
    *
    * field    : html에서 name으로 지정된 입력필드의 명
    *
    * 사용예
    ****************************************************************************************************
    */
 var cDate=new Date();
 var year=cDate.getYear();
 var month=(cDate.getMonth()).toString();
 month=month.length==1?"0"+month:month;
 if (month=="00"){
  month="12";
  year--;
 }
 var day=cDate.getDate().toString();
 day=day.length==1?"0"+day:day;
 field.value="" +year+month+day;
}

function setOneYearBefore(field){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란을 오늘보다 1년전의 날짜로 채워준다..
    *
    * field    : html에서 name으로 지정된 입력필드의 명
    *
    * 사용예
    ****************************************************************************************************
    */
 var cDate=new Date();
 var year=cDate.getYear()-1;
 var month=(cDate.getMonth()+1).toString();
 month=month.length==1?"0"+month:month;
 var day=cDate.getDate().toString();
 day=day.length==1?"0"+day:day;
 field.value="" +year+month+day;
}

function isEmpty(str){
    /*
    *********************************************************************************************************
    *   함수설명  : 문자열이 빈문자열 혹은 공백만 있는 문자열이지 검사한다.
    * str    : 문자열
    ***********************************************************************************************************
    */
 if (trim(str) == '') return true;
 return false;

}

function isContains(str,ch){
    /*
    *********************************************************************************************************
    *   함수설명: 문자열이 특정문자열을 포함하고 있는지 체크한다.
    * str    : 특정문자 포함여부를 체크할 대상 문자열
    * ch    : 지정된 특정문자
    * 
    ***********************************************************************************************************
    */
     var i=0;
     for(i=0;i < str.length;i++){
     if(str.charAt(i)==ch) return true;
     }
     return false;
}


function isContainsOnly(str,chars) {
    /*
    *********************************************************************************************************
    *   함수설명  : 해당문자열이 지정된 문자들만을 포함하고 있는지 검사한다.
    * str    : 검사할 문자열
    * chars   : 지정된 문자들의 나열
    ***********************************************************************************************************
    */     
    for (var inx = 0; inx < str.length; inx++) {
       if (chars.indexOf(str.charAt(inx)) == -1)
       return false; 
    }
    return true;
}

function isUnderMaxLen(strName,str , maxLen){
    /*
    *********************************************************************************************************
    *   함수설명: 문자열의 글자수체크를 한다. checkInputLength 등의 함수 내에서 호출된다.
    *   StrName : 글자수 체크를 할 문자열의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * str    : 글자 수 체크를 할 문자열
    * maxLen    : 해당 필드의 최대글자수 (한글2,영문1)
    * 
    ***********************************************************************************************************
    */
     var i, len=0;
     var korLen = maxLen / 2;
     

     
     
     
     for(i=0;i < str.length; i++) (str.charCodeAt(i) > 255)? len+=2:len++;
     if (maxLen < len) {
      alert(strName + "은(는) 영문(숫자)"+maxLen+"자, 한글"+korLen+"자까지만 가능합니다. 현재 글자수(영문기준) : "+len);
      return false;
     }
     return true;
}

 

function isValidDay(year, month, day) {
    /*
    *********************************************************************************************************
    *   함수설명  : 지정하는 년,월,일이 달력상으로 존재하는 날짜인지 검사한다.
    * year    : 년
    * month   : 월
    * day    : 일
    ***********************************************************************************************************
    */     
    var m = parseInt(month,10) - 1;
    var d = parseInt(day,10);

    var end = new Array(31,28,31,30,31,30,31,31,30,31,30,31);
    if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
        end[1] = 29;
    }

    return (d >= 1 && d <= end[m]);
}

function checkInputLength(fieldCalledName,field,maxLen){
    /* 
    ****************************************************************************************************
    *  함수설명: 한글2,영문숫자1을 기준으로 입력란의 글자수를 검사한다.
    *
    * fieldCalledName : 글자수 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객
    *
    * 사용예 <TEXTAREA NAME="testArea" ROWS="8" COLS="70" onkeyup="javascript:checkInputLength('긴내용',this,50);"></TEXTAREA>
 *   날짜나 주민등록번호처럼 정해진 자리수의 최대길이 제한은  <input type=text..>에서 maxlength 속성으로 최대길이를 같이 정의할 것을 추천한다.
    * 단, 한글2자,영문1자 기준으로 글자수 입력제한을 할 필요가 있을 때는 input type=text 에도 이 함수를 써야 한다.
    ****************************************************************************************************
    */
    
 if(field.value != ""){

  if(isContains(field.value,"'")) {
      alert(" ' 문자는 허용되지 않습니다.");
      field.focus(); 
   field.value=delChar(field.value,"'");
            field.select();
      return false;
  } 

  if(!isUnderMaxLen(fieldCalledName,field.value,maxLen)) {
      field.focus(); 
      field.value=field.value.substring(0,maxLen); 
      return false;
  }
 }
  
 return true;
}

function checkInputNumber(fieldCalledName,field){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란에 숫자만이 입력되는지 체크한다.
    *
    * fieldCalledName : 글자수 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    *
    * 사용예
    ****************************************************************************************************
    */
 if(field.value != ""){
  if(!isContainsOnly(field.value, "0123456789")) {
    alert(fieldCalledName + "은(는) 숫자외의 문자열을 입력할 수 없습니다.");
  field.focus(); 
  field.value=field.value.substring(0,field.value.length-1); 
  return false;
  }
 }
    return true;
}


function checkNumber(fieldCalledName,field,min,max){
    /* 
    ****************************************************************************************************
    *  함수설명: 입력란에 최종적으로 적합한 범위내의 숫자가 들어왔는지 검사한다.
    *
    * fieldCalledName : 글자수 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    * min    : 최소값
    * max    : 최대값
    *
    * 사용예
    ****************************************************************************************************
    */

 field.value=trim(field.value);
 if(!checkInputNumber(fieldCalledName,field)) {
  return false;
 }


 var fieldNumber=parseInt(field.value);
 if ( !(fieldNumber>=min && fieldNumber<=max) )
 {
  alert(fieldCalledName+"의 값이" + parseInt(field.value)+ "로 [" + min + " ~ " + max + "] 사이의 범위를 벗어나 있습니다.");
  field.focus();
  return false;
 }
    return true;
}

function checkDate(fieldCalledName,field){
    /* 
    ****************************************************************************************************
    *  함수설명: 최종적으로 입력된 날짜가 적합한지를 검사한다.
    *
    * fieldCalledName : 글자수 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    *
    * 사용예  <input type='text' name='test3' onBlur="javascript:checkDate('시작일',this);" >
    ****************************************************************************************************
    */
 field.value=trim(field.value);    
 if(!checkInputNumber(fieldCalledName,field)) {
  return false;
 }
 var year = field.value.substring(0, 4);
 var month = field.value.substring(4, 6);
 var day = field.value.substring(6,8);
 //alert(year+"년 "+month+"월 "+day+"일");

    if (year < 1900 || year >2037){
        alert('날짜가 잘못 입력되었습니다. 년도는 1900년에서 2037년까지 입니다.');
  field.select(); 
        return false;
    }
    if (month <1 || month > 12){
        alert('날짜가 잘못 입력되었습니다. 달은 1월에서 12월까지 입니다.');
  field.select(); 
        return false;
    }
    if (day < 1 ||  !isValidDay(year, month,day)){
        alert('날짜가 잘못 입력되었습니다. '+ year+ "년 " +month+'월에는 '+ day+'일이 없습니다.');
  field.value=field.value.substring(0,field.value.length-2);
  field.select();         
        return false;
    } 
 return true;

}
function checkDateFromTo(fromField,toField){
    /* 
    ****************************************************************************************************
    *  함수설명: 최종적으로 입력된 시작일과 종료일 두 날짜가 적합한지를 검사한다.
    *
    * fromField    : 시작일자 입력란의 필드 객체
    * ToField    : 종료일자 입력란의 필드 객체
    *
    * 사용예  <input type='text' name='test3' onBlur="javascript:checkDate('시작일',this);" >
    ****************************************************************************************************
    */
 if(!checkDate("시작일자",fromField) || !checkDate("종료일자",toField) ) {
 return false;
 }
 else if(fromField.value > toField.value){
 alert("시작일자가 종료일자보다 큽니다");
 fromField.focus();
 fromField.select(); 
 return false; 
 }
 return true;

}


function checkNotEmpty(fieldCalledName,field){
    /* 
    ****************************************************************************************************
    *  함수설명: 필수적으로 입력되어야 하는 입력란이 비어있지거나 공백 밖에 있지 않은지 검사한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    *
    * 사용예  <input type='text' name='test3' onBlur="javascript:checkDate('시작일',this);" >
    ****************************************************************************************************
    */
    if(isEmpty(field.value))
    {
     alert(fieldCalledName+"은(는) 필수적으로 입력되어야 하는 값이므로 비워두면 안 됩니다");
     field.focus();
     field.select();
     return false;
    }
    return true;
}

function checkRCN(field){
    /* 
    ****************************************************************************************************
    *  함수설명: 해당필드가 주민등록번호로 적합한지 검사를 한다.
    *
    * field : html에서 name으로 지정된 입력필드 객체
    *
    * 사용예 :  <input type="text" value="" name='RCN' maxlength=13 onBlur="javascript:checkRCN(this);"
         onkeyup="javascript:checkInputNumber('주민등록번호',this);">
    *                   최종제출값 검사 스크립트에도 포함
    ****************************************************************************************************
    */
 field.value=trim(field.value);
 if(!checkInputNumber("주민등록번호",field)) return false;  // 숫자로만 이루어 있지 않으면 부적합
 if(field.value.length!=13) {        // 글자수가 13자리가 아니라면 부적합
  alert("주민등록번호 자리수가 모자랍니다."); 
        field.focus();
        field.select();
  return false;
 }

 var sex = field.value.substring(6,7); 

 if(!isContainsOnly(sex,"1234")) { 
  alert("주민등록번호 8번째 자리의 성별표시가 맞지 않습니다."); 
        field.focus();
        field.select();
  return false;
 } 
 

 var year = field.value.substring(0, 2);
 var month = field.value.substring(2, 4);
 var day = field.value.substring(4,6);

 
 if ( sex=="1" || sex=="2" ) year="19"+year;
 if ( sex=="3" || sex=="4" ) year="20"+year;

 // alert(year+"년 "+month+"월 "+day+"일" + "성별코드은 "+ sex);

    if (!isValidDay(year, month,day)){       // 앞자리의 생년월일 체크
        alert("주민등록번호 앞의 생년월일 부분이 잘못되었습니다. "+ year+ "년 " +month+"월 "+ day+"일이라는 날은 존재하지 않습니다.");
        field.focus();
        field.select();
        return false;
        }

 var mappingMulti=[2,3,4,5,6,7,8,9,2,3,4,5];
 var mustLastDigit=0;
 for (var i=0;i<12;i++)
 {
  mustLastDigit+= parseInt(field.value.substring(i,i+1))*mappingMulti[i];
 }
 mustLastDigit= (11-mustLastDigit%11)%10;
 if (field.value.substring(12,13)!=mustLastDigit) {   //주민등록번호 마지막자리 검사
  alert("유효한 주민등록번호가 아닙니다."); 
        field.focus();
        field.select();
  return false; 
 }
 return true;
}

function checkTogetherText(fieldCalledName,field,maxLen,notNull){
    /* 
    ****************************************************************************************************
    *  함수설명: 텍스트의 필수입력여부체크와 길이체크를 같이 한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    * maxlen    : 허용되는 글자의 최대길이
    * notNull   : 필수입력 필드인지의 여부(true,false)
    *
    * 사용예  <input type='text' name='test3' onBlur="javascript:checkTogetherText('테스트필드',this,10,true);"
       onkeyup="javascript:checkInputNumber('테스트필드',this);" >
    ****************************************************************************************************
    */
 field.value=trim(field.value);
    
    if ( ( notNull==true) && checkNotEmpty(fieldCalledName,field) && checkInputLength(fieldCalledName,field,maxLen) ) {
     return true;
    }
 
 if( (notNull==false)&& checkInputLength(fieldCalledName,field,maxLen) ) {
     return true; 
 }
 return false;

}
 
function checkTogetherNumber(fieldCalledName, field,min,max,notNull) {
    /* 
    ****************************************************************************************************
    *  함수설명: 해당 필드의 필수입력여부체크와 숫자의 범위체크를 같이 한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    * min    : 허용되는 숫자의 최소값
    * max    : 허용되는 숫자의 최대값
    * notNull   : 필수입력 필드인지의 여부(true,false)
    *
    * 사용예  <input type='text' name='test3' onBlur="javascript:checkTogetherNumber('테스트필드',this,10,100,true);"
       onkeyup="javascript:checkInputNumber('테스트필드',this);" >
    ****************************************************************************************************
    */

    if ( ( notNull==true) && checkNotEmpty(fieldCalledName,field) && checkNumber(fieldCalledName,field,min,max)) {
     return true;
    }
    if ( notNull==false) {
     if(isEmpty(field.value) || (checkNumber(fieldCalledName,field,min,max)) ) return true;
    }
    
    return false;
}

function checkTogetherDate(fieldCalledName, field, notNull){
    /* 
    ****************************************************************************************************
    *  함수설명: 날짜의 필수입력여부체크와 적합성 체크를 같이 한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field    : html에서 name으로 지정된 입력필드 객체
    * notNull   : 필수입력 필드인지의 여부(true,false)
    *
    * 사용예  <input type='text' name='test3' onBlur="javascript:checkTogetherText('테스트필드',this,true);"
       onkeyup="javascript:checkInputNumber('테스트필드',this);" >
    ****************************************************************************************************
    */   
    
    if ( ( notNull==true) && checkNotEmpty(fieldCalledName,field) && checkDate(fieldCalledName,field)) {
     return true;
    }
    if ( notNull==false) {
     if(isEmpty(field.value) || (checkDate(fieldCalledName,field)) ) return true;
    }
    
    return false;
}

function checkDatesFromTo(from_field_name, to_field_name) {
    /* 
    ****************************************************************************************************
    *  함수설명: 필드 이름을 기준으로 최종적으로 입력된 시작일과 종료일 두 날짜의 짝들이 적합한지를 검사한다.
    *
    * from_field_name  : 시작일자 입력란의 필드명을 나타내는 문자열
    * to_field_name  : 종료일자 입력란의 필드명을 나타내는 문자열
    ****************************************************************************************************
    */ 
    
 var from_fields = document.getElementsByName(from_field_name);
 var to_fields = document.getElementsByName(to_field_name);

 for(var i=0; i<from_fields.length; i++) {
  if(!checkDateFromTo(from_fields[i], to_fields[i])) return false
 }
 return true;
}

function checkRCNs(field_name) {
    /* 
    ****************************************************************************************************
    *  함수설명: 해당필드가 주민등록번호로 적합한지 검사를 한다.
    *
    * field_name   : html에서 name으로 지정된 입력필드명을 나타내는 문자열
    *
    ****************************************************************************************************
    */ 
 var fields = document.getElementsByName(field_name); 
 for(var i=0; i<fields.length; i++) {
  //if(!checkRCNs(fields[i])) return false
  if(!checkRCN(fields[i])) return false
 }
 return true;
}

function checkTogetherTexts(fieldCalledName, field_name, maxLen, notNull) {
    /* 
    ****************************************************************************************************
    *  함수설명: 해당 필드명을 가진 텍스트필드들의  필수입력여부체크와 길이체크를 같이 한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field_name   : html에서 name으로 지정된 입력필드명을 나타내는 문자열
    * maxlen    : 허용되는 글자의 최대길이
    * notNull   : 필수입력 필드인지의 여부(true,false)
    ****************************************************************************************************
    */

 var fields = document.getElementsByName(field_name); 
 for(var i=0; i<fields.length; i++) {
  if(!checkTogetherText(fieldCalledName, fields[i], maxLen, notNull)) return false
 }
 return true;
}


function checkTogetherNumbers(fieldCalledName, field_name, min, max, notNull) {
    /* 
    ****************************************************************************************************
    *  함수설명: 해당 필드명을 가진 필드들의 필수입력여부체크와 길이체크를 같이 한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field_name   : html에서 name으로 지정된 입력필드명을 나타내는 문자열
    * min    : 허용되는 숫자의 최소값
    * max    : 허용되는 숫자의 최대값
    * notNull   : 필수입력 필드인지의 여부(true,false)
    *
    ****************************************************************************************************
    */
 var fields = document.getElementsByName(field_name); 
 for(var i=0; i<fields.length; i++) {
  if(!checkTogetherNumber(fieldCalledName, fields[i], min, max, notNull)) return false
 }
 return true;
}

function checkTogetherDates(fieldCalledName, field_name, notNull) {
    /* 
    ****************************************************************************************************
    *  함수설명:  해당 필드명을 가진 필드들의 필수입력여부체크와 날짜 적합성 체크를 같이 한다.
    *
    * fieldCalledName : 체크를 할 입력필드의 한글명칭. 에러 메시지 출력 시에 사용한다.
    * field_name   : html에서 name으로 지정된 입력필드명을 나타내는 문자열
    * notNull   : 필수입력 필드인지의 여부(true,false)
    ****************************************************************************************************
    */   
 var fields = document.getElementsByName(field_name); 
 for(var i=0; i<fields.length; i++) {
  if(!checkTogetherDate(fieldCalledName, fields[i], notNull)) return false
 }
 return true;
}

function checkDateWithin(checkField,fromDate,toDate){
    /* 
    ****************************************************************************************************
    *  함수설명: 정해진 두 날짜 사이에 어느 날짜가 속하는지 검사한다.
    * checkField    : 검사를 할 필드
    * fromDate    : 시작일자를 나타낸 문자열
    * ToDate    : 종료일자를 나타낸 문자열
    ****************************************************************************************************
    */ 
 
 if  ( (fromDate>checkField.value) || (checkField.value>toDate )) {
     alert("날짜가 적절한 범위를 벗어났습니다."); 
     checkField.focus();
     checkField.select(); 
     return false;
 }
 else return true;

}


function checkDatesInOut(innerFromField_name,innerToField_name, outerFromField,outerToField){
    /* 
    ****************************************************************************************************
    *  함수설명: 시작일과 종료일의 짝들이 큰 범위의 시작일과 종료일의 범위에 속하는지를 검사한다.
    * innerFromField_name : 작은 범위의 시작일 필드이름을 나타내는 문자열. 예를 들면 수감별 감사시작일 입력 필드의 이름.
    * innerToField_name : 작은 범위의 종료일 필드이름을 나타내는 문자열. 예를 들면 수감별 감사종료일 입력 필드의 이름.
    * outerFromField  : 큰 범위의 시작일 필드객체. 예를 들면 전체 감사기간의 시작일 필드 객체
    * outerToField  : 큰 범위의 종료일 필드객체. 예를 들면 전체 감사기간의 종료일 필드 객체
    *
    * 사용예    (스크립트 내에서)
    * if(!checkDatesInOut("aud_sdate", "aud_edate", document.all.tot_aud_sdate,document.all.tot_aud_edate)) return false;
    ****************************************************************************************************
    */ 
 
 if(!checkDateFromTo(outerFromField, outerToField)) return false;
 
 var innerFromFields = document.getElementsByName(innerFromField_name); 
 var innerToFields = document.getElementsByName(innerToField_name);  

 for(var i=0; i<innerToFields.length; i++) {
 if(!checkDateFromTo(innerFromFields[i], innerToFields[i])) return false;
 if(!checkDateWithin(innerFromFields[i],outerFromField.value, outerToField.value)) return false;
 if(!checkDateWithin(innerToFields[i],outerFromField.value, outerToField.value)) return false;
 } 
 return true;
}

function getQuartetFromDate(date){
    /*
    *********************************************************************************************************
    *   함수설명 : 날짜에 해당하는 분기를 반환한다.
    * date   : YYYYMMDD
    ***********************************************************************************************************
    */
 if(date.substring(5,7) == "01") {
  date = date.substring(0,4) + "년 " + " 1/4분기";
 } else if(date.substring(5,7) == "04") {
  date = date.substring(0,4) + "년 " + " 2/4분기";
 } else if(date.substring(5,7) == "07") {
  date = date.substring(0,4) + "년 " + " 3/4분기";
 } else if(date.substring(5,7) == "10") {
  date = date.substring(0,4) + "년 " + " 4/4분기";
 } else {
  date = date.substring(0,4) + "년 ";
 }
 return date;
}

/**
 * Tab_onclick 이벤트
 * 탭 선택시 처리
 */
function btnTab1_onclick() {
 frmThis.btnTab1.style.backgroundImage = meURL_TABON;
 frmThis.btnTab2.style.backgroundImage = meURL_TAB;
 frmThis.btnTab1.className="BTNTABON";
 frmThis.btnTab2.className="BTNTAB";
 
 pnlTab1.style.visibility = "visible";
 pnlTab2.style.visibility = "hidden";
 pnlTab3.style.visibility = "hidden";
 
 //서브버튼컨트롤
 gSetAuthority(frmThis.imgDetail, true);
 gSetAuthority(frmThis.imgHovrWrt, false);
}


/**
 * Tab_onclick 이벤트
 * 탭 선택시 처리
 */
function btnTab2_onclick() {
 frmThis.btnTab1.style.backgroundImage = meURL_TAB;
 frmThis.btnTab2.style.backgroundImage = meURL_TABON;
 frmThis.btnTab1.className="BTNTAB";
 frmThis.btnTab2.className="BTNTABON";
 
 pnlTab1.style.visibility = "hidden";
 if ( type == "N" ) {
  pnlTab2.style.visibility = "visible";
 } else {
  pnlTab3.style.visibility = "visible";
 }
 
 //서브버튼컨트롤
 gSetAuthority(frmThis.imgDetail, false);
 gSetAuthority(frmThis.imgHovrWrt, true);
}

/* 
********************************************************************************************************* 
*   함수설명  : 문자열에서  문자간의 공백을 제거한다. 
* str    : 문자열 
*********************************************************************************************************** 
*/ 
function PerfactTrim(val){ 
       var rtnVal = ""; 
       var len = val.length; 
 
       for(var i=0;i<len;i++){ 
         if(val.substring(i, i+1) != " "){ 
          rtnVal = rtnVal + val.substring(i, i+1);  
         } 
       } 
       
       return rtnVal; 
}  

/*
***************************************************************************************
* 함수설명 : 메시지를 출력한다.
***************************************************************************************
*/
function gWriteText (objTagID, strText){
 
 if (objTagID == ""){
 
  window.status = strText;
 }
 else if (objTagID == "vbOKOnly" ){
   
  gOkMsgBox(strText, objTagID);
    } 
 else if (objTagID == "vbYesNo"){
   
  return gYesNoMsgBox(strText, objTagID);
 }
 else if (objTagID == "vbYesNoCancel"){
   
  return gYesNoCancelMsgBox(strText , objTagID);
 }  
   else if (objTagID == "vbProc"){   
    
    gShowWait(strText); 
 } 
 else{ 
  
  objTagID.innerText = strText;
 }
}
/*
***************************************************************************************
* 함수설명 : 처리중 화면을 띄운다.
***************************************************************************************
*/
function gShowWait (flgWait){
 
 if (flgWait == true) {
  window.document.forms(0).style.cursor="wait" ;
  document.all.procBack.style.visibility="visible";    
  mobjSCGLCtl.DoEventQueue(); 
 }else {
  window.document.forms(0).style.cursor="default";
  document.all.procBack.style.visibility="hidden" ;
 } 
}

/* ==================================jyk 추가 start ==============================================*/

/*
***************************************************************************************
* 함수설명 : Grid에서 체크된 항목이 없는지 체크한다.
***************************************************************************************
*/
function gCheckSaveData (objsprSht, strChkBox){
 
 if (mobjSCGLSpr.GetClip2(objsprSht, strChkBox, 1, -1, 0, "\t").indexOf("1") < 0) {
  gErrorMsgBox("저장할 자료" +meMAKE_CHOICE, "");
  
  return true ;
 }
}

/*
***************************************************************************************
* 함수설명 : 실데이타가 변경된 경우 체크박스 체크, flag 처리 
***************************************************************************************
*/
function gSetByChangeData (objsprSht, lngCol, lngRow, strChkBox){

 if (mobjSCGLSpr.GetDataField(objsprSht, lngCol, false) != strChkBox){
  var opOldFlg = mobjSCGLSpr.GetFlag(objsprSht, lngRow, true);
 
  mobjSCGLSpr.CellChanged(objsprSht, lngCol, lngRow);
  
  if (mobjSCGLSpr.GetTextBinding(objsprSht, strChkBox, lngRow)=="0"){
   mobjSCGLSpr.SetTextBinding(objsprSht, strChkBox, lngRow, "1");
  }
 }

}

/*
***************************************************************************************
* 함수설명 : 체크한 데이타 취합해서 XML로 작성
***************************************************************************************
*/
function gMakeXMLwithChkData (objSprSht, strChkBox){

 var strXMLData = "",  strTmpXML = "";  // 삭제할 조건(XML), XMLData를 Edit하기 위한 변수
 var intSelCnt = 0;
 var arrReturn = new Array();
 
 for( var i=0; i <=  mobjSCGLSpr.GetMaxRows(objSprSht) ; i++){
   
  if (mobjSCGLSpr.GetTextBinding(objSprSht, strChkBox, i )=="1"){
   intSelCnt++;
   
  //삭제하도록 선택한 데이타의 data flag 값 변경
   switch (mobjSCGLSpr.GetFlag(objSprSht, i, true)){
    case meCLS_FLAG :
     mobjSCGLSpr.SetFlag(objSprSht, meDEL_FLAG ,  i);
     break;
    case meINS_FLAG :
     mobjSCGLSpr.SetFlag(objSprSht, meID_FLAG ,  i);
     break;
    case meUPD_FLAG :
     mobjSCGLSpr.SetFlag(objSprSht, meUD_FLAG ,  i);
     break;
    case meIU_FLAG :
     mobjSCGLSpr.SetFlag(objSprSht, meIUD_FLAG ,  i);
     break;
   } 
   
   // XMLdata 만들기--->공통함수화(?) 아니면 시너지함수 활용해서....
   strTmpXML = mobjSCGLSpr.GetClipBinding(objSprSht, "-1", i, i, 2);
   strTmpXML = strTmpXML.substring(16, strTmpXML.indexOf("</DataSet>"));
   strTmpXML = "<Table><DataFlag>" + mobjSCGLSpr.GetFlag(objSprSht, i, true) + "</DataFlag>" + strTmpXML;
   strXMLData = strXMLData + strTmpXML ;
  }
 }
 strXMLData = "<DataSet>" + strXMLData + "</DataSet>" ;
 arrReturn[0] = strXMLData;
 arrReturn[1] = intSelCnt;
 return arrReturn ;
}

/*
***************************************************************************************
* 함수설명 : 삭제한 데이타 row  sheet에서 삭제
***************************************************************************************
*/
function gDeleteChkData (objSprSht, strChkBox){

 for( var i=0; i <=  mobjSCGLSpr.GetMaxRows(objSprSht) ; i++){
  if (mobjSCGLSpr.GetTextBinding(objSprSht, strChkBox, i )=="1"){
   //삭제된 Grid의 Row를 시트에서 삭제  
   mobjSCGLSpr.DeleteRow(objSprSht, i);
   i--;
  }
 }
}

/*
***************************************************************************************
* 함수설명 : 체크박스 전체를 선택/선택해제 처리
***************************************************************************************
*/
function gAllCheckChkBox (objSprSht, lngCol, strChkBox, chkFlg){
 
 if (chkFlg=="Y"){
  for( var i=1; i <=  mobjSCGLSpr.GetMaxRows(objSprSht) ; i++){
   mobjSCGLSpr.SetTextBinding(objSprSht, strChkBox, i, "0");
   chkFlg = "N";
  }
 } else { 
  for( var j=1; j <=  mobjSCGLSpr.GetMaxRows(objSprSht) ; j++){
   mobjSCGLSpr.SetTextBinding(objSprSht, strChkBox, j, "1");
   chkFlg = "Y";
  }
 }
 
 return chkFlg;
}

/*
***************************************************************************************
* 함수설명 : 체크박스 클릭 시 상태에 따라 내부 flag 설정
***************************************************************************************
*/
function gSetFlagByChkBox (objSprSht, lngCol, lngRow, opOldFlg){

 if (mobjSCGLSpr.GetText(objSprSht, lngCol, lngRow)=="1" ){
  if (mobjSCGLSpr.GetFlag(objSprSht, lngRow, true)!=0) 
   opOldFlg = mobjSCGLSpr.GetFlag(objSprSht, lngRow, true);
  else if ( opOldFlg==null )
   opOldFlg = 2;

   
  mobjSCGLSpr.SetFlag(objSprSht, opOldFlg,  lngRow);
 }
 else{
  opOldFlg = mobjSCGLSpr.GetFlag(objSprSht, lngRow, true);
  mobjSCGLSpr.SetFlag(objSprSht, 0,  lngRow);
 }
 
 return opOldFlg;
}
/* ==================================jyk 추가 end ==============================================*/

/*****************************************************************************************/
function rmEmptyRowSht(objSprSht) 
{ 
 var str = ""; 
 for(var i = 1; i <= mobjSCGLSpr.GetMaxRows(objSprSht); i++){ 
  if(mobjSCGLSpr.GetTextBinding(objSprSht,"ATTCH_FILE_NM", i) == "") 
   mobjSCGLSpr.DeleteRow(objSprSht, i--); 
 } 
   gWriteText(lblStatus, str); 
 return objSprSht; 
} 
/****************************************************************************************/
function gFileUp(mobjSCGLFileUp)
{
 var rtnFile;
 rtnFile = mobjSCGLFileUp.OpenFileUp(1, "", FTP_SERVER, 21, FTP_USR, FTP_PWD, FILEDOWN_DIR, false, true);
 return rtnFile;
}

/*
***************************************************************************************
* 브라우저 쿠키값 간단하게 확인하기
*브라우져의 주소란에 
*javascript:alert(document.cookie)
***************************************************************************************
*/


/*
***************************************************************************************
*
*파일 용량 체크하기
*<form>
*<input type=file name="filename" onChange="getFileSize(this.value,this.name)">
*</form>

*<img name=tmp width=0 height=0>
***************************************************************************************
*/
function getFileSize(url,name)
{
 tmp.dynsrc = url;
 if (tmp.fileSize > 8388608)
 {
  alert ("8M 이상은 안되요");
  document.getElementById(name).value = "";
 }
}

/*
***************************************************************************************
* 방문자 해상도 구하기
***************************************************************************************
*/ 

document.write(screen.width+" * "+screen.height)





/*
***************************************************************************************
*모든 링크 점선 한방에 없애기

*아래 함수를 <head>에 넣으면 파일에 있는 모든 링크와 이미지에 점선이 없어집니다.
***************************************************************************************
*/
function bluring()
{
if(event.srcElement.tagName=="A"||event.srcElement.tagName=="IMG")document.body.focus();
}
document.onfocusin=bluring

 
/*
***************************************************************************************
*버튼으로 새창(_blank) 띄우기

*window.open 액션에서 옵션이 없으면 새창이 뜨게된다
*<input type=button value="새창" onclick="window.open('test.htm');">
***************************************************************************************
*/


/*
***************************************************************************************
*한번 클릭하면 비활성화 되는 전송버튼

*<input type="submit" value="전송" onClick="this.disabled=true">
***************************************************************************************
*/

/*
***************************************************************************************
*이메일 형식 체크
* if(str.pet_email.value.length > 0){
*   var regExp = /[a-z0-9]{2,}@[a-z0-9-]{2,}\.[a-z0-9]{2,}/i;
*    if(!regExp.test(str.pet_email.value)){
*     alert("잘못된 e-mail 형식입니다.");
*     str.pet_email.value = "";
*     str.pet_email.focus();
*     return false;
*    }
* }
***************************************************************************************
*/

/*
***************************************************************************************
*아이프레임을 내용에 따라 크기(폭,높이) 바꿔주는 스크립트

*아래 스크립트는 iframe에 들어갈 파일을 건드리지 않아도 됩니다. 
*객체에 대한 read/write권한을 위해서 같은 계정내의 파일이기만 하면 됩니다. 
*iframe에 들어갈 파일의 로딩이 완료되는 순간 doResize() 함수를 호출하여 iframe을 포함하는 TD태그의 width와 height를 강제로 바꿔줍니다. 
*Windows 2000, IE 6.0 에서는 잘 보이는데 다른 환경에서는 어떨런지 모르겠네요 :)

* 사용예.
*<table border="0" cellpadding="0" cellspacing="0"> 
*<tr> 
*<td id="container"><iframe src="your_file.html" name="myframe" width="100%" height="100%" marginwidth="0" marginheight="0" frameborder="no" onload="doResize()"></iframe></td> 
*</tr> 
*</table> 
***************************************************************************************
*/
function doResize() 
{ 
container.height = myframe.document.body.scrollHeight; 
container.width = myframe.document.body.scrollWidth; 
}  



/*
***************************************************************************************
*하루동안 팝업 창 띄우지 않기

*사용예
*=================== 팝업창 소스 ===================

*<SCRIPT language="JavaScript">
*	function setCookie( name, value, expiredays )
*    	{
*		var todayDate = new Date();
*		todayDate.setDate( todayDate.getDate() + expiredays );
*		document.cookie = name + "=" + escape( value ) + "; path=/; expires=" + todayDate.toGMTString() + ";"
*	}
*	function closeWin()
*	{
*		if ( document.forms[0].Notice.checked )
*   			setCookie( "Notice1", "done" , 1);
*			self.close();
*	}
*</SCRIPT>

*	<input type="checkbox" name="Notice" ><a href= "javascript:history.onclick=closeWin()">하루동안 창띄우지 않기</a>
***************************************************************************************
*/

function getCookie( name )
{
var nameOfCookie = name + "=";
var x = 0;
while ( x <= document.cookie.length )
{
var y = (x+nameOfCookie.length);
if ( document.cookie.substring( x, y ) == nameOfCookie ) {
if ( (endOfCookie=document.cookie.indexOf( ";", y )) == -1 )
endOfCookie = document.cookie.length;
return unescape( document.cookie.substring( y, endOfCookie ) );
}
x = document.cookie.indexOf( " ", x ) + 1;
if ( x == 0 )
break;
}
return "";
}

if ( getCookie( "Notice1" ) != "done" )
{
noticeWindow  =  window.open('newwin/pop.htm','Notice1','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=363,height=733,left=30,top=120');
noticeWindow.opener = self;

}





 
/*
***************************************************************************************
*일정시간마다 새로고침

* window.setTimeout('window.location.reload()',5000); //5초마다 리플리쉬 시킨다.
***************************************************************************************
*/
 

/*
***************************************************************************************
*핫키

*특정 키를 누르면 지정한 곳으로 이동하게 해주는 소스입니다.
***************************************************************************************
*/
var key = new Array();
key['n'] = "http://phpschool.com/bbs2/inc_board.html?code=news2";
key['q'] = "http://phpschool.com/v2/html/q_a_board.html";
key['d'] = "http://phpschool.com/bbs2/inc_board.html?code=down2";

function getKey(keyStroke) {
   isNetscape=(document.layers);
   eventChooser = (isNetscape) ? keyStroke.which : event.keyCode;
   which = String.fromCharCode(eventChooser).toLowerCase();
   for (var i in key)
     if (which == i) window.location = key[i];
}
document.onkeypress = getKey;

 

/*---------------------------------------------

* String 문자 자르기.
---------------------------------------------*/
String.prototype.cut = function(len) {
 var str = this;
 var l = 0;
 for (var i=0; i<str.length; i++) {
  l += (str.charCodeAt(i) > 128) ? 2 : 1;
  if (l > len) return str.substring(0,i);
 }
 return str;
}

/*---------------------------------------------

* String 공백 지우기.
---------------------------------------------*/
String.prototype.trim = function(){
 // Use a regular expression to replace leading and trailing
 // spaces with the empty string
 return this.replace(/(^\s*)|(\s*$)/g, "");
}

/*---------------------------------------------
* String 총 바이트 수 구하기.
---------------------------------------------*/
String.prototype.bytes = function() {
 var str = this;
 var l = 0;
 for (var i=0; i<str.length; i++) l += (str.charCodeAt(i) > 128) ? 2 : 1;
 return l;
}

/*---------------------------------------------
* iframe의 height를 body의 내용만큼 자동으로 늘려줌.
---------------------------------------------*/
function resizeRetry(){
 if(ifrContents.document.body.readyState == "complete"){
  clearInterval(ifrContentsTimer);
 }
 else{
  resizeFrame(ifrContents.name);
 }
}

var ifrContentsTimer;
var ifrContents;

function resizeFrame(name){

        var oBody = document.body;
        var oFrame = parent.document.all(name);
  ifrContents = oFrame;
        var min_height = 613; //iframe의 최소높이(너무 작아지는 걸 막기위함, 픽셀단위, 편집가능)
        var min_width = 540; //iframe의 최소너비
        var i_height = oBody.scrollHeight + 10; 

        var i_width = oBody.scrollWidth + (oBody.offsetWidth-oBody.clientWidth);

        if(i_height < min_height) i_height = min_height;
        if(i_width < min_width) i_width = min_width;
        oFrame.style.height = i_height;
        ifrContentsTimer = setInterval("resizeRetry()",100);
}

/*---------------------------------------------
* 클립보드에 해당 내용을 복사함.
---------------------------------------------*/

function setClipBoardText(strValue){
 window.clipboardData.setData('Text', strValue);
 alert("" + strValue +" \n\n위 내용이 복사되었습니다.\n\nCtrl + v 키를 사용하여, 붙여 넣기를 사용하실 수 있습니다.");
}


/*---------------------------------------------
select 에서 기존의 선택 값이 선택되게
----------------------------------------------*/
function selOrign(frm,val){
 for(i=0; i < frm.length ; i++){
  if(frm.options[i].value == val){
   frm.options.selectedIndex = i ;
   return;
  }
 }
}

/*---------------------------------------------
checkbox 에서 기존의 선택 값이 선택되게
----------------------------------------------*/
function chkboxOrign(frm,val){
 if(frm.length == null){
  if(frm.value == val)
   frm.checked = true;
 }else{
  for(i=0;i<frm.length;i++){
   if(frm[i].value == val){
    frm[i].checked = true;
   }
  }
  return;
 }
}

function chkboxOrign_multi(frm,objchk,val){
 var i = 0;
 for(i=0;i<frm.elements.length;i++){
  if(frm.elements[i].name == objchk){
   if(frm.elements[i].value == val){
    frm.elements[i].checked = true;
   }
  }
 }
}

/*---------------------------------------------
radio 에서 기존의 선택 값이 선택되게
----------------------------------------------*/
function radioOrign(frm,val){
 for(i=0; i < frm.length ; i++){
  if(frm[i].value == val){
   frm[i].checked = true ;
   return ;
  }
 }
}

/*---------------------------------------------
숫자만 입력받기
예) onKeyDown="return onlyNum();"
----------------------------------------------*/
function onlyNum(){
 if(
  (event.keyCode >= 48 && event.keyCode <=57) ||
  (event.keyCode >= 96 && event.keyCode <=105) ||
  (event.keyCode >= 37 && event.keyCode <=40) ||
  event.keyCode == 9 ||
  event.keyCode == 8 ||
  event.keyCode == 46
  ){
  //48-57(0-9)
  //96-105(키패드0-9)
  //8 : backspace
  //46 : delete key
  //9 :tab
  //37-40 : left, up, right, down
  event.returnValue=true;
 }
 else{
  //alert('숫자만 입력 가능합니다.');
  event.returnValue=false;
 }
}

/*---------------------------------------------
지정된 길이반큼만 입력받기
예) onKeyUp="return  checkAllowLength(현재숫자보여지는객체,숫자셀객체 ,80);" onKeyDown="return checkAllowLength(현재숫자보여지는객체,숫자셀객체 ,80);"
----------------------------------------------*/

function checkAllowLength(objView, objTar, max_cnt){
 if(event.keyCode > 31 || event.keyCode == "") {
  if(objTar.value.bytes() > max_cnt){
   alert("최대 " + max_cnt + "byte를 넘길 수 없습니다.");
   objTar.value = objTar.value.cut(max_cnt);
  }
 }
 objView.value = objTar.value.bytes();
}

  
/*--------------------------------------------
이미지 리사이즈
---------------------------------------------*/
function resizeImg(imgObj, max_width, max_height){

 var dst_width;
 var dst_height;
 var img_width;
 var img_height;

 img_width = parseInt(imgObj.width);
 img_height = parseInt(imgObj.height);

 if(img_width == 0 || img_height == 0){
  imgObj.style.display = '';
  return false;
 }

    // 가로비율 우선으로 시작
    if(img_width > max_width || img_height > max_height) {
        // 가로기준으로 리사이즈
        dst_width = max_width;
        dst_height = Math.ceil((max_width / img_width) * img_height);

        // 세로가 max_height 를 벗어났을 때
        if(dst_height > max_height) {
   dst_height = max_height;
   dst_width = Math.ceil((max_height / img_height) * img_width);
        }

        imgObj.width = dst_width;
        imgObj.height = dst_height;
    }
    // 가로비율 우선으로 끝

 imgObj.style.display = '';

 return true;
}
/*---------------------------------------------
xml data 읽어오기
----------------------------------------------*/
function getXmlHttpRequest(_url, _param){
    var objXmlConn;
    try{objXmlConn = new ActiveXObject("Msxml2.XMLHTTP.3.0");}
    catch(e){try{objXmlConn = new ActiveXObject("Microsoft.XMLHTTP");}catch(oc){objXmlConn = null;}}

    if(!objXmlConn && typeof XMLHttpRequest != "undefined") objXmlConn = new XMLHttpRequest();

    objXmlConn.open("GET", _url + "?" + _param, false);
    objXmlConn.send(null);
 
 //code|message 형태로 리턴
    return objXmlConn.responseText.trim().split("|");
}


/*---------------------------------------------------
cookie 설정
-------------------------------------------------------*/

function getCookieVal (offset) {
   var endstr = document.cookie.indexOf (";", offset);
   if (endstr == -1) endstr = document.cookie.length;
   return unescape(document.cookie.substring(offset, endstr));
}

function GetCookie (name) {
   var arg = name + "=";
   var alen = arg.length;
   var clen = document.cookie.length;
   var i = 0;
   while (i < clen) { //while open
      var j = i + alen;
      if (document.cookie.substring(i, j) == arg)
         return getCookieVal (j);
      i = document.cookie.indexOf(" ", i) + 1;
      if (i == 0) break;
   } //while close
   return null;
}

function SetCookie (name, value) {
   var argv = SetCookie.arguments;
   var argc = SetCookie.arguments.length;
   var expires = (2 < argc) ? argv[2] : null;
   var path = (3 < argc) ? argv[3] : null;
   var domain = (4 < argc) ? argv[4] : null;
   var secure = (5 < argc) ? argv[5] : false;
   document.cookie = name + "=" + escape (value) +
      ((expires == null) ? "" :
         ("; expires=" + expires.toGMTString())) +
      ((path == null) ? "" : ("; path=" + path)) +
      ((domain == null) ? "" : ("; domain=" + domain)) +
      ((secure == true) ? "; secure" : "");
}

/* ---------------------------------------------
 * 함수명 : checkSpecialChar
 * 설  명 : 특수문자 체크
 * 예) if(!checkSpecialChar()) return;
 ---------------------------------------------*/
function checkSpecialChar(_obj){
    if(_obj.value.search(/[\",\',<,>]/g) >= 0) {
        alert("문자열에 특수문자( \",  ',  <,  > )가 있습니다.\n특수문자를 제거하여 주십시오!");
        _obj.select();
        _obj.focus();
    }
}

