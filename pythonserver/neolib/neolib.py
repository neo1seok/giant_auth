


import  re
import subprocess
import os
import sys
class NeoRunnableClasss:
	isJustRunThisClass = True

	def __init__(self,**kwargs):
		self.mapArgs = {}
		self.maps = getMapsFromArgs(sys.argv)
		self.setDefArges()


		print("__init__",self.__class__)

		self.mapArgs.update(self.defMapArgs)
		for key,vlaue in kwargs.items():
			self.mapArgs[key] = vlaue

	# def __init__(self):
	# 	None
	def setDefArges(self):
		self.defMapArgs = {
			'exit': True,
		}

	def Run(self):

		try:
			self.exit = self.mapArgs['exit']
		except:
			self.exit = True

		self.InitRun()
		#try:
		self.doRun()

		self.outLog()

		#except Exception as inst:
		#	print(inst.args)
		#finally:
		self.endRun()




	def InitRun(self):
		None

	def doRun(self):
		None


	def outLog(self):
		None

	def endRun(self):

		if self.exit :exit()


class NeoAnalyzeClasss(NeoRunnableClasss):
	strlines = ""
	def SetClopBoard(self):
		None
	def outLog(self):
		fb = open('out.txt', 'wb')
		fb.write(self.strlines.encode())
		fb.close()
		self.SetClopBoard()
		#neolib4Win.SetClipBoard(self.strlines)
		None


def executeAsync( cmd):
	fd = subprocess.Popen(cmd, shell=True,
						  stdin=subprocess.PIPE,
						  stdout=subprocess.PIPE,
						  stderr=subprocess.PIPE)
	return fd.stdout, fd.stderr

def listarg2Map(list):
	maparg = {}
	i = 0
	while i < len(list):
		value = list[i]


		if value.startswith("-"):
			value = re.sub("-","",value)
			print(value)
			nextvalue = list[i + 1] if i +1 < len(list) else ""
			nextvalue = "" if  nextvalue.startswith("-") else nextvalue

			maparg[value] = nextvalue


		i=i+1
	return maparg

def getMapsFromArgs(argv):
	length = len(argv)
	print(argv)

	maps = {tmp[1:]: '' for tmp in argv if tmp.startswith('-')}

	for key, val in maps.items():
		idx = argv.index('-' + key)
		print(idx)
		if idx + 1 >= length: continue
		if argv[idx + 1].startswith('-'): continue

		maps[key] = argv[idx + 1]


	return maps

def HexString2ByteArray(hexstr) :
	return bytes.fromhex(hexstr)

def ByteArray2HexString(bytes,sep="") :
	return  sep.join('{:02X}'.format(x) for x in bytes)

def HexString2Text(hexstr,enc="utf-8") :
	return HexString2ByteArray(hexstr).decode(enc)

def Text2HexString(str,enc="utf-8",sep="") :
	return ByteArray2HexString(str.encode(enc),sep)



def StrFromFile(filepath,enc='utf-8'):
	fb = open(filepath,'rb')
	rt = fb.read()
	str = rt.decode(enc)
	fb.close()
	return str

def StrToFile(str,filepath,enc='utf-8'):
	fb = open(filepath,'wb')
	fb.write(str.encode(enc))
	fb.close()


def MakeDoubleListFromTxt(strtxt):
	strmenu = StrFromFile(strtxt)
	mapobj = map(lambda x: tuple(x.split('\t')), strmenu.split('\r\n'))
	return list(filter(lambda x: len(x) > 1, mapobj))

def MakeDir(path):
	if not os.path.exists(path):
		os.makedirs(path)


class ConvStringForm:


	patttotal = r'([A-Za-z0-9_ ]+)(\t|\n|$)'
	pattcamel = r'([A-Za-z][a-z0-9]+)'
	#pattcamel = r'([A-Z][a-z0-9]*)'

	def __init__(self,**kwargs):
		self.maparg = kwargs
		self.InitValue()

	def InitValue(self):

		self.mapMakeArray = {
			"und": self.makeListFromUnderLine,
			"spc": self.makeListFromSpaceDiv,
			"cam": self.makeListFromCamelForm,
		}
		self.mapMakeString = {
			"und": self.convUnserLine,
			"und_row": self.convUnserLineLower,
			"cam": self.convCamelForm,

		}
		self.intype = ""
		self.outtype = ""

		if 'intype' in self.maparg:
			self.intype = self.maparg['intype']

		if 'outtype' in self.maparg:
			self.outtype = self.maparg['outtype']

		self.updateFunction()


		None

	def updateFunction(self):
		if self.intype != "":
			self.arrfunc = self.mapMakeArray[self.intype]

		if self.outtype != "":
			self.strfunc = self.mapMakeString[self.outtype]


	def convCamelForm(self,listarray):
		newarra = []
		for tmp in listarray:
			hd = tmp[0:1].upper()
			boddy = tmp[1:].lower()
			newarra.append(hd+boddy)
		return "".join(newarra)

	def convCamelForm(self,listarray):
		newarra = []
		for tmp in listarray:
			hd = tmp[0:1].upper()
			boddy = tmp[1:].lower()
			newarra.append(hd+boddy)
		return "".join(newarra)

	def convUnserLine(self, listarray):
		newarra = []
		for tmp in listarray:
			newarra.append(tmp.upper())
		return "_".join(newarra)

	def convUnserLineLower(self, listarray):
		return self.convUnserLine(listarray).lower()

	def makeListFromUnderLine(self, orgstr):
		return orgstr.split("_")

	def makeListFromSpaceDiv(self, orgstr):
		return orgstr.split(" ")

	def makeListFromCamelForm(self, orgstr):
		result = re.findall(self.pattcamel,orgstr)
		for tmp in result:
			print(tmp[1])
			None
		return  list(map((lambda n:n),result))



	def ConvertString(self,inputstring):
		self.updateFunction()
		return self.convertWord(inputstring)




	def convertWord(self,strword):
		array = self.arrfunc(strword)
		return self.strfunc(array)

def deffilter(root,file):
	return  True

def listAllFile(basedir,filter=deffilter):
	listaa = []
	for root, dirs, files in os.walk(basedir):
		root = root.replace("\\","/")
		for file in files:
			if not filter(root,file): continue
			listaa.append((root,file))

	return listaa

def getExtNameFromPath(path):
	return os.path.splitext(path)[1]


def removeEmptyFolder(basedir):
	listaa = []
	while True:
		for root, dirs, files in os.walk(basedir):
			# print(root,len(files),len(dirs))
			sublen = len(files) + len(dirs)
			if sublen == 0: listaa.append(root)

		if len(listaa) == 0: return

		for tmp in listaa:
			# shutil.rmtree(tmp)
			print(tmp)
			os.rmdir(tmp)

			def removeEmptyFolder(basedir):
				listaa = []
				while True:
					for root, dirs, files in os.walk(basedir):
						# print(root,len(files),len(dirs))
						sublen = len(files) + len(dirs)
						if sublen == 0: listaa.append(root)

					if len(listaa) == 0: return

					for tmp in listaa:
						# shutil.rmtree(tmp)
						print(tmp)
						os.rmdir(tmp)




						# if __name__ != '__main__':
# 	exit()
#
# print(ConvStringForm(intype="und",outtype='cam').ConvertString("TEST_ABCCC_DDDD"))
# print(ConvStringForm(intype="spc",outtype='cam').ConvertString("TEST ABCCC DDDD"))
# print(ConvStringForm(intype="cam",outtype='und').ConvertString("TestAbhAaaAcc"))