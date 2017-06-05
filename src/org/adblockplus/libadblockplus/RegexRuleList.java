package org.adblockplus.libadblockplus;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import android.content.Context;
import android.content.res.AssetManager;

import android.util.Log;
import android.util.JsonReader;
import android.util.JsonToken;

//import android.util.*;

public class RegexRuleList{

	RegexRule[] rules;
	int numRules = 0;
	private final String TAG = "RegexRule";
	private final int DEFAULT_SIZE = 100;

	public RegexRuleList(){

		rules = new RegexRule[DEFAULT_SIZE];

	}

	public RegexRuleList(int capacity){
		rules = new RegexRule[capacity];
	}

	public void parseRules(Context context){
		// Sub-regex to potentially match http://, https://
        String http = "(?:https?:\\/\\/)?";
        // Sub-regex to potentially match anything not containing a /, ending with @ or .
        String subdomains = "(?:[^\\/]*[@\\.])?";
        // Sub-regex to potentially match the path following the domain (starts with /)
        String path = "(?:\\/.*)?";
        String name = null;
		// String[] domains;
		String domains = "";
		String pattern = "";
        JsonReader reader = null;
		
		Log.d(TAG, "OPENING FILE");

      	final AssetManager assetManager = context.getResources().getAssets();
      	InputStream theRules = null;
      	try
      	{
        	theRules = assetManager.open("apirules/apirules.json");
      	}
      	catch (final IOException e)
      	{
        	Log.e(TAG, "Failed to get assets", e);
      	}
				
		if(theRules != null)
			Log.d(TAG, "File found");
		
		try
		{
			// BufferedReader reader = new BufferedReader(new InputStreamReader(theRules, "UTF-8"));
			reader = new JsonReader(new InputStreamReader(theRules, "UTF-8"));
			// StringBuilder sb = new StringBuilder;
		}
		catch(Exception e)
		{
		      Log.d(TAG, "FILE CANNOT BE READ");
		}
		
		try
		{

			Log.d(TAG, "Reader = null? " + (reader == null));
			reader.beginObject();
			Log.d(TAG, "Functioning properly");
		}
		catch(IOException i)
		{
			Log.d(TAG, "Exception occurred during policy parse");
			i.printStackTrace();
			System.err.println("An error occurred while parsing the policy language");
		}
		
		Log.d(TAG, "BEGINNING FILE DUMP");
		
		// Log.d(TAG, reader.toString());
		
		// try
// 		{
// 			JsonObject theObj = reader.readObject();
// 			reader.close();
// 			Log.d(TAG, "Name: " + theObj.getString());
// 		}
// 		catch(IOException i)
// 		{
// 			i.printStackTrace();
// 		}
		
		
		// String name;
		Log.d(TAG, "Attempting to parse file");
		try
		{
			JsonToken thePeek;
			boolean foundPattern = false, foundDomains = false;
			while(reader.hasNext()) {
				thePeek = reader.peek();
				Log.d(TAG, "Peek: " + thePeek);
				if(thePeek == JsonToken.NAME)
				{
					name = reader.nextName();
				}
				else if(thePeek == JsonToken.BEGIN_ARRAY)
				{
					Log.d(TAG, "Beginning Array");
					reader.beginArray();
				}
				else if(thePeek == JsonToken.END_ARRAY)
				{
					Log.d(TAG, "Ending Array");
					reader.endArray();
				}
				else if(thePeek == JsonToken.BEGIN_OBJECT)
				{
					Log.d(TAG, "Beginning Object");
					reader.beginObject();
				}
				else if(thePeek == JsonToken.END_OBJECT)
				{
					Log.d(TAG, "Ending Object");
					reader.endObject();
				}
// 				name = reader.nextName();
// 				Log.d(TAG, "Peek: " + reader.peek());
				if(name.equals("Version"))
				{
					Log.d(TAG, "Printing Version");
					Log.d(TAG, "" + reader.nextDouble());
				}
				else if(name.equals("pattern"))
				{
					Log.d(TAG, "Found Pattern");
					foundPattern = true;
					pattern = reader.nextString();
					// Log.d(TAG, reader.nextString());
					
				}
				else if(name.equals("allowDomain"))
				{
					Log.d(TAG, "Found allowed domains");
					foundDomains = true;
					domains = reader.nextString();
				}
				else if (reader.peek() == JsonToken.STRING)//(reader.peek() != JsonToken.BEGIN_ARRAY && reader.peek() != JsonToken.END_ARRAY && reader.peek() != JsonToken.BEGIN_OBJECT && reader.peek() != JsonToken.END_OBJECT)
				{
					// Log.d(TAG, "Printing whatever");
					// Log.d(TAG, reader.nextString());
					reader.nextString();
				}
				
				if(foundDomains && foundPattern)
					break;
			}
			Log.d(TAG, "Entering RegexRule for " + pattern);
			addRule(new RegexRule(Pattern.compile(pattern),parseAllowedDomains(domains)));
		}
		catch(IOException i)
		{
			Log.d(TAG, "Exception occurred during policy parse");
			i.printStackTrace();
			Log.d(TAG, "Error reading file");
		}
		
				//
		// boolean readernext = true;
		// while(readernext)
		// {
		// 	try
		// 	{
		// 		Log.d(TAG, "Checking hasNext");
		// 		readernext = reader.hasNext();
		// 	}
		// 	catch(IOException i)
		// 	{
		// 		Log.d(TAG, "Exception occurred during JSON dump");
		// 		i.printStackTrace();
		// 		System.err.println("Exception occurred during JSON dump");
		// 	}
		//
		// 	try
		// 	{
		// 		Log.d(TAG, "Outputting nextString");
		// 		Log.d(TAG, reader.nextString());
		// 	}
		// 	catch(IOException i)
		// 	{
		// 		Log.d(TAG, "Exception occurred during JSON dump");
		// 		i.printStackTrace();
		// 		System.err.println("Exception occurred during JSON dump");
		// 	}
		// }
		
		// Use Environment?
		// File rulesHome = Environment.getDataDirectory();
		// Use getExternalStorageDirectory();?
		// File theRules = new File(rulesHome,"apirules.json");
		/*
        InputStream is;
        try{
        	//is = new FileInputStream(new File("apirules.json")); // FIX ANDROID IO
        	is = new FileInputStream("apirules.json");
        	Log.d(TAG, "input stream = null?" + (is == null));
        	InputStreamReader isr = new InputStreamReader(is);
        	Log.d(TAG, "InputStreamReader = null?" + (isr == null));
			reader = new JsonReader(isr);
        }catch(Exception e){
        	Log.d(TAG, "FILE CANNOT BE READ");
        }

		try{

			Log.d(TAG, "Reader = null? " + (reader == null));
			reader.beginObject();
			Log.d(TAG, "Functioning properly");
		}catch(IOException i){
			Log.d(TAG, "Exception occurred during policy parse");
			i.printStackTrace();
			System.err.println("An error occurred while parsing the policy language");
		}			
			*/


	    //JSONObject obj = new JSONObject(jsonString);
	    //JSONArray jsonRules = obj.optJSONArray("rules");
	    //obj = jsonRules.getJSONObject(2);		//get script object
	   	//String regexString = "(?=" + http + subdomains + obj.optString("domains").toString() + path + ")" + obj.optString("pattern").toString();*/
	   	
	   	// String regexString = "^" + http + subdomains + "google\\.com" + path + "(?=" + "foresee-(trigger|alive|analytics|surveydef|dhtml-popup).*\\.js" + ")";
	   	// addRule(new RegexRule(Pattern.compile(regexString), new String[] {"(citibank.com)|(espn.go.com)"}));

		///TODO: add list parsing here, read in rules and use addRule method to add them to rules array, XML or JSON from android/java library, sort by exceptions after reading all rules
		
	}
	public void addRule(RegexRule ruleToAdd){			//add them in unsorted order then sort at the end of the constructor??
		if(rules.length == numRules){					//if array has no room
			rules = Arrays.copyOf(rules, (2*rules.length));
		}
		rules[numRules++] = ruleToAdd;
	}
	
