// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class Argument {
	private final ArrayList<String> myNames = new ArrayList<String>();
	private final ArrayList<String> myDependencies = new ArrayList<String>();
	private final ArrayList<String> myBlocks = new ArrayList<String>();
	private boolean myIsMandatory = false;
	private boolean myExistsOnCommandLine = false;
	private BaseType myType = null;
	private String myDescription = "";
	private IParseResult myResult;
	private boolean isHidden = false;


	public Argument(String argumentName, IParseResult result) {
		myNames.add(argumentName);
		myResult = result;
	}

	public boolean parse(List<String> args) {

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
	int findArgument(List<String> args, Changeable<Integer> hitCount) {
		int ix = -1;
		int count = 0;

		for (String myName : myNames) {
			for (String arg : args) {
				if (myName.equals(arg)) {
					if (ix == -1) {
						ix = args.indexOf(myName);
					}
					// Count number of hits
					++count;
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

	void setHidden() {
		isHidden = true;
	}

	boolean isHidden() {
		return isHidden;
	}

	String getPrimaryName() {
		return myNames.get(0);
	}

	List<String> getAliases() {
		return myNames.subList(myNames.size() - 1 >= 1 ? 1 : 0, myNames.size() - 1);
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

	boolean hasVariableParameterCount() {
		return myType.getMaxParameterCount() != myType.getMinimumParameterCount();
	}

	int getMaxArgumentCount() {
		return myType.getMaxParameterCount();
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
						myResult.noSuchMutuallyExclusiveArgumentDefined(getPrimaryName(), blocker);
						result = false;
					} else if (blockedBy.isSuccessFullyParsed()) {
						myResult.argumentsAreMutuallyExclusive(getPrimaryName(), blockedBy.getPrimaryName());
						result = false;
					}
				}
			}
		}

		return result;
	}

	public boolean hasArgumentType() {
		return myType != null;
	}
}
