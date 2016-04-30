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
	private final IParseResult myResult;

	/**
	 * Constructs a command line parser
	 *
	 * @param message The object to be used to print parse messages.
	 */
	public CmdParser4J(IParseResult message) {
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

		boolean result = checkArgumentTypes() && checkConstraints(args);

		ArrayList<Map.Entry<Integer, Argument>> argumentIndexes = new ArrayList<Map.Entry<Integer, Argument>>();
		GetIndexes(argumentIndexes, args);

		if( argumentIndexes.size() == 0 && args.size() > 0) {
			// Arguments provided on the command line, but no matches found.
			myResult.unknownArguments( args.toString() );
			result = false;
		}
		else if( argumentIndexes.size() > 0 && argumentIndexes.get(0).getKey() > 0 ) {
			// Unknown arguments before first matching Argument.
			List<String> unknown = args.subList(0, argumentIndexes.get(0).getKey() );
			myResult.unknownArguments( unknown.toString() );
			result = false;
		}
		else {

			// Now let each argument parse any parameter until the next argument.
			// This ensures that an argument isn't considered as a parameter to another argument.
			for (int i = 0; result && i < argumentIndexes.size(); ++i) {
				Map.Entry<Integer, Argument> curr = argumentIndexes.get(i);

				int argumentPos = curr.getKey();
				int nextArgumentPos;

				// Are there more arguments left? If so, stop at that one. Otherwise take parameters until end.
				if (i == (argumentIndexes.size() - 1)) {
					nextArgumentPos = args.size();
				} else {
					nextArgumentPos = argumentIndexes.get(i + 1).getKey();
				}

				// Get a copy of the argument and the parameters after the argument.
				// Must use a new list because a subList returns a list that affects the original one, and since
				// the Argument.parse() modifies it we can't allow that.
				List<String> parameters = new ArrayList<String>( args.subList(argumentPos, nextArgumentPos) );

				// Let the argument parse its parameters
				result = curr.getValue().parse(parameters);

				if (result && parameters.size() > 0) {
					// Leftovers from command line
					myResult.unknownArguments(parameters.toString());
					result = false;
				}
			}

			result &= checkMandatory();
			result &= checkDependencies();
			result &= checkMutualExclusion();
		}

		return result;
	}

	/**
	 *
	 * @return
	 */
	private boolean checkArgumentTypes() {
		boolean res = true;

		// Find any argument that has no type
		for( Argument a : myArguments.values() )
		{
			if( !a.hasArgumentType() ) {
				res = false;
				myResult.ArgumentMissingType(a.getPrimaryName());
			}
		}

		return res;
	}

	/**
	 *
	 * @param argumentIndexes
	 * @param arguments
	 */
	void GetIndexes(ArrayList<Map.Entry<Integer, Argument>> argumentIndexes, final ArrayList<String> arguments) {
		Changeable<Integer> hit = new Changeable<Integer>(0);
		for (Argument a : myArguments.values()) {
			int ix = a.findArgument(arguments, hit);
			if (ix != -1) {
				argumentIndexes.add(new AbstractMap.SimpleEntry<Integer, Argument>(ix, a));
			}
		}

		Collections.sort(argumentIndexes, new Comparator<Map.Entry<Integer, Argument>>() {
			@Override
			public int compare(Map.Entry<Integer, Argument> left, Map.Entry<Integer, Argument> right) {
				return left.getKey().compareTo(right.getKey());
			}
		});
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

		// Find all arguments duplicates
		for (Argument a : myArguments.values()) {
			Changeable<Integer> hitCount = new Changeable<Integer>(0);
			int ix = a.findArgument(args, hitCount);

			if (ix >= 0) {
				if (hitCount.get() > 1) {
					// Same argument multiple times - that's bad
					res = false;
					myResult.argumentSpecifiedMultipleTimes(a.getPrimaryName());
				}
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

	public IParseResult getMessageParser() {
		return myResult;
	}
}
