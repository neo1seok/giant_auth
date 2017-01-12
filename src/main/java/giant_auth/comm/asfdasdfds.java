package giant_auth.comm;


class CHIP {
	int seq = 0;
	String chp_uid = "";
	String prd_uid = "";
	String sn = "";
	String key_value = "";
	String updt_date = "";
	String reg_date = "";
	String comment = "";

};
class CONFIRM {
	int seq = 0;
	String cfm_uid = "";
	String chp_uid = "";
	String rand_number = "";
	String mac = "";
	String result = "";
	String error = "";
	String updt_date = "";
	String reg_date = "";
	String comment = "";

};
class PRODUCT {
	int seq = 0;
	String prd_uid = "";
	String prd_no = "";
	String key_type = "";
	String slot_no = "";
	String key_value = "";
	String single_key = "";
	String prd_name = "";
	String prd_info = "";
	String prd_url = "";
	String updt_date = "";
	String reg_date = "";
	String comment = "";

};
class COMMENT {
	int seq = 0;
	String cmt_uid = "";
	String chp_uid = "";
	String msg = "";
	String updt_date = "";
	String reg_date = "";
	String comment = "";

};
class SESSION {
	int seq = 0;
	String ssn_uid = "";
	String chp_uid = "";
	String cfm_uid = "";
	String rand_number = "";
	String ip = "";
	String updt_date = "";
	String reg_date = "";
	String comment = "";

};
class GPSINFO {
	int seq = 0;
	String gps_uid = "";
	String chp_uid = "";
	String cfm_uid = "";
	String ip = "";
	String pos_type = "";
	String gps_info = "";
	String updt_date = "";
	String reg_date = "";
	String comment = "";

};