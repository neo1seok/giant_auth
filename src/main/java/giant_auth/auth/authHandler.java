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
* @�ۼ��� : neo1seok
* @���α׷� ���� :
* This class is Handler that create sessions ,do authentication,make encrypted msg.
* Usually needed data is saved mysql db.
* authSublet use this class when be requested by clients(pos/get by http protocol) 
* mapInvoke is map object have main processor by mapping.
* At mapInvoke, key is command string,value is process 
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

	
	}
	
	
	
	/**
	* @Name : invokeReqStartSessionIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @���� :
	* REQ_START_SESSION �� ���� ����� ���� �ϴ� �Լ�
	* session �� ���� �ϰ� challenge ���� ���� �ϴ� ���μ���
	* sn�� ���� �޾Ƽ� db���� masterkey�� ���� ������ ������,
	* db�� ���� ������ ���� �� �� �ʿ��� ���� ���� �Ѵ�. 
	*/
	IInvoke invokeReqStartSession = new IInvoke() {
		public void run() throws Exception {
			device_msk_uid = "";
			sn = rcvProto.params.get("sn");
			String key_uid = rcvProto.params.get("key_uid");
			
			sndProto.params.put("challenge", Util.ZeroHexStr(32));
			sndProto.params.put("uid", "");
			
			
			Map<String, Object> rowChp = itableChipHandler.selectSingle("sn", sn);
			if(rowChp == null){
				sndProto.params.put("result", RESULT.FAIL.toString());
				sndProto.params.put("error", AUTH_ERROR.NO_SN.toString());
				return ;
			}
			//int slotno = (int) rowChp.get("slot_no");
			msk_uid = (String) rowChp.get("msk_uid");
			chp_uid = (String) rowChp.get("chp_uid");
			
			
			
			if(msk_uid == null||msk_uid.isEmpty() ){
				sndProto.params.put("result", RESULT.FAIL.toString());
				sndProto.params.put("error", AUTH_ERROR.NO_MASTERKEY.toString());
				return ;
			}
			if(key_uid != null){
				Map<String, Object> rowMasterKey = itableMasterKeyHandler.selectSingle("msk_"+key_uid);
				device_msk_uid = rowMasterKey.get("msk_uid").toString();
			}
			
			
			
			
			latest_msk_uid = getLatestMasterKey();
			challenge = Util.getRand(32);
			hostchallenge = Util.getRand(20);
			mapArg.clear();
			
			
			mapArg.put("chp_uid", chp_uid);
			mapArg.put("challenge", challenge);
			mapArg.put("hostchallenge", hostchallenge);
			mapArg.put("msk_uid", device_msk_uid);
			mapArg.put("latest_msk_uid", latest_msk_uid);
			mapArg.put("comment", "SESSION_START");
			//mapArg.put("sn", sn);
			
			Log("TEST");
			
			for(Entry<String, String> tmp : mapArg.entrySet()){
				Log("{0} {1}",tmp.getKey(),tmp.getValue());
			}

			int cfmseq = itableSessionHandler.Insert(mapArg);
			Map<String, Object> dataRowSession = itableSessionHandler.selectSingle(cfmseq);
			ssn_uid = dataRowSession.get("ssn_uid").toString();
			
			sndProto.params.put("challenge", challenge);
			sndProto.params.put("uid", ssn_uid);
		}
	};
	

	/**
	* @Name : invokeAuthenticationIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @���� :
	* AUTHENTICATION �� ���� ����� �����ϴ� ���μ���
	* ���� ���� mac ���� 
	* ����� session �������� ����  challenge�� sn���� �������� mac���� ���ϰ� 
	* ���޹��� mac���� ���ϴ� ������� ������ �Ѵ�.  
	* ���� ���� �ֽ� ������Ʈ�� ������ Ű�� ������ ��� Update �� OK�� �����Ͽ� 
	* ������Ʈ�� �Ѵ�.
	*/
	IInvoke invokeAuthentication = new IInvoke() {

		public void run() throws Exception {
			String update = "";
			UpdateDataFromSession();
			//NLoger.clog("AUTHENTICATION");
			mac = rcvProto.params.get("mac");
			
			sndProto.params.put("update", "");
			
			
			String valid_msk_uid = FindValidMac(mac,msk_uid,sectorID,sn);
			
			if(valid_msk_uid == null){
				sndProto.params.put("result", RESULT.FAIL.toString());
				sndProto.params.put("error", AUTH_ERROR.NOT_MATCH_MAC.toString());
				return ;

			}
			
			if(valid_msk_uid != msk_uid){
				NLoger.clog("MAY NOT UPDATED DB BEFORE");
				mapArg.clear();
				mapArg.put("msk_uid", valid_msk_uid);
				itableChipHandler.Update(chp_uid, mapArg);
				
			}
			
		
			
			
			if ( !valid_msk_uid.equals(latest_msk_uid)){
				update = "OK";
				sndProto.params.put("update", "OK");
			}
		}
	};
	
	
	/**
	* @Name : invokeReqHostchallengeIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @���� :
	* REQ_HOSTCHALLENGE �� ���� ����� �����ϴ� ���μ���
	* hostchallenge ���� �����Ѵ�.	* 
	*/
	private IInvoke invokeReqHostchallenge = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();
			hostchallenge = Util.getRand(20);
			
			mapArg.clear();
			mapArg.put("hostchallenge", hostchallenge);
			itableSessionHandler.Update(ssn_uid, mapArg);
			NLoger.clog("hostchallenge:{0}",hostchallenge);
			sndProto.params.put("hostchallenge", hostchallenge);
		}
	};
	
	/**
	* @Name : invokeReqUpdateinfoIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @���� :
	* REQ_UPDATEINFO �� ���� ����� �����ϴ� ���μ���
	* ���ο� ������ Ű�� ����̽��� �Է��ϱ� ���� WRITE CODE��
	* MAC�� ���� �ϴ� ���μ���
	*/
	IInvoke invokeReqUpdateinfo = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();
			
			String gen_nonce= rcvProto.params.get("gen_nonce");
			
			
			Map<String, Object> rowMakterKey = itableMasterKeyHandler.selectSingle(msk_uid);
			String masterKey = rowMakterKey.get("key_value").toString();
			
			derivedkey = Util.DeriveKey(masterKey,sectorID,sn);

			
			
			
			Map<String, Object> rowNeMasterKey = itableMasterKeyHandler.selectSingle(latest_msk_uid);
		
			
			
			String version = rowNeMasterKey.get("version").toString();
			String newmasterKey = (String) rowNeMasterKey.get("key_value");
			String newderivedkey = Util.DeriveKey(newmasterKey,sectorID,sn);
			
			
			String rand4Code = Util.Rand4Code(gen_nonce, hostchallenge);
			
	
			String keyEnc = Util.KeyEnc(derivedkey, rand4Code, sectorID, sn);
			

			
			String write_code = Util.Encryptyon(newderivedkey,keyEnc);
			
	
			
			String mac_write = Util.CalcMAC4WriteCode(keyEnc, newderivedkey,sectorID, sn);
			
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
				
				sndProto.params.put("gen_nonce", gen_nonce);
				sndProto.params.put("newderivedkey", newderivedkey);
				sndProto.params.put("hostchallenge", hostchallenge);
				sndProto.params.put("rand4Code", rand4Code);
				sndProto.params.put("keyEnc", keyEnc);
				sndProto.params.put("derivedkey", derivedkey);
			}
			
			sndProto.params.put("write_code", write_code);
			sndProto.params.put("mac", mac_write);
			sndProto.params.put("masterkey_ver", version);
		}
	};
	/**
	* @Name : invokeNotyUpdateresultIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* NOTY_UPDATERESULT �� ���� ����� �����ϴ� ���μ���
	* db ������ chip  �������� ���� ������Ʈ �� ������ Ű�� ���� ���� �Ѵ�.
	*/
	IInvoke invokeNotyUpdateresult = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();
			String result= rcvProto.params.get("result");
			
			
			
			
			if(!result.equals(RESULT.OK.toString())){
				sndProto.params.put("result", RESULT.FAIL.toString());
				return ;
			}
			
			mapArg.clear();
			mapArg.put("msk_uid", latest_msk_uid);
			itableChipHandler.Update(chp_uid, mapArg);
			//itableSessionHandler.Update(ssn_uid, mapArg);
		}
	};
	
	
	/**
	* @Name : invokeReqAppKeyIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* REQ_APP_KEY �� ���� ����� �����ϴ� ���μ���
	* �ٸ� app key�� �����ϰ� ��ȣȭ �ؼ� ���� �ϴ� ���μ���
	*/
	IInvoke invokeReqAppKey = new IInvoke() {
		public void run() throws Exception {
			UpdateDataFromSession();
			String appid= rcvProto.params.get("appid");
			String appKey = Util.getRand(16);
			Map<String, Object> rowMakterKey = itableMasterKeyHandler.selectSingle(msk_uid);
			String masterKey = rowMakterKey.get("key_value").toString();
			derivedkey = Util.DeriveKey(masterKey,sectorID,sn);

			String IV = Util.getRand(8);
			
			String KeyEnc = Util.KeyEnc4DataTransfer(derivedkey, IV, Util.ZeroHexStr(8), "0000", sn);
			String KeyEncshaInput =Util.shaInput; 
			
			String painInfo = appKey+Util.ZeroHexStr(16);
			String Cipher =Util.Encryptyon(painInfo, KeyEnc);
			
			String H = Util.CalcH(appid, IV, Cipher);
			String HshaInput =Util.shaInput;
			
			String mac = Util.CalcMAC4DataTransfer(derivedkey, H, sectorID, sn);
			String macshaInput =Util.shaInput;
			
			
			sndProto.params.put("app_id", appid);
			sndProto.params.put("IV", IV);
			sndProto.params.put("Cipher", Cipher);
			sndProto.params.put("mac", mac);
			
			if(isDebug){
				sndProto.params.put("derivedkey", derivedkey);
				sndProto.params.put("KeyEnc", KeyEnc);
				sndProto.params.put("KeyEncshaInput", KeyEncshaInput);
				sndProto.params.put("painInfo", painInfo);
				sndProto.params.put("H", H);
				sndProto.params.put("HshaInput", HshaInput);
				sndProto.params.put("macshaInput", macshaInput);
			}
			
		}
	};
	
	/**
	* @Name : invokeNotyAppkeyresultIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* NOTY_APPKEYRESULT �� ���� ����� �����ϴ� ���μ���
	* app key ó���� �Ϸ� �Ǿ��ٴ� ���μ��� 
	*/
	IInvoke invokeNotyAppkeyresult = new IInvoke() {
		public void run() throws Exception {
			//System.out.println("NOTY_APPKEYRESULT:");
			UpdateDataFromSession();
			String result= rcvProto.params.get("result");
			sndProto.params.put("result", "OK");
			
		}
	};
	
	/**
	* @Name : invokeReqEndSessionIInvoke
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* REQ_END_SESSION �� ���� ����� �����ϴ� ���μ���
	* ���� ������ ������ ��� 
	* db���� ���� ���� ������ ������Ʈ �ϰų� �����.
	*/
	IInvoke invokeReqEndSession = new IInvoke() {
		public void run() throws Exception {
			//System.out.println("REQ_END_SESSION:");
			ssn_uid = rcvProto.params.get("uid");
			
			mapArg.clear();
			mapArg.put("comment", "SESSION_END");
		
			itableSessionHandler.Update(ssn_uid, mapArg);
			idbHandling.Excute("DELETE FROM giant_auth.session where updt_date < DATE_SUB(now(),    INTERVAL '1' WEEK);");
			idbHandling.Excute("DELETE FROM giant_auth.session where updt_date < DATE_SUB(now(),    INTERVAL '3' HOUR) and comment = 'SESSION_END';");
			
			sndProto.params.put("result", "OK");
			
			
		}
	};


	
	
		
	/**
	* @Name : FindValidMac
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* ���� ���� mac���� db���� ���ͼ� ���� mac�� ���Ͽ�
	* ��ȿ�� ���� �����ϴ� �Լ�
	* db�� ����� chip������ ������ Ű�� ������ 2�� ������ ���� ������ ����Ѵ�.
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
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* �������� ���� ���μ��� ���� �� ȣ���ϴ� �Լ�
	*/
	@Override
	public void InitRun() throws Exception {
		idbHandling.open();

	}
	
	
	
	/**
	* @Name : UpdateDataFromSession
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* ���� uid�� ������ 
	* ���� uid�� ����  db���� �ʿ��� ������ ���
	* �� fields ���鿡�� �������ִ� �Լ� 
	*/
	
	
	void UpdateDataFromSession() throws SQLException{
			//Map<String, Object> dataRowSession = GetUID(refString);
			ssn_uid = rcvProto.params.get("uid");
			
			UpdateData(ssn_uid);
			
	}
	/**
	* @Name : UpdateData
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* ���� uid�� ����  db���� �ʿ��� ������ ���
	* �� fields ���鿡�� �������ִ� �Լ� 
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
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* json���� �Ľ� �ؼ� ���� ���μ����� ���� ��Ű�� �Լ�
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
	* @Name : endRun
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* ���μ����� ������ ������ ȣ�� �Ǵ� �Լ�
	* db�� Close �Ѵ�.
	*/
	@Override
	public void endRun() throws Exception {
		// TODO Auto-generated method stub
		idbHandling.close();

	}

	/**
	* @Name : SetDebug
	* @�ۼ��� : 2017. 1. 17.
	* @�ۼ��� : neo1seok
	* @�����̷� :
	* @���� :
	* ����� ���θ� �����ϴ� �Լ�
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
