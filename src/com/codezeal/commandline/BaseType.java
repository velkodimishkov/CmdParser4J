package com.codezeal.commandline;

import java.util.ArrayList;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
abstract class BaseType<T> {
	private final int myMinParameterCount;
	private final int myMaxParameterCount;
	private int myAvailableParameterCount = 0;
	private final CmdParser4J myParser;
	protected final Argument myArgument;
	protected final ArrayList<T> myResults = new ArrayList<T>();

	public BaseType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount) {
		myParser = parser;
		myArgument = argument;
		myMinParameterCount = minParameterCount;
		myMaxParameterCount = maxParameterCount;
	}

	public int getAvailableParameterCount() {
		return myResults.size();
	}

	final boolean parse(ArrayList<String> args, int argumentIx) {
		// Save and remove the argument name
		String argumentName = args.remove(argumentIx);
		boolean res = hasEnoughParametersLeft(args, argumentIx);

		if (res) {
			// We only do this loop if the current type takes at least one parameter
			for (int currentParameter = 0;
			     res && currentParameter < myMaxParameterCount
					     && args.size() > 0
					     && argumentIx < args.size(); ++currentParameter) {
				// Get the next parameter from the 'front', i.e. where our parameters start.
				String parameter = args.remove(argumentIx);
				res = doTypeParse(parameter);
				if (res) {
					++myAvailableParameterCount;
				}
			}
		} else {
			myParser.appendParseMessage(String.format("There are not enough parameters for the argument %s, %d wanted", argumentName, myMinParameterCount));
		}

		res = isSuccessFullyParsed();

		if (res) {
			retrieveResult(myParser);
		} else {
			myParser.appendParseMessage(String.format("Parsing of argument '%s' failed", argumentName));
		}

		return res;
	}

	/**
	 * Indicates if the parser is satisfied with the parse.
	 *
	 * @return true if ok, otherwise false.
	 */
	boolean isSuccessFullyParsed() {
		return getAvailableParameterCount() >= myMinParameterCount && getAvailableParameterCount() <= myMaxParameterCount;
	}

	/**
	 * Performs type-specific parsing.
	 *
	 * @param parameter The parameter value to parse
	 * @return true if ok, otherwise false.
	 */
	protected abstract boolean doTypeParse(String parameter);

	/**
	 * Determines if there are enough parameters to parse
	 */
	private boolean hasEnoughParametersLeft(ArrayList<String> args, int argumentIx) {
		return args.size() - argumentIx - myMinParameterCount >= 0;
	}

	/**
	 *
	 */
	T getResult(int index, T defaultValue) {
		T res = defaultValue;

		if (myResults.size() >= index && index < myResults.size()) {
			res = myResults.get(index);
		}

		return res;
	}

	/**
	 * Retrieves the result from the type parser and sets it in the command parser.
	 *
	 * @param cmdParser The command parser
	 */
	abstract void retrieveResult(CmdParser4J cmdParser);

	int getMaxParameterCount() {
		return myMaxParameterCount;
	}

	int getParameterCount() {
		return myMinParameterCount;
	}
}
