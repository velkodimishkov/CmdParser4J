// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

/**
 * Parameter type boolean
 * Created by Per Malmberg on 2015-12-05.
 */
public class IntegerType extends BaseType<Integer> {

	public IntegerType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount) {
		super(parser, argument, minParameterCount, maxParameterCount);
	}

	@Override
	protected boolean doTypeParse(String parameter) {
		int count = myResults.size();

		if (parameter != null) {
			try {
				Integer value = Integer.valueOf(parameter);
				myResults.add(value);
			}
			catch (NumberFormatException ex)
			{
				// Parse failed.
			}
		}

		return count < myResults.size();
	}

	@Override
	void retrieveResult(CmdParser4J cmdParser) {
		cmdParser.setResult(myArgument.getPrimaryName(), this);
	}
}
