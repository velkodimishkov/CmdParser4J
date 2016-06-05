// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline.envelopes;

import com.codezeal.commandline.BooleanType;

public interface IBooleanTypeEnvelope {
	void add(String primaryName, BooleanType type);
	boolean get(String primaryName, int index, boolean defaultValue);
}
