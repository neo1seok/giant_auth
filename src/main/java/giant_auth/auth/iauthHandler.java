package giant_auth.auth;

import giant_auth.comm.CMD;
import giant_auth.comm.Util;

import java.sql.SQLException;

import com.google.gson.Gson;

public interface iauthHandler {
	
	void InitRun() throws Exception;
	String doRunByJson(String json) throws Exception;
	String doRunByCompressed(String compressedJsonBase64) throws Exception;
	void endRun() throws Exception;
	void setAddress(String ipAddress, int port);
	

}
