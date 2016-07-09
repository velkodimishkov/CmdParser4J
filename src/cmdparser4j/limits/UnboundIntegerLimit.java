// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.limits;

public class UnboundIntegerLimit extends NumericLimit<Integer> {
	public UnboundIntegerLimit() {
		super(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
}
