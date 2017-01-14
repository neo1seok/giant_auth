package giant_auth.Admin;

import giant_auth.comm.AES_Secure;
import giant_auth.comm.BaseHandler;
import giant_auth.comm.IInvoke;
import giant_auth.comm.Protocol;
import giant_auth.comm.RESULT;
import giant_auth.comm.SecureInfo;
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

public class AdminHandler extends BaseHandler<String>   {
	
	
	 private static final Logger logger = Logger.getLogger(AdminHandler.class.getName());
		
	List<Map<String, Object>> productInfo;

	private AES_Secure aesSecure;
	
	
	public AdminHandler() {
		super();
		
		aesSecure = new AES_Secure(SecureInfo.dbCryptoKey);

		// TODO Auto-generated constructor stub
		/*
		String prd_name = request.getParameter("prd_name");
		String sn = request.getParameter("sn");
		String prod_info = request.getParameter("prod_info");
		String prd_url = request.getParameter("prd_url");
		 * */
		
		mapInvoke.put(ADMIN_CMD.INSERT_CHIP.toString(), new IInvoke() {
			public void run() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException {
				
				
				System.out.println("CMD INSERT_CHIP");
				String sn = rcvProto.params.get("sn");
						
				Map<String,Object> mapchip = itableChipHandler.selectSingle("sn",sn );
				
				if(mapchip != null ){
					sndProto.params.put("Result",RESULT.FAIL.toString());
					sndProto.params.put("ERR","SN is Already Exist");
					return ;
				}
				
				//String sn = rcvProto.params.get("sn");
				String msk_uid = getLatestMasterKey() ;
				
			
				
				
				
				mapArg.clear();
				
			
				SetMapArg("sn");
				mapArg.put("msk_uid",msk_uid );
				
				
				
				if(!NeoHexString.IsHexStr(sn)){
					sndProto.params.put("Result","NOT OK");
					sndProto.params.put("ERR","SN Value Must be Hex String Form");
					return ;
				}
				
				if(sn.length() != 18){
					sndProto.params.put("Result","NOT OK");
					sndProto.params.put("ERR","SN Digit Must be 9(Hex String 18)");
					return ;
				}
				

 
				int seq = itableChipHandler.Insert(mapArg);
				System.out.println(Integer.toString(seq));
				sndProto.params.put("msk_uid",String.format("chk_%d",seq));
				
				
				
				

			

			}
		});
		
		
		mapInvoke.put(ADMIN_CMD.LIST_CHIP.toString(), new IInvoke() {
			public void run() throws SQLException {

				System.out.println("CMD LIST_CHIP");
				List<Map<String, Object>> listmapChip = itableChipHandler.select("chp_uid,msk_uid,sn","");

				
				for(Map<String, Object> tmp :listmapChip){
					String chp_uid = tmp.get("chp_uid").toString();
					String msk_uid = tmp.get("msk_uid").toString();
					String sn = tmp.get("sn").toString();
					sndProto.params.put(chp_uid, String.format("%s|%s", msk_uid,sn));
					
					
				}
				

			}
		});
		mapInvoke.put(ADMIN_CMD.MODIFY_MASTERKEY_CHIP.toString(), new IInvoke() {
			public void run() throws SQLException {

				System.out.println("CMD MODIFY_CHIP");

				String msk_uid = rcvProto.params.get("msk_uid");
				
				
				Map<String,Object> mapchip = itableChipHandler.selectSingle("sn", rcvProto.params.get("sn"));
				
				if(mapchip == null ){
					sndProto.params.put("Result",RESULT.FAIL.toString());
					sndProto.params.put("ERR","SN is not Exist");
					return ;
				}
				String chp_uid = mapchip.get("chp_uid").toString();
				mapArg.clear();
				
				SetMapArg("msk_uid");
				
				itableChipHandler.Update(chp_uid, mapArg);

				

			}
		});
		
		mapInvoke.put(ADMIN_CMD.INSERT_MASTERKEY.toString(), new IInvoke() {
			public void run() throws SQLException {

				System.out.println("CMD INSERT_MASTERKEY");
				
				String sversion = rcvProto.params.get("version");
				

				String key_value = rcvProto.params.get("key_value");
				
				if(key_value == null){
					
					key_value = Util.getRand(32);
					rcvProto.params.put("key_value",key_value);
					System.out.println(key_value);
				}
				if(!NeoHexString.IsHexStr(key_value)){
					sndProto.params.put("Result","NOT OK");
					sndProto.params.put("ERR","key_value  Must be Hex String Form");
					return ;
				}
				
				if(key_value.length() != 64){
					sndProto.params.put("Result","NOT OK");
					sndProto.params.put("ERR","key_value Digit Must be 32(Hex String 64)");
					return ;
				}
				
				
				Map<String, Object> latest_mapMasterKey = getLatestMasterKeyRowMap() ;
				
				Map<String,Object> mapMasterKey = itableMasterKeyHandler.selectSingle("key_value", rcvProto.params.get("key_value"));
				
				if(mapMasterKey != null ){
					sndProto.params.put("Result",RESULT.FAIL.toString());
					sndProto.params.put("ERR","key_value is already Exist");
					return ;
				}
				
				if(sversion == null){
					if(latest_mapMasterKey == null) sversion = "1";
					else {
						
						sversion = latest_mapMasterKey.get("version").toString();
						int version = Integer.parseInt(sversion);
						sversion = Integer.toString(version+1);
					}
		
				}
				
				System.out.println(sversion);
				
				mapArg.clear();
				SetMapArg("key_value");
				mapArg.put("version",sversion);
				int seq = itableMasterKeyHandler.Insert(mapArg);
				
				
				sndProto.params.put("version",sversion);
				sndProto.params.put("msk_uid",String.format("msk_%d",seq));
				
				
						
				
				
			

				

			}
		});
		mapInvoke.put(ADMIN_CMD.LIST_MASTERKEY.toString(), new IInvoke() {
			public void run() throws SQLException {

				System.out.println("CMD LIST_MASTERKEY");
				

				Map<String, Object> latest_mapMasterKey = getLatestMasterKeyRowMap() ;
				
				List<Map<String, Object>> listmapMasterKey = itableMasterKeyHandler.select("msk_uid,version","");
				
				for(Map<String, Object> tmp :listmapMasterKey){
					String msk_uid = tmp.get("msk_uid").toString();
					String version = tmp.get("version").toString();
					sndProto.params.put(msk_uid, version);
					
				}

				
				
				
			

				

			}
		});
		
		
		
	}
	void SetMapArg(String keyName){
		String value = rcvProto.params.get(keyName);
		if(value == null) {
			Log("{0} i null",keyName);
			return;
		}
		mapArg.put(keyName,value );
	}
	
	

	
	public static void main(String[] args) {
		
		AdminHandler adminHandler = new AdminHandler();
		adminHandler.Open();
		//String w = adminHandler.queryProuductInfo();
		adminHandler.Close();
		//System.out.println(w);

		
	}

}