	public String[] parseAllowedDomains(String domains){
		String[] domainArray = new String[50];
		int numDomains = 0;
		int start = 0, end = 0, index;
		
		StringBuilder parser = new StringBuilder(domains);
		for(index = 0; index < parser.length(); index++)
		{
			if(numDomains >= domainArray.length)
			{
				domainArray = Arrays.copyOf(domainArray, domainArray.length * 2);
			}
			if(parser.charAt(index) == '(')
			{
				Log.d(TAG, "Start of domain");
				start = index + 1;
			}
			if(parser.charAt(index) == ')')
			{
				Log.d(TAG, "End of domain");
				end = index;
				domainArray[numDomains++] = parser.substring(start,end);
				Log.d(TAG, "domain: " + domainArray[numDomains - 1]);
			}
		}
		domainArray = Arrays.copyOf(domainArray, numDomains);
		return domainArray;
	}
	
	public RuleIteratorInterface iterator(String request){
		return new RuleIterator(request);
	}

	private class RuleIterator implements RuleIteratorInterface{			//iterator over list of RegexRules
		private int nextIndex = 0;
		private String request;

		public RuleIterator(String requestToMatch){		//pass the request URL to the iterator, because each request needs its own iterator
			request = requestToMatch;
		}

		public boolean hasNext(){
			return (nextIndex < numRules);
		}

		public RegexRule next(){
			return rules[nextIndex++];	
		}

		public RegexRule getNextMatch(){
			RegexRule temp;

			if(rules == null || rules.length == 0){			//throw exception if rule list has not been instantiated or is empty
				throw new IllegalStateException();
			}

			while(hasNext()){	

				temp = next();

				if(temp.matches(request)){		//use matcher.find() to simplify regex construction
					return temp;
				}

			}

			return null;
		}
	}
}	
