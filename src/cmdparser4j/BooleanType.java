// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.NumericLimit;

/**
 * Parameter type boolean
 * Created by Per Malmberg on 2015-12-05.
 */
public class BooleanType extends BaseType<Boolean, Boolean> {
	private static final String ZERO = "0";
	private static final String ONE = "1";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	public BooleanType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount ) {
		super(parser, argument, minParameterCount, maxParameterCount, new NumericLimit<Boolean>(false, true));
	}

	@Override
	protected boolean doTypeParse(String parameter) {
		int count = myResults.size();

		if (parameter != null) {
			String p = parameter.toLowerCase();
			if (p.equals(ZERO) || p.equals(ONE)) {
				myResults.add("1".equals(p));
			} else if (p.equals(TRUE) || p.equals(FALSE)) {
				myResults.add(p.equals(TRUE));
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
		// Boolean type can't be out of range
		return true;
	}
}
