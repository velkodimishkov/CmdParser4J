// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

/**
 * Interface used by the parser to notify the application of parse errors/events.
 * Created by Per Malmberg on 2016-01-30. *
 */
public interface IParseResult {
	void unknownArguments(String unknownArguments);

	void ArgumentSpecifiedMultipleTimes(String primaryName);

	void missingMandatoryArgument(String primaryName);

	String getParseResult();

	void noSuchArgumentDefined(String argument, String dependsOn);

	void missingDependentArgument(String primaryName, String dependsOn);

	void noSuchMutuallyExclusiveArgumentDefined(String primaryName, String blocker);

	void argumentsAreMutuallyExclusive(String first, String second);

	void notEnoughParameters(String argumentName, int myMinParameterCount);

	void failedToParseArgument(String argumentName);
}
