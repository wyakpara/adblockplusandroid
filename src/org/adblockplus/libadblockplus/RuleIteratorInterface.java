
package org.adblockplus.libadblockplus;

/**This interface is for an iterator that returns RegexRule's that match the given String requestToMatch
**/
public interface RuleIteratorInterface{

	/**
	*This method returns the next matching RegexRule
	*
	*
	*@param UrlToCheck: A url to be compared to the regular expression rules. 
	*@throws IllegalStateException if the data structure holding the regex rules is null/has not been instantiated yet/
	*@return RegexRule: The regular expression rule that matched the given URL, Null if there were no matches. 
	**/
	public RegexRule getNextMatch();
	
	public int getCurrentIndex();
}	