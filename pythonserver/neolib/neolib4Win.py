import win32clipboard
import win32process
import win32api
import neolib.neolib as neolib

def GetClipBoard():
	try:
		win32clipboard.OpenClipboard()
		strret = win32clipboard.GetClipboardData( win32clipboard.CF_UNICODETEXT)  # set clipboard data
		win32clipboard.CloseClipboard()

	except TypeError:
		pass

	return strret

def SetClipBoard(str):
	try:
		win32clipboard.OpenClipboard()
		win32clipboard.EmptyClipboard()
		win32clipboard.SetClipboardData( win32clipboard.CF_UNICODETEXT,str)  # set clipboard data
		win32clipboard.CloseClipboard()
	except TypeError:
		pass

def	ProcIDFromWnd(hwnd):
	thdID,prdID = win32process.GetWindowThreadProcessId(hwnd)
	return prdID

def KillProcess( uID):

	hProcess = win32api.OpenProcess(0x1fffff, False, uID);
	if hProcess != None:
		ret = win32api.TerminateProcess(hProcess, 0)
		win32process.GetExitCodeProcess(hProcess)
		win32api.CloseHandle(hProcess);

def KillProcessFromHandle( hwnd):
	pid = ProcIDFromWnd(hwnd)
	KillProcess(pid)

class NeoAnalyzeClasss(neolib.NeoAnalyzeClasss):
	def SetClopBoard(self):
		SetClipBoard(self.strlines)
		None
