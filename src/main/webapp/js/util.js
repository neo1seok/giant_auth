/**
 * 
 */

function CheckHexStr(input,lenth_){
	//alert(input);
	  // 입력한 ID를 Check하기 위해 가져옵니다.
	console.log('CheckHexStr');
	console.log(input);
	console.log(input.length);
	console.log(2*lenth_);
	
	if(input.length != 2*lenth_) return false
    
    
    // 정규표현식으로 한글만 선택하도록 만듭니다.
    var languageCheck = /[^a-fA-F0-9]/;
	console.log(languageCheck.test(input));
    if (languageCheck.test(input)) {
        return false;
    }
    return true;
}

function keyChange(item) {

	var kyeinput = document.input.key_value;
	var slotnoinput = document.input.slot_no;

	var test = item.value;
	kyeinput.disabled = false;
	//slotnoinput.disabled = false;
	var message = document.getElementById("message");

	switch (test) {
	case 'chip_key':
		kyeinput.disabled = true;
		//slotnoinput.disabled = true;
		message.innerHTML = "칩 개별로 키저장";
		break;
	case 'mstr_key':
		//kyeinput.value = "2";
		message.innerHTML = "상품 별로 마스터키와 슬롯 지정 ";
		break;
	case 'one_key':
		//kyeinput.value = "3";
		//slotnoinput.disabled = true;
		message.innerHTML = "상품별로 1개의 키 존재 ";
		break;

	}

}

function UpdateDropBox(mapKeyType) {
	var frm = document.input;

	for (var i = 0; i < 16; i++) {
		var op = new Option();
		op.value = i; // 값 설정
		op.text = i; // 텍스트 설정

		//op.selected = true; // 선택된 상태 설정 (기본값은 false이며 선택된 상태로 만들 경우에만 사용)

		frm.slot_no.options.add(op); // 옵션 추가

	}
	
	for ( var k in mapKeyType) {
		var op = new Option();
		op.value = k; // 값 설정
		op.text = mapKeyType[k]; // 텍스트 설정

		//op.selected = true; // 선택된 상태 설정 (기본값은 false이며 선택된 상태로 만들 경우에만 사용)

		frm.key_type.options.add(op); // 옵션 추가
		
		
		
	}


}

function addOption(value,txt){
    var frm = document.input;
    var op = new Option();
    op.value = value; // 값 설정
    op.text = txt; // 텍스트 설정

    op.selected = true; // 선택된 상태 설정 (기본값은 false이며 선택된 상태로 만들 경우에만 사용)

    frm.prd_no.options.add(op); // 옵션 추가
    
	
	
}



function UpdateProductDropInfo(){
	
	
	var jsArr = new Array();

	
	
	
	
	
	
	//mapValue = obj["mapvValue"];
	//mapValue = obj;

	//document.getElementById("message").innerHTML = obj["mapvValue"]["prd 1000342"];

	for ( var k in mapValue) {
		addOption(k, k);
		console.log(mapValue[k]["prd_name"]);
		console.log(mapValue[k]["key_type"]);
		console.log(mapKeyType[mapValue[k]["key_type"]]);
		
		
		
	}

	

}