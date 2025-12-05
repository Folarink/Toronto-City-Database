build: DatabaseInterface.class

DatabaseInterface.class: DatabaseInterface.java
	javac DatabaseInterface.java

run: DatabaseInterface.class
	java -cp .:mssql-jdbc-12.4.2.jre8.jar DatabaseInterface

clean:
	rm DatabaseInterface.class
