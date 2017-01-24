package giant_auth.Admin;


import giant_auth.comm.BaseHandler;
import giant_auth.comm.IInvoke;
import giant_auth.comm.Protocol;
import giant_auth.comm.RESULT;
import giant_auth.comm.TABLE_NAMES;
import giant_auth.comm.Util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.neolib.Util.NLoger;
import com.neolib.Util.NeoHexString;
import com.neolib.db.IDbTableHandling;


/**
* @FileName : adminHandler.java
* @Project : giant_auth
* @Date : 2017. 1. 17.
* @author : neo1seok
* @explain  :
*/


public class adminHandler extends BaseHandler<ADMIN_CMD>   {
	
	
	 private static final Logger logger = Logger.getLogger(adminHandler.class.getName());
		
	List<Map<String, Object>> productInfo;

	//private AES_Secure aesSecure;
	
	
	public adminHandler() {
		super();
		
		
		mapInvoke.put(ADMIN_CMD.INSERT_CHIP, invokeInsertChip);
		mapInvoke.put(ADMIN_CMD.LIST_CHIP, invokeListChip);
		mapInvoke.put(ADMIN_CMD.MODIFY_MASTERKEY_CHIP, invokeModifyMasterkeyChip);
		mapInvoke.put(ADMIN_CMD.INSERT_MASTERKEY, invokeInsertMasterkey);
		mapInvoke.put(ADMIN_CMD.LIST_MASTERKEY, invokeListMasterkey);
		//neo1seok 2017.01.17 : mapInvoke 에 프로세서를 매핑한다.
		//Maps the processor to mapInvoke.

		
	}
	protected ADMIN_CMD convertCmd(String strcmd){
		return ADMIN_CMD.valueOf(strcmd);
	}
	
	
	/**
	* @Name : invokeInsertChipIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* chip 정보를  db에 입력 한다.
	* 
	* Input chip information to db.
	* 
	*/
	IInvoke invokeInsertChip = new IInvoke() {
		public void run() throws Exception {
			//System.out.println("CMD INSERT_CHIP");
			String sn = rcvProto.params.get("sn");
			// neo1seok 2017.01.17 : sn 을 요청값으로 얻어온다.
			//get Sn as the request value.
					
			Map<String,Object> mapchip = itableChipHandler.selectSingle("sn",sn );
			
			if(mapchip != null )// neo1seok 2017.01.17 :sn이 db에 있을 때  
				//	When sn is in db
			{
				sndProto.params.put("Result",RESULT.FAIL.toString());
				sndProto.params.put("ERR","SN is Already Exist");
				return ;
			}
			
			//String sn = rcvProto.params.get("sn");
			String msk_uid = getLatestMasterKey() ;
			mapArg.clear();
			SetMapArg("sn");
			mapArg.put("msk_uid",msk_uid );// neo1seok 2017.01.17 : 
			
			if(!NeoHexString.IsHexStr(sn))// neo1seok 2017.01.17 :sn 이 hex string 형태가 아닐 때  
				//When sn is not a hex string
			{
				sndProto.params.put("Result","NOT OK");
				sndProto.params.put("ERR","SN Value Must be Hex String Form");
				return ;
			}
			
			if(sn.length() != 18)// neo1seok 2017.01.17 :sn의 자리수가 다를 때 
				//	When the number of digits of the seconds is different
			{
				sndProto.params.put("Result","NOT OK");
				sndProto.params.put("ERR","SN Digit Must be 9(Hex String 18)");
				return ;
			}
			


			int seq = itableChipHandler.Insert(mapArg);// neo1seok 2017.01.17 :db에 입력 한다. 
			//insert to db
			System.out.println(Integer.toString(seq));
			sndProto.params.put("chk_uid",String.format("chk_%d",seq));// neo1seok 2017.01.17 :  새로 저장된 chip 의 uid를 설정한다.
			//Set uid of newly saved chip.
			sndProto.params.put("msk_uid",msk_uid);// neo1seok 2017.01.17 :  새로 저장된 마스터키 의 uid를 설정한다.
			//Set masterkey uid of newly saved chip.

		}
	};
	
	/**
	* @Name : invokeListChipIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* 전체 chip정보를 리스트 한다. 
	* List the entire chip information.
	*/
	IInvoke invokeListChip = new IInvoke() {
		public void run() throws Exception {
			List<Map<String, Object>> listmapChip = itableChipHandler.select("chp_uid,msk_uid,sn","");

			
			for(Map<String, Object> tmp :listmapChip){
				String chp_uid = tmp.get("chp_uid").toString();
				String msk_uid = tmp.get("msk_uid").toString();
				String sn = tmp.get("sn").toString();
				sndProto.params.put(chp_uid, String.format("%s|%s", msk_uid,sn));
				// neo1seok 2017.01.17 :chip uid 당 msk_uid와 sn값을 응답해 준다. 
				//It replies msk_uid and sn values ​​per chip uid.

				
				
			}
		}
	};
	
