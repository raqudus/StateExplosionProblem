//===============================================================================================
// Name        : Main.java
// Author      : Abdul Qudus, Philip Frick & Johan Sjšberg
// Version     : 1.0
// Copyright   : Ericsson ///
// Description : State Explosion Problem - Java
//===============================================================================================
package ericsson.transitions;

import java.util.*;
import java.io.*;
import java.awt.*;

public class Main {

	public static void main (String[] arg) throws FileNotFoundException, Exception {

		// Opens the inputfile and saves the data in variables ==================================

		if(arg.length < 2)
		{
			System.out.println("Syntex Error: Run with input parameters, 'input file' 'output file'");
			return;
		}
		File fileInput = new File(arg[0]);
		ArrayList<String> nameOfState = new ArrayList<String>();
		ArrayList<ArrayList<String>> propertiesOfState = new ArrayList<ArrayList<String>>();

		if(fileInput.exists()) {
		
			Scanner inputfile = new Scanner(new File(arg[0]));

			StateNamesAndRabs input = new StateNamesAndRabs();
			input.collectData(inputfile);

			nameOfState = input.getStateNames();
			propertiesOfState = input.getStateRabs();

			inputfile.close();
		}
		else {
			System.out.println("Inputfile not found");
		}

		// Creates an output file ===============================================================

		File fileOutput = new File(arg[1]); 

		if(fileOutput.canWrite()) {
			PrintWriter outputfile = new PrintWriter(new File(arg[1]));

			// Compares the arrays of properties and sorts out the possible transitions =====
			TransitionAnalysis.listOfTransitions(nameOfState, propertiesOfState, outputfile);
			outputfile.close();
		}
		else {
			System.out.println("Can not write to file");
		}

		// Opens the output file ================================================================

		Desktop file = Desktop.getDesktop();
		file.open(new File(arg[1]));
	}
}