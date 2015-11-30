# README #

This is a simple front end to the Veeva Vault using Java.  Its similar in approach to the Documentum idql32 - command line app 

### What is this repository for? ###

* Overview

The idea of this app is to provide a command line entry to the VeevaVault. Its similar to idql.exe - a Documentum command utility for accessing a Documentum docbase 

Its based on the cliche java based command line app.  It allows you to define commands and have a console based environment. 

You just run the ivql64.exe from any Windows cmd session.  You will need Java 8 installed to run

### How do I get set up? ###

* Summary of set up

I have created launch4j version in the run dir - but to run  this use

java -jar run/vql64.jar or

ivql64.exe

There is a simple XML settings file wher you can store the username and password.  You need to edit this as it wont save anything from the command line.   Simply edit the fill called settings.xml and use the load command

* Commands

To login - use auth username password

To see which vaults you have access to 

vaults

To execute a VQL

query VQL

To list all documents

documents

When you execute a command it will save the results in a file called veeva.csv which you can edit in Excel or any csv editing tool.

To get help on the command line use ?list.  You can see all the available commands if you use ?list-all



