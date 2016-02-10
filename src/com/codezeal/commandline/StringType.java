// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

/**
 * Parameter type string
 * Created by Per Malmberg on 2015-12-05.
 */
public class StringType extends BaseType<String> {
	public StringType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount) {
		super(parser, argument, minParameterCount, maxParameterCount);
	}

	@Override
	protected boolean doTypeParse(String parameter) {

		boolean res = parameter != null && parameter.length() > 0;
		if (res) {
			myResults.add(parameter);
		}

		return res;
	}

	@Override
	void retrieveResult(CmdParser4J cmdParser) {
		cmdParser.setResult(myArgument.getPrimaryName(), this);
	}
}
