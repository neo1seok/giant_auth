package giant_auth.comm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by neo1seok on 2016-06-13.
 */
public class Protocol {
    public String cmd = "TEST";

    public Map<String,String> params = new LinkedHashMap<String,String>();
    public String crc16 = "";
    public  Protocol(){
        //mapvValue.put("PGH","181818");
        //mapvValue.put("PGH 2","281818");
    }

    public void Clrear() {
        cmd = "";
        crc16 = "";
        params.clear();
    }
    public String toJsonString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public String toPrettyJsonString(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    
    public static Protocol fromJsonString(String json){
        Gson gson = new Gson();
        

        return gson.fromJson(json,Protocol.class);
    }


    public void CalculateCRC16() {


    }
}
