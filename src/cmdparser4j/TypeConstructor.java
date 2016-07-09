// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.NumericLimit;
import cmdparser4j.limits.StringLengthLimit;
import cmdparser4j.limits.UnboundedIntegerLimit;
import cmdparser4j.limits.UnboundedStringLimit;

public class TypeConstructor {
	private final Argument myArg;
	private final CmdParser4J myParser;

	public TypeConstructor(Argument argument, CmdParser4J parser) {
		myArg = argument;
		myParser = parser;
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type boolean.
	 *
	 * @param parameterCount Number of parameters this argument requires
	 * @return The argument constructor
	 */
	public Constructor asBoolean(int parameterCount) {
		return asBoolean(parameterCount, parameterCount);
	}

	/**
	 * Specifies that the argument takes {@code minParameterCount} to {@code maxParameterCount} number of parameters
	 * of type boolean.
	 *
	 * @param minParameterCount Minimum number of parameters this argument requires
	 * @param maxParameterCount Maximum number of parameters this argument accepts
	 * @return The argument constructor
	 */
	public Constructor asBoolean(int minParameterCount, int maxParameterCount) {
		myArg.setType(new BooleanType(myParser, myArg, minParameterCount, maxParameterCount));
		return new Constructor(myArg);
	}

	/**
	 * Specifies that the argument takes {@code minParameterCount} to {@code maxParameterCount} number of parameters
	 * of type single boolean.
	 *
	 * @return The argument constructor
	 */
	public Constructor asSingleBoolean() {
		myArg.setType(new SingleBooleanType(myParser, myArg));
		return new Constructor(myArg);
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type string.
	 *
	 * @param parameterCount Number of parameters this argument requires
	 * @return The argument constructor
	 */
	public Constructor asString(int parameterCount) {
		return asString(parameterCount, parameterCount, new UnboundedStringLimit());
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type string.
	 *
	 * @param parameterCount Number of parameters this argument requires
	 * @param lengthLimit Min and maximum length, inclusive
	 * @return The argument constructor
	 */
	public Constructor asString(int parameterCount, StringLengthLimit lengthLimit) {
		return asString(parameterCount, parameterCount, lengthLimit);
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type string.
	 *
	 * @param minParameterCount Minimum number of parameters this argument requires
	 * @param maxParameterCount Maximum number of parameters this argument accepts
	 * @return The argument constructor
	 */
	public Constructor asString(int minParameterCount, int maxParameterCount) {
		return asString(minParameterCount, maxParameterCount, new UnboundedStringLimit());
	}

	/**
	 * Specifies that the argument takes {@code minParameterCount} to {@code maxParameterCount} number of parameters
	 * of type string.
	 *
	 * @param minParameterCount Minimum number of parameters this argument requires
	 * @param maxParameterCount Maximum number of parameters this argument accepts
	 * @param lengthLimit Min and maximum length, inclusive
	 * @return The argument constructor
	 */
	public Constructor asString(int minParameterCount, int maxParameterCount, StringLengthLimit lengthLimit) {
		myArg.setType(new StringType(myParser, myArg, minParameterCount, maxParameterCount, lengthLimit));
		return new Constructor(myArg);
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type string.
	 *
	 * @param parameterCount Number of parameters this argument requires
	 * @return The argument constructor
	 */
	public Constructor asInteger(int parameterCount) {
		return asInteger(parameterCount, parameterCount, new UnboundedIntegerLimit());
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type string.
	 *
	 * @param parameterCount Number of parameters this argument requires
	 * @param bounds Min and maximum values, inclusive
	 * @return The argument constructor
	 */
	public Constructor asInteger(int parameterCount, NumericLimit<Integer> bounds) {
		return asInteger(parameterCount, parameterCount, bounds);
	}

	/**
	 * Specifies that the argument takes {@code minParameterCount} to {@code maxParameterCount} number of parameters
	 * of type string.
	 *
	 * @param minParameterCount Minimum number of parameters this argument requires
	 * @param maxParameterCount Maximum number of parameters this argument accepts
	 * @param bounds Min and maximum values, inclusive
	 * @return The argument constructor
	 */
	public Constructor asInteger(int minParameterCount, int maxParameterCount, NumericLimit<Integer> bounds) {
		myArg.setType(new IntegerType(myParser, myArg, minParameterCount, maxParameterCount, bounds));
		return new Constructor(myArg);
	}
}
