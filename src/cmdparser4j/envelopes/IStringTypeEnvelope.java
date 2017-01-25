// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j.envelopes;

import cmdparser4j.StringType;


public interface IStringTypeEnvelope {
	void add(String primaryName, StringType type);
	String get(String primaryName, int index, String defaultValue);
}
