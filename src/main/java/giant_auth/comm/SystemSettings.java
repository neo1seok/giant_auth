package giant_auth.comm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import com.neolib.Util.NLoger;
 


public class SystemSettings {
	
	public static String buildDate = "2017.01.02";
	public static String dbname;
	public static  String address;
	public  static String id;
	public  static String passwd;
	public  static int port;
	static {
		 System.out.println("Static Constructor of the class");
         try {
			SetSystem();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  static void SetSystem() throws IOException {
		// TODO Auto-generated method stub
		String result = "";
		InputStream inputStream;
	 
		Properties prop = new Properties();
		String propFileName = "config.properties";
		
		

		inputStream = SystemSettings.class.getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		System.out.println("dbname:"+dbname);
		System.out.println("address:"+address);
		System.out.println("id:"+id);
		System.out.println("passwd:"+passwd);
		
	// get the property value and print it out
		dbname = prop.getProperty("dbname");
		address = prop.getProperty("address");
		id = prop.getProperty("id");
		passwd = prop.getProperty("passwd");
		
		System.out.println("dbname:"+dbname);
		System.out.println("address:"+address);
		System.out.println("id:"+id);
		System.out.println("passwd:"+passwd);
		
		NLoger.clog("dbname:"+dbname);
		NLoger.clog("address:"+address);
	}
	
	public static void main(String[] args) throws IOException {
		SetSystem();

	}
	


}
