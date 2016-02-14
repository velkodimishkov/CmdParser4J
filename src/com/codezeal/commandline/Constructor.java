// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
public class Constructor {
	private final Argument myArg;
	private final CmdParser4J myParser;
	public static final int NO_PARAMETER_LIMIT = Integer.MAX_VALUE;

	public Constructor(Argument argument, CmdParser4J parser) {
		myArg = argument;
		myParser = parser;
	}

	/**
	 * Specifies that the argument is mandatory
	 *
	 * @return The argument constructor
	 */
	public Constructor setMandatory() {
		myArg.setMandatory();
		return this;
	}

	/**
	 * Specifies aliases for the argument
	 *
	 * @param aliases The aliases
	 * @return THe argument constructor
	 */
	public Constructor withAlias(String... aliases) {
		myArg.addAliases(aliases);
		return this;
	}

	public Constructor describedAs(String description) {
		myArg.setDescription(description);
		return this;
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
		return this;
	}

	/**
	 * Specifies that the argument takes {@code minParameterCount} to {@code maxParameterCount} number of parameters
	 * of type single boolean.
	 *
	 * @return The argument constructor
	 */
	public Constructor asSingleBoolean() {
		myArg.setType(new SingleBooleanType(myParser, myArg));
		return this;
	}

	/**
	 * Specifies that the argument takes {@code parameterCount} number of parameters
	 * of type string.
	 *
	 * @param parameterCount Number of parameters this argument requires
	 * @return The argument constructor
	 */
	public Constructor asString(int parameterCount) {
		return asString(parameterCount, parameterCount);
	}

	/**
	 * Specifies that the argument takes {@code minParameterCount} to {@code maxParameterCount} number of parameters
	 * of type string.
	 *
	 * @param minParameterCount Minimum number of parameters this argument requires
	 * @param maxParameterCount Maximum number of parameters this argument accepts
	 * @return The argument constructor
	 */
	public Constructor asString(int minParameterCount, int maxParameterCount) {
		myArg.setType(new StringType(myParser, myArg, minParameterCount, maxParameterCount));
		return this;
	}

	/**
	 * Specifies a dependency on another argument
	 *
	 * @param dependencyPrimaryName The primary name of another argument.
	 * @return The argument constructor
	 */
	public Constructor dependsOn(String dependencyPrimaryName) {
		myArg.addDependency(dependencyPrimaryName);
		return this;
	}

	/**
	 * Specifies that this argument is blocked by another one, i.e.they are mutually exclusive.
	 * @param blockedByPrimaryName The primary name of the parameter that blocks this parameter.
	 * @return The argument constructor
	 */
	public Constructor blockedBy( String blockedByPrimaryName ) {
		myArg.addBlockedBy( blockedByPrimaryName );
		return this;
	}

	public Constructor setHidden(){
		myArg.setHidden();
		return this;
	}
}
