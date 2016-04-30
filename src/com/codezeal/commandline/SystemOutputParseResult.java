// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

/**
 * Created by Per Malmberg on 2016-01-30.
 */
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
	public void ArgumentMissingType(String primaryName) {
		appendLine("'" + primaryName + "' is missing type information. This is a programming error - contact the author of the application");
	}

	void appendLine(String format, Object... arguments) {
		String msg = String.format(format, arguments);
		sb.append(String.format("%s%n", msg)); // %n => newline
	}
}
