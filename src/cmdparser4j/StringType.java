// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.StringLengthLimit;

/**
 * Parameter type string
 * Created by Per Malmberg on 2015-12-05.
 */
public class StringType extends BaseType<String, Integer> {
	public StringType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount, StringLengthLimit limit) {
		super(parser, argument, minParameterCount, maxParameterCount, limit);
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

	@Override
	protected boolean checkLimits() {
		boolean res= true;

		for( int i = 0; res && i < myResults.size(); ++i ) {
			String v = myResults.get(i);
			res = v.length() >= myLimit.getLower() && v.length() <= myLimit.getUpper();
			if( !res ) {
				myLimit.reportLimitViolation( myArgument.getPrimaryName(), myParser.getMessageParser());
			}
		}

		return res;
	}
}
