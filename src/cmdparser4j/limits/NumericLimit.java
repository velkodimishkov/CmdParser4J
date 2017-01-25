// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.limits;

import cmdparser4j.IParseResult;

public class NumericLimit<T> {
	private T myLower;
	private T myUpper;

	public NumericLimit(T lower, T upper)
	{
		myLower = lower;
		myUpper = upper;
	}

	public T getLower() { return myLower;}
	public T getUpper() { return myUpper;}

	public void reportLimitViolation(String primaryName, IParseResult messageParser) {
		messageParser.outsideLimits(primaryName, this);
	}
}
