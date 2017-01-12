package giant_auth.auth;

import giant_auth.comm.BaseHandler;
import giant_auth.comm.CMD;
import giant_auth.comm.ERROR;
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

public class authHandler extends BaseHandler<CMD> implements iauthHandler {

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
	
//	String Util.getRand(int size){
//		byte[] buff = new byte[size];
//		r.nextBytes(buff);
//		String strrandnum = NeoHexString.ByteArrayToHexStr(buff);
//		return strrandnum;
//	}
	public authHandler() {
		super();
		// TODO Auto-generated constructor stub
		NLoger.isSystemConsole = true;
		

		mapInvoke.put(CMD.REQ_START_SESSION, new IInvoke() {
			public void run() throws SQLException {
				sn = rcvProto.mapvValue.get("sn");
				
				sndProto.mapvValue.put("challenge", Util.ZeroHexStr(32));
				sndProto.mapvValue.put("uid", "");
				
				
				Map<String, Object> rowChp = itableChipHandler.selectSingle("sn", sn);
				if(rowChp == null){
					sndProto.mapvValue.put("result", RESULT.FAIL.toString());
					sndProto.mapvValue.put("error", ERROR.NO_SN.toString());
					return ;
				}
				int slotno = (int) rowChp.get("slot_no");
				msk_uid = (String) rowChp.get("msk_uid");
				chp_uid = (String) rowChp.get("chp_uid");
				
				
				
				if(msk_uid == null||msk_uid.isEmpty() ){
					sndProto.mapvValue.put("result", RESULT.FAIL.toString());
					sndProto.mapvValue.put("error", ERROR.NO_MASTERKEY.toString());
					return ;
				}
				
				latest_msk_uid = getLatestMasterKey();
				challenge = Util.getRand(32);
				hostchallenge = Util.getRand(20);
				mapArg.clear();
				
				
				mapArg.put("chp_uid", chp_uid);
				mapArg.put("challenge", challenge);
				mapArg.put("hostchallenge", hostchallenge);
				//mapArg.put("msk_uid", msk_uid);
				mapArg.put("latest_msk_uid", latest_msk_uid);
				//mapArg.put("sn", sn);
				
				Log("TEST");
				
				for(Entry<String, String> tmp : mapArg.entrySet()){
					Log("{0} {1}",tmp.getKey(),tmp.getValue());
				}

				int cfmseq = itableSessionHandler.Insert(mapArg);
				Map<String, Object> dataRowSession = itableSessionHandler.selectSingle(cfmseq);
				ssn_uid = dataRowSession.get("ssn_uid").toString();
				
				sndProto.mapvValue.put("challenge", challenge);
				sndProto.mapvValue.put("uid", ssn_uid);
				
			}
		});
	

		mapInvoke.put(CMD.AUTHENTICATION, new IInvoke() {



			public void run() throws SQLException, NoSuchAlgorithmException {
				UpdateDataFromSession();
				
				mac = rcvProto.mapvValue.get("mac");
				
				sndProto.mapvValue.put("update", "");
				
				
						
				
				Map<String, Object> rowMakterKey = itableMasterKeyHandler.selectSingle(msk_uid);
				
		
				String masterKey = rowMakterKey.get("key_value").toString();
				derivedkey = Util.DeriveKey(masterKey,sectorID,sn);
				System.out.println("masterKey:");
				System.out.println(masterKey);
				
				System.out.println("derivedkey:");
				
				System.out.println(derivedkey);
				

				String calcmac = Util.CalcMAC(derivedkey, challenge, sectorID,sn);
				
				NLoger.clog("calcmac:{0}\nmac:{1}", calcmac,mac);
				lastError = "NOT_MATCH_MAC";
				String update = "";
				if (!calcmac.equals(mac))		{
					sndProto.mapvValue.put("result", RESULT.FAIL.toString());
					sndProto.mapvValue.put("error", ERROR.NOT_MATCH_MAC.toString());
					return;
				}
				
			
				
				
				if ( !msk_uid.equals(latest_msk_uid)){
					update = "OK";
					sndProto.mapvValue.put("update", "OK");
				}
		

			}

		});

		mapInvoke.put(CMD.REQ_HOSTCHALLENGE, new IInvoke() {

			public void run() throws SQLException {
				UpdateDataFromSession();
				
				
				sndProto.mapvValue.put("hostchallenge", hostchallenge);
			}
		});
		mapInvoke.put(CMD.REQ_UPDATEINFO, new IInvoke() {

			public void run() throws SQLException, NoSuchAlgorithmException {
				UpdateDataFromSession();
				
				String gen_nonce= rcvProto.mapvValue.get("gen_nonce");
				
				
				Map<String, Object> rowMakterKey = itableMasterKeyHandler.selectSingle(msk_uid);
				String masterKey = rowMakterKey.get("key_value").toString();
				
				derivedkey = Util.DeriveKey(masterKey,sectorID,sn);
	
				
				
				
				Map<String, Object> rowNeMasterKey = itableMasterKeyHandler.selectSingle(latest_msk_uid);
			
				System.out.println("derivedkey:");
				System.out.println(derivedkey);
				
				String newmasterKey = (String) rowNeMasterKey.get("key_value");
				String newderivedkey = Util.DeriveKey(newmasterKey,sectorID,sn);
				
				System.out.println("gen_nonce:");
				System.out.println(gen_nonce);
				
				System.out.println("newderivedkey:");
				System.out.println(newderivedkey);
				
				System.out.println("hostchallenge:");
				System.out.println(hostchallenge);
				
				String rand4Code = Util.Rand4Code(gen_nonce, hostchallenge);
				
				System.out.println("rand4Code:");
				System.out.println(rand4Code);
				
				String keyEnc = Util.KeyEnc(derivedkey, rand4Code, sectorID, sn);
				
				System.out.println("keyEnc:");
				System.out.println(keyEnc);
				
				String write_code = Util.Encryptyon(newderivedkey,keyEnc);
				
				System.out.println("write_code:");
				System.out.println(write_code);
				
				String mac_write = Util.CalcMAC4WriteCode(keyEnc, newderivedkey,sectorID, sn);
				
				System.out.println("mac_write:");
				System.out.println(mac_write);
				
				if(isDebug){
					sndProto.mapvValue.put("gen_nonce", gen_nonce);
					sndProto.mapvValue.put("newderivedkey", newderivedkey);
					sndProto.mapvValue.put("hostchallenge", hostchallenge);
					sndProto.mapvValue.put("rand4Code", rand4Code);
					sndProto.mapvValue.put("keyEnc", keyEnc);
					sndProto.mapvValue.put("derivedkey", derivedkey);
				}
				
				sndProto.mapvValue.put("write_code", write_code);
				sndProto.mapvValue.put("mac", mac_write);
				

		
			}
		});
		mapInvoke.put(CMD.NOTY_UPDATERESULT, new IInvoke() {

			public void run() throws SQLException {
				UpdateDataFromSession();
				String result= rcvProto.mapvValue.get("result");
				
				
				
				
				if(!result.equals(RESULT.OK.toString())){
					sndProto.mapvValue.put("result", RESULT.FAIL.toString());
					return ;
				}
				
				mapArg.clear();
				mapArg.put("msk_uid", latest_msk_uid);
				itableChipHandler.Update(chp_uid, mapArg);
				//itableSessionHandler.Update(ssn_uid, mapArg);
				
			}
		});
		mapInvoke.put(CMD.REQ_APP_KEY, new IInvoke() {

			public void run() throws SQLException, NoSuchAlgorithmException {
				UpdateDataFromSession();
				String appid= rcvProto.mapvValue.get("appid");
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
				
				
				sndProto.mapvValue.put("app_id", appid);
				sndProto.mapvValue.put("IV", IV);
				sndProto.mapvValue.put("Cipher", Cipher);
				sndProto.mapvValue.put("mac", mac);
				
				if(isDebug){
					sndProto.mapvValue.put("derivedkey", derivedkey);
					sndProto.mapvValue.put("KeyEnc", KeyEnc);
					sndProto.mapvValue.put("KeyEncshaInput", KeyEncshaInput);
					sndProto.mapvValue.put("painInfo", painInfo);
					sndProto.mapvValue.put("H", H);
					sndProto.mapvValue.put("HshaInput", HshaInput);
					sndProto.mapvValue.put("macshaInput", macshaInput);
				}
				
			}
		});
		
		mapInvoke.put(CMD.NOTY_APPKEYRESULT, new IInvoke() {

			public void run() throws SQLException {
				UpdateDataFromSession();
				String result= rcvProto.mapvValue.get("result");
				sndProto.mapvValue.put("data", Util.ZeroHexStr(32));
				
				
			}
		});
	}



	

