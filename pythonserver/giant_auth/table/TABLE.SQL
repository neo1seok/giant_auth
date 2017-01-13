
CREATE TABLE `chip` (
	`seq` int(11) NOT NULL COMMENT '순번',
	`chp_uid` varchar(20) NOT NULL COMMENT '고유 아이디',
	`sn` varchar(20) DEFAULT NULL COMMENT '',
	`msk_uid` varchar(20) DEFAULT NULL COMMENT '',
	`slot_no` int(11) DEFAULT NULL COMMENT '',
	`updt_date` datetime NOT NULL COMMENT '',
	`reg_date` datetime NOT NULL COMMENT '등록일자',
	`comment` text DEFAULT NULL COMMENT '기타정보',
PRIMARY KEY (`seq`,`chp_uid`,`sn`)
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	
CREATE TABLE `auth` (
	`seq` int(11) NOT NULL COMMENT '순번',
	`ath_uid` varchar(20) NOT NULL COMMENT '고유 아이디',
	`chp_uid` varchar(20) DEFAULT NULL COMMENT '',
	`rand_number` varchar(64) DEFAULT NULL COMMENT '발급암호종류',
	`mac` varchar(64) DEFAULT NULL COMMENT '',
	`result` varchar(10) DEFAULT NULL COMMENT '',
	`error` text DEFAULT NULL COMMENT '',
	`updt_date` datetime NOT NULL COMMENT '',
	`reg_date` datetime NOT NULL COMMENT '등록일자',
	`comment` text DEFAULT NULL COMMENT '기타정보',
PRIMARY KEY (`seq`,`ath_uid`)
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	
CREATE TABLE `masterkey` (
	`seq` int(11) NOT NULL COMMENT '순번',
	`msk_uid` varchar(20) NOT NULL COMMENT '고유 아이디',
	`key_value` text DEFAULT NULL COMMENT '암호키',
	`version` varchar(10) DEFAULT NULL COMMENT '',
	`updt_date` datetime NOT NULL COMMENT '',
	`reg_date` datetime NOT NULL COMMENT '등록일자',
	`comment` text DEFAULT NULL COMMENT '기타정보',
PRIMARY KEY (`seq`,`msk_uid`)
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	
CREATE TABLE `session` (
	`seq` int(11) NOT NULL COMMENT '순번',
	`ssn_uid` varchar(20) NOT NULL COMMENT '고유 아이디',
	`chp_uid` varchar(20) DEFAULT NULL COMMENT '',
	`msk_uid` varchar(20) DEFAULT NULL COMMENT '',
	`latest_msk_uid` varchar(20) DEFAULT NULL COMMENT '',
	`challenge` varchar(64) DEFAULT NULL COMMENT '',
	`hostchallenge` varchar(40) DEFAULT NULL COMMENT '',
	`updt_date` datetime NOT NULL COMMENT '',
	`reg_date` datetime NOT NULL COMMENT '등록일자',
	`comment` text DEFAULT NULL COMMENT '기타정보',
PRIMARY KEY (`seq`,`ssn_uid`)
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	