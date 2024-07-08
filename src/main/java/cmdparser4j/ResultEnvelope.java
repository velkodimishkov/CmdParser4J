// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.envelopes.IIntegerTypeEnvelope;
import cmdparser4j.envelopes.IStringTypeEnvelope;
import cmdparser4j.envelopes.IBooleanTypeEnvelope;

import java.util.HashMap;

public class ResultEnvelope implements IStringTypeEnvelope, IBooleanTypeEnvelope, IIntegerTypeEnvelope {
	private final HashMap<String, StringType> myStringResults = new HashMap<String, StringType>();
	private final HashMap<String, BooleanType> myBooleanResults = new HashMap<String, BooleanType>();
	private final HashMap<String, IntegerType> myIntegerResults = new HashMap<String, IntegerType>();

	private <ArgumentType extends BaseType, ValueType> ValueType getValue(String primaryName, int index, ValueType defaultValue, HashMap<String, ArgumentType> map) {
		ValueType res = defaultValue;
		ArgumentType a = map.get(primaryName);

		if (a != null) {
			res = (ValueType) a.getResult(index, defaultValue);
		}
		return res;
	}

	public int getAvailableStringParameterCount(String primaryName) {
		return getAvailableParameterCount(primaryName, myStringResults);
	}

	public int getAvailableBooleanParameterCount(String primaryName) {
		return getAvailableParameterCount(primaryName, myBooleanResults);
	}

	public int getAvailableIntegerParameterCount(String primaryName) {
		return getAvailableParameterCount(primaryName, myIntegerResults);
	}

	private int getAvailableParameterCount(String primaryName, HashMap<String, ? extends BaseType> map) {
		int res = 0;
		BaseType t = map.get(primaryName);

		if (t != null) {
			res = t.getAvailableParameterCount();
		}

		return res;
	}


	@Override
	public void add(String primaryName, StringType type) {
		myStringResults.put(primaryName, type);
	}

	@Override
	public String get(String primaryName, int index, String defaultValue) {
		return getValue(primaryName, index, defaultValue, myStringResults);
	}

	@Override
	public void add(String primaryName, BooleanType type) {
		myBooleanResults.put(primaryName, type);
	}

	@Override
	public boolean get(String primaryName, int index, boolean defaultValue) {
		return getValue(primaryName, index, defaultValue, myBooleanResults);
	}

	@Override
	public void add(String primaryName, IntegerType type) {
		myIntegerResults.put(primaryName, type);
	}

	@Override
	public int get(String primaryName, int index, int defaultValue) {
		return getValue(primaryName, index, defaultValue, myIntegerResults);
	}
}