	Map<String, Object> GetSnFromID(RefParam<String> chp_uid)
			throws SQLException {

		String sn = rcvProto.mapvValue.get("SN");
		Map<String, Object> datarow = idbHandling.getTable(TABLE_NAMES.chip)
				.selectSingle("sn", sn);
		if (datarow != null)
			chp_uid.value = datarow.get("chp_uid").toString();

		return datarow;

	}

	public void InitRun() throws Exception {
		idbHandling.open();

	}
	String getLatestMasterKey() throws SQLException {
		//idbHandling.Query("SELECT seq, msk_uid, key_value, version, updt_date, reg_date, comment FROM masterkey order by version desc limit 1;");
		Map<String, Object> mapRow = itableMasterKeyHandler.selectSingleWhere("msk_uid, key_value, version", "order by version desc limit 1");
		return (String) mapRow.get("msk_uid");
	}
	Map<String, Object> GetUID(RefParam<String> ssn_uid) throws SQLException {

		ssn_uid.value = rcvProto.mapvValue.get("uid");

		Map<String, Object> datarow = idbHandling.getTable(TABLE_NAMES.session)
				.selectSingle("ssn_uid", ssn_uid.getValue());
		return datarow;

	}
	void UpdateDataFromSession() throws SQLException{
			//Map<String, Object> dataRowSession = GetUID(refString);
			ssn_uid = rcvProto.mapvValue.get("uid");
			
			UpdateData(ssn_uid);
			
	}
	void UpdateData(String uid) throws SQLException{
		Map<String, Object> dataRowSession = itableSessionHandler.selectSingle(uid);
		
		chp_uid = dataRowSession.get("chp_uid").toString();
		//msk_uid = dataRowSession.get("msk_uid").toString();
		latest_msk_uid = dataRowSession.get("latest_msk_uid").toString();
		challenge = dataRowSession.get("challenge").toString();
		hostchallenge = dataRowSession.get("hostchallenge").toString();
		Map<String, Object> rowChp = itableChipHandler.selectSingle( chp_uid);
		sn = rowChp.get("sn").toString();
		msk_uid = rowChp.get("msk_uid").toString();
		
		
		
	}
	public String doRunByJson(String json) throws Exception {
		System.out.println("RECV:" + json);
		Gson gson = new Gson();
		

		rcvProto = Protocol.fromJsonString(json);

		CMD cmd = CMD.valueOf(rcvProto.cmd);
		sndProto = new Protocol();

		sndProto.cmd = rcvProto.cmd;
		
		sndProto.mapvValue.put("result", "OK");
		sndProto.mapvValue.put("error", "");
		
		mapArg.clear();

		mapInvoke.get(cmd).run();
		json = sndProto.toJsonString();
		
		System.out.println("SND:" + json);

		return json;

	}

