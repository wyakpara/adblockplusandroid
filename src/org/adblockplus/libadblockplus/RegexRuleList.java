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
		
		// Log.d(TAG, "OPENING FILE");

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
		
		try
		{
			reader = new JsonReader(new InputStreamReader(theRules, "UTF-8"));
		}
		catch(Exception e)
		{
		      Log.d(TAG, "FILE CANNOT BE READ");
		}
		
		try
		{
			reader.beginObject();
		}
		catch(IOException i)
		{
			i.printStackTrace();
			System.err.println("An error occurred while parsing the policy language");
		}
		AddRules(reader);
	}
	
	// Parse file until we find a name we want
	// Parse that object, and grab everything from it
	// if we find a nested object, throw error 'cause that shouldn't happen
	public void AddRules(JsonReader reader) {
		Pregex theRule = new Pregex();
		int numRules = 0;
		try
		{
			// reader.setLenient(true);
			JsonToken thePeek;
	        String name = null;
			boolean ruleInProgress = false, ruleObjectStarted = false;
			// while(reader.hasNext() || reader.peek() != JsonToken.END_DOCUMENT) {
			while(reader.peek() != JsonToken.END_DOCUMENT) {
				Log.d(TAG, "Number of Rules: " + numRules);
				
				// if(numRules == 401)
				// 	break;
				
				thePeek = reader.peek();
				Log.d(TAG, "Peek: " + thePeek);
				if(thePeek == JsonToken.NAME)
				{
					name = reader.nextName();
				}
				else if(thePeek == JsonToken.NUMBER)
				{
					reader.nextInt();
				}
				else if(thePeek == JsonToken.BOOLEAN)
				{
					reader.nextBoolean();
				}
				else if(thePeek == JsonToken.BEGIN_ARRAY)
				{
					// Log.d(TAG, "Beginning Array");
					reader.beginArray();
				}
				else if(thePeek == JsonToken.END_ARRAY)
				{
					// Log.d(TAG, "Ending Array");
					reader.endArray();
				}
				else if(thePeek == JsonToken.BEGIN_OBJECT)
				{
					// Log.d(TAG, "Beginning Object");
					reader.beginObject();
					if(ruleInProgress && ruleObjectStarted)
						// Throw an error!
						Log.d(TAG, "Error!");
					if(ruleInProgress && !ruleObjectStarted)
						ruleObjectStarted = true;
				}
				else if(thePeek == JsonToken.END_OBJECT)
				{
					// Log.d(TAG, "Ending Object");
					reader.endObject();
					if(ruleObjectStarted)
					{
						// Log.d(TAG, "Adding Rule");
						ruleObjectStarted = false;
						ruleInProgress = false;
						addRule(theRule.makeRegex());
						theRule.clear();
					}
				}
				// For when we find something else.
				else if (reader.peek() == JsonToken.STRING && !(name.equals("pattern") || name.equals("allowDomain")))
				{
					// Log.d(TAG, "Found whatever");
					// Log.d(TAG, reader.nextString());
					reader.nextString();
				}
				
				if(name.equals("script") || name.equals("image") || name.equals("iframe") || name.equals("object") || name.equals("subobject"))
				// if(name.equals("script") || name.equals("iframe") || name.equals("object") || name.equals("subobject"))
				{
					// Grab the regex rule from this particular object somehow
					ruleInProgress = true;
				}
				
				if(name.equals("domains"))
				{
					theRule.addDomains(reader.nextString());
				}

				if(name.equals("pattern") && ruleInProgress)
				{
					
					String pattern = reader.nextString();
					// Log.d(TAG, "Found Pattern " + pattern);
					// theRule.addPattern(reader.nextString());
					theRule.addPattern(pattern);
					
				}
				else if(name.equals("allowDomain") && ruleInProgress)
				{
					// Log.d(TAG, "Found allowed domains");
					// foundDomains = true;
					// domains = reader.nextString();
					theRule.addAllowedDomains(reader.nextString());
				}
				

		        name = "";
				
				if(!reader.hasNext() && (ruleObjectStarted || ruleInProgress))
				{
					// Log.d(TAG, "Adding last Rule");
					ruleObjectStarted = false;
					ruleInProgress = false;
					addRule(theRule.makeRegex());
					numRules++;
					theRule.clear();
				}
			}
		}
		catch(IOException i)
		{
			Log.d(TAG, "Exception occurred during policy parse");
			i.printStackTrace();
			Log.d(TAG, "Error reading file");
		}
	}
	
	public void addRule(RegexRule ruleToAdd){			
		//if array has no room
		if(rules.length == numRules){
			rules = Arrays.copyOf(rules, (2*rules.length));
		}
		rules[numRules++] = ruleToAdd;
	}
	
	private String[] parseDomains(String domains) {
		String[] domainArray = new String[10];
		int numDomains = 0;
		int start = 0, index;
		boolean beginning = true;
		
		StringBuilder parser = new StringBuilder(domains);
		for(index = 0; index < parser.length(); index++)
		{
			// Double array size if necessary.
			if(numDomains >= domainArray.length)
			{
				domainArray = Arrays.copyOf(domainArray, domainArray.length * 2);
			}
			// If this isn't the first domain
			if(parser.charAt(index) == '|' && !beginning)
			{
				domainArray[numDomains++] = parser.substring(start, index);
				start = index + 1;
				Log.d(TAG, "domain: " + domainArray[numDomains - 1]);
			}
			// If this is the first domain
			if(parser.charAt(index) == '|' && beginning)
			{
				start = index + 1;
				domainArray[numDomains++] = parser.substring(0,index);
				Log.d(TAG, "domain: " + domainArray[numDomains - 1]);
			}
			// If this is the last domain
			if(index == parser.length() - 1 && parser.charAt(index) != '|')
			{
				domainArray[numDomains++] = parser.substring(start,index + 1);
				Log.d(TAG, "domain: " + domainArray[numDomains - 1]);
			}
		}
		domainArray = Arrays.copyOf(domainArray, numDomains);
		return domainArray;
	}
	
	private String[] parseAllowedDomains(String domains){
		String[] domainArray = new String[10];
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
				// Log.d(TAG, "Start of domain");
				start = index + 1;
			}
			if(parser.charAt(index) == ')')
			{
				// Log.d(TAG, "End of domain");
				end = index;
				domainArray[numDomains++] = parser.substring(start,end);
				// Log.d(TAG, "domain: " + domainArray[numDomains - 1]);
			}
		}
		domainArray = Arrays.copyOf(domainArray, numDomains);
		return domainArray;
	}
	
	private class Pregex {
		private String pattern;
		private String[] allowedDomains;
		private String[] domains;
		
		private Pregex() {}
		
		private void addPattern(String newPattern) {
			pattern = newPattern;
		}
		
		private void addAllowedDomains(String newDomains) {
			allowedDomains = parseAllowedDomains(newDomains);
		}
		
		private void addDomains(String newDomains) {
			domains = parseDomains(newDomains);
		}
		
		// We don't need a non-null allowedDomains array.
		private boolean isReady() {
			if(pattern != null)
				return true;
			else
				return false;
		}
		
		private boolean hasAllowedDomains() {
			if(allowedDomains == null)
				return false;
			else
				return true;
		}
		
		private void clear() {
			pattern = null;
			allowedDomains = null;
		}
		
		private RegexRule makeRegex() {
			if(this.hasAllowedDomains())
				return new RegexRule(Pattern.compile(pattern), allowedDomains, domains);
			else
				return new RegexRule(Pattern.compile(pattern), domains);
		}
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
				// Log.d(TAG, "CALLING MATCHES!?");
				
				if(temp.matches(request)){		//use matcher.find() to simplify regex construction
					return temp;
				}

			}

			return null;
		}
	}
}	
