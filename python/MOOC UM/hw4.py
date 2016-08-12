def computepay(h,r):
    pay = h * r
    if h > 40 :
        pay = pay + (h - 40) * 0.5 * r
    return pay

hrs = raw_input("Enter Hours:")
h = float(hrs)
rate = raw_input("Enter Hourly Rate:")
r = float(rate)

p = computepay(h, r)
print p