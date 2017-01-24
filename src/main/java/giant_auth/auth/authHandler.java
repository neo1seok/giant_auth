package giant_auth.auth;

import giant_auth.comm.BaseHandler;
import giant_auth.comm.GiantDbHanlingMySQL;
import giant_auth.comm.IInvoke;
import giant_auth.comm.Protocol;
import giant_auth.comm.RESULT;
import giant_auth.comm.TABLE_NAMES;
import giant_auth.comm.Util;

import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.gson.Gson;
import com.mysql.jdbc.Connection;
import com.neolib.Util.NLoger;
import com.neolib.Util.NeoHexString;
import com.neolib.Util.RefParam;
import com.neolib.db.DefDbTableHandling;
import com.neolib.db.IDbHanling;
import com.neolib.db.IDbTableHandling;


/***
 * 
* @FileName : authHandler.java
* @Project : giant_auth
* @Date : 2017. 1. 17.
* @author : neo1seok
* @프로그램 설명 :
* This class is Handler that create sessions ,do authentication,make encrypted msg.
* Usually needed data is saved mysql db.
* authSublet use this class when be requested by clients(pos/get by http protocol) 
* mapInvoke is map object have main processor by mapping.
* At mapInvoke, key is command ,value is process 
* 
* 이 클래스는 세션을 생성,인증을 실행,메시지를 암호화 하는 핸들러 이다.
* 보통 필요한 데이터는 my sql db 에 저장된다.
* 클라이언트의 요청이 있을 때 authSublet이 이 클래스를 이용한다.
* mapInvoke는 map 오브젝트 이고 , 각 프로세서로 매핑된다.
*  mapInvoke 에서 키는 커맨드이고,값은 각 프로세서 이다. 
 */
public class authHandler extends BaseHandler<AUTH_CMD> implements iauthHandler {

//	interface IInvoke {
//		void run() throws Exception;
//	}

	
	
	
	
	Random r = new Random();
	
	private String ssn_uid;
	private String chp_uid;
	private String cfm_uid;
	private String lastError;
	
	private String sn;
	private String challenge;
	private String hostchallenge;
	String sectorID="0000";
		
	private String mac;

	protected String derivedkey;
	protected String latest_msk_uid;

	protected String msk_uid;

	private boolean isDebug;

	protected String device_msk_uid;

	private List<Map<String, Object>> listValidKeyList;
	
	public authHandler() {
		super();
		// TODO Auto-generated constructor stub
		NLoger.isSystemConsole = true;
		
		mapInvoke.put(AUTH_CMD.REQ_START_SESSION, invokeReqStartSession);
		mapInvoke.put(AUTH_CMD.AUTHENTICATION, invokeAuthentication);
		mapInvoke.put(AUTH_CMD.REQ_HOSTCHALLENGE, invokeReqHostchallenge);
		mapInvoke.put(AUTH_CMD.REQ_UPDATEINFO, invokeReqUpdateinfo);
		mapInvoke.put(AUTH_CMD.NOTY_UPDATERESULT, invokeNotyUpdateresult);
		mapInvoke.put(AUTH_CMD.REQ_APP_KEY, invokeReqAppKey);
		mapInvoke.put(AUTH_CMD.NOTY_APPKEYRESULT, invokeNotyAppkeyresult);
		mapInvoke.put(AUTH_CMD.REQ_END_SESSION, invokeReqEndSession);
		// neo1seok 2017.01.17 : mapInvoke 에 프로세서를 매핑한다.
		//neo1seok 2017.01.17 :Maps the processor to mapInvoke.


	
	}
	
	
	
