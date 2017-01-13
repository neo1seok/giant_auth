import collections
import os
import re
import  shutil
import time

import pymysql
import  simplejson as json

import neolib.neolib as neolib
import neolib.neolib4Win as neolib4Win
import xlrd


class dbHandleing:




	def __init__(self,**kwargs):
		self.conn = pymysql.connect(**kwargs)
		self.cur = self.conn.cursor(pymysql.cursors.DictCursor)

	@staticmethod
	def makeMapMapDBFromListMapDB(key,listMapDB):
		return collections.OrderedDict([(tmprow[key],tmprow ) for tmprow in listMapDB])

	def select(self,sql):
		#print(sql)
		self.cur.execute(sql)
		return self.cur.fetchall()
	def selectToMap(self,key,sql):
		return self.makeMapMapDBFromListMapDB(key,self.select(sql))

	def excute(self,sql):
		#print(sql)
		self.cur.execute(sql)

	def lastSeq(self,tablename):

		sql = "SELECT seq FROM {0} order by seq desc limit 1 ;".format(tablename)
		mapres = self.select(sql)
		if len(mapres) == 0: return 0

		return int(mapres[0]['seq'])

	def insert(self,table,prefix,maprow,lastseq):




		return self.insertList(table,prefix,[maprow],lastseq)



	def insertList(self,table,prefix,listmaprow,lastseq):
		if len(listmaprow)  == 0: return
		maprow = listmaprow[0]

		arraycol = [key if key != 'index' else '`index`' for key,vlaue in maprow.items()]

		lastseq+=1



		sqldstfmt = "INSERT {0} (   seq  ,{1}_uid  ,{2}  ,updt_date  ,reg_date  ) VALUES \n{3}"
		# 0:table 1:prefix 2:col araay 3:values
		sqlvalueContents = "({1}  ,'{0}_{1}',{2},now(),now())"
		# 0:prefix 1:seq 2: values
		values = []
		for maprow in listmaprow:
#			arrayval = list(map(self.mapfunction,maprow.items()))
			arrayval = ["'%s'" % vlaue for key, vlaue in maprow.items()]

			values.append(sqlvalueContents.format(prefix,lastseq,",".join(arrayval)))
			lastseq+=1

		sql = sqldstfmt.format(table,prefix,",".join(arraycol),",\n".join(values))
		#print(sql)
		self.cur.execute(sql);

		return lastseq

	def mapfunction(self, tmp):
		key, value = tmp

		if type(value) == str:
			#print(type(value))
			value = value.replace("'", "\\'")
		return "'%s'" % value

		#0:table 1:prefix 2:seq 3:col araay 4:values

		sql = sqldstfmt.format(table,prefix,lastseq,",".join(arraycol),",".join(arrayval))

		self.cur.execute(sql);


		return lastseq+1

	def deleteTable(self,table):
		self.cur.execute('DELETE FROM %s;'%table)


class BaseMySQLRunnable(neolib4Win.NeoAnalyzeClasss):

	dsttable = "";
	prefix = ""
	colline = ""
	dispalaytitle ="시나리오 상태"

	def setDefArges(self):
		super(BaseMySQLRunnable, self).setDefArges()
		self.defMapArgs.update({'dbaddress': 'localhost'})
		self.defMapArgs.update({'deleteTable': True})


	def InitRun(self):


		self.dbaddress = self.mapArgs['dbaddress']
		self.listmap = []

		print("dst db:"+self.dbaddress)

		self.dstdbHD = dbHandleing(host=self.dbaddress, port=3306, user='ictk', passwd='#ictk1234',	  db='adts', charset='utf8')
		None


	def endRun(self):
		self.dstdbHD.conn.close()
		neolib.NeoRunnableClasss.endRun(self)


	def mainProcess(self):
		None


	def append(self, str):
		print(str)
		self.strlines += str

		self.strlines += "\n"

	def appendA(self, *args,**kwargs):

		self.strlines += '\t'.join(args)
		print(self.strlines)

		self.strlines += "\n"


	sentencepatt = r'G([0-9]+):(F|T):([0-9A-Za-z가-힣^.,_$=\-+()*\^ ]*)\^\\'


	def getValue(self, key, maps):
		if key not in maps: return ''
		return maps[key]


	def appendLine(self, **kwargs):
		mapresult = collections.OrderedDict()
		for tmp in self.cols:
			mapresult[tmp] = ''

		for key,value in kwargs.items():
			if type(value) == str:
				value = value.replace("'", "\\'")
			mapresult[key] = value

		self.listmap.append(mapresult)

	def doRun(self):
		self.strlines = ""
		if self.mapArgs['deleteTable'] :
			self.deleteTable()


		self.cols = re.split(r',\s*',self.colline)
		self.listmap = []


		self.processInserValues()

		# for row in self.listmap:
		# 	print(row)

		self.processInserToDB()

		self.processAfterDB()

		time.sleep(0.3)


		None



	def deleteTable(self):
		self.dstdbHD.deleteTable(self.dsttable)

	def processInserValues(self):
		None

	def processInserToDB(self):
		lastseq = self.dstdbHD.lastSeq(self.dsttable)
		self.dstdbHD.insertList(self.dsttable, self.prefix, self.listmap, lastseq)

	def processAfterDB(self):
		None


class MakeCreateTableFor(neolib4Win.NeoAnalyzeClasss):
	createTableForm ="""
CREATE TABLE `{0}` (
{1}
PRIMARY KEY ({2})
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	"""
#0:tablename 1:fields info 2:pri key array

	fieldForm ="	`{0}` {1} {2} COMMENT '{3}',"
