import sys
import time
import neolib.neolib as neolib
import http
import simplejson as json
import socket
import http.client
import logging
import threading
import errno
from logging import handlers

# create a socket object

class HandleClient:
	def __init__(self,clientsocket,logger):
		self.mapProc = {
			0x10: (self.ReqStartSession, "REQ_START_SESSION"),
			0x11: (self.Authentication, "AUTHENTICATION"),
			0x12: (self.ReqHostchallenge, "REQ_HOSTCHALLENGE"),
			0x13: (self.ReqUpdateinfo, "REQ_UPDATEINFO"),
			0x14: (self.NotyUpdateresult, "NOTY_UPDATERESULT"),
			0x15: (self.ReqAppKey, "REQ_APP_KEY"),
			0x16: (self.NotyAppkeyresult, "NOTY_APPKEYRESULT"),

		}

		self.mapErro={
			"NO_SN": 0xf0,
			"NOT_MATCH_MAC": 0xf1,
			"NO_MASTERKEY": 0xf2,

		}
		self.logger = logger
		self.clientsocket =clientsocket
		self.conn = http.client.HTTPConnection('localhost:8080')


	def reqGet(self,params):
		jsonbase = json.dumps({"cmd": self.cmdname, "params": params})
		strrequest = "/giant_auth/auth?json={0}".format(jsonbase)
		strrequest = strrequest.replace(" ","")
		print(strrequest)
		self.logger.debug("strrequest:%s", strrequest)
		self.conn.request("GET", strrequest)
		resp = self.conn.getresponse()

		#print(resp.status, resp.reason)
		self.logger.debug("%s", "{0} {1}".format(resp.status, resp.reason))
		data1 = resp.read()
		print(data1.decode())
		self.logger.debug("res:%s", data1.decode())
		res = json.loads(data1.decode());
		return res['params']

	def LRC(self,buff,st,ed):
		res = 0;
		for tmp in buff[st:ed]:
			res = res^tmp
		return res.to_bytes(1,byteorder='big')

	def maketoBuff(self,icmd,data):
		resbuff = b"\x02"
		resbuff += (len(data)+1).to_bytes(2,byteorder='big')
		resbuff += icmd.to_bytes(1, byteorder='big')
		resbuff += data
		resbuff += b"\x03"
		resbuff += self.LRC(resbuff,1,len(resbuff))

		return resbuff

	def subStrin(selfb,buff,st,length):
		return buff[st:st+length],st+length

	def parseFromBuff(self,buff):
		print(neolib.ByteArray2HexString(buff))
		lrc =  self.LRC(buff, 0, len(buff)-1)
		print(lrc)

		stx = buff[0:1]
		blength = buff[1:3]
		size = int.from_bytes(blength,byteorder='big')

		print(size)
		cmd = buff[3:4]
		icmd = int.from_bytes(cmd, byteorder='big')
		idx = 4
		data,idx = self.subStrin(buff,idx,size-1)
		etx, idx = self.subStrin(buff, idx,1)
		lrc, idx = self.subStrin(buff, idx, 1)

		self.logger.debug("%s", "{0},{1},{2},{3},".format(stx,icmd,etx,lrc))
		self.logger.debug("DATA:%s",neolib.ByteArray2HexString(data))
		return 	stx,size,icmd,data,etx,lrc

	def processResult(self,mapSrv):
		result = mapSrv['result']
		error = mapSrv['error']
		if result == 'OK':
			return neolib.HexString2ByteArray("00")

		if error in self.mapErro:
			err = self.mapErro[error]
		else:
			err =0xff

		return  err.to_bytes(1, byteorder='big')





	def ReqStartSession(self,data):
		self.logger.debug("ReqStartSession")
		sn = data[0:9]

		self.mapSrv = self.reqGet({"sn": neolib.ByteArray2HexString(sn)})
		self.uid = self.mapSrv['uid']
		result = self.mapSrv['result']

		bchallenge = neolib.HexString2ByteArray(self.mapSrv['challenge'])


		return bchallenge

	def Authentication(self,data):
		self.logger.debug('Authentication')

		mac = data[0:32]
		self.mapSrv = self.reqGet({"uid":self.uid,"mac": neolib.ByteArray2HexString(mac)})
		bupdate = b'\x00'

		if self.mapSrv["result"] == "OK" and self.mapSrv['update'] == 'OK':
			bupdate = b'\x01'


		return bupdate
	def ReqHostchallenge(self,data):
		self.logger.debug('ReqHostchallenge')
		self.mapSrv = self.reqGet({"uid":self.uid})

		bhostchallenge = neolib.HexString2ByteArray(self.mapSrv['hostchallenge'])

		return bhostchallenge

	def ReqUpdateinfo(self,data):
		self.logger.debug('ReqUpdateinfo')
		gen_nonce = data[0:32]
		self.mapSrv = self.reqGet({"uid":self.uid,"gen_nonce": neolib.ByteArray2HexString(gen_nonce)})

		bwrite_code = neolib.HexString2ByteArray(self.mapSrv['write_code'])
		bmac = neolib.HexString2ByteArray(self.mapSrv['mac'])

		return bwrite_code+bmac
	def NotyUpdateresult(self,data):
		self.logger.debug('NotyUpdateresult')
		result = ""
		if int.from_bytes(data[0:1],byteorder='big') == 0:
			result = "OK"


		self.mapSrv = self.reqGet({"uid":self.uid,"result":result})

		return b''
	def ReqAppKey(self,data):
		self.logger.debug('ReqAppKey')
		appid = data[0:16]
		self.mapSrv = self.reqGet({"uid":self.uid,"appid": neolib.ByteArray2HexString(appid)})
		bdata = b''
		bdata += neolib.HexString2ByteArray(self.mapSrv['app_id'])
		bdata += neolib.HexString2ByteArray(self.mapSrv['IV'])
		bdata += neolib.HexString2ByteArray(self.mapSrv['Cipher'])
		bdata += neolib.HexString2ByteArray(self.mapSrv['mac'])


		return bdata

	def NotyAppkeyresult(self,data):
		self.logger.debug('NotyAppkeyresult')
		result = ""
		if int.from_bytes(data[0:1], byteorder='big') == 0:
			result = "OK"

		mapa = {"uid": self.uid , "result": result}

		self.mapSrv = self.reqGet(mapa)

		return b''

	def ReqEndSession(self):
		self.logger.debug('ReqEndSession')


		mapa = {"uid": self.uid}
		self.cmdname = "REQ_END_SESSION"
		self.mapSrv = self.reqGet(mapa)

		return b''

	def doProc(self,buff):
		self.bresult = b'\x00'
		stx, size, icmd, data, etx, lrc = self.parseFromBuff(buff)
		processer = self.mapProc[icmd][0]
		self.cmdname = self.mapProc[icmd][1]
		resdata = b''
		try:
			resdata = processer(data)
		except IOError as e:
			self.logger.error("I/O error({0}): {1}".format(e.errno, e.strerror))
			self.logger.error("Unexpected error:", sys.exc_info()[0])
			return b''
		except ValueError:
			self.logger.error("Could not convert data to an integer.")
			self.logger.error("Unexpected error:", sys.exc_info()[0])
			return b''
		except Exception as ext:
			self.logger.error("doProc:%s", ext)
			self.logger.error("Unexpected error:", sys.exc_info()[0])
			return b''
		except:
			self.logger.error("Unexpected error:", sys.exc_info()[0])
			return b''


		self.bresult = self.processResult(self.mapSrv)


		return self.maketoBuff(icmd,self.bresult+resdata)

	def Test(self):

		self.conn = http.client.HTTPConnection('localhost:8080')
		dres = self.doProc(self.maketoBuff(0x10,neolib.HexString2ByteArray("4C4715000000000047")))
		#dres = self.doProc(self.maketoBuff(0x10, neolib.HexString2ByteArray("4C4722334455667747")))
		# dres = self.doProc(self.maketoBuff(0x11,neolib.HexString2ByteArray("14A148EF48A7863A930BEF984C6411E3EF3540954ED55F6F10C5173CB6EC27E5")))
		# dres = self.doProc(self.maketoBuff(0x12,b''))
		# dres = self.doProc(self.maketoBuff(0x13,neolib.HexString2ByteArray("14A148EF48A7863A930BEF984C6411E3EF3540954ED55F6F10C5173CB6EC27E5")))
		# dres = self.doProc(self.maketoBuff(0x14,neolib.HexString2ByteArray("01")))
		# dres = self.doProc(self.maketoBuff(0x15,neolib.HexString2ByteArray("EF3540954ED55F6F10C5173CB6EC27E5")))
		self.conn.close()
	def RunClient(self):
		self.conn = http.client.HTTPConnection('localhost:8080')
		while True:
			try:
				buff = self.clientsocket.recv(1024)

				self.logger.debug(neolib.ByteArray2HexString(buff))

				if buff == b'':
					break

				sndbuff = self.doProc(buff)

				self.logger.debug(neolib.ByteArray2HexString(sndbuff))

				time.sleep(0.1)
				self.clientsocket.send(sndbuff)
				time.sleep(0.1)
			except socket.error as  e:
				if e.errno == errno.ECONNRESET:
					# Handle disconnection -- close & reopen socket etc.
					self.logger.debug("DISCONNECTED")
					break
				raise

			except Exception as ext:
				self.logger.debug("RunClient Exception")
				self.logger.error(ext)
				#raise
				break

		self.conn.close()
		#self.clientsocket.close()



	def RunServer(self):
		serversocket = socket.socket(
			socket.AF_INET, socket.SOCK_STREAM)

		# get local machine name
		host = '0.0.0.0'

		port = 5510

		# bind to the port
		serversocket.bind((host, port))

		# queue up to 5 requests
		serversocket.listen(5)
		self.uid = ""
		while True:
			# establish a connection
			self.logger.debug('waiting')
			clientsocket, addr = serversocket.accept()
			#handle = HandleClient()
			self.logger.debug("Got a connection from %s" % str(addr))