	/**
	* @Name : invokeReqStartSessionIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* REQ_START_SESSION 에 대한 명령을 실행 하는 프로세스
	* session 을 시작 하고 challenge 값을 리턴한다.
	* sn을 전달 받아서 db에서 masterkey에 대한 정보를 얻어오고,
	* db에 세션 정보를 생성 한 후 필요한 값을 저장 한다. 
	* 
	* 
	* Process that execute commands for REQ_START_SESSION
	*  starts session and returns a challenge value
	* Sn to get information about masterkey in db,
	* Create the session information in db and store the required value.
	* 
	*/
	IInvoke invokeReqStartSession = new IInvoke() {
		public void run() throws Exception {
			device_msk_uid = "";
			sn = rcvProto.params.get("sn");
			// neo1seok 2017.01.17 :요청값으로 부터 sn을 얻어온다. 
			// Retrieves sn from the request value.			

			//
			
			sndProto.params.put("challenge", Util.ZeroHexStr(32));
			sndProto.params.put("uid", "");
			// neo1seok 2017.01.17 : 응답 값이 존재 해야 하기 때문에 설정함
			//Set because response value must exist.


			Map<String, Object> rowChp = itableChipHandler.selectSingle("sn", sn);
			if(rowChp == null)// neo1seok 2017.01.17 :sn 이 없는 경우 
				//If there is no sn
			{
				sndProto.params.put("result", RESULT.FAIL.toString());
				sndProto.params.put("error", AUTH_ERROR.NO_SN.toString());
				return ;
			}
			//int slotno = (int) rowChp.get("slot_no");
			msk_uid = (String) rowChp.get("msk_uid");
			chp_uid = (String) rowChp.get("chp_uid");
			
			
			
			if(msk_uid == null||msk_uid.isEmpty() )// neo1seok 2017.01.17 : master Key가 설정 안된 경우  
				//If Master Key is not set
			{
				sndProto.params.put("result", RESULT.FAIL.toString());
				sndProto.params.put("error", AUTH_ERROR.NO_MASTERKEY.toString());
				return ;
			}
			
			
			
			
			latest_msk_uid = getLatestMasterKey();// neo1seok 2017.01.17 : 최신 마스터 키를 구한다.
			//Obtain the latest master key.

			
			challenge = Util.getRand(32);// neo1seok 2017.01.17 :challenge 값 생성 
			//Generate challenge value
			hostchallenge = Util.ZeroHexStr(20);//Util.getRand(20);
			
			mapArg.clear();
			mapArg.put("chp_uid", chp_uid);
			mapArg.put("challenge", challenge);
			mapArg.put("hostchallenge", hostchallenge);
			mapArg.put("msk_uid", device_msk_uid);
			mapArg.put("latest_msk_uid", latest_msk_uid);
			mapArg.put("comment", "SESSION_START");
			 
		
			Log("TEST");
			
			for(Entry<String, String> tmp : mapArg.entrySet()){
				Log("{0} {1}",tmp.getKey(),tmp.getValue());
			}

			int cfmseq = itableSessionHandler.Insert(mapArg);
			// neo1seok 2017.01.17 :세션 정보를 db에 입력 한다.
			//Insert session information in db.
			
			Map<String, Object> dataRowSession = itableSessionHandler.selectSingle(cfmseq);
			ssn_uid = dataRowSession.get("ssn_uid").toString();
			
			sndProto.params.put("challenge", challenge);
			sndProto.params.put("uid", ssn_uid);
			// neo1seok 2017.01.17 : challenge 값과 세션 uid 를 리턴값으로 설정
			//Set the challenge value and the session uid as return values

		}
	};
	

