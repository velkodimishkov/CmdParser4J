package com.codezeal.commandline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
class Argument {
	private final ArrayList<String> myNames = new ArrayList<String>();
	private final ArrayList<String> myDependencies = new ArrayList<String>();
	private final ArrayList<String> myBlocks = new ArrayList<String>();
	private boolean myIsMandatory = false;
	private boolean myExistsOnCommandLine = false;
	private BaseType myType = null;
	private String myDescription = "";
	private IParseResult myResult;


	public Argument(String argumentName, IParseResult result) {
		myNames.add(argumentName);
		myResult = result;
	}

	public boolean parse(ArrayList<String> args) {

		boolean result = true;

		// Can we find the argument?
		Changeable<Integer> hits = new Changeable<Integer>(0);
		int argumentIx = findArgument(args, hits);

		if (argumentIx >= 0) {
			// Argument found, parse it
			myExistsOnCommandLine = true;
			result = myType.parse(args, argumentIx);
		}

		return result;
	}

	/**
	 * Searches for this argument in the provided list.
	 */
	int findArgument(ArrayList<String> args, Changeable<Integer> hitCount) {
		int ix = -1;
		int count = 0;

		for (String myName : myNames) {
			for (String arg : args) {
				if (myName.equals(arg)) {
					if (ix == -1) {
						ix = args.indexOf(myName);
						++count;
					} else {
						// More than one hit
						++count;
					}
				}
			}
		}

		hitCount.set(count);

		return ix;
	}

	boolean isMandatory() {
		return myIsMandatory;
	}

	void setType(BaseType type) {
		myType = type;
	}

	void setMandatory() {
		myIsMandatory = true;
	}


	String getPrimaryName() {
		return myNames.get(0);
	}

	String getAliases() {
		StringBuilder sb = new StringBuilder();
		if (myNames.size() > 1) {
			sb.append("[");
			int count = 0;
			for (String arg : myNames.subList(1, myNames.size() - 1)) {
				if (count > 0) {
					sb.append("|");
				}
				++count;
				sb.append(arg);
			}
			sb.append("]");
		}
		return sb.toString();
	}

	void addAliases(String[] aliases) {
		Collections.addAll(myNames, aliases);
	}

	boolean isSuccessFullyParsed() {
		// It can only be successfully parsed if it actually exists on the command line.
		return myExistsOnCommandLine && myType.isSuccessFullyParsed();
	}

	String getDescription() {
		return myDescription;
	}

	void setDescription(String description) {
		myDescription = description;
	}

	String getUsage() {
		String s = String.format("%s", getPrimaryName());
		if (myType.getMaxParameterCount() == Constructor.NO_PARAMETER_LIMIT) {
			s += " <arg1> ... <argN>";
		} else {
			for (int i = 0; i < myType.getMaxParameterCount(); ++i) {
				s += (i == 0 ? "" : " ") + " <arg" + (i + 1) + ">";
			}
		}

		return s;
	}

	boolean hasVariableParameterCount() {
		return myType.getMaxParameterCount() != myType.getMinimumParameterCount();
	}

	void addDependency(String dependencyArgument) {
		myDependencies.add(dependencyArgument);
	}

	void addBlockedBy(String blockedByPrimaryName) {
		myBlocks.add(blockedByPrimaryName);
	}

	boolean checkDependencies(HashMap<String, Argument> arguments) {
		boolean result = true;

		// Only check if the current Argument has been parsed itself.
		if (isSuccessFullyParsed()) {
			for (String dep : myDependencies) {
				Argument dependsOn = arguments.get(dep);
				if (dependsOn == null) {
					// Can't find the argument, this is a programming error
					myResult.noSuchArgumentDefined(getPrimaryName(), dep);
					result = false;
				} else if (!dependsOn.isSuccessFullyParsed()) {
					myResult.missingDependentArgument(getPrimaryName(), dep);
					result = false;
				}
			}
		}

		return result;
	}

	boolean checkMutualExclusion(HashMap<String, Argument> argumentsToTestAgainst, List<String> alreadyTested) {
		boolean result = true;

		// Only check if the current Argument has been parsed itself.
		if (isSuccessFullyParsed()) {
			for (String blocker : myBlocks) {
				if (!alreadyTested.contains(blocker)) {
					Argument blockedBy = argumentsToTestAgainst.get(blocker);
					if (blockedBy == null) {
						// Can't find the argument, this is a programming error
						myResult.noSuchMutuallyExclusiveArgumentDefined( getPrimaryName(), blocker);
						result = false;
					} else if (blockedBy.isSuccessFullyParsed()) {
						myResult.argumentsAreMutuallyExclusive( getPrimaryName(), blockedBy.getPrimaryName());
						result = false;
					}
				}
			}
		}

		return result;
	}
}
