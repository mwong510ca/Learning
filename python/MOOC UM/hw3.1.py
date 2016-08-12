hrs = raw_input("Enter Hours:")
h = float(hrs)
rate = raw_input("Enter hourly rate:")
r = float(rate)
pay = h * r

if h > 40 :
    pay = pay + (h - 40) * 0.5 * r
    
print pay