	/**
	* @Name : invokeModifyMasterkeyChipIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* 
	*/
	IInvoke invokeModifyMasterkeyChip = new IInvoke() {
		public void run() throws Exception {
			//String msk_uid = rcvProto.params.get("msk_uid");
			String sn = rcvProto.params.get("sn");
			// neo1seok 2017.01.17 :요청값으로 부터 sn값을 받는다.
			//get Sn as the request value.
 
			
			Map<String,Object> mapchip = itableChipHandler.selectSingle("sn", sn);
			
			if(mapchip == null )			// neo1seok 2017.01.17 :sn값이 db에 없을 때
				//When sn value is not in db
			{
				sndProto.params.put("Result",RESULT.FAIL.toString());
				sndProto.params.put("ERR","SN is not Exist");
				return ;
			}
			
			String chp_uid = mapchip.get("chp_uid").toString();
			mapArg.clear();
			
			SetMapArg("msk_uid");
			
			itableChipHandler.Update(chp_uid, mapArg);
			// neo1seok 2017.01.17 :chip에 마스터키 uid값을 업데이트 한다.
			//Update the master key uid value on the chip.
		}
	};
	/**
	* @Name : invokeInsertMasterkeyIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* master key 값을 db에 입력해 준다.
	* key_value 값이 없을 경우 랜덤으로 생성해서 넣어 준다.
	* 
	* * Enter the master key value to db.
	* Key_value If there is no value, generate it randomly.
	*/
	IInvoke invokeInsertMasterkey = new IInvoke() {
		public void run() throws Exception {
			String sversion = "";
			//String sversion = rcvProto.params.get("version");
			

			String key_value = rcvProto.params.get("key_value");
			
			if(key_value == null)// neo1seok 2017.01.17 :key_value 값이 요청 값에 없을 경우 
				//If the key_value value is not in the request value
			{
				
				key_value = Util.getRand(32);// neo1seok 2017.01.17 :랜덤 생성 한다. 
				//Randomly generated.
				rcvProto.params.put("key_value",key_value);
				System.out.println(key_value);
			}
			
			if(!NeoHexString.IsHexStr(key_value))// neo1seok 2017.01.17 :key_value 이 hex String 이 아닐 경우 
				//Key_value is not a hex String
			{
				sndProto.params.put("Result","NOT OK");
				sndProto.params.put("ERR","key_value  Must be Hex String Form");
				return ;
			}
			
			if(key_value.length() != 64)// neo1seok 2017.01.17 : key_value 자리수가 틀릴 경우 
				//If the key_value digit is wrong

			{
				sndProto.params.put("Result","NOT OK");
				sndProto.params.put("ERR","key_value Digit Must be 32(Hex String 64)");
				return ;
			}
			
			
			Map<String, Object> latest_mapMasterKey = getLatestMasterKeyRowMap() ;
			
			Map<String,Object> mapMasterKey = itableMasterKeyHandler.selectSingle("key_value", rcvProto.params.get("key_value"));
			
			if(mapMasterKey != null )// neo1seok 2017.01.17 : key_value 값이 이미 있을 경우  
				//If the key_value value already exists
 			{
				sndProto.params.put("Result",RESULT.FAIL.toString());
				sndProto.params.put("ERR","key_value is already Exist");
				return ;
			}
			
//			if(sversion == null){
//				if(latest_mapMasterKey == null) sversion = "1";
//				else {
//					
//					sversion = latest_mapMasterKey.get("version").toString();
//					int version = Integer.parseInt(sversion);
//					System.out.println(version);
//					sversion = Integer.toString(version+1);
//				}
//	
//			}
			
			System.out.println(sversion);
			
			mapArg.clear();
			SetMapArg("key_value");
			mapArg.put("version",sversion);
			int seq = itableMasterKeyHandler.Insert(mapArg);
			// neo1seok 2017.01.17 :마스터 키 값을 저장 한다. 
			//Stores the master key value.

			
			//sndProto.params.put("version",sversion);
			sndProto.params.put("msk_uid",String.format("msk_%d",seq));
			
			// neo1seok 2017.01.17 : 마스터키 uid를 응답 값으로 설정 
			//	Set master key uid as response value

		}
	};
	/**
	* @Name : invokeListMasterkeyIInvoke
	* @date : 2017. 1. 17.
	* @author : neo1seok
	* @explain :
	* 마스터 키를 리스트 한다.
	* 
	* List the master keys.
	* 
	*/
	IInvoke invokeListMasterkey = new IInvoke() {
		public void run() throws Exception {

			Map<String, Object> latest_mapMasterKey = getLatestMasterKeyRowMap() ;
			
			List<Map<String, Object>> listmapMasterKey = itableMasterKeyHandler.select("msk_uid,version","");
			
			for(Map<String, Object> tmp :listmapMasterKey){
				String msk_uid = tmp.get("msk_uid").toString();
				String version = tmp.get("version").toString();
				sndProto.params.put(msk_uid, version);
				
			}
		}
	};

	void SetMapArg(String keyName){
		String value = rcvProto.params.get(keyName);
		if(value == null) {
			Log("{0} i null",keyName);
			return;
		}
		mapArg.put(keyName,value );
	}
	
	

	
	public static void main(String[] args) {
		
		adminHandler adminHandler = new adminHandler();
		adminHandler.Open();
		//String w = adminHandler.queryProuductInfo();
		adminHandler.Close();
		//System.out.println(w);

		
	}

}
