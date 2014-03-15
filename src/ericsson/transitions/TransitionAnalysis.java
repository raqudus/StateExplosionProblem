//===============================================================================================
// Name        : TransitionAnalysis.java
// Author      : Abdul Qudus, Philip Frick & Johan Sjšberg
// Version     : 1.0
// Copyright   : Ericsson ///
// Description : State Explosion Problem - Java
//===============================================================================================

package ericsson.transitions;
import java.io.PrintWriter;
import java.util.*;

public class TransitionAnalysis {

	// Method to print a trigger in the correct way =============================================

	/**
	 * This method prints a trigger in a proper way.
	 * @param rab
	 * @return A String
	 */

	public static String fixOutput(String rab) {

		if(rab.contains("Idle")) {
			return rab;
		}
		
		String text = rab.substring(0, rab.indexOf("(")).toLowerCase();
		String rate = rab.substring(rab.indexOf("("), rab.indexOf(")")+1);
		int i = 0;

		if(text.contains("conversational cs speech")) 			{i = 1;} 
		else if(text.contains("conversational cs unknown"))		{i = 2;}
		else if(text.contains("streaming cs unknown")) 			{i = 3;}
		else if(text.contains("interactive ps")) 				{i = 4;}
		else if(text.contains("streaming ps")) 					{i = 5;}

		switch(i) {
		case 0 : text = rab; break;
		case 1 : text = "CONV_CS_SPEECH" 	+ rate; break;
		case 2 : text = "CONV_CS_UNK" 		+ rate; break;
		case 3 : text = "STR_CS_UNK" 		+ rate; break;
		case 4 : text = "INT_PS" 			+ rate; break;
		case 5 : text = "STR_PS"			+ rate; break;
		default : System.out.println("Somthing wrong with the method \"fixOutput\""); break;
		}

		return text;
	}

	// Release and Establish Methods ============================================================

	/**
	 * This method compares two state and returns the RAB difference.
	 * @param firstState
	 * @param secondState
	 * @return An ArrayList containing String elements
	 */
	
	public static ArrayList<String> findPossibleTransitions(ArrayList<String> firstState, ArrayList<String> secondState) {

		ArrayList<String> transitions = new ArrayList<String>();

		if(firstState.size() > secondState.size()) {
			transitions.addAll(firstState);
			for(int i = 0; i < secondState.size(); i++) {
				if(transitions.indexOf(secondState.get(i)) >= 0) {
					transitions.remove(transitions.indexOf(secondState.get(i)));
				}
			}
		}
		else {
			transitions.addAll(secondState);
			for(int i = 0; i < firstState.size(); i++) {
				if(transitions.indexOf(firstState.get(i)) >= 0) {
					transitions.remove(transitions.indexOf(firstState.get(i)));
				}
			}
		}

		return transitions;
	}

	/**
	 * This method returns one of the two possible actions, Release or establish.
	 * @param firstState
	 * @param secondState
	 * @return A String
	 */
	
	public static String establishOrRelease(ArrayList<String> firstState, ArrayList<String> secondState) {

		if(firstState.size() > secondState.size()) {
			return "RAB_REL";
		}
		else {
			return "RAB_EST";
		}
	}

	// Channel switching methods ================================================================
	
	/**
	 * This method extracts the "Interactive PS" RAB rates.
	 * @param state
	 * @return An ArrayList containing String elements
	 */
	
	public static ArrayList<String> extractRabRate(ArrayList<String> state) {

		ArrayList<String> rabAndRate = new ArrayList<String>();
		ArrayList<String> rabRates = new ArrayList<String>();

		for(int i = 0; i < state.size(); i++) {
			if(state.get(i).contains("Interactive PS")) {
				for(String rab: state.get(i).split("\\(|\\)")) {
					rabAndRate.add(rab);
				}
				state.remove(i);
				for(String singleRate: rabAndRate.get(1).split("\\/")) {
					rabRates.add(singleRate);
				}
				break;
			}
		}
		return rabRates;
	}

