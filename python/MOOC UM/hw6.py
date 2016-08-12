text = "X-DSPAM-Confidence:    0.8475";

pos = text.find('0');
value = text[pos:len(text)];
print float(value);