package giant_auth.comm;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;

import org.apache.commons.codec.binary.Base64;

import com.neolib.Util.NLoger;

public class AES256Cipher {
	 
	 private static volatile AES256Cipher INSTANCE;
	 
	 //final static String secretKey   = "12345678901234567890123456789012"; //32bit
	 final static String secretKey   = "1234567890123456"; //32bit
	 static String IV                = ""; //16bit
	  
	 public static AES256Cipher getInstance(){
	     if(INSTANCE==null){
	         synchronized(AES256Cipher.class){
	             if(INSTANCE==null)
	                 INSTANCE=new AES256Cipher();
	         }
	     }
	     return INSTANCE;
	 }
	 
	 private AES256Cipher(){
	     IV = secretKey.substring(0,16);
	 }
	 
	  //암호화
	  public static String AES_Encode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
	      byte[] keyData = secretKey.getBytes();
	  
	   SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	   
	   Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	   c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
	   
	   byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	   String enStr = new String(Base64.encodeBase64(encrypted));
	   
	   return enStr;
	  }
	 
	  //복호화
	  public static String AES_Decode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
	    byte[] keyData = secretKey.getBytes();
	    System.out.println(new String(keyData) );
	    SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));
	   
	    byte[] byteStr = Base64.decodeBase64(str.getBytes());
	   
	    return new String(c.doFinal(byteStr),"UTF-8");
	  }
	  
	  //키생서
	  public static byte[] generationAES256_KEY() throws NoSuchAlgorithmException{
	   KeyGenerator kgen = KeyGenerator.getInstance("AES");
	   kgen.init(256);
	   SecretKey key = kgen.generateKey();
	   
	   return key.getEncoded();
	   
	  }
	  public static void main(String [] args) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		  
		  String id = "testid"; 
		    String custrnmNo = "111111"; 
		    String custNm = "테스트"; 
		    
		  AES256Cipher aes256 = AES256Cipher.getInstance();
		    
		    String enId = aes256.AES_Encode(id);     
		    String enYyyymmdd = aes256.AES_Encode(custrnmNo);     
		    String enCustNm = aes256.AES_Encode(custNm);    
		    
		    String desId = aes256.AES_Decode(enId);     
		    String desYyyymmdd = aes256.AES_Decode(enYyyymmdd);     
		    String desCustNm = aes256.AES_Decode(enCustNm);
		    
		    
		    NLoger.clog("{0}\n{1}", enId,desId);
		    
		  
		    
		    byte[] tmp = aes256.generationAES256_KEY();
		    System.out.println(new String(tmp) );
		    
		    System.out.println(enId);
		    System.out.println(desId);
	  }
	}
	 