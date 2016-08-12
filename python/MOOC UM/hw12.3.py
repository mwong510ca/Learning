# Note - this code must run in Python 2.x and you must download
# http://www.pythonlearn.com/code/BeautifulSoup.py
# Into the same folder as this program

import urllib
from BeautifulSoup import *

url = "http://python-data.dr-chuck.net/known_by_Manson.html" #raw_input('Enter URL: ')
count = (int) (raw_input('Enter count: '))
position = (int) (raw_input('Enter position: '))

print url
loop1 = 1
while (loop1 < count) :
	html = urllib.urlopen(url).read()
	soup = BeautifulSoup(html)

	# Retrieve all of the anchor tags
	tags = soup('a')
	msg = tags[position - 1].get('href', None)
	url = msg
	print url
	loop1 = loop1 + 1
	if (loop1 == count) :
		html = urllib.urlopen(url).read()
		soup = BeautifulSoup(html)
		tags = soup('a')
		print tags[position - 1].get('href', None)
