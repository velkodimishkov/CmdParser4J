// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.NumericLimit;
import cmdparser4j.limits.StringLengthLimit;

public class SystemOutputParseResult implements IParseResult {
	private StringBuilder sb = new StringBuilder();

	@Override
	public void unknownArguments(String unknownArguments) {
		appendLine("Unknown arguments on the command line: %s", unknownArguments);
	}

	@Override
	public void argumentSpecifiedMultipleTimes(String primaryName) {
		appendLine("The argument '%s' is specified multiple times.", primaryName);
	}

	@Override
	public void missingMandatoryArgument(String primaryName) {
		appendLine("The mandatory argument '%s' is missing", primaryName);
	}

	@Override
	public String getParseResult() {
		return sb.toString();
	}

	@Override
	public void noSuchArgumentDefined(String argument, String dependsOn) {
		appendLine("Argument '%s' depends on '%s', but no such argument is defined - contact the author of the application", argument, dependsOn);
	}

	@Override
	public void missingDependentArgument(String primaryName, String dependsOn) {
		appendLine("Argument '%s' depends on '%s', but the latter is missing", primaryName, dependsOn);
	}

	@Override
	public void noSuchMutuallyExclusiveArgumentDefined(String primaryName, String blocker) {
		appendLine("Argument '%s' is mutually exclusive to '%s', but no such argument is defined - contact the author of the application", primaryName, blocker);
	}

	@Override
	public void argumentsAreMutuallyExclusive(String first, String second) {
		appendLine("Arguments '%s' and '%s' are mutually exclusive.", first, second);
	}

	@Override
	public void notEnoughParameters(String argumentName, int myMinParameterCount) {
		appendLine("There are not enough parameters for the argument %s, %d wanted", argumentName, myMinParameterCount);
	}

	@Override
	public void failedToParseArgument(String argumentName) {
		appendLine("Parsing of argument '%s' failed", argumentName);
	}

	@Override
	public void argumentMissingType(String primaryName) {
		appendLine("'" + primaryName + "' is missing type information. This is a programming error - contact the author of the application");
	}

	@Override
	public void failedToLoadConfiguration(String fileNameArgument)
	{
		appendLine("Could not load the configuration specified by argument '" + fileNameArgument + "'");
	}

	@Override
	public void outsideLimits(String primaryName, NumericLimit limit) {
		appendLine("Parameter for argument '" + primaryName + "' is outside allowed limits of " + limit.getLower().toString() + " - " + limit.getUpper().toString() );
	}

	@Override
	public void outsideLimits(String primaryName, StringLengthLimit limit) {
		appendLine("Parameter for argument '" + primaryName + "' is outside allowed lengths of " + limit.getLower().toString() + " - " + limit.getUpper().toString() );
	}

	@Override
	public void exception(Exception e) {
		// We don't log these at all as we give more user-friendly messages via other logs.
	}

	void appendLine(String format, Object... arguments) {
		String msg = String.format(format, arguments);
		sb.append(String.format("%s%n", msg)); // %n => newline
	}
}
