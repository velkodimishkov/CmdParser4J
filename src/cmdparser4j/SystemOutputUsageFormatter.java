// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import java.util.List;

public class SystemOutputUsageFormatter implements IUsageFormatter {
	private StringBuilder sb = new StringBuilder();
	private int indent;

	public SystemOutputUsageFormatter(String header) {
		sb.append(header);
		indent = header.length();
	}

	private String repeat(char c, int n) {
		return new String(new char[n]).replace('\0', c);
	}


	@Override
	public void prepareMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, String description) {
		FormatArgument(primaryName, hasVariableParameterCount, maxArgumentCount, aliases, description);
	}

	private void FormatArgument(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, String description) {
		sb.append(String.format("%n%s%s", repeat(' ', indent), primaryName));

		if (aliases.size() > 0) {
			boolean firstAlias = true;

			sb.append(" (");
			for (String alias : aliases) {
				if (!firstAlias) {
					sb.append(", ");
				}
				firstAlias = false;
				sb.append(alias);
			}
			sb.append(")");
		}

		if (maxArgumentCount == Constructor.NO_PARAMETER_LIMIT) {
			sb.append(" <arg1> ... <argN>");
		} else if (hasVariableParameterCount) {
			sb.append(String.format(" <arg1> [... <arg%d>]", maxArgumentCount));
		} else {
			for (int i = 0; i < maxArgumentCount; ++i) {
				sb.append(i == 0 ? "" : " " + " <arg" + (i + 1) + ">");
			}
		}

		sb.append(String.format("%n%s%s", repeat(' ', indent * 2), description));
	}

	@Override
	public void prepareNonMandatory(String primaryName, boolean hasVariableParameterCount, int maxArgumentCount, List<String> aliases, String description) {
		FormatArgument(primaryName, hasVariableParameterCount, maxArgumentCount, aliases, description);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
