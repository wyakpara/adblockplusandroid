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
import android.util.Log;
import android.util.JsonReader;

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
        JsonReader reader = null;
        InputStream is;
        try{
        	//is = new FileInputStream(new File("apirules.json"));
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


	    //JSONObject obj = new JSONObject(jsonString);
	    //JSONArray jsonRules = obj.optJSONArray("rules");
	    //obj = jsonRules.getJSONObject(2);		//get script object
	   	//String regexString = "(?=" + http + subdomains + obj.optString("domains").toString() + path + ")" + obj.optString("pattern").toString();*/
	   	
	   	String regexString = "^" + http + subdomains + "google\\.com" + path + "(?=" + "foresee-(trigger|alive|analytics|surveydef|dhtml-popup).*\\.js" + ")";
	   	addRule(new RegexRule(Pattern.compile(regexString), new String[] {"(citibank.com)|(espn.go.com)"}));
		///TODO: add list parsing here, read in rules and use addRule method to add them to rules array, XML or JSON from android/java library, sort by exceptions after reading all rules
		
	}
	public void addRule(RegexRule ruleToAdd){			//add them in unsorted order then sort at the end of the constructor??
		if(rules.length == numRules){					//if array has no room
			rules = Arrays.copyOf(rules, (2*rules.length));
		}
		rules[numRules++] = ruleToAdd;
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
