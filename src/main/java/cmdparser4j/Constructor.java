// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

public class Constructor {
	private final Argument myArg;
	public static final int NO_PARAMETER_LIMIT = Integer.MAX_VALUE;

	public Constructor(Argument argument) {
		myArg = argument;
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

	/**
	 * Sets the description for the parameter
	 * @param description The description
	 * @return The argument constructor
	 */
	public Constructor describedAs(String description) {
		myArg.setDescription(description);
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

	/**
	 * Hides the argument from usage output.
	 * @return The argument constructor
	 */
	public Constructor setHidden(){
		myArg.setHidden();
		return this;
	}

	public void setHelpCommand() {
		myArg.setHelpCommand();
	}
}
