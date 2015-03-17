import sys
import gspread

try:
	email  = input("Please enter google email: ")
except SyntaxError:
	email

try:
	passwd = input("Please enter google passwd: ")
except SyntaxError:
	passwd

# Login with your Google account
gc = gspread.login(email, passwd)

#You can open a spreadsheet by its title as it appears in Google Docs
sh = gc.open("Santix Loc") # <-- Look ma, no keys!

# By title
worksheet = sh.worksheet("Locs")

#Getting All Values From a Worksheet as a List of Lists
L = worksheet.get_all_values()

for el in L:
	if el != L[0]:
		print(el[0] +'='+ el[2])
