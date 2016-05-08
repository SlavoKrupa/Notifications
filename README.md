# Notifications
Proof of concept for notifications from postgresql implemented in different languages with performance comparison.

Prerequisities and setup
Running posgreSQL with this parameters

Username: postgres
Password: password
port: 5432
database name: postgres

Run
Notifications\PostgreSQL\SchemaAndTrigger.sql


Start C#, Java or python server (or combination)


Open
Notifications\Frontend\dist\index.html


Click to icon for connection to websocket


Run massive data test:

Notifications\PostgreSQL\MassiveDataTestScript.sql

Or soak test: 
Use of DBInsertion utility

1. You need to have maven running on your computer
2. open command line
3. find the directory DBInsertion
4. mvn package (to build the project)
5. mvn exec:java -Dexec.args="<seconds>"
