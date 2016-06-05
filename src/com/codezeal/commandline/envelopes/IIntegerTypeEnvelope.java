// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline.envelopes;

import com.codezeal.commandline.IntegerType;

public interface IIntegerTypeEnvelope {
	void add(String primaryName, IntegerType type);
	int get(String primaryName, int index, int defaultValue);
}
