all: compile

compile:
	java -jar lib/jtb132di.jar -te minijava.jj
	java -jar lib/javacc5.jar minijava-jtb.jj
	mkdir -p out/production/MiniJava
	javac -d out/production/MiniJava Main.java

clean:
	rm -rf minijava-jtb.jj visitor syntaxtree JavaCharStream.java MiniJavaParser* ParseException.java Token.java TokenMgrError.java out/production/MiniJava/* *~

