// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import java.util.List;

public interface IUsageFormatter {
	void prepareMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, List<String> dependencyNames, String description);

	void prepareNonMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, List<String> dependencyNames, String description);

	String toString();
}

