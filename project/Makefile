all: mini	\
	main

mini:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj

main:
	javac CheckProgram.java
	javac Helper.java
	javac Main.java
ifdef file
	java Main $(file)
endif

run:
	java Main $(file)

clean:
	rm -f *.class *~

cleanall:
	rm -f ./syntaxtree/*.class ./visitor/*.class  *.class *~

