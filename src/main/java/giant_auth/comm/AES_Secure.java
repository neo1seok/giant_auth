package giant_auth.comm;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.neolib.Util.NLoger;
import com.neolib.Util.NeoHexString;

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 */
public class AES_Secure {
    private IvParameterSpec iv;
    private Key keySpec;
   String mapvalue ="64CA1B0D5D3753540CE2C7FEDD009E67";
    
    

    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     * @param key 암/복호화를 위한 키값
     * @return 
     * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
     */
    public AES_Secure(String key) {
        
    	byte [] resmap = NeoHexString.HexStrToByteArray(mapvalue);
        byte[] keyBytes = toMD5(key);
        
      
        ;
        NLoger.clog(NeoHexString.ByteArrayToHexStr(keyBytes));
        
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        
        
        for(int i = 0 ; i < keyBytes.length ; i++){
        	keyBytes[i] ^= resmap[i];
        }
        this.iv = new IvParameterSpec(keyBytes);
        NLoger.clog(NeoHexString.ByteArrayToHexStr(keyBytes));
        this.keySpec = keySpec;
    }
    public static byte [] toMD5(String strKey) {
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("MD5");
	        md.reset();
	        md.update(strKey.getBytes("UTF-8"));
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    return md.digest();
	}

    /**
     * AES256 으로 암호화 한다.
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
        return encrypt(str.getBytes("UTF-8"));
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String decrypt2Str(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
  
        return new String(decrypt(str), "UTF-8");
    }
    /**
     * AES256 으로 암호화 한다.
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(byte [] org) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = c.doFinal(org);
        String enStr = new String(Base64.encodeBase64(encrypted));
        return enStr;
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public byte [] decrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, iv);
        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        return c.doFinal(byteStr);
    }
    public static void Encodings(AES_Secure aese,String org) throws NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException{
		String enc = aese.encrypt(NeoHexString.HexStrToByteArray(org));
		byte []  reback = aese.decrypt(enc);
		
		NLoger.clog("{0}\t{1}\t{2} ", org,enc,NeoHexString.ByteArrayToHexStr(reback));
    
    }
    public static void main(String [] args){
    	NLoger.isSystemConsole = true;
    	AES_Secure aese = new AES_Secure("FUCKK MB!!!!!");
    	try {
    		Encodings(aese,"0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000");
    		Encodings(aese,"0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940001");
    		Encodings(aese,"0000000000000000000000000000000000000000000000000000000000000000");
    		Encodings(aese,"0000000000000000000000000000000000000000000000000000000000000000");
    		Encodings(aese,"0000000000000000000000000000000000000000000000000000000000000000");
    		Encodings(aese,"0000000000000000000000000000000000000000000000000000000000000000");
    		Encodings(aese,"000000000000000000000000000000000000000000000045476000000000C047");
    		Encodings(aese,"00000000000000000000000000000000000000000000004547DB00000000C047");
    		Encodings(aese,"000000000000000000000000000000000000000000000045476A00000000C047");
    		Encodings(aese,"000000000000000000000000000000000000000000000045475B00000000C047");

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    
    }
}