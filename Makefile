all: compile

compile:
	java -jar lib/jtb132di.jar -te minijava.jj
	java -jar lib/javacc5.jar minijava-jtb.jj
	mkdir -p out/production/Compilers2
	javac -d out/production/Compilers2 Main.java

clean:
	rm -rf minijava-jtb.jj visitor syntaxtree JavaCharStream.java MiniJavaParser* ParseException.java Token.java TokenMgrError.java out/production/Compilers2/* *~

