package giant_auth.comm;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import com.neolib.Util.NLoger;
import com.neolib.Util.RefParam;
import com.neolib.db.IDbHanling;
import com.neolib.db.IDbTableHandling;

public class BaseHandler<T> {
	
	protected Protocol rcvProto;
	protected Protocol sndProto;
	protected IDbHanling<TABLE_NAMES> idbHandling;
	protected Map<T, IInvoke> mapInvoke = new LinkedHashMap<T, IInvoke>();
	protected Map<String, String> mapArg = new LinkedHashMap<String, String>();
	protected RefParam<String> refString = new RefParam<String>();
	
	protected IDbTableHandling itableChipHandler;
	//protected IDbTableHandling itableProductHandler;
	protected IDbTableHandling itableAuthHandler;
	protected IDbTableHandling itableMasterKeyHandler;
	
	//protected IDbTableHandling itableCommentHandler;
	protected IDbTableHandling itableSessionHandler;
	//protected IDbTableHandling itableGpsInfo;


	public BaseHandler() {
		super();
		

		// TODO Auto-generated constructor stub
		idbHandling = new GiantDbHanlingMySQL();
		
		this.itableChipHandler = idbHandling.getTable(TABLE_NAMES.chip);
		//this.itableProductHandler = idbHandling.getTable(TABLE_NAMES.product);
		this.itableAuthHandler = idbHandling.getTable(TABLE_NAMES.auth);
		this.itableMasterKeyHandler = idbHandling.getTable(TABLE_NAMES.masterkey);
		this.itableSessionHandler = idbHandling.getTable(TABLE_NAMES.session);
		//this.itableGpsInfo = idbHandling.getTable(TABLE_NAMES.gpsinfo);

	}
	public void Log(String fmt,Object...args){
		NLoger.clog(fmt, args);
		//System.out.println(MessageFormat.format(fmt, args));
	}
	public void Open(){
		try {
			this.idbHandling.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Close(){
		try {
			this.idbHandling.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Protocol doProcess(Protocol input) throws Exception{
		Log("doProcess");
		
		 
		NLoger.debug("tewst");
		NLoger.info("test");
		NLoger.debug("tewst");
		NLoger.info("test");
		NLoger.debug("tewst");
		NLoger.info("test");
		NLoger.debug("tewst");
		NLoger.info("test");
		
		
		this.rcvProto = input;
		this.sndProto =new Protocol(); 
		sndProto.cmd = rcvProto.cmd;
		
		sndProto.mapvValue.clear();
		sndProto.mapvValue.put("Result","OK");
		
		
		mapInvoke.get(input.cmd).run();
		
		
	
		return this.sndProto; 
	}
	

}
