fname = raw_input("Enter file name: ")
fh = open(fname)
lst = list()
for line in fh:
    words = line.strip().split(" ")
    for word in words :
        if lst.count(word) == 0 :
	        lst.append(word)
lst.sort()
print lst

