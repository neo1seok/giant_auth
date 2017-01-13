import hashlib
import base64
import gzip

import http
import requests
import socket

import  simplejson as json
import neolib.neolib as neolib

class TestGiant2ClientRunnable(neolib.NeoRunnableClasss):
	sndjson = [
		{"cmd": "REQ_SESSION", "crc16": "", "mapvValue": {}},
		{"cmd": "REQ_CONFIRM", "crc16": "", "mapvValue": {"UID": "ssn_84", "SN": "012350AA53213799EE",
														  "MAC": "0025FAA2B506AFF1B59F47DEF02472316F3512742DD482863939FC9365450DC4"}},

		{"cmd": "REQ_POSITION", "mapvValue": {"UID": "ssn_84", "POSTYPE": "GPS", "POS_INFO": "서울특별시"}, "crc16": ""},
	]

	def SubHexStr(self, ORG, stindex,count):
		return ORG[stindex * 2: stindex * 2 + count* 2]

	def CalcMAC(self, key,  strChallenge,  SN):

		if len(SN) != 9 * 2:
			return


		sector0 = "0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000"
				#'0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000'

		SN01 = self.SubHexStr(SN,0,2) # [0 * 2: 0 * 2 + 2 * 2]   #; // "0123";
		SN23 = self.SubHexStr(SN,2,2) #SN[2 * 2: 2 * 2 + 2 * 2]   #; // "50AA";
		SN47 = self.SubHexStr(SN,4,4) #SN[2 * 4: 2 * 4 + 2 * 4]   #; // "53213799";
		SN8 = self.SubHexStr(SN,8,1) #SN[2 * 8: 2 * 4 + 2 * 1]  #; // "EE";

		Zero11 = "0000000000000000000000";
		shaInput = key + strChallenge + "08400000" + Zero11 + SN8 + SN47 + SN01 + SN23
		print("shaInput:\n"+shaInput)
		resbyte = hashlib.sha256(neolib.HexString2ByteArray(shaInput))
		hex_dig = resbyte.hexdigest()


		return hex_dig.upper()  # neolib.ByteArray2HexString(resbyte.hexdigest(),'').upper()

	def start(self):
		# create a socket object
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

		# get local machine name
		# host = socket.gethostname()
		host = "192.168.0.76"

		port = 5510

		# connection to hostname on the port.
		self.s.connect((host, port))



	def doUnit(self,strsnd):
		buff = strsnd.encode()

		size = len(buff)
		print("buff {0} size:{1}".format(neolib.ByteArray2HexString(buff, ''), size))
		self.s.send(size.to_bytes(4, byteorder='big'))

		self.s.send(buff);

		# Receive no more than 1024 bytes
		tm = self.s.recv(4)

		rsize = int.from_bytes(tm, 'big')
		print("r buff size:{0}".format(size))

		tm = self.s.recv(rsize)

		return tm.decode('utf-8')

	def doEnd(self):
		self.s.close()


	def doRun(self):

		self.start()



		for tmp in self.sndjson:
			map = tmp['mapvValue']
			if tmp["cmd"] == 'REQ_SESSION':

				None
			elif tmp["cmd"] == 'REQ_CONFIRM':
				map['UID'] = mapSession['UID']
				map['MAC'] = self.CalcMAC('0000A1AC57FF404E45D40401BD0ED3C673D3B7B82D85D9F313B55EDA3D940000',
									 mapValue['RN'],
									 '012350AA53213799EE'

									 )

				None

			elif tmp["cmd"] == 'REQ_POSITION':
				map['UID'] = mapSession['UID']

				None

			strsnd = json.dumps(tmp)

			strrcev = self.doUnit(strsnd)


			print("The time got from the server is %s" % strrcev)

			#rproto = json.loads(strrcev, object_hook=object_decoder)
			rproto = json.loads(strrcev)

			mapValue = rproto["mapvValue"]


			if tmp["cmd"] == 'REQ_SESSION':
				mapSession = mapValue






			print(rproto["mapvValue"])
			self.doEnd()

class TestHTTPCLient(TestGiant2ClientRunnable):
	def compress(self,str):
		s_out =gzip.compress(str.encode())
		sret = base64.b64encode(s_out).decode()

		print(sret)
		sret = sret.replace('+','-').replace( '/', '_')
		print(sret)



		print(base64.urlsafe_b64encode(s_out).decode())




		return base64.urlsafe_b64encode(s_out).decode()

	def decompress(self,str):
		print(str)

		# str = str.replace('-', '+').replace('_', '/')
		#str += "="
		print(str)

		#s_out = base64.b64decode(str.encode())


		s_out =base64.urlsafe_b64decode(str.encode())
		return gzip.decompress(s_out).decode();

	def start(self):
		self.conn = http.client.HTTPConnection('localhost:8080')
		print(self.conn);

	def doUnit(self, strsnd):


		compjson =self.compress(strsnd)
		print(strsnd)
		print(compjson)
		strrequest = "/giant2Auth/NFC?cmd=CMDBYJSON&jsonbase64={0}".format(compjson)

		print(strrequest)

		self.conn.request("GET", strrequest)

		resp = self.conn.getresponse()
		print(resp.status, resp.reason)
		recvcompress = resp.read().decode()
		print(recvcompress)


		return self.decompress(recvcompress)

	def doEnd(self):
		self.conn.close()
	def reqGet(self,conn,jsonbase):
		strrequest = "/giant_auth/auth?json={0}".format(jsonbase)
		strrequest = strrequest.replace(" ","")
		print(strrequest)
		conn.request("GET", strrequest)
		resp = conn.getresponse()

		print(resp.status, resp.reason)
		data1 = resp.read()
		print(data1.decode())
		res = json.loads(data1.decode());
		return res['params']

	def doRun(self):

		#conn = http.client.HTTPConnection('localhost:8080')
		conn = http.client.HTTPConnection('35.163.249.213:8080')
		print(conn);

		mapvValue = self.reqGet(conn,'{"cmd":"REQ_START_SESSION","params":{"sn":"4C4715000000000047","masterkey_ver":"0"}}')
		#uid = "ssn_31"#mapvValue["uid"]
		uid = mapvValue["uid"]
		challenge = mapvValue["challenge"]
		print(challenge)

		mapvValue = self.reqGet(conn, json.dumps({"cmd":"AUTHENTICATION","params":{"uid":uid,"mac":"E64E53710C8FBEFF5642A8D0525450D41606379CA22356E23761AB2A8DB01B37"} }))

		#return

		#mapvValue = self.reqGet(conn, json.dumps({"cmd": "REQ_APP_KEY", "mapvValue": {"uid": uid,"appid": "14A148EF48A7863A930BEF984C6411EA"}}))




		mapvValue = self.reqGet(conn, json.dumps({"cmd": "REQ_HOSTCHALLENGE", "params": {"uid": uid}}))

		mapvValue = self.reqGet(conn, json.dumps({"cmd": "REQ_UPDATEINFO", "params": {"uid": uid,"gen_nonce":"73FDDB80C9A738EBFABD52092CC8902AE42216C355A00808D5C6EE5D9FA9F500"}}))
		mapvValue = self.reqGet(conn, json.dumps({"cmd": "NOTY_UPDATERESULT", "params": {"uid": uid,"result": "OK"}}))








		None

if __name__ != '__main__':
	exit()

TestHTTPCLient().Run()