	/**
	* @Name : invokeAuthenticationIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* 
	* AUTHENTICATION 에 대한 명령을 실행하는 프로세스
	* 전달 받은 mac 값과 
	* 저장된 session 정보에서 받은  challenge값 sn값을 바탕으로 mac값을 비교하고 
	* 전달받은 mac값을 비교하는 방식으로 인증을 한다.  
	* 인증 한후 최신 업데이트된 마스터 키가 존재할 경우 Update 를 OK로 설정하여 
	* 업데이트를 한다.
	* 
	* 
	* Process for excute commands for AUTHENTICATION
	* The value of mac delivered
	* Compare the mac values ​​based on the challenge value sn value received from the stored session information
	* Authentication is performed by comparing the mac values ​​received.
	* If the latest updated master key exists after authentication, set Update to OK
	* Update.
	*/
	IInvoke invokeAuthentication = new IInvoke() {

		public void run() throws Exception {
			String update = "";
			UpdateDataFromSession();// neo1seok 2017.01.17 :세션uid로부터 필요한 값을 필드에 업데이트 
			//Update required fields from session uid to field
			
			mac = rcvProto.params.get("mac");		// neo1seok 2017.01.17 :요청으로 부터 mac값을 가져온다. 
			//Get the mac value from the request.
			
			sndProto.params.put("update", "");// neo1seok 2017.01.17 : 응답 값을 설정한다. 
			//Set the response value.

			
			
			String valid_msk_uid = FindValidMac(mac,msk_uid,sectorID,sn);// neo1seok 2017.01.17 : mac 값으로 db내에서 계산해서  마스터키를 응답한다.
			//Computes in db as a mac value and responds to the master key.

			
			if(valid_msk_uid == null)// neo1seok 2017.01.17 : 맥값을 못찾았을 때  
				//When the mac value is not found
			{
				sndProto.params.put("result", RESULT.FAIL.toString());
				sndProto.params.put("error", AUTH_ERROR.NOT_MATCH_MAC.toString());
				return ;

			}
			
			if(valid_msk_uid != msk_uid)// neo1seok 2017.01.17 :db에 적혀 있는 값과 실제 칩에 들어 있는 값이 다를 때
				// When the value written in db differs from the value contained in the actual chip
			{
				NLoger.clog("MAY NOT UPDATED DB BEFORE");
				mapArg.clear();
				mapArg.put("msk_uid", valid_msk_uid);
				itableChipHandler.Update(chp_uid, mapArg);
				
			}
			
		
			
			
			if ( !valid_msk_uid.equals(latest_msk_uid))// neo1seok 2017.01.17 :Update 가 필요한 경우  
				//	If you need an update
			{
				update = "OK";
				sndProto.params.put("update", "OK");
			}
		}
	};
	
	
	/**
	* @Name : invokeReqHostchallengeIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* REQ_HOSTCHALLENGE 에 대한 명령을 실행하는 프로세스
	* hostchallenge 값을 리턴한다.	*
	* 
	*  
	* The process that executes the command for REQ_HOSTCHALLENGE
	* Returns hostchallenge value. 
	*/
	private IInvoke invokeReqHostchallenge = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();// neo1seok 2017.01.17 :세션uid로부터 필요한 값을 필드에 업데이트
			//Update required fields from session uid to field
			hostchallenge = Util.getRand(20);// neo1seok 2017.01.17 : hostchallenge 값을 생성 한다.
			//Create a hostchallenge value.

			
			mapArg.clear();
			mapArg.put("hostchallenge", hostchallenge);
			itableSessionHandler.Update(ssn_uid, mapArg);// neo1seok 2017.01.17 :db중 세션에 적는다. 
			//	Write to the session in db.

			
			NLoger.clog("hostchallenge:{0}",hostchallenge);
			sndProto.params.put("hostchallenge", hostchallenge);// neo1seok 2017.01.17 :응답 값에 설정한다. 
			//	Set to the response value.

		}
	};
	
	/**
	* @Name : invokeReqUpdateinfoIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* REQ_UPDATEINFO 에 대한 명령을 실행하는 프로세스
	* 새로운 마스터 키를 디바이스에 입력하기 위한 WRITE CODE와
	* MAC을 리턴 하는 프로세스
	* 
	* The process that executes the command for REQ_UPDATEINFO
	* Process to return WRITE CODE and MAC to input new master key to device
	*/
	IInvoke invokeReqUpdateinfo = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();// neo1seok 2017.01.17 :세션uid로부터 필요한 값을 필드에 업데이트
			//Update required fields from session uid to field
			
			String gen_nonce= rcvProto.params.get("gen_nonce");// neo1seok 2017.01.17 :gen_nonce 를 받는다.
			//Receive gen_nonce.

			
			
			Map<String, Object> rowMakterKey = itableMasterKeyHandler.selectSingle(msk_uid);
			String masterKey = rowMakterKey.get("key_value").toString();// neo1seok 2017.01.17 :db로부터 마스터 키를 얻어온다. 
			//	Retrieves the master key from db
			derivedkey = Util.DeriveKey(masterKey,sectorID,sn);// neo1seok 2017.01.17 :db에 저장된 칩의 마스터 키에 대한 derived keyf 구한다.
			//Get the derived key for the master key of the chip stored in db.
			
			
			Map<String, Object> rowNeMasterKey = itableMasterKeyHandler.selectSingle(latest_msk_uid);
			String version = rowNeMasterKey.get("version").toString();
			String newmasterKey = (String) rowNeMasterKey.get("key_value");
			String newderivedkey = Util.DeriveKey(newmasterKey,sectorID,sn);// neo1seok 2017.01.17 : 최신 등록된 마스터키의 derived key를 구한다.
			//Get the derived key of the most recently registered master key.

			
			
			String rand4Code = Util.Rand4Code(gen_nonce, hostchallenge);
			String keyEnc = Util.KeyEnc(derivedkey, rand4Code, sectorID, sn);
			String write_code = Util.Encryptyon(newderivedkey,keyEnc);// neo1seok 2017.01.17 : 새 derived 키를 암호화 한다.
			//Encrypt the new derived key.

			
	
			
			String mac_write = Util.CalcMAC4WriteCode(keyEnc, newderivedkey,sectorID, sn);// neo1seok 2017.01.17 : derived 키에 대한 mac값을 구한다.
			//Obtain the mac value for the derived key.

			
			NLoger.clog("hostchallenge:{0}",hostchallenge);
			NLoger.clog("mac_write:{0}",mac_write);
			
	
			
			if(isDebug){
				System.out.println("derivedkey:");
				System.out.println(derivedkey);
				
				System.out.println("gen_nonce:");
				System.out.println(gen_nonce);
				
				System.out.println("newderivedkey:");
				System.out.println(newderivedkey);
				
				System.out.println("hostchallenge:");
				System.out.println(hostchallenge);
				
				
				System.out.println("rand4Code:");
				System.out.println(rand4Code);
				
				System.out.println("keyEnc:");
				System.out.println(keyEnc);
				
				System.out.println("write_code:");
				System.out.println(write_code);
				
				
				System.out.println("mac_write:");
				System.out.println(mac_write);
				
//				sndProto.params.put("gen_nonce", gen_nonce);
//				sndProto.params.put("newderivedkey", newderivedkey);
//				sndProto.params.put("hostchallenge", hostchallenge);
//				sndProto.params.put("rand4Code", rand4Code);
//				sndProto.params.put("keyEnc", keyEnc);
//				sndProto.params.put("derivedkey", derivedkey);
			}
			
			sndProto.params.put("write_code", write_code);
			sndProto.params.put("mac", mac_write);
			//sndProto.params.put("masterkey_ver", version);
			// neo1seok 2017.01.17 :응답값에  write_code,mac 을 적는다.
			//Write write_code, mac in the response value.

		}
	};
	/**
	* @Name : invokeNotyUpdateresultIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* NOTY_UPDATERESULT 에 대한 명령을 실행하는 프로세스
	* db 내용중 chip  정보에서 새로 업데이트 된 마스터 키의 값을 저장 한다.
	* 
	* * The process that executes the command for NOTY_UPDATERESULT
* The value of the newly updated master key is saved from the chip information in the db contents.
	* 
	* 
	*/
	IInvoke invokeNotyUpdateresult = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();// neo1seok 2017.01.17 :세션uid로부터 필요한 값을 필드에 업데이트
			//Update required fields from session uid to field
			String result= rcvProto.params.get("result");
			
			
			
			
			if(!result.equals(RESULT.OK.toString())){
				sndProto.params.put("result", RESULT.FAIL.toString());
				return ;
			}
			
			mapArg.clear();
			mapArg.put("msk_uid", latest_msk_uid);
			itableChipHandler.Update(chp_uid, mapArg);
			// neo1seok 2017.01.17 :클라이언트가 새 키등록이  성공 했을 경우 db를 업데이트 한다.
			//If the client successfully registers the new key, update the db.

			//itableSessionHandler.Update(ssn_uid, mapArg);
		}
	};
	
	
	/**
	* @Name : invokeReqAppKeyIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* REQ_APP_KEY 에 대한 명령을 실행하는 프로세스
	* 다른 app key를 생성하고 암호화 해서 응답 하는 프로세스
	* 
	* 
* The process that executes the command for REQ_APP_KEY
* Process of generating and encrypting another app key
	* 
	*/
	IInvoke invokeReqAppKey = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();// neo1seok 2017.01.17 :세션uid로부터 필요한 값을 필드에 업데이트
			//Update required fields from session uid to field
			
			String app_id= rcvProto.params.get("app_id");// neo1seok 2017.01.17 : appid를 요청 값을 받는다.
			//Appid receives the request value.
			String appKey = Util.getRand(16);//eo1seok 2017.01.17 :  client용 app key를 생성 한다.
			//Create an app key for the client.
			
			Map<String, Object> rowMakterKey = itableMasterKeyHandler.selectSingle(msk_uid);
			String masterKey = rowMakterKey.get("key_value").toString();
			derivedkey = Util.DeriveKey(masterKey,sectorID,sn);// neo1seok 2017.01.17 :마스터키의 derived key를 구한다.
			//Get the derived key of the master key.


			String IV = Util.getRand(8);// neo1seok 2017.01.17 :IV를 생성 한다. 
			//Create IV
		
			String KeyEnc = Util.KeyEnc4DataTransfer(derivedkey, IV, Util.ZeroHexStr(8), "0000", sn);
			String KeyEncshaInput =Util.shaInput; 
			
			String painInfo = appKey+Util.ZeroHexStr(16);
			String Cipher =Util.Encryptyon(painInfo, KeyEnc);// neo1seok 2017.01.17 : appKey를 암호화 한다. 
			//Encrypt the appKey.

			
			String H = Util.CalcH(app_id, IV, Cipher);
			String HshaInput =Util.shaInput;
			
			String mac = Util.CalcMAC4DataTransfer(derivedkey, H, sectorID, sn);// neo1seok 2017.01.17 : derived key 에 대한 mac값을 구한다.
			//	Get the mac value for the derived key.
			
			String macshaInput =Util.shaInput;
			
			
			sndProto.params.put("app_id", app_id);
			sndProto.params.put("IV", IV);
			sndProto.params.put("Cipher", Cipher);
			sndProto.params.put("mac", mac);
			// neo1seok 2017.01.17 :응답 값으로 app_id,IV,Cipher ,mac을 설정한다. 
			//Set app_id, IV, Cipher, mac as response values.

			
			if(isDebug){
//				sndProto.params.put("derivedkey", derivedkey);
//				sndProto.params.put("KeyEnc", KeyEnc);
//				sndProto.params.put("KeyEncshaInput", KeyEncshaInput);
//				sndProto.params.put("painInfo", painInfo);
//				sndProto.params.put("H", H);
//				sndProto.params.put("HshaInput", HshaInput);
//				sndProto.params.put("macshaInput", macshaInput);
			}
			
		}
	};
	
	/**
	* @Name : invokeNotyAppkeyresultIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* NOTY_APPKEYRESULT 에 대한 명령을 실행하는 프로세스
	* app key 처리가 완료 되었다는 프로세스 
	*/
	IInvoke invokeNotyAppkeyresult = new IInvoke() {
		public void run() throws Exception {
			//System.out.println("NOTY_APPKEYRESULT:");
			UpdateDataFromSession();// neo1seok 2017.01.17 : 세션uid로부터 필요한 값을 필드에 업데이트
			//Update required fields from session uid to field
			
			String result= rcvProto.params.get("result");
			sndProto.params.put("result", "OK");
			
		}
	};
	
	/**
	* @Name : invokeReqEndSessionIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* REQ_END_SESSION 에 대한 명령을 실행하는 프로세스
	* 최종 세션이 끝났을 경우 
	* db에서 세션 관련 정보를 업데이트 하거나 지운다.
	* 
	* The process that executes the command for * REQ_END_SESSION
* When the final session is over
* Db updates or clears session-related information.
	* 
	*/
	IInvoke invokeReqEndSession = new IInvoke() {
		public void run() throws Exception {
			//System.out.println("REQ_END_SESSION:");
			ssn_uid = rcvProto.params.get("uid");
			
			mapArg.clear();
			mapArg.put("comment", "SESSION_END");
			// neo1seok 2017.01.17 :세션이 종료되었을때 db에  SESSION_END값을 저장한다.
			//SESSION_END value is stored in db when session ends.
			
			itableSessionHandler.Update(ssn_uid, mapArg);
			
			
			idbHandling.Excute("DELETE FROM giant_auth.session where updt_date < DATE_SUB(now(),    INTERVAL '1' WEEK);");
			// neo1seok 2017.01.17 :일주일전 저장된 세션 정보를 db에서 지운다. 
			//Delete session information from the db  that was saved a week ago.
			
			idbHandling.Excute("DELETE FROM giant_auth.session where updt_date < DATE_SUB(now(),    INTERVAL '3' HOUR) and comment = 'SESSION_END';");
			// neo1seok 2017.01.17 : 세시간 전에 저장되었고  SESSION_END 값이 설정된 필드 정보를  지운다. 
			//Delete field information that was saved three hours ago and has a SESSION_END value set.
			sndProto.params.put("result", "OK");
			
			
		}
	};


	
	
		
	/**
	* @Name : FindValidMac
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* 전달 받은 mac으로 db에서 얻어와서 계산된 mac을 비교하여
	* 유효한 값을 리턴하는 함수
	* db에 저장된 chip정보중 마스터 키의 정보와 2개 이후의 값을 범위로 계산한다.
	* 
	* Functions that Compares the calculated mac obtained from db with the transferred mac
	* and return valid values
	* Calculate the information of the master key among the chip information stored in db and the values ​​after two values.
	*  
	*/
	
	String FindValidMac(String mac,String msk_uid,String sectorID, String SN) throws NoSuchAlgorithmException, SQLException{
		listValidKeyList = idbHandling.Query(String.format("SELECT msk_uid, key_value  FROM giant_auth.masterkey ",msk_uid));
		NLoger.clog("FindValidMac");
		NLoger.clog("challenge;{0}",challenge);
		NLoger.clog("mac:{0}",mac);
		NLoger.clog("msk_uid:{0}",msk_uid);
		String ret_msk_uid = null;
		for ( Map<String, Object> maprow :listValidKeyList){
		
			  
			String masterKey = maprow.get("key_value").toString();
			ret_msk_uid = maprow.get("msk_uid").toString();
			String derivedkey = Util.DeriveKey(masterKey,sectorID,sn);
			String calcmac = Util.CalcMAC(derivedkey, challenge, sectorID,sn);
			NLoger.clog("msk_uid:{0},calcmac:{1}",ret_msk_uid,calcmac);
			
		}
		listValidKeyList = idbHandling.Query(String.format("SELECT msk_uid, key_value  FROM giant_auth.masterkey where seq >= (SELECT seq FROM giant_auth.masterkey where msk_uid = '%s') order by seq asc limit 3",msk_uid));
		for ( Map<String, Object> maprow :listValidKeyList){
			
			String masterKey = maprow.get("key_value").toString();
			ret_msk_uid = maprow.get("msk_uid").toString();
			
			String derivedkey = Util.DeriveKey(masterKey,sectorID,sn);
			
			String calcmac = Util.CalcMAC(derivedkey, challenge, sectorID,sn);
			NLoger.clog("msk_uid:{0},calcmac:{1}",ret_msk_uid,calcmac);
			//NLoger.clog("calcmac:{0}\nmac:{1}", calcmac,mac);
			
			if(isDebug){
				NLoger.clog("derivedkey:{0}", derivedkey);
				NLoger.clog("calcmac:{0}", calcmac);
			}
			
			String update = "";
			if (calcmac.equals(mac))		{
				return ret_msk_uid;
			}
			
		}
		
		
	
		return null;
	}
	

	/**
	* @Name : InitRun
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* 서블렛에서 실제 프로세서 시작 시 호출하는 함수
	* db를 Open한다.
	* 
	* Functions that are called by the servlet when the actual processor is started
	* Open db.
	* 
	*/
	@Override
	public void InitRun() throws Exception {
		idbHandling.open();

	}
	
	/**
	* @Name : endRun
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* 프로세스가 끝나는 시점에 호출 되는 함수
	* db를 Close 한다.
	* 
	* Function called at the end of the process
	*Close db.
	* 
	*/
	@Override
	public void endRun() throws Exception {
		// TODO Auto-generated method stub
		idbHandling.close();

	}
	
	/**
	* @Name : UpdateDataFromSession
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* 세션 uid를 구한후 
	* 세션 uid를 통해  db에서 필요한 정보를 얻어
	* 각 fields 값들에게 세팅해주는 함수
	* 
	* A function that sets each field value
	* After obtaining the session uid
    *  
	*  
	*/
	
	
	
	void UpdateDataFromSession() throws SQLException{
			//Map<String, Object> dataRowSession = GetUID(refString);
			ssn_uid = rcvProto.params.get("uid");
			
			UpdateData(ssn_uid);
			
	}
	/**
	* @Name : UpdateData
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* 세션 uid를 통해  db에서 필요한 정보를 얻어
	* 각 fields 값들에게 세팅해주는 함수
	*  
	* A function that sets each field value 
	* Get the necessary information from the db through the session uid

	*/
	
	
	void UpdateData(String uid) throws SQLException{
		Map<String, Object> dataRowSession = itableSessionHandler.selectSingle(uid);
		
		chp_uid = dataRowSession.get("chp_uid").toString();
		device_msk_uid = dataRowSession.get("msk_uid").toString();
		latest_msk_uid = dataRowSession.get("latest_msk_uid").toString();
		challenge = dataRowSession.get("challenge").toString();
		hostchallenge = dataRowSession.get("hostchallenge").toString();
		Map<String, Object> rowChp = itableChipHandler.selectSingle( chp_uid);
		sn = rowChp.get("sn").toString();
		
		msk_uid = rowChp.get("msk_uid").toString();
		
		
		
		
		
	}
	
	/**
	* @Name : doRunByJson
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* json값을 파싱 해서 실제 프로세스를 구동 시키는 함수
	* 
	* Function to parse the json value to drive the actual process
	* 
	*/
	@Override
	public String doRunByJson(String json) throws Exception {
		System.out.println("RECV:" + json);
		Gson gson = new Gson();
		

		rcvProto = Protocol.fromJsonString(json);

		AUTH_CMD cmd = AUTH_CMD.valueOf(rcvProto.cmd);
		sndProto = new Protocol();

		sndProto.cmd = rcvProto.cmd;
		
		sndProto.params.put("result", "OK");
		sndProto.params.put("error", "");
		System.out.println("CMD:" + cmd);
		mapArg.clear();
		mapInvoke.get(cmd).run();
		json = sndProto.toJsonString();
		
		System.out.println("SND:" + json);

		return json;

	}

	

	

	/**
	* @Name : SetDebug
	* @date : 2017. 1. 17.
	* @author : neo1seok

	* @explain :
	* 디버깅 여부를 설정하는 함수
	* 
	* Functions to set whether to debug
	* 
	*/
	@Override
	public void setDebug(boolean isDebug) {
		// TODO Auto-generated method stub
		this.isDebug = isDebug;
		
	}

	public static void main(String[] args) {
		
	
			
			
			



	}

	public void setAddress(String ipAddress, int port) {
		// TODO Auto-generated method stub
		
		
		
	}




}
