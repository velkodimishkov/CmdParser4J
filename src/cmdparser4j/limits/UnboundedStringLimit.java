// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.limits;

public class UnboundedStringLimit extends StringLengthLimit {

	public UnboundedStringLimit() {
		super(1, Integer.MAX_VALUE);
	}
}
