package giant_auth.comm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.neolib.db.DefDbHanlingMySQL;
import com.neolib.db.DefDbTableHandling;


public class GiantDbHanlingMySQL extends DefDbHanlingMySQL<TABLE_NAMES>
{

	public GiantDbHanlingMySQL()
	{
		try {
			SystemSettings.SetSystem();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dbname = SystemSettings.dbname;
		this.address = SystemSettings.address;
		this.id = SystemSettings.id;
		this.passwd = SystemSettings.passwd;
		
//		this.dbname = "giant_nfc";
//		//this.address = "localhost";
//		this.address = "192.168.0.75";
//		this.id = "ictk";
//		this.passwd = "#ictk1234";

		

		AddTable(TABLE_NAMES.chip,new DefDbTableHandling(this,"chp"));
		AddTable(TABLE_NAMES.auth,new DefDbTableHandling(this,"ath"));
		AddTable(TABLE_NAMES.masterkey,new DefDbTableHandling(this,"msk"));
		AddTable(TABLE_NAMES.session,new DefDbTableHandling(this,"ssn"));
		


	}
	
	


}