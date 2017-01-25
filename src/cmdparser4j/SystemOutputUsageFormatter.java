// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import java.util.*;

public class SystemOutputUsageFormatter implements IUsageFormatter {

	private class DataContainer {
		private String commandName;
		private String aliases;
		private String parameters;
		private String description;
		private List<String> dependencies = new ArrayList<String>();

		public DataContainer(String primaryName, String aliases, String parameters, String description, List<String> dependencies) {
			this.commandName = primaryName;
			this.aliases = aliases;
			this.parameters = parameters;
			this.description = description;
			this.dependencies = dependencies;
		}

		public String getCommandName() {
			return commandName;
		}

		public String getAliases() {
			return aliases;
		}

		public String getParameters() {
			return parameters;
		}

		public String getDescription() {
			return description;
		}

		public List<String> getDependencies() {
			return dependencies;
		}
	}

	private HashMap<String, DataContainer> args = new HashMap<String, DataContainer>();

	private String strHeader;
	final private static int commandIndent = 2;
	final private static int descriptionIndent = 35;
	final private static int dependentIndent = 6;

	public SystemOutputUsageFormatter(String header) {
		strHeader = String.format("%s%n", header);
	}

	private String repeat(char c, int n) {
		return new String(new char[n]).replace('\0', c);
	}

	@Override
	public void prepareMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, List<String> dependencyNames, String description) {
		FormatArgument(primaryName, hasVariableParameterCount, maxArgumentCount, aliases, dependencyNames, description);
	}

	private void FormatArgument(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, List<String> dependencyNames, String description) {

		StringBuilder strBuild = new StringBuilder();

		// Setup aliases string
		String aliasStr = "";
		if (aliases.size() > 0) {
			boolean firstAlias = true;
			strBuild.setLength(0); // Reset StringBuilder

			strBuild.append(" (");
			for (String alias : aliases) {
				if (!firstAlias) {
					strBuild.append(", ");
				}
				firstAlias = false;
				strBuild.append(alias);
			}
			strBuild.append(")");
			aliasStr = strBuild.toString();
		}

		// Setup parameter string
		String paramStr;
		if (maxArgumentCount == Constructor.NO_PARAMETER_LIMIT) {
			paramStr = " <arg1> ... <argN>";
		} else if (hasVariableParameterCount) {
			paramStr = String.format(" <arg1> [... <arg%d>]", maxArgumentCount);
		} else {
			strBuild.setLength(0); // Reset StringBuilder
			for (int i = 0; i < maxArgumentCount; ++i) {
				strBuild.append(i == 0 ? "" : " " + " <arg" + (i + 1) + ">");
			}
			paramStr = strBuild.toString();
		}

		DataContainer dataContainer = new DataContainer(primaryName, aliasStr, paramStr, description, dependencyNames);
		args.put(primaryName, dataContainer);
	}

	@Override
	public void prepareNonMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, List<String> dependencyNames, String description) {
		FormatArgument(primaryName, hasVariableParameterCount, maxArgumentCount, aliases, dependencyNames, description);
	}

	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();

		strBuild.append(strHeader);

		SortedSet<String> keys = new TreeSet<String>(args.keySet());

		for (String key : keys) {
			DataContainer dc = args.get(key);
			strBuild.append(String.format("Command:%n"));

			strBuild.append(String.format("%s%s", repeat(' ', commandIndent), dc.getCommandName()));

			int charCounter = dc.getCommandName().length() + commandIndent;

			if (!dc.getAliases().isEmpty() && dc.getAliases() != null) {
				strBuild.append(String.format(" %s", dc.getAliases()));
				charCounter += dc.getAliases().length() + 1; // Add space
			}
			if (!dc.getParameters().isEmpty() && dc.getParameters() != null) {
				strBuild.append(String.format(" %s", dc.getParameters()));
				charCounter += dc.getParameters().length() + 1; // Add space
			}

			int tmpIndent = 0;
			for (int i = charCounter; i < descriptionIndent; i++) {
				tmpIndent += 1;
			}

			strBuild.append(String.format("%s%s%n", repeat(' ', tmpIndent), dc.getDescription()));

			if (dc.getDependencies().size() > 0) {
				strBuild.append(String.format("%s%s%n%s", repeat(' ', dependentIndent), "Dependencies:", repeat(' ', dependentIndent)));

				for (String dependency : dc.getDependencies()) {
					strBuild.append(dependency + ' ');
				}
				strBuild.append(String.format("%n"));
			}
		}

		return strBuild.toString();
	}
}
