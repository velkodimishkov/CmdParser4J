// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.envelopes;

import cmdparser4j.BooleanType;

public interface IBooleanTypeEnvelope {
	void add(String primaryName, BooleanType type);
	boolean get(String primaryName, int index, boolean defaultValue);
}
