import socket
import neolib.neolib as neolib
import time

def parseFromBuff(buff):
	print(neolib.ByteArray2HexString(buff))
	lrc = LRC(buff, 0, len(buff) - 1)
	print(lrc)

	stx = buff[0:1]
	blength = buff[1:3]
	size = int.from_bytes(blength, byteorder='big')

	print(size)
	cmd = buff[3:4]
	icmd = int.from_bytes(cmd, byteorder='big')
	data = buff[4:4 + size]
	etx = buff[4 + size:4 + size + 1]
	lrc = buff[4 + size + 1:4 + size + 2]
	return stx, size, icmd, data, etx, lrc

def LRC( buff, st, ed):
	res = 0;
	for tmp in buff[st:ed]:
		res = res ^ tmp
	return res.to_bytes(1, byteorder='big')

def maketoBuff(icmd, data):
	resbuff = b"\x02"
	resbuff += (len(data)+1).to_bytes(2, byteorder='big')
	resbuff += icmd.to_bytes(1, byteorder='big')
	resbuff += data
	resbuff += b"\x03"
	resbuff += LRC(resbuff, 1, len(resbuff))

	return resbuff
def proClient(buff):
	print(neolib.ByteArray2HexString(buff))
	stx = buff[0:1]
	blength = buff[1:3]
	size = int.from_bytes(blength,byteorder='big')
	print(size)
	cmd = buff[3:4]
	print(stx,blength,cmd)

	return
def procReqServ(icmd,hexstrdat):
	buff = maketoBuff(icmd,	  neolib.HexString2ByteArray(hexstrdat))
	print("snd",neolib.ByteArray2HexString(buff))
	s.send(buff)
	rbuff = s.recv(1024)
	res = parseFromBuff(rbuff)
	print("rcv",neolib.ByteArray2HexString(rbuff))
	print("res", res)


buff = maketoBuff(0x10,neolib.HexString2ByteArray("4C472233445566774a"))

# create a socket object
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# get local machine name
#host = socket.gethostname()
#host = 'localhost'
host = "35.163.249.213"

port = 5510
#port = 51717

# connection to hostname on the port.
s.connect((host, port))

#s.send('test'.encode())
# Receive no more than 1024 bytes



procReqServ(0x10,"4C4715000000000047")
#procReqServ(0x10,"4C4722334455667747")
time.sleep(1)

buff = neolib.HexString2ByteArray('02000216000317')
s.send(buff)
rbuff = s.recv(1024)
print(rbuff)
time.sleep(10)


#procReqServ(0x11,"14A148EF48A7863A930BEF984C6411E3EF3540954ED55F6F10C5173CB6EC27E5")
# procReqServ(0x12,"")
# procReqServ(0x13,"14A148EF48A7863A930BEF984C6411E3EF3540954ED55F6F10C5173CB6EC27E5")
# procReqServ(0x14,"01")
procReqServ(0x15,"EF3540954ED55F6F10C5173CB6EC27E5")

s.close()

#print("The time got from the server is %s" % tm.decode('ascii'))