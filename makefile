all: a

a:
	@find -name "*.java" > sources.txt
	@javac @sources.txt