	public String doRunByCompressed(String compressedJsonBase64)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("ORG:" + compressedJsonBase64);
		String json = Util.decompress(compressedJsonBase64);

		String retjson = doRunByJson(json);

		return Util.compressURL(retjson);

	}

	public void endRun() throws Exception {
		// TODO Auto-generated method stub

	}

	public static void TestConn1() throws ClassNotFoundException, SQLException {
		final String USER = "ictk";
		final String PASS = "#ictk1234";

		final String DB_URL_1 = "jdbc:mysql://localhost:3306/giant2";

		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Connecting to database...");
		java.sql.Connection conn = DriverManager.getConnection(DB_URL_1, USER,
				PASS);
		System.out.println("Conneced to database...");
		// sfsdf stmt = conn.createStatement();

		conn.close();

	}

	public static void TestConn2() throws ClassNotFoundException, SQLException {
		final String USER = "ictk";
		final String PASS = "#ictk1234";

		final String DB_URL_1 = "jdbc:mysql://localhost:3306/giant2";

		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

		final String DB_URL = "jdbc:mysql://{0}:{1}/{2}";

		String dbname = "giant2";
		String address = "localhost";
		String id = "ictk";
		String passwd = "#ictk1234";

		Class.forName("com.mysql.jdbc.Driver");
		String test = MessageFormat.format("{0,number,#}", 10000);
		String connStr = MessageFormat.format(DB_URL, address,
				Long.toString(3306), dbname);

		java.sql.Connection conn = DriverManager.getConnection(connStr, id,
				passwd);

		Statement state = conn.createStatement();

		ResultSet fdasfsda = state.executeQuery("select * from session");

		ResultSetMetaData metadata = fdasfsda.getMetaData();
		List<String> list = new LinkedList<String>();

		for (int i = 0; i < metadata.getColumnCount(); i++) {
			int colindex = i + 1;
			System.out.println(MessageFormat.format("{0} {1}",
					metadata.getColumnName(colindex),
					metadata.getColumnLabel(colindex)));
			list.add(metadata.getColumnLabel(colindex));
		}

		while (fdasfsda.next()) {
			System.out.println(MessageFormat.format("{0}",
					fdasfsda.getObject("reg_date")));
			for (int i = 0; i < metadata.getColumnCount(); i++) {
				int colindex = i + 1;
				String lablename = metadata.getColumnLabel(colindex);

				list.add(metadata.getColumnLabel(colindex));
			}

			;

		}

		conn.close();

		System.exit(0);

	}

	public static Protocol SnRTest(iauthHandler iHandler,CMD cmd,Map<String,String> map) throws Exception {
		
		Protocol protool = new Protocol();
		protool.cmd = cmd.toString();
		protool.mapvValue = map;
		
		
		String sndjson = protool.toJsonString();
		System.out.println(MessageFormat.format("SND:\n {0}", sndjson));
		String res = iHandler.doRunByJson(sndjson);
		
		System.out.println(MessageFormat.format("RCV:\n {0}", res));
		
		Protocol resprotool = Protocol.fromJsonString(res);
		return resprotool;
		

	}

	public static void main(String[] args) {
		
		String keyaa = MessageFormat.format("G{0}", Integer.toString(2223));
		keyaa = MessageFormat.format("{0,number,00000}", 23);
		System.out.println(keyaa);
		System.exit(0);
		
		// JDBC driver name and database URL
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

		final String DB_URL = "jdbc:mysql://{0}:{1}/{2}";

		String dbname = "giant2";
		String address = "localhost";
		String id = "ictk";
		String passwd = "#ictk1234";

		// Database credentials

		iauthHandler iHandler = new authHandler();

		Protocol resprotool = new Protocol();

		try {
			//System.out.println(NfcAuthHandler.CalcMAC("0000000000000000000000000000000000000000000000000000000000000000", "81210510B70443682D692F11D9D61825119759E333450903280D6C5CDF0A8079", "4547B100000000C047"));
			String org = "H4sIAAAAAAAAADWOuwrCQBBFf0WmFtzdOJPNdvuYqBgTjIltCNFOQQRtxH93FCzPuXC4L5iuJ3DQ8n6ITV1u2h3M4Trensfx8jiDe8FfO2i2snHbDlWzEhSIvorDzkchNCFgJMMZZshkc-spDxaLkEhRvuQ8hUxxuQxchmjZJhUKk3SpMSZEKzXfd2t50ded9AjFVP7QDT-dfMdijdK0ULQwdqbRZdopgvccpvuk6Xvp_QGEA_k00QAAAA=";
			String sdfdsaf = Util.decompress(org);
			System.out.println(Util.compressURL(sdfdsaf));
			System.out.println(Util.compress(sdfdsaf));
			System.out.println(sdfdsaf);
			
			System.exit(0);
			
			Map<String,String> map = new LinkedHashMap<String, String>();
			
			

			// TestConn2();

			String fdasfaf = "fasdfasdf";

			fdasfaf = fdasfaf.replace("f", "1");

			String sn = "012350AA53213799EE";
			String key = "0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000";

			iHandler.InitRun();

			
			
			
			
			//resprotool = SnRTest(iHandler,CMD.REQ_SESSION,map);


			String uid = resprotool.mapvValue.get("UID").toString();
			String rn = resprotool.mapvValue.get("RN").toString();

			String mac = Util.CalcMAC(key, rn,"0000" ,sn);

			
			map.clear();
			map.put("UID", uid);
			map.put("SN", sn);
			map.put("MAC", mac);
			
			//resprotool = SnRTest(iHandler,CMD.REQ_CONFIRM,map);
			
			
			map.clear();
			map.put("UID", uid);
			map.put("INFO", "TEST CONNENTS");
			
			//resprotool = SnRTest(iHandler,CMD.INSERT_COMMENT,map);
			
			
			map.clear();
			map.put("UID", uid);
			
			//resprotool = SnRTest(iHandler,CMD.REQ_COMMENTS,map);

			
			
			
			


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setAddress(String ipAddress, int port) {
		// TODO Auto-generated method stub
		
		
		
	}
	public void SetDebug(boolean isDebug) {
		// TODO Auto-generated method stub
		this.isDebug = isDebug;
		
	}

}
