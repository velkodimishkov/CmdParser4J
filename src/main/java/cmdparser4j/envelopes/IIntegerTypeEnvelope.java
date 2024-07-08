// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.envelopes;

import cmdparser4j.IntegerType;

public interface IIntegerTypeEnvelope {
	void add(String primaryName, IntegerType type);
	int get(String primaryName, int index, int defaultValue);
}
