// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.limits;

public class UnboundedIntegerLimit extends NumericLimit<Integer> {
	public UnboundedIntegerLimit() {
		super(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
}
