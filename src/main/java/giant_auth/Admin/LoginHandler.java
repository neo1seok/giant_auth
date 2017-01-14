package giant_auth.Admin;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import com.neolib.Util.NeoHexString;

import giant_auth.comm.BaseHandler;
import giant_auth.comm.IInvoke;



public class LoginHandler extends BaseHandler<String> {
	
	public LoginHandler() {
		super();
		
		mapInvoke.put("UPDATE_CHIP", new IInvoke() {
			public void run() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException {
				
				
				System.out.println("CMD UPDATE_CHIP");
				
			
				
				
				

			

			}
		});
	}

}
