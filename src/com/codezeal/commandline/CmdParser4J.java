// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

import java.util.*;

/**
 * A command line parser for Java.
 * Commands arguments can be specified in any order, except those that make a variable number
 * of arguments which as to be placed at the end of the argument list.
 * <p/>
 * Created by Per Malmberg on 2015-12-05.
 */
public class CmdParser4J {
	private final HashMap<String, Argument> myArguments = new HashMap<String, Argument>();
	private final Hashtable<String, StringType> myStringResult = new Hashtable<String, StringType>();
	private final Hashtable<String, BooleanType> myBoolResult = new Hashtable<String, BooleanType>();
	private final String myArgumentPrefix;
	private final IParseResult myResult;

	/**
	 * Constructs a command line parser
	 *
	 * @param argumentPrefix The prefix which commands are expected to be prefixed with, such as '-', '--' or '/'
	 *                       This is used when determining where the next argument starts. Must be specified.
	 *                       Even though this argument must be specified, it is fully legal to use arguments without
	 *                       the leading prefix, but be aware that such arguments cannot be distinguished
	 *                       from argument parameters as they lack the prefix.
	 */
	public CmdParser4J(String argumentPrefix, IParseResult message) {
		myArgumentPrefix = argumentPrefix;
		myResult = message;
	}

	/**
	 * Parses the command line
	 *
	 * @param args The arguments
	 * @return true on success, false on failure
	 */
	public boolean parse(String... args) {
		ArrayList<String> a = new ArrayList<String>();
		Collections.addAll(a, args);
		return parse(a);
	}

	/**
	 * Parses the command line
	 *
	 * @param args The arguments
	 * @return true on success, false on failure
	 */
	private boolean parse(ArrayList<String> args) {

		args = removeEmptyArguments(args);

		boolean result = checkConstraints(args);

		if (result) {
			Iterator<Argument> currentArg = myArguments.values().iterator();

			// Let each Argument have a go until there are no more arguments available
			// (we rely on that the Argument removes the parts it uses from the 'args' array)
			while (currentArg.hasNext() && result && args.size() > 0) {
				Argument a = currentArg.next();
				result = a.parse(args);
			}

			if (result && args.size() > 0) {
				// Leftovers on commandline.
				myResult.unknownArguments(args.toString());
				result = false;
			}

			result &= checkMandatory();
			result &= checkDependencies();
			result &= checkMutualExclusion();
		}

		return result;
	}


	private boolean checkDependencies() {
		boolean result = true;
		for (Argument a : myArguments.values()) {
			result &= a.checkDependencies(myArguments);
		}

		return result;
	}

	private boolean checkMutualExclusion() {
		boolean result = true;
		// We don't want to check blockers 'a' -> 'b', then 'b' -> 'a' as that will give the same error message twice

		HashMap<String, Argument> testAgainst = new HashMap<String, Argument>();
		List<String> alreadyTested = new ArrayList<String>();
		testAgainst.putAll(myArguments);

		for (String key : myArguments.keySet()) {
			Argument arg = myArguments.get(key);
			boolean blocksFound = !arg.checkMutualExclusion(testAgainst, alreadyTested);
			if (blocksFound) {
				// Remove argument to prevent double checks
				alreadyTested.add(key);
				testAgainst.remove(key);
			}
			result &= !blocksFound;
		}

		return result;
	}

	private boolean checkConstraints(ArrayList<String> args) {
		boolean res = true;
		// Find all arguments with unlimited parameters specified on the command line
		ArrayList<Integer> variable = new ArrayList<Integer>();
		ArrayList<Integer> argumentIndexes = new ArrayList<Integer>();

		for (Argument a : myArguments.values()) {
			Changeable<Integer> hitCount = new Changeable<Integer>(0);
			int ix = a.findArgument(args, hitCount);

			if (ix >= 0) {
				if (hitCount.get() > 1) {
					// Same argument multiple times - that's bad
					res = false;
					myResult.ArgumentSpecifiedMultipleTimes(a.getPrimaryName());
				} else if (a.hasVariableParameterCount()) {
					variable.add(ix);
				} else {
					argumentIndexes.add(ix);
				}
			}
		}

		if (res && variable.size() > 1) {
			res = false;
			myResult.multipleMultiArgumentsSpecified();
		}

		if (res && variable.size() == 1) {
			// Check if the argument is last on the list
			int max = variable.get(0);
			for (Integer curr : argumentIndexes) {
				max = Math.max(max, curr);
			}

			if (variable.get(0) < max) {
				res = false;
				myResult.multiArgumentMustBePlacedLast();
			}
		}

		return res;
	}

	/**
	 * Checks that all mandatory arguments have been parsed
	 *
	 * @return true if ok, otherwise false.
	 */
	private boolean checkMandatory() {
		boolean result = true;
		for (Argument a : myArguments.values()) {
			if (a.isMandatory() && !a.isSuccessFullyParsed()) {
				myResult.missingMandatoryArgument(a.getPrimaryName());
				result = false;
			}
		}
		return result;
	}

