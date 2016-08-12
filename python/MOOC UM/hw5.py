largest = None
smallest = None
invalid_input = False
while True:
    num = raw_input("Enter a number: ")
    if num == "done" : break
    try :
        num = num + 0
    except :
        invalid_input = True
    if largest is None :
        largest = num
    if smallest is None :
        smallest = num
    if num > largest  :
        largest = num
    if num < smallest :
        smallest = num        
    
if invalid_input :
	print "Invalid input"
    
print "Maximum is", largest
print "Minimum is", smallest