#0:name 1:type 2:null type 3:comment


	xlsDbFile = "D:/PROJECT/자동단말기검수/DOCS/설계서/DB설계서_161123.xlsx"

	dropTableForm = """
	DROP TABLE {0};
	"""

	def makeMapFromDoubllist(self,ret):
		maplist =  collections.OrderedDict()
		listaa = []
		for tmp in ret:
			if tmp[0] != '':
				listaa = []
				maplist[tmp[0]] = listaa
			print(tmp)
			listaa.append(tuple(tmp[1:6]))
		return maplist

	def makeMapFromTxt(self,strtxt):
		ret = neolib.MakeDoubleListFromTxt(strtxt)

		return  self.makeMapFromDoubllist(ret)

	def makeMapFromExcel(self,srcxlsfile):

		#fname = 'D:/PROJECT/toolrnd/DeviceTesterSystem/DOCS/DB설계서_161107.xlsx'

		# Open the workbook
		xl_workbook = xlrd.open_workbook(srcxlsfile)
		sheet_names = xl_workbook.sheet_names()
		print('Sheet Names', sheet_names)
		xl_sheet = xl_workbook.sheet_by_name('TABLE정보')
		print('Sheet name: %s' % xl_sheet.name)
		# Or grab the first sheet by index
		#  (sheets are zero-indexed)
		#
		# xl_sheet = xl_workbook.sheet_by_index(0)
		# print ('Sheet name: %s' % xl_sheet.name)

		# Pull the first row by index
		#  (rows/columns are also zero-indexed)
		#
		# row = xl_sheet.row(1)  # 1st row
		# rows = [tmp for tmp in xl_sheet.get_rows()][2:]
		# print(rows)
		# print([[tmp.value for tmp in row][1:] for row in [tmp for tmp in xl_sheet.get_rows()][2:]])
		# for row in rows:
		# 	print([tmp.value for tmp in row][1:])
		# # print("\t".join(vals.value for vals in [tmp.value	for tmp in row][1]))
		rows = [tmp for tmp in xl_sheet.get_rows()][3:]


		ret = [ tuple([tmp.value for tmp in row][1:]) for row in rows]

		return self.makeMapFromDoubllist(ret)

		return


	def doRun(self):
		ret = self.makeMapFromExcel(self.xlsDbFile)
		self.strlines = self.makeSqlDropAndCreate(ret,self.createTableForm,self.fieldForm)
		neolib.StrToFile(self.strlines, "adts/TABLE.SQL")
		self.strlines = self.makeSqlDropAndCreate(ret, self.dropTableForm, '')
		neolib.StrToFile(self.strlines, "adts/DROP.SQL")

		None

	def makeSqlDropAndCreate(self,ret,mainFmt,fieldForm):


		def convType(row):
			name, type ,nullinfo,comment,pki = row
			return fieldForm.format(name, type ,nullinfo,comment)

		strlines = ""
		for key, fields in ret.items():
			#print(list(filter(lambda x: x[4] =='PRI', fields)))
			listpki = ["`"+name +"`" for name, type, nullinfo, comment, pki in filter(lambda x: x[4] == 'PRI', fields)]

			listmethod = list(map(convType, fields))
			strlines += mainFmt.format(key, "\n".join(listmethod),",".join(listpki))

		return strlines









		None
"""
MakeDataFieldsClass
해당 클래스는 C# 프로젝트 내에 datafield 클래스 문자열 만들어 주는 클래스 이다.
tableList.txt 파일 은 테이블 , 필드 이름과 타입 정보가 들어 있는데(이는 엑셀로 부터 뭍여 오기 한것임)
여기서 클래스이름을 키로 하고 필드이름 과 타입정보를 한 튜플을 배열화 한 맵정보를 만들고
이를 다시 C#에서 사용 가능한 클래스의 문자열로 만들어 out.txt와
클립보드에 넣어 주는 클래스 이다.
 """
class MakeDataFieldsClass(MakeCreateTableFor):
	fmtclassForm = "public class {0} {{\n{1} \n}}\n"
	#0:classname 1:fields list

	fmtfieldForm = "\tpublic {0} {1} {2};"
	#0:typename 1:fieldname 2: initial





	def doRun(self):
		def convType(row ):
			name, type, nullinfo, comment,pki = row
			newtype = 'string'
			strinit = '= ""'
			result = re.match(r'int\(\d+\)',type)
			if result != None :
				newtype = 'int'
				strinit = ''
			if type == 'datetime':
				newtype = 'DateTime'
				strinit = '= DateTime.Now'



			return "\tpublic %s %s %s;"% (newtype,name,strinit)
		#ret = self.makeMapFromTxt("adts/tableList.txt")
		ret = self.makeMapFromExcel(self.xlsDbFile)


		print(ret)
		self.strlines = ''
		classconv = neolib.ConvStringForm(intype='und', outtype='cam')
		for key, fields in ret.items():
			#listmethod = ["\tpublic string %s ;" % name for name,type in fields]

			listmethod = list(map(convType,fields))
			classname = classconv.ConvertString(key)
			self.strlines += "public class {0} {{\n{1} \n}}\n".format(classname, "\n".join(listmethod))

		# print(str)
		# fb = open('out.txt', 'wb')
		# fb.write(str.encode())
		# fb.close()
		#
		# neolib4Win.SetClipBoard(str)