	/**
	 * Removes empty arguments
	 */
	private ArrayList<String> removeEmptyArguments(ArrayList<String> args) {
		ArrayList<String> cleaned = new ArrayList<String>();
		for (String a : args) {
			if (a.length() > 0) {
				cleaned.add(a);
			}
		}

		return cleaned;
	}

	/**
	 * Defines an argument with the provided argument name.
	 * Use the returned {@code Constructor} object to further define the argument properties.
	 *
	 * @param argumentName The argument name.
	 * @return A {@code Constructor} object
	 */
	public Constructor accept(String argumentName) {
		Argument a = new Argument(argumentName, myResult);
		myArguments.put(a.getPrimaryName(), a);
		return new Constructor(a, this);
	}

	void setResult(String primaryName, StringType type) {
		myStringResult.put(primaryName, type);
	}

	void setResult(String primaryName, BooleanType type) {
		myBoolResult.put(primaryName, type);
	}

	/**
	 * Gets the first parameter for the given {@code argumentName}
	 *
	 * @param argumentName The argument name
	 * @return The parameter value, or false if not found
	 */
	public boolean getBool(String argumentName) {
		return getBool(argumentName, 0, false);
	}

	/**
	 * Gets the parameter at {@code index} for the given {@code argumentName}
	 *
	 * @param argumentName The argument name
	 * @param index        The index
	 * @return The parameter value, or false if not found
	 */
	public boolean getBool(String argumentName, int index) {
		return getBool(argumentName, index, false);
	}

	/**
	 * Gets the parameter at {@code index} for the given {@code argumentName}
	 *
	 * @param argumentName The argument name
	 * @param index        The index
	 * @param defaultValue The default value
	 * @return The parameter value, or {@code defaultValue} if not found
	 */
	public boolean getBool(String argumentName, int index, boolean defaultValue) {
		boolean res = defaultValue;
		BooleanType b = myBoolResult.get(argumentName);
		if (b != null) {
			res = b.getResult(index, defaultValue);
		}
		return res;
	}

	/**
	 * Gets the first parameter for the given {@code argumentName}
	 *
	 * @param argumentName The argument name
	 * @return The parameter value, or null if not found
	 */
	public String getString(String argumentName) {
		return getString(argumentName, 0, null);
	}

	/**
	 * Gets the parameter at {@code index} for the given {@code argumentName}
	 *
	 * @param argumentName The argument name
	 * @param index        The index
	 * @return The parameter value, or null if not found
	 */
	public String getString(String argumentName, int index) {
		return getString(argumentName, index, null);
	}

	/**
	 * Gets the parameter at {@code index} for the given {@code argumentName}
	 *
	 * @param argumentName The argument name
	 * @param index        The index
	 * @param defaultValue The default value
	 * @return The parameter value, or {@code defaultValue} if not found
	 */
	public String getString(String argumentName, int index, String defaultValue) {
		String res = defaultValue;
		StringType b = myStringResult.get(argumentName);
		if (b != null) {
			res = b.getResult(index, defaultValue);
		}
		return res;
	}

	/**
	 * Gets a string describing the usage, suitable for printing to the console.
	 */
	public void getUsage(IUsageFormatter usage) {
		// Print mandatory
		for (Argument a : myArguments.values()) {
			if (a.isMandatory() && !a.isHidden()) {
				usage.prepareMandatory(a.getPrimaryName(), a.hasVariableParameterCount(), a.getMaxArgumentCount(), a.getAliases(), a.getDescription());
			}
		}

		// Print non mandatory
		for (Argument a : myArguments.values()) {
			if (!a.isMandatory() && !a.isHidden()) {
				usage.prepareNonMandatory(a.getPrimaryName(), a.hasVariableParameterCount(), a.getMaxArgumentCount(), a.getAliases(), a.getDescription());
			}
		}
	}


	private <T extends BaseType> int getAvailableParameterCount(String argumentName, Hashtable<String, T> source) {
		int result = 0;
		T t = source.get(argumentName);
		if (t != null) {
			result = t.getAvailableParameterCount();
		}

		return result;
	}


	/**
	 * Gets the available parameter count for the give argument name of type boolean
	 *
	 * @param argumentName The argument name
	 * @return The number of available parameters
	 */
	public int getAvailableBooleanParameterCount(String argumentName) {
		return getAvailableParameterCount(argumentName, myBoolResult);
	}

	/**
	 * Gets the available parameter count for the give argument name of type string
	 *
	 * @param argumentName The argument name
	 * @return The number of available parameters
	 */
	public int getAvailableStringParameterCount(String argumentName) {
		return getAvailableParameterCount(argumentName, myStringResult);
	}

	public String getArgumentPrefix() {
		return myArgumentPrefix;
	}

	public IParseResult getMessageParser() {
		return myResult;
	}
}
