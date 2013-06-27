package se.kayarr.ircbot.shared;

public class Strings {
	//Borrowed from KRAIC...
	/**
	 * Performs a check to see if {@code stringToCheck} matches {@code matchString},
	 * which can contain wildcards. (*)
	 * 
	 * @param matchString The string (optionally with wildcards) to match against
	 * @param stringToCheck The string to match
	 * @return Whether or not the string to check matches the match-string
	 */
	public static boolean matches(String matchString, String stringToCheck) {
		String[] tokens = matchString.split("\\*", -1);
		
		if(tokens.length == 0)
			return true; //The entire string was just a wildcard, which matches EVERYTHING
		
		if(tokens.length == 1)
			return tokens[0].equals(stringToCheck); //There was no wildcard at all, so let's just check if both strings are equal
		
		if(tokens[0].length() > 0 && !stringToCheck.startsWith(tokens[0]))
			return false; //The matchstring started with a word, and if the string to check doesn't start with that...
		
		if(tokens[tokens.length-1].length() > 0 && !stringToCheck.endsWith(tokens[tokens.length-1]))
			return false; //The matchstring ended with a word, and if the string to check doesn't end with that...
		
		for(String token : tokens) { //Do actual matching
			int pos = stringToCheck.indexOf(token);
			
			if(pos == -1) return false;
			
			stringToCheck = stringToCheck.substring(pos + token.length());
		}
		
		return true;
	}
}
