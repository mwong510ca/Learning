import json
import urllib
from BeautifulSoup import *

url = "http://python-data.dr-chuck.net/comments_271027.json" #raw_input('Enter URL: ')
html = urllib.urlopen(url).read()
soup = BeautifulSoup(html)

data = urllib.urlopen(url).read()
info = json.loads(data)
total = 0

for item in info['comments'] :
	total = total + item['count']
print total
    
