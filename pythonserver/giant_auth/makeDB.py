import neolib.neolib as neolib
import neolib.db as neodb
import time

import  simplejson as json
import re
import collections

class CreateTableAnd(neodb.MakeCreateTableFor):
	xlsDbFile = "TABLE정보giant_auth.xlsx"
	def doRun(self):
		ret = self.makeMapFromExcel(self.xlsDbFile)
		self.strlines = self.makeSqlDropAndCreate(ret,self.createTableForm,self.fieldForm)
		neolib.StrToFile(self.strlines, "table/TABLE.SQL")
		self.strlines = self.makeSqlDropAndCreate(ret, self.dropTableForm, '')
		neolib.StrToFile(self.strlines, "table/DROP.SQL")

		None

if __name__ != '__main__':
	exit()


CreateTableAnd().Run()