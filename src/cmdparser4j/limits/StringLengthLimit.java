// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.limits;

import cmdparser4j.IParseResult;

public class StringLengthLimit extends NumericLimit<Integer> {
	public StringLengthLimit(Integer lowerLength, Integer upperLength) {
		super(lowerLength, upperLength);
	}

	@Override
	public void reportLimitViolation(String primaryName, IParseResult messageParser) {
		messageParser.outsideLimits(primaryName, this);
	}
}
