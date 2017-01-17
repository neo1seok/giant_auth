package giant_auth.comm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;

import com.google.gson.stream.JsonReader;
import com.neolib.Util.NLoger;
import com.neolib.Util.NeoHexString;

public class Util {
	static Random r = new Random();
	public static String shaInput = "";
	public static String getRand(int size){
		byte[] buff = new byte[size];
		r.nextBytes(buff);
		String strrandnum = NeoHexString.ByteArrayToHexStr(buff);
		return strrandnum;
	}
	public static String compress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return null;
		}
		System.out.println("String length : " + str.length());
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		String outStr = obj.toString("UTF-8");
		System.out.println("Output String length : " + outStr.length());
		byte[] outarray = obj.toByteArray();

		return new String(Base64.encodeBase64(outarray));
		// return outarray;
	}

	public static String compressURL(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return null;
		}
		System.out.println("String length : " + str.length());
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		String outStr = obj.toString("UTF-8");
		System.out.println("Output String length : " + outStr.length());
		byte[] outarray = obj.toByteArray();
		
		String ret = new String(Base64.encodeBase64(outarray));
		
		ret = ret.replace('+','-').replace( '/', '_');
		return ret;

		//return new String(Base64.encodeBase64URLSafe(outarray)) +"=";
		// return outarray;
	}

	public static String decompress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return null;
		}
		System.out.println("Input byte length : " + str.length());

		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(
				Base64.decodeBase64(str)));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis,
				"UTF-8"));

		String outStr = "";
		String line;
		char[] buff = new char[4096];

		String strret = "";
		int totalsize = 0;
		while (true) {
			int ret = bf.read(buff, 0, 4096);
			if (ret == 0 || ret < 0)
				break;

			strret += new String(Arrays.copyOf(buff, ret));
			totalsize += ret;

		}

		System.out.println("Output String lenght : " + outStr.length());
		return strret;
	}
	static String HexStrSubStr(String org, int index, int count) {

		return org.substring(2 * index, 2 * (index + count));
	}

	public static String SHA256(String hexdata) throws NoSuchAlgorithmException {
		//System.out.println("shainput");
		//System.out.println(hexdata);
		MessageDigest sh = MessageDigest.getInstance("SHA-256");
		byte [] input = NeoHexString.HexStrToByteArray(hexdata);

		sh.update(NeoHexString.HexStrToByteArray(hexdata));

		byte byteData[] = sh.digest();

		return NeoHexString.ByteArrayToHexStr(byteData);
	}
	
	public static String ZeroHexStr(int count){
		
		String ret = "";
		for(int i =0 ; i < count ; i++){
			ret += "00";
		}
		return ret;
	}
	public static String Encryptyon(String makerNEwKey, String keyEnc){
		System.out.println("CalcWriteCode");
		byte [] btMasterKey = NeoHexString.HexStrToByteArray(makerNEwKey);
		byte [] btKeyEnc = NeoHexString.HexStrToByteArray(keyEnc);
		byte [] newCode = new byte[32];
		for(int i = 0 ;i<32;i++){
			newCode[i] = (byte) (btMasterKey[i]^btKeyEnc[i]);
		}
	
		return NeoHexString.ByteArrayToHexStr(newCode);
	}
	public static String DeriveKey(String MasterKey, String sectorID, String SN){
		//System.out.println("DeriveKey");
		// TODO Auto-generated method stub
		if (SN.length() != 9 * 2)
			return "";

		// String sector0 =
		// "0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000";
		String SN01 = HexStrSubStr(SN, 0, 2);// SN.Substring(0 * 2, 2 *
												// 2);//"0123";
		String SN8 = HexStrSubStr(SN, 8, 1);// SN.Substring(2 * 8, 2 *
											// 1);//"EE";
		
		
		shaInput = MasterKey + "1C" + "04" + sectorID+SN8 + SN01 + ZeroHexStr(25)+ SN+ZeroHexStr(23);
		//String shaInput = MasterKey + "1C" + "04" + p2+SN8 + SN01 + ZeroHexStr(25)+ "128218005836200463B2244ABDF0D470F1395EBB2BAD94E35DCFDE6DD3DCD001";
		try {
			return SHA256(shaInput).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			return "";
		}

	}
	
	
	public static String CalcMAC(String key, String strChallenge, String sectorID,String SN)
			throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		//System.out.println("CalcMAC");
		if (SN.length() != 9 * 2)
			return "";

		// String sector0 =
		// "0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000";
		String SN01 = HexStrSubStr(SN, 0, 2);// SN.Substring(0 * 2, 2 *
												// 2);//"0123";
		String SN23 = HexStrSubStr(SN, 2, 2);// SN.Substring(2 * 2, 2 *
												// 2);//"50AA";
		String SN47 = HexStrSubStr(SN, 4, 4);// SN.Substring(2 * 4, 2 *
												// 4);//"53213799";
		String SN8 = HexStrSubStr(SN, 8, 1);// SN.Substring(2 * 8, 2 *
											// 1);//"EE";

		String Zero11 = "0000000000000000000000";
		
		//NLoger.clog("{0}\n{1}","key",key);
		//NLoger.clog("{0}\n{1}","strChallenge",strChallenge);
		//NLoger.clog("{0}\n{1}","sectorID",sectorID);
		//NLoger.clog("{0}\n{1}","SN",SN);
		
		
		shaInput = key + strChallenge + "0840"+sectorID + Zero11 + SN8 + SN47+ SN01 + SN23;
		
		// NeoLog.WriteLine("shaInput:\n{0}", shaInput);
		return SHA256(shaInput).toUpperCase();

	}
	
	
	
	
	public static String Rand4Code(String genNonce, String hostChallenge) throws NoSuchAlgorithmException{
	System.out.println("Rand4Code");
		shaInput = genNonce + hostChallenge+"160000";
		
		return SHA256(shaInput).toUpperCase();
	}
	public static String KeyEnc(String makerKey, String rand4Code,String sectorID,String SN) throws NoSuchAlgorithmException{
		System.out.println("KeyEnc");
		// String sector0 =
		// "0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000";
		String SN01 = HexStrSubStr(SN, 0, 2);// SN.Substring(0 * 2, 2 *
												// 2);//"0123";
		String SN23 = HexStrSubStr(SN, 2, 2);// SN.Substring(2 * 2, 2 *
												// 2);//"50AA";
		String SN47 = HexStrSubStr(SN, 4, 4);// SN.Substring(2 * 4, 2 *
												// 4);//"53213799";
		String SN8 = HexStrSubStr(SN, 8, 1);// SN.Substring(2 * 8, 2 *
											// 1);//"EE";
		
		shaInput = makerKey + "1502"+sectorID+SN8 + SN01+ZeroHexStr(25)+rand4Code;
		
		return SHA256(shaInput).toUpperCase();
	}

	
	
	
	public static String CalcMAC4WriteCode(String keyEnc, String makerNEwKey,String sectorID, String SN)
			throws NoSuchAlgorithmException {
		System.out.println("CalcMAC4WriteCode");
		String SN01 = HexStrSubStr(SN, 0, 2);// SN.Substring(0 * 2, 2 *
		// 2);//"0123";
String SN23 = HexStrSubStr(SN, 2, 2);// SN.Substring(2 * 2, 2 *
		// 2);//"50AA";
String SN47 = HexStrSubStr(SN, 4, 4);// SN.Substring(2 * 4, 2 *
		// 4);//"53213799";
String SN8 = HexStrSubStr(SN, 8, 1);// SN.Substring(2 * 8, 2 *
	// 1);//"EE";
		// TODO Auto-generated method stub
		shaInput = keyEnc + "1282"+sectorID+SN8 + SN01+ZeroHexStr(25)+makerNEwKey;
		
		return SHA256(shaInput).toUpperCase();
	
	}
	
	public static String KeyEnc4DataTransfer(String makerKey, String IV,String count, String sectorID,String SN) throws NoSuchAlgorithmException{
		System.out.println("KeyEnc4DataTransfer");
		// String sector0 =
		// "0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000";
		String SN01 = HexStrSubStr(SN, 0, 2);// SN.Substring(0 * 2, 2 *
												// 2);//"0123";
		String SN23 = HexStrSubStr(SN, 2, 2);// SN.Substring(2 * 2, 2 *
												// 2);//"50AA";
		String SN47 = HexStrSubStr(SN, 4, 4);// SN.Substring(2 * 4, 2 *
												// 4);//"53213799";
		String SN8 = HexStrSubStr(SN, 8, 1);// SN.Substring(2 * 8, 2 *
											// 1);//"EE";
		
		shaInput = makerKey + IV+count+ZeroHexStr(16)+"0840"+sectorID+ ZeroHexStr(11)+SN8 + SN47+ SN01 + SN23;
		
		return SHA256(shaInput).toUpperCase();
	}
	public static String CalcH(String appID,String IV,String Cipher)
			throws NoSuchAlgorithmException {
		
		
		// TODO Auto-generated method stub
		shaInput = appID+IV+Cipher;
		return SHA256(shaInput).toUpperCase();
	
	}
	public static String CalcMAC4DataTransfer(String derivedKey,String H, String sectorID, String SN)
			throws NoSuchAlgorithmException {
		System.out.println("CalcMAC4DataTransfer");
		String SN01 = HexStrSubStr(SN, 0, 2);// SN.Substring(0 * 2, 2 *
				// 2);//"0123";
		String SN23 = HexStrSubStr(SN, 2, 2);// SN.Substring(2 * 2, 2 *
				// 2);//"50AA";
		String SN47 = HexStrSubStr(SN, 4, 4);// SN.Substring(2 * 4, 2 *
				// 4);//"53213799";
		String SN8 = HexStrSubStr(SN, 8, 1);// SN.Substring(2 * 8, 2 *
	// 1);//"EE";
		
		
		// TODO Auto-generated method stub
		shaInput = derivedKey +H+ "0840"+sectorID+ZeroHexStr(11)+SN8 + SN47+ SN01 + SN23;;
		
		return SHA256(shaInput).toUpperCase();
	
	}
	
	
	public static byte[] shortTobyte(short in,ByteOrder order) {
		 
		ByteBuffer buff = ByteBuffer.allocate(Short.SIZE/8);
		buff.order(order);
 
		// 인수로 넘어온 integer을 putInt로설정
		
		buff.putShort(in);
		
 
		System.out.println("intTobyte : " +NeoHexString.ByteArrayToHexStr(buff.array()) );
		return buff.array();
	}
 
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
		System.out.println(NeoHexString.HexStringToText("313131"));
		shortTobyte((short)15,ByteOrder.LITTLE_ENDIAN);
		
		System.exit(0);
		String result = Util.DeriveKey("5836200463B2244ABDF0D470F1395EBB2BAD94E35DCFDE6DD3DCD007689201D0","0000","0123658B07FD38B9EE");
		System.out.println(result);
		
		System.exit(0);
		
		System.out.println("main 2");
		System.out.println("main 2");
		System.out.println("main 3");
		String refstr = "{\"mapvValue\": {\"MAC\": \"0025FAA2B506AFF1B59F47DEF02472316F3512742DD482863939FC9365450DC4\", \"SN\": \"012350AA53213799EE\",\"UID\": \"ssn_84\" },\"crc16\": \"\", \"cmd\": \"REQ_CONFIRM\"}";
		String refdfasfsf = "H4sIAHqgbFcC_x2MMQ-CMBBG_wq52aG96xXqVtpewgBGjK7EoJsYI9GF8N-lTt_Le8m3wHR9fS_Xx-cO-2KB1odtQSlk8R5rVtaL6JqdmDImUWhKJG2FWGNpMEZTYWXJkZPgyLJhFYOBXQGn7v-kkVh5z4SaSudSyu3cxBzn-TlUBtbNjO9R2-xyHqdbxj4dh3DopOlbWH8vnpamqgAAAA==";

		try {
			
			System.out.println(refstr);
			System.out.println(NeoHexString.TextToHexString(refstr));
			Protocol sadfsadf0 = Protocol.fromJsonString(refstr);
			System.out.println(sadfsadf0.cmd);

			String json = Util.decompress(refdfasfsf);
			System.out.println(json);
			System.out.println(NeoHexString.TextToHexString(json));

			Protocol sadfsadf = Protocol.fromJsonString(json);
			System.out.println(sadfsadf.cmd);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Protocol prot = new Protocol();

		String ret = prot.toJsonString();

		System.out.println(ret);

		try {

			String enc = compress("TEST PROGRAM");
			System.out.println(enc);
			String dec = decompress(enc);
			System.out.println(dec);

			String encURL = compressURL("TEST PROGRAM");
			System.out.println(encURL);
			dec = decompress(encURL);
			System.out.println(dec);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
