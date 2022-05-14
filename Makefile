all: compile

compile:
	java -jar lib/jtb132di.jar -te minijava.jj
	java -jar lib/javacc5.jar minijava-jtb.jj
	javac -d out/production/Compilers2 Main.java

clean:
	rm -f out/production/Compilers2/* *~

