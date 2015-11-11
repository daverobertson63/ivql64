package com.roche.veeva;

/**
 * 
 * Main entry for the command line version of idql for Veeva
 * This is a demonstration package - extensions are welcome.
 */


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import asg.cliche.ShellFactory;

public class VeevaConnect {

	public static void main(String[] args) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		System.out.println("(C) Roche 2015 - ivql64 java ");
		System.out.println("?help for all help");
		System.out.println("?list for a list of the veevavault commands");
		
		// Used to setup the settings file templete
		
		/*
		 Properties veevaProps = new Properties();
		 veevaProps.setProperty("userName", "/somethingpath1");
		 veevaProps.setProperty("password", "/somethingpath2");
		 veevaProps.setProperty("csvFile", "/somethingpath2");
		 veevaProps.storeToXML(new FileOutputStream("settings.xml"), "");
		 */
		
		 
		 Properties loadVeevaProps = new Properties();
		 
		 try {
			 loadVeevaProps.loadFromXML(new FileInputStream("settings.xml"));
			 Globals.csvFile = loadVeevaProps.getProperty("csvFile","veeva.csv");
			 Globals.userName = loadVeevaProps.getProperty("userName","default");
			 Globals.password = loadVeevaProps.getProperty("password","default");
			 Globals.documents = loadVeevaProps.getProperty("documents","SELECT id, name__v, type__v FROM documents");
			 
			 //System.out.println(Globals.userName);
			 
		 }
		 
		 catch (Exception e)
		 {
			 
			 System.out.println("Settings file does not exist");
			 
		 }
		
		try{
						
			ShellFactory.createConsoleShell("ivql", "", new VeevaCommandLine()).commandLoop();

		}
		catch(Exception e)
		{
			System.out.println(e);
			throw e;
		}
	
	}

}








	