class HandleServer:
	def __init__(self):
		# conn = http.client.HTTPConnection('localhost:8080')
		#self.handler = handlers.TimedRotatingFileHandler(filename="log.txt", when='D')
		handler ,ch = self.createHandler("log.txt")
		self.logger = self.createLogger("tcp_giant_auth")
		#self.logger2 = self.createLogger("tcp_giant_auth2")
		self.addHandler(self.logger,handler ,ch )
		#self.addHandler(self.logger2, handler ,ch )
		self.logger.debug("%s __init__", self.__class__.__name__)

	# self.conn = http.client.HTTPConnection('localhost:8080')
	def createHandler(self,filename):
		handler = handlers.TimedRotatingFileHandler(filename=filename, when='D')
		ch = logging.StreamHandler()
		ch.setLevel(logging.DEBUG)

		# create formatter
		formatter = logging.Formatter('%(relativeCreated)6d %(threadName)s %(asctime)s - %(name)s - %(funcName)s(%(lineno)d) %(levelname)s - %(message)s')

		# add formatter to ch
		ch.setFormatter(formatter)
		handler.setFormatter(formatter)

		return  handler,ch

	def addHandler(self,logger,handler ,ch):
		# handler = handlers.TimedRotatingFileHandler(filename=filename, when='D')
		# ch = logging.StreamHandler()
		# ch.setLevel(logging.DEBUG)
		#
		# # create formatter
		# formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
		#
		# # add formatter to ch
		# ch.setFormatter(formatter)
		# handler.setFormatter(formatter)

		logger.addHandler(ch)
		logger.addHandler(handler)
		logger.setLevel(logging.DEBUG)

	def createLogger(self, loggename):
		# handler = handlers.TimedRotatingFileHandler(filename=loggename + ".txt", when='D')
		#self.loggename = loggename
		# create logger
		logger = logging.getLogger(loggename)
		#self.logger.setLevel(logging.DEBUG)

		return logger

		# create console handler and set level to debug
		ch = logging.StreamHandler()
		ch.setLevel(logging.DEBUG)

		# create formatter
		formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')

		# add formatter to ch
		ch.setFormatter(formatter)
		handler.setFormatter(formatter)
		# add ch to logger
		self.logger.addHandler(ch)
		self.logger.addHandler(handler)
		return self.logger

	def setLogger(self,loggename):
		self.loggename = loggename
		# create logger
		self.logger = logging.getLogger(loggename)
		self.logger.setLevel(logging.DEBUG)


	def worker(self,clientsocket, addr,idx):
		self.logger.debug("START CLIENT HANDLER")
		try:
			handleClient = HandleClient(clientsocket,self.logger)
			handleClient.RunClient();

		except Exception as e:
			self.logger.error(e)
			self.logger.error(sys.exc_info()[0])

		self.threads[idx]
		del (self.threads[idx])
		clientsocket.close()
		self.logger.debug("END CLIENT HANDLER")
		#self.threads.remove(t)




		None


	def RunServer(self):
		idx = 0
		self.threads = {}
		serversocket = socket.socket(
			socket.AF_INET, socket.SOCK_STREAM)
		# get local machine name
		host = '0.0.0.0'
		port = 5510
		# bind to the port
		try:
			serversocket.bind((host, port))
			# queue up to 5 requests
			serversocket.listen(50)

			while True:
				# establish a connection
				self.logger.debug('LISTENING to CLIENT')
				self.clientsocket, addr = serversocket.accept()
				#handle = HandleClient()
				self.logger.debug("Got a connection from %s , thread num:%d" % (str(addr),len(self.threads)+1))
				t = threading.Thread(target=self.worker,args= (self.clientsocket, addr,idx))

				self.logger.debug(t)
				self.threads[idx]= t
				t.start()
				idx += 1
		except Exception as e:
			self.logger.error(e)
			self.logger.error(sys.exc_info()[0])


#HandleClient().Test()
#HandleClient().RunServer()
HandleServer().RunServer()
exit()
