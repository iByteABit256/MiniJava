all: compile

compile:
	java -jar lib/jtb132di.jar -te minijava.jj
	java -jar lib/javacc5.jar minijava-jtb.jj
	javac Main.java

clean:
	rm -f *.class *~

