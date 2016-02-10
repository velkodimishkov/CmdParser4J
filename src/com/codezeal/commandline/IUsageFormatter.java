// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

import java.util.List;

/**
 * Created by Per Malmberg on 2016-02-02.
 */
public interface IUsageFormatter {
	void prepareMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, String description);

	void prepareNonMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, String description);

	String toString();
}

