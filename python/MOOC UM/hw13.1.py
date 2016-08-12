import urllib
import xml.etree.ElementTree as ET
from BeautifulSoup import *

url = "http://python-data.dr-chuck.net/comments_271023.xml" #raw_input('Enter URL: ')
html = urllib.urlopen(url).read()
soup = BeautifulSoup(html)

data = urllib.urlopen(url).read()
tree = ET.fromstring(data)

results = tree.findall('comments')
print len(results)
items = results[0].findall('comment')
size = len(items)
total = 0
for item in items :
	total = total + (int) (item.find('count').text)
print total
 