// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
public class SingleBooleanType extends BooleanType {
	public SingleBooleanType(CmdParser4J parser, Argument argument) {
		super(parser, argument, 0, 0);
		myResults.add(true);
	}

	@Override
	public boolean isSuccessFullyParsed() {
		return true;
	}
}