	/**
	 * This method checks if the only difference between two states are the "Interactive PS" RAB rates.  
	 * @param firstState
	 * @param secondState
	 * @return An ArrayList containing ArrayLists with String elements
	 */
	
	public static ArrayList<ArrayList<String>> findPossibleRateTransition(ArrayList<String> firstState, ArrayList<String> secondState) {

		ArrayList<ArrayList<String>> noPossibleTransition = new ArrayList<ArrayList<String>>();

		ArrayList<String> firstStateCopy = new ArrayList<String>();
		ArrayList<String> secondStateCopy = new ArrayList<String>();
		firstStateCopy.addAll(firstState);
		secondStateCopy.addAll(secondState);

		ArrayList<ArrayList<String>> rabRates = new ArrayList<ArrayList<String>>();
		rabRates.add(extractRabRate(firstStateCopy));
		rabRates.add(extractRabRate(secondStateCopy));

		if (firstStateCopy.containsAll(secondStateCopy) && secondStateCopy.containsAll(firstStateCopy) && (rabRates.get(0).size() > 0 && rabRates.get(1).size() > 0)) {
			return rabRates;
		}
		else {
			return noPossibleTransition;
		}
	}

	/**
	 * This method checks if one rate is equal to  another.
	 * @param a
	 * @param b
	 * @param c
	 * @param expected
	 * @return A boolean with the value true or false
	 */
	
	public static boolean checkIfEqual(String a, String b, String c, String expected) {

		return (a.equals(expected) || b.equals(expected) || c.equals(expected));
	}

	/**
	 * This method returns the possible rate transition if there is one.
	 * @param rabRates
	 * @return A String
	 */
	
	public static String checkIfTransitionIsPossible (ArrayList<ArrayList<String>> rabRates) {

		String [] upLinkRates = {"","URA","RACH","0","8","16","64","128","384","EUL",""};
		String [] downLinkRates = {"","URA","FACH","0","8","16","64","128","384","HS",""};
		String rateTransition[] = {"", ""};

		if(rabRates.size() > 1) {
			for(int i = 1; i < upLinkRates.length - 1; i++) {

				if(upLinkRates[i].equals(rabRates.get(0).get(0))) {
					if(checkIfEqual(upLinkRates[i - 1], upLinkRates[i], upLinkRates[i + 1], rabRates.get(1).get(0))) {
						rateTransition[0] = rabRates.get(1).get(0); 
					}
					//URA (URA/URA) and RACH (RACH/FACH) can go to any other states in one transition
					if(i == 1 || i == 2) {
						rateTransition[0] = rabRates.get(1).get(0);
					}
					//All states can in turn switch to RACH (RACH/FACH) in one transition 
					if(rabRates.get(1).get(0).equals("RACH")) {
						rateTransition[0] = "RACH";
					}
					//All states can in turn switch to EUL (EUL/HS) in one transition 
					if(rabRates.get(1).get(0).equals("EUL")) {
						rateTransition[0] = rabRates.get(1).get(0);
						rateTransition[1] = rabRates.get(1).get(1);
					}
					//EUL (EUL/HS) can go to any other states in one transition
					if(upLinkRates[i].equals("EUL")) {
						rateTransition[0] = rabRates.get(1).get(0);
					}
				}

				if(downLinkRates[i].equals(rabRates.get(0).get(1))) {
					if(checkIfEqual(downLinkRates[i - 1], downLinkRates[i], downLinkRates[i + 1], rabRates.get(1).get(1))) {
						rateTransition[1] = rabRates.get(1).get(1); 
					}
					//URA (URA/URA) and FACH (RACH/FACH) can go to any other states in one transition
					if(i == 1 || i == 2) {
						rateTransition[1] = rabRates.get(1).get(1);
					}
					//All states can in turn switch to FACH (RACH/FACH) in one transition
					if(rabRates.get(1).get(1).equals("FACH")) {
						rateTransition[1] = "FACH";
					}
					//HS (EUL/HS) can go to any other states in one transition
					if(upLinkRates[i].equals("EUL")) {
						rateTransition[1] = rabRates.get(1).get(1);
					}
				}
			}
		}

		if(rateTransition[0].isEmpty() || rateTransition[1].isEmpty())
		{
			return "";
		}
		//Uplink rate URA can only be combined with downlink rate URA  
		if((rateTransition[0] == "URA" && rateTransition[1] != "URA") || (rateTransition[1] == "URA" && rateTransition[0] != "URA")) {
			return "";
		}
		//Uplink rate EUL can only be combined with downlink rate HS
		else if(rateTransition[0].equals("EUL") && !rateTransition[1].contains("HS")) {
			return "";
		}
		//Uplink rate RACH can only be combined with downlink rate FACH
		else if((rateTransition[0] == "RACH" && rateTransition[1] != "FACH") || (rateTransition[1] == "FACH" && rateTransition[0] != "RACH")) {
			return "";
		}
		else {
			return rateTransition[0] + "/" + rateTransition[1];
		}
	}

