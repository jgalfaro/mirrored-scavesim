CC=javac
DATE=`date +%y%m%d-%H:%M`
FILE=Main

all: class
	
class:
	$(CC) $(FILE).java

run:	class
	java $(FILE)

clean:
	rm -f *.class

#$(CC) $(FILE).java 2>/dev/null
