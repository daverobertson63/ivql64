package com.roche.veeva;

/**
 * 
 * This contains all of the primary command line functions
 * 
 */

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.io.FileUtils;

import java.util.Map;
import java.util.Properties;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import asg.cliche.Command;
import asg.cliche.Param;

public class VeevaCommandLine {

	public VeevaVaults vv = new VeevaVaults();	// Master storage for all operations in this class.
	public boolean verboseMode=false;	// If this is set then we get some debug 

	@Command(description="Simple hello")
	
	public String hello(  ) {
		return "Hello and welcome to the Veeva API interactive!";
	}
	
	@Command (description="Set the verbose mode of debug")
	public String verbose( @Param(name="True/False", description="True or False") boolean on) {
		
		if ( on )
			verboseMode = true;
		else
			verboseMode = false;
		
		return "Verbose mode is: " + on;
		
	}
	
	@Command (description="Show the session token")
	public String session()
	{
		
		if (vv.token.length() > 0 )
			
			return vv.token;
		
		else
			return "No session id available";
		
	}
	
	@Command (description="List available vaults")
	public String vaults()
	{
		
		if ( vv.VeevaVaults.size() == 0 )
		{
			return "No vaults available";
		}
		
		for (int i = 0; i < vv.VeevaVaults.size(); i++) {

			VeevaVault objects = vv.VeevaVaults.get(i);
			
			System.out.print(i + ": ");
			System.out.print("name: " + objects.name);
			System.out.print("url: " + objects.url);
			System.out.println("id: " + objects.id);
		
		}
		
		return "All available vaults...";
		
		
	}

	
	/**
	 * Enter interactive username and password from the console
	 */
	public void iauth()
	{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		BufferedReader br = null;
		String jsonData = "";
		
					
		Console console = System.console();
		if (console == null) {
		    System.out.println("Couldn't get Console instance - you will need to enter username/password manually");
		        return;
		   }

		console.printf("Veeva password%n");
		String username = console.readLine("Enter your  username: ");
		
		console.printf("Username entered was: %s%n", username);
		
		char passwordArray[] = console.readPassword("Enter your  password: ");
		console.printf("Password entered was: %s%n", new String(passwordArray));
		String password = new String(passwordArray);
		
		return;
	}
	
	
	/**
	 *  Authentication the user - use values stored in settings.xml
	 * @return
	 */
	@Command (description="Login with details stored in settings.xml")
	
	public String authstored()
	{
		 auth(Globals.userName, Globals.password);
		 return "Login from stored details";
		
	}
	
	
	/**
	 * Authentication the user - do the actual authorisation and set the token
	 */
	@Command (description="Login with username and password")
	public String auth(String username, String password)
	{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		BufferedReader br = null;
				
		try {

			// build the auth string and just go for it.
			HttpPost httpPost = new HttpPost(Globals.url + username + "&password=" + password);

			// This is not required as you put them on the command line... but... would be nice :)
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response2 = httpclient.execute(httpPost);

			if ( verboseMode)
				System.out.println(response2.getStatusLine());

			BufferedReader brhttp = new BufferedReader(new InputStreamReader((response2.getEntity().getContent())));

			String output = null;
			String result = "";

			
			while ((output = brhttp.readLine()) != null) {

				result += output + "\n";
			}
			if ( verboseMode){
				System.out.println("Output from Server .... \n");
				System.out.println(result);
				System.out.println("<------------------------->\n");
			}
			
			JSONObject nodeRoot  = new JSONObject(result); 

			if (verboseMode)
				System.out.println(nodeRoot.toString());

			String responseStatus = nodeRoot.getString("responseStatus");

			if ( !responseStatus.contentEquals("SUCCESS"))
			{
				
				vv.setToken("");
				return "Authentication Failed";
				
			}

			// This seems to be needed - I dont know why
			JSONObject responseCode = new JSONObject(nodeRoot.toString());

			// Get the list of available vaults
			JSONArray vaults = responseCode.getJSONArray("vaultIds");

			
			// Set the token ID for use in subsequent calls
			vv.setToken( nodeRoot.getString("sessionId"));


			if ( verboseMode)
			{
				System.out.println("Vaults available");
			}
			
			// Look the vaults and look for available https endpoints we can use for API
			for (int i = 0; i < vaults.length(); i++) {

				JSONObject objects = vaults.getJSONObject(i);

				if ( verboseMode)
				{
					System.out.println(objects.get("name"));
					System.out.println(objects.getInt("id"));
					System.out.println(objects.get("url"));
				}
				vv.addVault(objects.getString("name"), objects.getString("url"), objects.getInt("id"));

			}
		}
		catch(Exception e)
		{
			// TODO
			System.out.println(e);
			return "Error in authentication: " + e.getMessage();
		}

		return "Connected to Veeva Vaults with valid sessionID";
	}
	
	/**
	 * 
	 * 
	 * @return Message
	 * @throws Exception 
	 */
	@Command (description="List all documents in the system")
	public String documents( ) throws Exception
	{
		
		String vql = "SELECT id, name__v, type__v FROM documents";
		
		this.query(vql);
        
		return "All Documents query finished";
		
		
	}
	
