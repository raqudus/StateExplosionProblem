//===============================================================================================
// Name        : StateNamesAndRabs.java
// Author      : Abdul Qudus, Philip Frick & Johan Sjšberg
// Version     : 1.0
// Copyright   : Ericsson ///
// Description : State Explosion Problem - Java
//===============================================================================================

package ericsson.transitions;

import java.util.*;

public class StateNamesAndRabs {
	
	private ArrayList<String> nameOfState = new ArrayList<String>();
	private ArrayList<ArrayList<String>> stateRabs = new ArrayList<ArrayList<String>>();
	
	// Methods used within this class ===========================================================
	
	/**
	 * This method creates a substring containing the first word of a line, which in this case is the state name.  
	 * @param singleLine
	 * @return A String 
	 */
	
	private static String extractNameOfState (String singleLine) {
		String nameOfState = singleLine.substring(0, singleLine.indexOf("#")).trim();
		return nameOfState;
	}
	
	// ==========================================================================================
	
	/**
	 * This method divides a states RAB combination into single RABs.
	 * @param singleLine
	 * @return An ArrayList containing String elements
	 */
	
	private ArrayList<String> splitRabs (String singleLine) {
		
		String stringOfRabs = singleLine.substring(singleLine.indexOf('[') + 1, singleLine.indexOf(']'));
		ArrayList<String> arrayOfRabs = new ArrayList<String>();

		if (stringOfRabs.contains("+")) {
			for (String rab: stringOfRabs.split(" \\+ ")) {
				arrayOfRabs.add(rab);
			}
		}
		else {
			arrayOfRabs.add(stringOfRabs); 
		}        
		
		return arrayOfRabs;
	}
	
	// Methods other classes will call on =======================================================
	
	/**
	 * This method collects data from a specified file and stores the information in variables.
	 * @param inputFile
	 * @return void
	 */
	
	public void collectData (Scanner inputFile) {
		
		while (inputFile.hasNextLine()) {
			String line = inputFile.nextLine(); 
			if(line.isEmpty() || line.indexOf("#") == -1)
				continue;
			
			nameOfState.add(extractNameOfState(line));
			stateRabs.add(splitRabs(line));
		} 
	}
	
	// ==========================================================================================
	
	/**
	 * This method returns the all state names.
	 * @return An ArrayList containing String elements
	 */
	
	public ArrayList<String> getStateNames() {
		return nameOfState;
	}
	
	// ==========================================================================================
	
	/**
	 * This method returns all the states RAB combinations. 
	 * @return An ArrayList containing ArrayLists with String elements
	 */
	
	public ArrayList<ArrayList<String>> getStateRabs() {
		return stateRabs;
	}
}