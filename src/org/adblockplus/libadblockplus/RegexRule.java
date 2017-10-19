package org.adblockplus.libadblockplus;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

public class RegexRule implements Comparable<RegexRule>{

	// private Pattern[] exceptions;
	private static int numExempted = 0;
	private String[] exceptions;
	private int numExceptions = 0;
	private Pattern regex;
	private String[] domains;
	private long numChecked = 0;

    public static final String EXEMPT = "Exempted";

	private final String TAG = "RegexRule";

	public RegexRule(Pattern rule, String[] theDomains){	//basic constructor for rules that don't have exceptions
		numExceptions = 0;
		exceptions = null;
		regex = rule;
		domains = theDomains;
	}


	public RegexRule(Pattern rule, String[] excepStrings, String[] theDomains){

		this(rule, theDomains);	//call the basic constructor
		exceptions = excepStrings;
		numExceptions = excepStrings.length;

		// // Sub-regex to potentially match http://, https://
		//         String http = "(?:https?:\\/\\/)?";
		//         // Sub-regex to potentially match anything not containing a /, ending with @ or .
		//         String subdomains = "(?:[^\\/]*[@\\.])?";
		//         // Sub-regex to potentially match the path following the domain (starts with /)
		//         String path = "(?:\\/.*)?";
		//
		//         System.out.println(rule.toString());
		//         exceptions = new Pattern[excepStrings.length];

		// if(excepStrings != null && excepStrings.length != 0){	//if there is a valid array of exceptions, override values from basic constructor
		// 	numExceptions = excepStrings.length;	//assume array of exceptions is well-formed and has no extra space, so the size of the array is the number of exceptions
		//
		// 	for(int i = 0; i < excepStrings.length; i++){	//compile each domain exception string to a pattern and store in exceptions array
		// 		exceptions[i] = Pattern.compile("^" + http + subdomains + addEscapeCharacters(excepStrings[i]) + path + "$");	//compile pattern with properly
		// 		//escaped exception domain string
		// 		System.out.println(exceptions[i].toString());
		// 		numExceptions++;
		// 	}
		//
		// }

	}

	// public String addEscapeCharacters(String exception){	//function to properly escape the periods in a domain string to make the string regex-conducive
	// 	String[] pieces = exception.split("\\.");//split the string on the periods
	// 	String s = "";//create blank string
	//
	// 	for(int i = 0; i < pieces.length - 1; i++){//loop through first n-1 pieces and escape the periods that were between them
	// 		pieces[i] = pieces[i] + "\\.";
	// 		s += pieces[i];
	// 	}
	// 	if(pieces.length > 0){	//add the last piece back onto the string
	// 		s += pieces[pieces.length-1];
	// 	}
	// 	return s;
	// }

	public int compareTo(RegexRule compare){	//returns -1,0, or 1 if compare is greater than, equal to, or less than this(respectively). Throws nullpointer exception if compare is null

		if(this.getNumExceptions() > compare.getNumExceptions()){
			return 1;
		}
		else if(this.getNumExceptions() == compare.getNumExceptions()){
			return 0;
		}
		else{
			return -1;
		}

	}

	public int getNumExceptions(){
		return numExceptions;
	}

	// public Pattern[] getExceptions(){
	// 	return exceptions;
	// }
	
	public String[] getExceptions() {
		return exceptions;
	}

	public boolean hasExceptions(){
		return (numExceptions > 0);		
	}
	
	public boolean domainMatch(String requestUrl) {
		URI uri;
		String requestDomain = "";
		try{
			uri = new URI(requestUrl);
			requestDomain = uri.getHost();
		} catch(URISyntaxException e) {
			Log.d(TAG, "Invalid Domain.");
		}
		
		// Log.d("RequestDomain", "Request URL: " + requestUrl);
		// Log.d("RequestDomain", "Request domain: " + requestDomain);
		

			for(String domain: domains) {

			if(domain.equals("*") || requestDomain.equals(domain))
			{
				// if(domains[i].equals(requestDomain.substring(requestDomain.length() - domains[i].length()))
				// Log.d("RequestDomain", "Blocked with " + domains[i]);
				Log.d("RequestDomain", "Blocked with " + domain);
				return true;
			}
			// We cannot just check that the request domain ends with the domain in the rule; we have to 
			// make sure we won't overstep bounds in doing so.

			else if(requestDomain.length() > domain.length() && (requestDomain.endsWith(domain) && requestDomain.charAt(requestDomain.length() - domain.length() - 1) == '.'))
			{
				// Log.d("RequestDomain", "Blocked with " + domains[i]);
				Log.d("RequestDomain", "Blocked with " + domain);
				return true;
			}
		}
		return false;
	}
	
	public boolean exceptionMatch(String referrer) {
		URI uri;
		String referDomain = "";
		try{
			uri = new URI(referrer);
			referDomain = uri.getHost();
		} catch(URISyntaxException e) {
			Log.d(TAG, "Invalid Domain.");
		}
		
		// Log.d("RequestDomain", "Request URL: " + requestUrl);
		// Log.d("RequestDomain", "Request domain: " + requestDomain);
		
		

			for(String exception: exceptions) {

				if(exception.equals("*") || referDomain.equals(exception))
				{
					// if(domains[i].equals(requestDomain.substring(requestDomain.length() - domains[i].length()))
					// Log.d("RequestDomain", "Blocked with " + domains[i]);
					numExempted++;
					Log.d(EXEMPT, "Number of Exceptions seen: " + numExempted);
					Log.d(EXEMPT, "Excepted: " + referrer);
					Log.d(EXEMPT, "Exception: " + exception);
					return true;
				}
				// We cannot just check that the request domain ends with the domain in the rule; we have to 
				// make sure we won't overstep bounds in doing so.

				else if(referDomain.length() > exception.length() && (referDomain.endsWith(exception) && referDomain.charAt(referDomain.length() - exception.length() - 1) == '.'))
				{
					// Log.d("RequestDomain", "Blocked with " + domains[i]);
					numExempted++;
					Log.d(EXEMPT, "Number of Exceptions seen: " + numExempted);
					Log.d(EXEMPT, "Excepted: " + referrer);
					Log.d(EXEMPT, "Exception: " + exception);
					return true;
				}
		}
		return false;
	}

	public boolean matches(String request){	//returns true if given string matches regex, false otherwise
		if(domainMatch(request))
		{
			if(regex != null)
			{
				Matcher m = regex.matcher(request);
			 	return m.find();
			}
			else
				return true;
		}
		else
			return false;
	}
	
	public String regexToString() {
		return regex.toString();
	}

}