	// Types the possible transitions ===========================================================


	/**
	 * This method compares all the states with each other and prints out the possible transitions.
	 * @param nameOfState
	 * @param rabsOfState
	 * @param outputfile
	 * @return void
	 */
	
	public static void listOfTransitions (ArrayList<String> nameOfState, ArrayList<ArrayList<String>> rabsOfState, PrintWriter outputfile) {

		String rates = "";
		ArrayList<ArrayList<String>> rateTransition = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < nameOfState.size(); i++) {

			for(int j = 0; j < nameOfState.size(); j++) {

				//Checks the possible transitions from idle
				if(nameOfState.get(i).contains("UeRc:0") && !nameOfState.get(j).contains("UeRc:0") && rabsOfState.get(j).size() == 1) {

					outputfile.println(nameOfState.get(i) + ";" + nameOfState.get(j) + ";RAB_EST;" +  fixOutput(rabsOfState.get(j).get(0)));
				}
				//Checks the possible transitions to idle
				else if(nameOfState.get(j).contains("UeRc:0") && !nameOfState.get(i).contains("UeRc:0") && rabsOfState.get(i).size() == 1) {

					outputfile.println(nameOfState.get(i) + ";" + nameOfState.get(j) + ";RAB_REL;" + fixOutput(rabsOfState.get(i).get(0)));
				}
				
				//Check the possible Release or establish transitions 
				if(Math.abs(rabsOfState.get(i).size() - rabsOfState.get(j).size()) == 1) {

					ArrayList<String> transitions = findPossibleTransitions(rabsOfState.get(i), rabsOfState.get(j));

					if(transitions.size() == 1) {

						outputfile.println(nameOfState.get(i) + ";" + nameOfState.get(j) + ";" + establishOrRelease(rabsOfState.get(i), rabsOfState.get(j)) + ";" + fixOutput(transitions.get(0)));
					}
				}
				//Check the possible channel switch transitions 
				if(rabsOfState.get(i).size() == rabsOfState.get(j).size() && i != j) {

					rateTransition = findPossibleRateTransition(rabsOfState.get(i), rabsOfState.get(j));
					rates = checkIfTransitionIsPossible(rateTransition);

					if(rates.length() > 0 && rates.indexOf("null") < 0) {
						outputfile.println(nameOfState.get(i) + ";" + nameOfState.get(j) + ";CW_SW;PS_INT " + "(" + rates + ")");
						//outputfile.println(nameOfState.get(i) + ";" + nameOfState.get(j) + ";CW_SW; " +  fixOutput(rabsOfState.get(i).toString()) + " " + fixOutput(rabsOfState.get(j).toString()) );
					}
				}
			}
		}
	}
}