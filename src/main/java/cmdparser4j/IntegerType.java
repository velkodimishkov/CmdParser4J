// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.NumericLimit;

/**
 * Parameter type boolean
 * Created by Per Malmberg on 2015-12-05.
 */
public class IntegerType extends BaseType<Integer, Integer> {

	public IntegerType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount, NumericLimit<Integer> limit) {
		super(parser, argument, minParameterCount, maxParameterCount, limit);
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
				myParser.getMessageParser().exception( ex );
			}
		}

		return count < myResults.size();
	}

	@Override
	void retrieveResult(CmdParser4J cmdParser) {
		cmdParser.setResult(myArgument.getPrimaryName(), this);
	}

	@Override
	protected boolean checkLimits() {
		boolean res= true;

		for( int i = 0; res && i < myResults.size(); ++i ) {
			Integer v = myResults.get(i);
			res = v >= myLimit.getLower() && v <= myLimit.getUpper();
			if( !res ) {
				myLimit.reportLimitViolation( myArgument.getPrimaryName(), myParser.getMessageParser());
			}
		}

		return res;
	}
}
