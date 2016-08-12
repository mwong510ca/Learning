# Use the file name mbox-short.txt as the file name
fname = raw_input("Enter file name: ")
total = 0.0
count = 0
fh = open(fname)
for line in fh:
    if not line.startswith("X-DSPAM-Confidence:") : 
        continue
    value = (float) (line[20:26])
    count = count + 1
    total = total + value
print "Average spam confidence:", total/count