	/**
	 * Load the properties into globals
	 * @return
	 * @throws Exception
	 */
	@Command (description="reload the properties file?list")
	public String load( ) throws Exception
	{
	
	Properties loadVeevaProps = new Properties();
	 
	 try {
		 
		 loadVeevaProps.loadFromXML(new FileInputStream("settings.xml"));
		 
		 Globals.csvFile = loadVeevaProps.getProperty("csvFile","veeva.csv");
		 Globals.userName = loadVeevaProps.getProperty("userName","default");
		 Globals.password = loadVeevaProps.getProperty("password","default");
		 
		 Globals.url = loadVeevaProps.getProperty("url","default");
		 
		 Globals.documents = loadVeevaProps.getProperty("documents","SELECT id, name__v, type__v FROM documents");
		 
		 //System.out.println(Globals.userName);
	 
	}
	 
	 catch (Exception e)
	 {
		 System.out.println(e);
		 throw e;
	 }
	return "Loaded properties";
	}

	
/**
 * 
 * @param vql
 * @return
 * @throws Exception 
 */
	
	@Command (description="Run a query on the API eg select... ")
	public String query( String... vql) throws Exception
	{
		
		StringBuilder builder = new StringBuilder();
		for(String s : vql) {
			
		    builder.append(s);
		    builder.append(" ");
		}
		
		String _vql =  URLEncoder.encode(builder.toString(),java.nio.charset.StandardCharsets.UTF_8.toString());
		String safeURL = "/query?q=" + _vql;
		
		if ( verboseMode)
			System.out.println(_vql);
		
		String result = postCommand(safeURL);
		
		if ( verboseMode)
			System.out.println("JSON Result: " + result);
		
		JSONObject newJSON = new JSONObject(result);
		
		String csv = CDL.toString(newJSON.getJSONArray("data"));
		
		if ( verboseMode)
			System.out.println("JSON csv: " + csv);
		
		
		JsonFlattener parser = new JsonFlattener();
        CSVWriter writer = new CSVWriter();

        List<Map<String, String>> flatJson;
		try {
			flatJson = parser.parseJson(newJSON.getJSONArray("data").toString());
			writer.writeOut(flatJson);
			
			//TODO - Uopdate in settings file 
			writer.writeAsCSV(flatJson, Globals.csvFile);
		
		} 
		
		 catch (java.io.FileNotFoundException e) {
				 System.out.println("File cannot be opened");
		}
			
		 catch (Exception e ) {
			 e.printStackTrace();
			 throw e;
		 }
		
		return "Veeva query finished";	
		
	
        
		
		
		
		
	}
	
	
	/**
	 * List a pre sorted list of all documents
	 * @param vql
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Command (description="List using the documents API")
	public String list() throws UnsupportedEncodingException
	{
		
		
		String safeURL = "/objects/documents/?" + URLEncoder.encode("sort=name__v desc",java.nio.charset.StandardCharsets.UTF_8.toString());
		
		postCommand(safeURL);
		
		
		
		return "Documents query finished";
		
		
	}
	
	/**
	 * Post a command with http and include the token 
	 * returns a json string... not sure if this going be a problem for lots of things  
	 */
	
	public String postCommand (String command)
	{
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		BufferedReader br = null;
		final String USER_AGENT = "Mozilla/5.0";
				
		try {

			// build the auth string and just go for it.
			String postString = vv.VeevaVaults.get(0).url +"/";
			postString += vv.getAPIVersion() +"/";
			postString+=command;
			
			System.out.println(postString);
					
			HttpGet httpPost = new HttpGet(postString);
			//post.setHeader("User-Agent", USER_AGENT);
			httpPost.setHeader("Authorization", vv.getToken());

			// This is not required as you put them on the command line... but... would be nice :)
			//List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			//nvps.add(new BasicNameValuePair("Authorization", vv.getToken()));
			
			//httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			
			CloseableHttpResponse response = httpclient.execute(httpPost);

			if ( verboseMode)
				System.out.println(response.getStatusLine());

			BufferedReader brhttp = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output = null;
			String result = "";

			
			while ((output = brhttp.readLine()) != null) {

				result += output + "\n";
			}
			if ( verboseMode){
				System.out.println("Output from Server .... \n");
				System.out.println(result);
				System.out.println("<------------------------->\n");
			}
			
			JSONObject nodeRoot = new JSONObject(result);
			// This seems to be needed - I dont know why
			JSONObject responseCode = new JSONObject(nodeRoot.toString());
			
			//String csv = CDL.toString(responseCode.getJSONArray("data"));
			
			
			// TODO CDL
			System.out.println(result);
			//System.out.println(csv);
			
			// Close the HTTP 
			response.close();
			
			return responseCode.toString();

		}
		catch(Exception e)
		{
			// TODO encode a json error
			System.out.println(e);
			return "Error in documents: " + e.getMessage();
		}

		
	}

	

}



