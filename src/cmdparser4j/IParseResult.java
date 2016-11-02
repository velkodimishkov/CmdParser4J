// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.NumericLimit;
import cmdparser4j.limits.StringLengthLimit;

/**
 * Interface used by the parser to notify the application of parse errors/events.
 * Created by Per Malmberg on 2016-01-30. *
 */
public interface IParseResult {
	void unknownArguments(String unknownArguments);

	void argumentSpecifiedMultipleTimes(String primaryName);

	void missingMandatoryArgument(String primaryName);

	String getParseResult();

	void noSuchArgumentDefined(String argument, String dependsOn);

	void missingDependentArgument(String primaryName, String dependsOn);

	void noSuchMutuallyExclusiveArgumentDefined(String primaryName, String blocker);

	void argumentsAreMutuallyExclusive(String first, String second);

	void notEnoughParameters(String argumentName, int myMinParameterCount);

	void failedToParseArgument(String argumentName);

	void argumentMissingType(String primaryName);

	void failedToLoadConfiguration(String fileNameArgument);

	void outsideLimits(String primaryName, NumericLimit tNumericLimit);

	void outsideLimits(String primaryName, StringLengthLimit tNumericLimit);

	void exception( Exception e );
}
