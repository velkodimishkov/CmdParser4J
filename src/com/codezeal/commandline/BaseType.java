// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
abstract class BaseType<T> {
	private final int myMinParameterCount;
	private final int myMaxParameterCount;
	private final IParseResult myResult;
	protected final Argument myArgument;
	protected final CmdParser4J myParser;
	protected final ArrayList<T> myResults = new ArrayList<T>();

	public BaseType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount) {
		myParser = parser;
		myResult = parser.getMessageParser();
		myArgument = argument;
		myMinParameterCount = minParameterCount;
		myMaxParameterCount = maxParameterCount;
	}

	public int getAvailableParameterCount() {
		return myResults.size();
	}

	final boolean parse(List<String> args, int argumentIx) {
		// Save and remove the argument name
		String argumentName = args.remove(argumentIx);

		// Enough parameters left?
		boolean res = hasEnoughParametersLeft(args, argumentIx);

		if (res) {
			// We only do this loop if the current type takes at least one parameter
			for (int currentParameter = 0;
			     res && currentParameter < myMaxParameterCount // Don't take to many parameters
					     && args.size() > 0 // Still some left in data
					     && argumentIx < args.size() // Not yet reached end of data
					;
                 ++currentParameter
					) {
				// Get the next parameter from the 'front', i.e. where our parameters start.
				String parameter = args.remove(argumentIx);
				res = doTypeParse(parameter);
			}
		} else {
			myResult.notEnoughParameters( argumentName, myMinParameterCount);
		}

		res = isSuccessFullyParsed();

		if (res) {
			retrieveResult(myParser);
		} else {
			myResult.failedToParseArgument(argumentName);
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
	private boolean hasEnoughParametersLeft(List<String> args, int argumentIx) {
		// Calculate remaining arguments until the prefix is found.
		int remaining = 0;

		boolean done = false;
		for (int i = argumentIx; !done && i < args.size(); ++i) {
			if (args.get(i).startsWith(myParser.getArgumentPrefix())) {
				done = true;
			} else {
				++remaining;
			}

		}

		return remaining >= myMinParameterCount;
	}

	/**
	 *
	 */
	T getResult(int index, T defaultValue) {
		T res = defaultValue;

		if ( index >= 0 && index < myResults.size()) {
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

	int getMinimumParameterCount() {
		return myMinParameterCount;
	}
}
