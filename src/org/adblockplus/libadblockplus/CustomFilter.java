
package org.adblockplus.libadblockplus;

import android.util.Log;
import java.util.regex.Pattern;
import android.content.Context;

public class CustomFilter{

	public RegexRuleList rules;		//collection of regex rules
	public RegexRule matchedRule = null; //an object representing the individual regex rule that the requestUrl matches
	
	public CustomFilter(Context ABPContext){
		rules = new RegexRuleList();
		rules.parseRules(ABPContext);
	}
		
	public boolean block(String requestUrl, String referrer){ 
		int numRequests;
		int numBlocks;
		
		boolean blocked = false;
		RuleIteratorInterface iter = rules.iterator(requestUrl);
		matchedRule = iter.getNextMatch(); //call to an iterator that returns the next rule that matches the request, returns null if no more matches are found
		//Log.d("CustomFilter", "CustomFilter block function called URL: " + requestUrl + " referrer : " + referrer + "\n");

		if(referrer == null){
			return false;	//if request has no referrer, never block it
		}

		while(!blocked && matchedRule != null){
			//Log.d("CustomFilter", "in while loop\n");	//debug statement
			if(!isExempted(matchedRule, referrer)){
				blocked = true;
			}
			
			matchedRule = iter.getNextMatch();//if we have not returned by this point then get the next matched rule until there are no more. 
		
		}
		//Log.d("CustomFilter", "final decision: " + blocked + "\n");
		return blocked;	//when matchedRule == null and the loop finishes execution this means there are no more matches to this rule, so false is returned
			
	}

	public boolean isExempted(RegexRule rule, String referrer){		//return false if this referrer is not exempt, true if an exception is triggered and the referrer is exempt
		Pattern[] exceptions = rule.getExceptions();
		boolean result = false;

		if(rule.hasExceptions()){
			for(int i = 0; i < exceptions.length; i++){
				//Log.d("CustomFilter", "Pattern " + exceptions[i].toString() + " referrer " + referrer);
				if(exceptions[i].matcher(referrer).matches()){	//make a matcher on the pattern then call the matchers 'matches()' method
					//TODO? Add logging here to see which exception was triggered?
					//Log.d("CustomFilter", "THIS REFERRER IS AN EXCEPTION TO ITS RULE!");
					result = true;
					break;
				}
			}
		}	
		return result;	
	}

}
