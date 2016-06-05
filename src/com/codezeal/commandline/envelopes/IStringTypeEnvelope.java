// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline.envelopes;

import com.codezeal.commandline.StringType;


public interface IStringTypeEnvelope {
	void add(String primaryName, StringType type);
	String get(String primaryName, int index, String defaultValue);
}
