package com.codezeal.commandline;

/**
 * Parameter type boolean
 * Created by Per Malmberg on 2015-12-05.
 */
public class BooleanType extends BaseType<Boolean> {
	private static final String ZERO = "0";
	private static final String ONE = "1";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	public BooleanType(CmdParser4J parser, Argument argument, int minParameterCount, int maxParameterCount) {
		super(parser, argument, minParameterCount, maxParameterCount);
	}

	@Override
	protected boolean doTypeParse(String parameter) {
		int count = myResults.size();

		if (parameter != null) {
			parameter = parameter.toLowerCase();
			if (parameter.equals(ZERO) || parameter.equals(ONE)) {
				myResults.add(parameter.equals("1"));
			} else if (parameter.equals(TRUE) || parameter.equals(FALSE)) {
				myResults.add(parameter.equals(TRUE));
			}
		}

		return count < myResults.size();
	}

	@Override
	void retrieveResult(CmdParser4J cmdParser) {
		cmdParser.setResult(myArgument.getPrimaryName(), this);
	}
}
