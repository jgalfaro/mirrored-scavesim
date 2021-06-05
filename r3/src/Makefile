CC=javac
DATE=`date +%y%m%d-%H:%M`
FILE=	src/Main

all: class run


J3D_LIB := ./j3d/lib/ext/j3dcore.jar:./j3d/lib/ext/j3dutils.jar:./j3d/lib/ext/vecmath.jar
J3D_BIN := /usr/lib/jvm/java-6-sun-1.6.0.24/jre/lib/amd64/libj3dcore-ogl-cg.so:/usr/lib/jvm/java-6-sun-1.6.0.24/jre/lib/amd64/libj3dcore-ogl.so


CLASSPATH := $(PATH):$(J3D_LIB):$(J3D_BIN):
	
class:
	$(CC) src/*.java
	$(CC) src/network/*.java
run:	class
	java -classpath $(CLASSPATH) $(FILE) -Dtype=0

clean:
	rm -f *.class
	rm -f src/*.class
	rm -f src/network/*.class

#$(CC) $(FILE).java 2>/dev/null

