import re

handle = open("regex_sum_271021.txt")
total = 0

for line in handle :
    lst = re.findall('[0-9]+', line)
    for val in lst :
    	total += (int) (val)

print total
