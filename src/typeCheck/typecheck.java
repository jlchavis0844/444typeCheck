package typeCheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class typecheck {
	// private static String mystring = "";
	private static int openbracecount = 0;
	private static int closebracecount = 0;
	private static int maincount = 0;
	private static int functioncount = 0;
	private static int returncount = 0;

	// hash map that store the function info
	// key is function name, value is an arraylist
	// 1st item in arraylist is function return type
	// 2nd item is function unique id
	// 3rd is function param count
	// 4th param return type, 5th param name
	// 6th param return type, 7th param name
	// calculator add/minus/mul/ div functions only have 2 param.
	private static HashMap<String, ArrayList<String>> mFunctionIndex;

	// hashmap that store variable info
	// key is variable name, value is the return type
	private static HashMap<String, String> mVariableIndex;

	// hash map that store return info
	// key is the number of return start at 1 increase by 1
	// for every return we encounter, value is the variable
	// name that we are returning, use it to access the
	// variable hashmap to obtain the type of the variable.
	// the key (number) match the function unique ID,
	// function unique ID = 1 mean it is linked to return
	// key = 1
	private static HashMap<Integer, String> mReturnIndex;
	
	// hashmap that store pointer info
	// key is pointer name, value is the pointer type
	private static HashMap<String, String> mPointerIndex;

	/*
	 * public typecheck(String input){ mystring = input; }
	 */

	public typecheck() {
		mFunctionIndex = new HashMap<String, ArrayList<String>>();
		mVariableIndex = new HashMap<String, String>();
		mReturnIndex = new HashMap<Integer, String>();
	}

	// remove all comma, semi colon
	public static String RemoveAllCommaNSemicolon(String in) {
		in = in.replace(",", " ");
		in = in.replace(";", "");
		in = in.trim();
		return in;
	}

	public static void check(String input) {
		PatternMatching mycheck = new PatternMatching();
		input = RemoveAllCommaNSemicolon(input);
		// keep track of declared variable and function
		// so when we get an expression of the form
		// assignment we can check to see if
		// the variable exist , and does the function
		// return type match the variable type.

		// check number of open and close curly brace
		if (input.contains("{")) {
			openbracecount++;
			input = input.replace("{", "");
			input = input.trim();
		}
		if (input.contains("}")) {
			closebracecount++;
			input = input.replace("}", "");
			input = input.trim();
		}
		// split the string into array of string
		String[] arr = input.split("\\s+");
		// TODO: Change to input.split("((?<=[^\\w']+)|(?=[^\\w']+))");
		// check to make sure we only have 1 main
		// and main pass the pattern int main()
		if (input.contains("main()")) {
			maincheck(input, mycheck);
		}
		// check function pattern
		else if (PatternMatching.visitfunction(input)) {
			// check to see if the function name (procedure ID)
			// already appear in the hashmap
			// if it does error 3 no duplicate procedure ID allowed
			// get key from mFunctionIndex and compare to arr[1](the func name)
			if (isFunctionNameDuplicate(arr[1])) {
				// we found duplicate error 3
				System.out.println("error 3");
			}
			// no duplicated name found
			// populate the function hashmap with the function information
			else {
				functionPopulate(input);
				System.out.println("function header passed");
			}
		}
		// check variable declaration pattern
		else if (PatternMatching.visitVariableDeclaration(input)) {
			// check for variable duplicate
			if (isVariableNameDuplicate(arr[1])) {
				// we found duplicate error 3
				System.out.println("error 4 variable name duplicate");
			} else {
				// get the variable name and data type
				// save them in the variable hashmap
				// arr[1] var name, arr[0] var type
				mVariableIndex.put(arr[1], arr[0]);
				System.out.println("variable declaration passed");
			}
		}
		// check function call
		else if (PatternMatching.visitFunctionCall(input)) {
			boolean pass = true;
			String s = input;
			int j = 4;
			s = RemoveAllCommaNSemicolon(s);
			s = s.replaceAll("(", " ");
			s = s.replaceAll(")", "");
			String sarr[] = s.split(" ");
			// 1st item in array is function name
			// go into the function hashmap to check for it existence
			if (!isFunctionNameDuplicate(sarr[0])) {
				// no duplicate = no found = function is not declared before it
				// is call
				System.out.println("error 5 function name not found in hashmap");
				pass = false;
			}
			// check for number or argument and param
			// access the function hashmap get the value (arraylist) using
			// the key (function name), then get the 3rd index from array list
			// for the param number (count) compare it against number 2 (because
			// our caculator only have function using 2 argument/param
			else if (!(Integer.parseInt(mFunctionIndex.get(sarr[0]).get(2).trim()) == 2)) {
				System.out.println("error 6 miss match argument/param number");
				pass = false;
			} else {
				// check argument type error , since argument is variable
				// go to variable hashmap grab the value (the type)
				// compare to the function hashmap param type.
				for (int i = 0; i < sarr.length - 1; i++) {
					// go to variable hashmap grab the value (the type) using
					// sarr[i+1] the key
					// compare to the function hashmap param type starting at
					// arraylist 4th index increase by 2 each loop.
					if (!(mVariableIndex.get(sarr[i + 1]).equals(mFunctionIndex.get(sarr[0]).get(j)))) {
						System.out.println("error 7 data type miss match argument/param");
						pass = false;
						break;
					}
					j = j + 2;
				}
			}
			if (pass == true) {
				System.out.println("function call passed");
			}
			// reset pass for next run
			pass = true;
		} else if (PatternMatching.visitReturn(input)) {
			// check for return type
			// return varname;
			// remove the semicolon
			input = RemoveAllCommaNSemicolon(input);
			// arr[] should only have 2 item arr[0] = return, arr[1] = varname
			// pull out varname search variable hash table and get the varname
			// data type
			// match it vs the function return type (which function? look for
			// functionid = returncount
			// returncount start at zero everytime this condition pass it will
			// increase by 1 before exit else if

			// pick up the return type (value) from variable hashmap by supply
			// in the key(variable name)
			String varDatatype = mVariableIndex.get(arr[1]);
			// compare return type and function return type
			if (!(varDatatype.equals(functionReturnType(returncount)))) {
				System.out.println("error code 8 function return type and return data type do not match");
			}
			// update returncount for next return statement
			returncount++;
		} else if (PatternMatching.visitVarAssignFunc(input)) {
			// varname = functioncall(argument1, argument2);
			// if we get this statement
			// first we replace paranthesis with space then remove ; and ,
			input.replaceAll("(", " ");
			input.replaceAll(")", " ");
			// remove comma and semicolon
			input = RemoveAllCommaNSemicolon(input);
			input = input.trim();
			// we get this
			// varname = functioncall argument1 argument2
			// split them into an array using split and space delimiter
			String sVAF[] = input.split(" ");
			// pick up the variable data type by going to the variable hashmap
			// supply the key sVAF[0] and get the value (data type) out
			// compare it to the function return type (go to function hashmap
			// supply the function name for key and get value array then
			// subscript
			// zero to get function return type. Compare them if they do not
			// match
			// output error 9 else they pass
			if (!(mVariableIndex.get(sVAF[0]).equals(mFunctionIndex.get(sVAF[2]).get(0)))) {
				System.out.println("error code 9 function return type and variable data type do not match");
			}
		} else if (PatternMatching.visitIfStatOper(input)) {
			int left = input.length() - input.replace("(", "").length();
			int right = input.length() - input.replace(")", "").length();
			if (left != right) {
				System.err.println("error code 10: invalid if statement (unequal parenthesis count)");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}
			/**
			 * this line is found to contain an if statement that contains an
			 * operator we must check the left and right to side of the operator
			 * to see id both are function or both are variable
			 */

			input = input.replaceAll("\\(\\)", "(words)");// this is a hack to
															// work around empty
															// function calls.
			// String ifStatementOps =
			// ("(\\w+)(\\w+\\(\\w*?\\))?\\s?(=|>|>=|<|<=|!=)\\s?(\\w+)(\\w+\\(\\w*?\\))?");
			// Pattern ifStatOpCheck = Pattern.compile(ifStatementOps);
			// Matcher matcher = ifStatOpCheck.matcher(input);
			String[] groups = input.split("[^\\w']+");// split on space or
			// non-words

			// if there are not 3 or 5 groups, invalid if format
			if (groups.length != 3 && groups.length != 5) {
				System.err.println("error code 10: invalid if statement");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}

			// for tacking names and whether they are found
			String arg1 = null;
			String arg2 = null;
			boolean found1 = false;
			boolean found2 = false;

			// for checking variable names
			if (groups.length == 3) {// if groups size is 3, then the args are
				// variables.
				arg1 = groups[1];// 1st variable
				arg2 = groups[2];// 2nd variable
				String[] varNames = getVariableDictionary();// load names

				// check for variable names
				for (String s : varNames) {
					if (s.equals(arg1))
						found1 = true;

					if (s.equals(arg2))
						found2 = true;

					if (found1 && found2)// break if both are found
						break;
				}

				// if one or more of the variables are not found
				if (!found1 || !found2) {
					System.err.println("error code 10: invalid if statement (variable names not found))");
					Thread.dumpStack();
					//System.exit(10);
				}
			} else {// this is for checking the function names, same steps above
				arg1 = groups[1];
				arg2 = groups[3];
				String[] funNames = getFunctionDictionary();

				for (String s : funNames) {
					if (s.equals(arg1))
						found1 = true;

					if (s.equals(arg2))
						found2 = true;

					if (found1 && found2)
						break;
				}
				if (!found1 || !found2) {
					System.err.println("error code 10: invalid if statement (function names not found))");
					Thread.dumpStack();
					//System.exit(10);
				}
			}
		} else if (PatternMatching.visitIfStatFunc(input)) {
			/**
			 * for now, checking for a proper function will be ignored
			 */
			int left = input.length() - input.replace("(", "").length();
			int right = input.length() - input.replace(")", "").length();
			if (left != right) {
				System.err.println("error code 10: invalid if statement (unequal parenthesis count)");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}

			String[] groups = input.split("[^\\w']+");// split on space or
														// non-words
			String arg1 = groups[1];

			String[] funcNames = getFunctionDictionary();// load function names

			boolean match = false;
			for (String s : funcNames) {// check to see if function exits
				if (s.equals(arg1)) {
					match = true;
					break;// stop when found
				}
			}

			if (!match) {// if we didn't find the function name, error out
				System.err.println("error code 10: invalid if statement (function name not found))");
				Thread.dumpStack();
				//System.exit(10);
			}

			if (!mFunctionIndex.get(arg1).get(0).equals("bool")) {// check that
																	// the
																	// return
																	// type is
																	// correct
				System.err.println("error code 10: invalid if statement (function must return bool type))");
				Thread.dumpStack();
				//System.exit(10);
			}
		} else if (PatternMatching.visitIfStatVar(input)) {
			int left = input.length() - input.replace("(", "").length();
			int right = input.length() - input.replace(")", "").length();
			if (left != right) {
				System.err.println("error code 10: invalid if statement (unequal parenthesis count)");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}

			String[] groups = input.split("[^\\w']+");// split on space or
														// non-words
			String arg1 = groups[1];

			String[] varNames = getVariableDictionary();
			boolean match = false;
			for (String s : varNames) {
				if (s.equals(arg1)) {
					match = true;
					break;
				}
			}

			if (!match) {
				System.err.println("error code 10: invalid if statement (variable name not found))");
				Thread.dumpStack();
				//System.exit(10);
			}

			if (mVariableIndex.get(arg1) != "bool") {
				System.err.println("error code 10: invalid if statement (function must return bool type))");
				Thread.dumpStack();
				//System.exit(10);
			}
		} else if (PatternMatching.visitWhileStatVar(input)) {
			int left = input.length() - input.replace("(", "").length();
			int right = input.length() - input.replace(")", "").length();
			if (left != right) {
				System.err.println("error code 10: invalid while statement (unequal parenthesis count)");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}

			String[] groups = input.split("[^\\w']+");// split on space or
														// non-words
			String arg1 = groups[1];

			String[] varNames = getVariableDictionary();
			boolean match = false;
			for (String s : varNames) {
				if (s.equals(arg1)) {
					match = true;
					break;
				}
			}
			if (!match) {
				System.err.println("error code 10: invalid while statement (variable name not found))");
				Thread.dumpStack();
				//System.exit(10);
			}

			if (mVariableIndex.get(arg1) != "bool") {
				System.err.println("error code 10: invalid while statement (variable must be of type bool))");
				Thread.dumpStack();
				//System.exit(10);
			}
		} else if (PatternMatching.visitWhileStatFunc(input)) {
			int left = input.length() - input.replace("(", "").length();
			int right = input.length() - input.replace(")", "").length();

			if (left != right) {
				System.err.println("error code 10: invalid while statement (unequal parenthesis count)");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}

			input = input.replaceAll("\\(\\)", "(words)");// this is a hack to
															// work around empty
															// function calls.
			String[] groups = input.split("[^\\w']+");// split on space or
														// non-words
			String arg1 = groups[1];

			String[] funcNames = getFunctionDictionary();
			boolean match = false;
			for (String s : funcNames) {
				if (s.equals(arg1)) {
					match = true;
					break;
				}
			}

			if (!match) {
				System.err.println("error code 10: invalid while statement (function name not found))");
				Thread.dumpStack();
				//System.exit(10);
			}

			if (!mFunctionIndex.get(arg1).get(0).equals("bool")) {
				System.err.println("error code 10: invalid while statement (function must return bool type))");
				Thread.dumpStack();
				//System.exit(10);
			}
		} else if (PatternMatching.visitWhileStatOper(input)) {
			int left = input.length() - input.replace("(", "").length();
			int right = input.length() - input.replace(")", "").length();
			if (left != right) {
				System.err.println("error code 10: invalid while statement (unequal parenthesis count)");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}
			/**
			 * this line is found to contain an while statement that contains an
			 * operator we must check the left and right to side of the operator
			 * to see id both are function or both are variable
			 */

			input = input.replaceAll("\\(\\)", "(words)");// this is a hack to
															// work around empty
															// function calls.
			String[] groups = input.split("[^\\w']+");// split on space or
			// non-words

			// if there are not 3 or 5 groups, invalid if format
			if (groups.length != 3 && groups.length != 5) {
				System.err.println("error code 10: invalid while statement");// error
				Thread.dumpStack(); // message
				//System.exit(10);// quit
			}

			// for tacking names and whether they are found
			String arg1 = null;
			String arg2 = null;
			boolean found1 = false;
			boolean found2 = false;

			// for checking variable names
			if (groups.length == 3) {// if groups size is 3, then the args are
				// variables.
				arg1 = groups[1];// 1st variable
				arg2 = groups[2];// 2nd variable
				String[] varNames = getVariableDictionary();// load names

				// check for variable names
				for (String s : varNames) {
					if (s.equals(arg1))
						found1 = true;

					if (s.equals(arg2))
						found2 = true;

					if (found1 && found2)// break if both are found
						break;
				}

				// if one or more of the variables are not found
				if (!found1 || !found2) {
					System.err.println("error code 10: invalid while statement (variable names not found))");
					Thread.dumpStack();
					//System.exit(10);
				}
			} else {// this is for checking the function names, same steps above
				arg1 = groups[1];
				arg2 = groups[3];
				String[] funNames = getFunctionDictionary();

				for (String s : funNames) {
					if (s.equals(arg1))
						found1 = true;

					if (s.equals(arg2))
						found2 = true;

					if (found1 && found2)
						break;
				}
				if (!found1 || !found2) {
					System.err.println("error code 10: invalid while statement (function names not found))");
					Thread.dumpStack();
					//System.exit(10);
				}
			}
		} else if (PatternMatching.visitCharArray(input)) {
			checkArr(input);
		} else if (PatternMatching.visitAssignmentCheck(input)) {
			checkAssignCall(input);
		} else {
			System.err.println("failed to pass the typecheck");
		}
	}

	// take in returncount which is the equivalence of functioncount
	// get all of the item in function hashmap compare the functioncount
	// to returncount, if functioncount = returncount get the return type of
	// that function.
	public static String functionReturnType(int returncount) {
		String[] funcnameList = getFunctionDictionary();

		// convert int to string and trim it
		String sreturncount = "" + returncount;
		sreturncount = sreturncount.trim();

		// loop the whole list of function name
		for (int i = 0; i < funcnameList.length; i++) {
			// go to hashmap of function get the value using the function name
			// key
			// value is an array and the 2nd item in the array is the function
			// unique id (function count) which match the returncount
			if (mFunctionIndex.get(funcnameList[i]).get(1).equals(sreturncount)) {
				// we found the function for our return call
				// now get the return type from the function and return it.
				// function return type is 1st item in the arraylist
				return mFunctionIndex.get(funcnameList[i]).get(0);
			}
		}
		return null;
	}

	public static void maincheck(String input, PatternMatching mycheck) {
		maincount++;
		// case of more than 1 main() appear
		if (maincount > 1) {
			System.out.println("error code 1");
		}
		// case of int main() fail the pattern matching
		else if (!PatternMatching.visitmain(input)) {
			System.out.println("error code 2");
			System.out.println("main fail pattern matching not neccessary main with argument");
		} else {
			System.out.println("main passed");
		}
	}

	public static void functionPopulate(String input) {
		// split the input using split function
		// add each item in array into the hashmap
		// the function name will be the key
		// value is an arraylist of return type, function unique ID
		// param number(count), 1st param return type, 1st param name,
		// 2nd param return type, 2nd param name etc...
		// split the string into array of string using space between word
		String[] arr = input.split("\\s+");
		ArrayList<String> value = new ArrayList<String>();

		// add the return type first
		value.add(arr[0]);

		// add the unique id, this id is used
		// to match it against return type hashmap
		value.add("" + functioncount);
		// update function count by +1
		functioncount++;

		// add number of parameter (param count of the function)
		// the size of the arr[] - 4 (return type, name, open paren, close
		// paren)
		value.add("" + ((arr.length - 4) / 2));

		// we have 1 or more param
		if (!((arr.length - 4) / 2 == 0)) {
			// loop through the arr[] add all param
			// return type and name into the value arraylist
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].equalsIgnoreCase("(")) {
					// add param return type and name intp value list
					for (int j = i + 1; j < arr.length; j = j + 2) {
						if (!arr[j].equalsIgnoreCase(")")) {
							// param return type
							value.add(arr[j]);
							// param name
							value.add(arr[j + 1]);
						}
					}
					break;
				}
			}
		}

		mFunctionIndex.put(arr[1], value);
	}

	// loop throught the list of key(function name) in mFunctionIndex
	// return true if found duplicate, false if there is no duplicate
	public static boolean isFunctionNameDuplicate(String functionName) {
		// we have nothing in the hashmap mean no function was added at all
		// no duplicate possible
		if (mFunctionIndex.isEmpty() == true) {
			return false;
		}
		// we have at least 1 function added
		else {
			String[] funcnameList = getFunctionDictionary();
			// loop the whole list of function name
			for (int i = 0; i < funcnameList.length; i++) {
				// if found an equal (duplicated) return false
				if (functionName.equals(funcnameList[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public static String[] getFunctionDictionary() {
		// TO-DO: fill an array of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		List<String> keys = new ArrayList<String>();
		for (String key : mFunctionIndex.keySet()) {
			keys.add(key);
		}

		String[] mystrarr = new String[keys.size()];
		for (int j = 0; j < mystrarr.length; j++) {
			mystrarr[j] = keys.get(j);
		}

		return mystrarr;
	}

	// loop throught the list of key(variable name) in mVariableIndex
	// return true if found duplicate, false if there is no duplicate
	public static boolean isVariableNameDuplicate(String variableName) {
		// we have nothing in the hashmap mean no variable was added at all
		// no duplicate possible
		if (mVariableIndex.isEmpty() == true) {
			return false;
		}
		// we have at least 1 variable added
		else {
			String[] variableList = getVariableDictionary();
			// loop the whole list of variable name
			for (int i = 0; i < variableList.length; i++) {
				// if found an equal (duplicated) return false
				if (variableName.equals(variableList[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public static String[] getVariableDictionary() {
		// TO-DO: fill an array of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		List<String> keys = new ArrayList<String>();
		for (String key : mVariableIndex.keySet()) {
			keys.add(key);
		}

		String[] mystrarr = new String[keys.size()];
		for (int j = 0; j < mystrarr.length; j++) {
			mystrarr[j] = keys.get(j);
		}

		return mystrarr;
	}

	/**
	 * This function checks if the given array call for two different
	 * requirements<br>
	 * 1 - array index is numeric(12)<br>
	 * 2 - the variable being indexed is a String(13)<br>
	 * 
	 * @param input
	 *            - Line of text that contains the array index call
	 * @return always returns true, if a condition is violated, the program
	 *         exits
	 */
	public static boolean checkArr(String input) {
		// split on non-word chars, use lookahead to keep delimiters
		String[] groups = input.split("((?<=[^\\w']+)|(?=[^\\w']+))");
		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(groups));
		while (tokens.remove(" ") == true) {
		}
		;// remove all space chars as tokens

		int left = 0; // index of the left brace
		boolean stop = false;
		while (!stop && left < tokens.size()) {// iterate through loop looking
												// for [
			if (tokens.get(left).equals("[")) {// if found
				if (!tokens.get(left + 2).equals("]")) {// check left + 2 is ]
					System.err.println("error code 12: invalid array index call");// error
					Thread.dumpStack(); // message
					//System.exit(12);// quit
				}
				stop = true;// found [
			} else {
				left++;// keep looking
			}
		}

		// check if we found [, exit if not
		if (left == tokens.size()) {
			System.err.println("error code 12: invalid array index call");// error
			Thread.dumpStack(); // message
			//System.exit(12);// quit
		}

		// check if the token between [ and ] is numeric
		if (!tokens.get(left + 1).matches("\\d+")) {
			System.err.println("error code 12: invalid array index call (index is non-numeric)");// error
			Thread.dumpStack(); // message
			//System.exit(12);// quit
		}

		String[] varNames = getVariableDictionary();// get variable names
		boolean found = false;// this is for variable checking
		String arg = tokens.get(left - 1);// variable name will immediately
											// preceed [

		// search variables for arg
		for (String s : varNames) {
			if (arg.equals(s))
				found = true;
		}

		if (!found) {// if variable is not found, exit
			System.err.println("error code 12: invalid array index call " + arg + " not found");// error
			Thread.dumpStack(); // message
			//System.exit(13);// quit
		}

		if (!mVariableIndex.get(arg).equals("String")) {// variable is found,
														// but not a String
			System.err.println("error code 13: invalid array index call " + arg + " not of type String");// error
			Thread.dumpStack(); // message
			//System.exit(13);// quit
		}
		return true;// if the program is running by this point, the array call
					// worked.
	}

	public static void checkAssignCall(String input) {
		ArrayList<String> tokens = new ArrayList<>(Arrays.asList(input.split("((?<=[^\\w']+)|(?=[^\\w']+))")));

		while (tokens.contains(" ")) {
			tokens.remove(" ");
		}

		int index = tokens.indexOf("=");
		
		String leftArg = null;
		String leftType = null;
		String rightArg = null;
		String rightType = null;
		
		leftArg = tokens.get(index -1);
		rightArg = tokens.get(index+1);
		
		if(rightArg.equals("NULL") && (mVariableIndex.containsKey(leftArg))){
			if(!(mVariableIndex.get(leftArg).equals("char") ||
					mPointerIndex.get(leftArg).equals("int"))){
				System.err.println("error code 14: Invalid function: " + rightArg);// error
				Thread.dumpStack(); // message
				//System.exit(14);// quit
			}
		}
		
		if(tokens.contains("+") || tokens.contains("-")){
			
		}

		if (tokens.contains("(")) {// if this is a function call
			rightArg = tokens.get(index + 1);
			if (!mFunctionIndex.containsKey(rightArg)) {// check the function
														// exists
				System.err.println("error code 14: Invalid function: " + rightArg);// error
				Thread.dumpStack(); // message
				//System.exit(14);// quit
			}
			rightType = mFunctionIndex.get(rightArg).get(0);
		} else {// not a function, check the variable
			rightArg = tokens.get(index + 1);
			if (!mVariableIndex.containsKey(rightArg)) {// check variable exists
				System.err.println("error code 14: Invalid variable: " + rightArg);// error
				Thread.dumpStack(); // message
				//System.exit(14);// quit
			}
			rightType = mVariableIndex.get(rightArg);
		}

		leftArg = tokens.get(index - 1);
		if (!mVariableIndex.containsKey(leftArg)) {// check left hand variable
													// exists
			System.err.println("error code 14: invalid variable: " + leftArg);// error
			Thread.dumpStack(); // message
			//System.exit(14);// quit
		} else {
			leftType = mVariableIndex.get(leftArg);
		}

		if (!leftType.equals(rightType)) {
			System.err.println(
					"error code 14: Left side does not match the right side: " + leftType + " != " + rightType);// error
			Thread.dumpStack(); // message
			//System.exit(14);// quit
		}
	}

}