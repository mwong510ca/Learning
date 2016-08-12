import sqlite3

conn = sqlite3.connect('agesdb.sqlite')
cur = conn.cursor()

# Make some fresh tables using executescript()
cur.executescript('''
CREATE TABLE Ages ( 
  name VARCHAR(128), 
  age INTEGER
);

DELETE FROM Ages;
INSERT INTO Ages (name, age) VALUES ('Morag', 40);
INSERT INTO Ages (name, age) VALUES ('Bhaaldeen', 40);
INSERT INTO Ages (name, age) VALUES ('Camilla', 23);
INSERT INTO Ages (name, age) VALUES ('Adana', 34);
INSERT INTO Ages (name, age) VALUES ('Roark', 37);
INSERT INTO Ages (name, age) VALUES ('Ronin', 31);

SELECT hex(name || age) AS X FROM Ages ORDER BY X;
''')


