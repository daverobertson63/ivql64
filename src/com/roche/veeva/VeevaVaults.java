package com.roche.veeva;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;




public class VeevaVaults {

	String token="";	// The token to use 
	String APIVersion = "v12.0";	//Default API to use
	List<VeevaVault> VeevaVaults = new ArrayList<VeevaVault>();
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAPIVersion() {
		return APIVersion;
	}
	public void setAPIVersion(String aPIVersion) {
		APIVersion = aPIVersion;
	}

	
	public List<VeevaVault> getVeevaVaults() {
		return VeevaVaults;
	}
	
	public void setVeevaVaults(List<VeevaVault> veevaVaults) {
		VeevaVaults = veevaVaults;
	}
	public void addVault(String name, String url, int id) throws VeevaException
	{
		
		VeevaVault vault = new VeevaVault();
		
		vault.id = id;
		vault.url=url;
		vault.name = name;
		
		this.VeevaVaults.add(vault);
		return;
		
	}
	
	/** 
	 * Set the active vault - by number - then we can reference that when passing in the context
	 */
	
	public void setActiveVault(int vNumber) throws VeevaException
	{
		
		if (vNumber < 0 ||  vNumber > this.VeevaVaults.size())
		{
			throw  new VeevaException("VeevaVaults[Out of bounds index for active vault]");
		}
		
	}
	
	  public VeevaException syntaxError(String message) {
	        return new VeevaException(message + this.toString());
	    }

}