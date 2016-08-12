name = raw_input("Enter file:")
if len(name) < 1 : name = "mbox-short.txt"
handle = open(name)
counts = dict()

for line in handle :
    if line.startswith("From ") : 
        phase = line.split(" ")
        counts[phase[1]] = counts.get(phase[1], 0) + 1

maxCount = 0
maxKey = None
for key in counts.keys() :
    if counts.get(key) > maxCount :
        maxCount = counts.get(key)
        maxKey = key
        
print maxKey, maxCount