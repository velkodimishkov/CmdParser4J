// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package com.codezeal.commandline;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
public class CmdParser4JTest {

	private static final String VAR_OPT_SOME_FOLDER_FOO = "/var/opt/some folder/foo/";
	private static final String OTHER_PATH = "/other/path/";

	@Test
	public void testParse() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-m").asString(2);
		assertTrue(p.parse("-m", VAR_OPT_SOME_FOLDER_FOO, OTHER_PATH));
		assertEquals(2, p.getAvailableStringParameterCount("-m"));
		assertEquals(VAR_OPT_SOME_FOLDER_FOO, p.getString("-m"));
		assertEquals(OTHER_PATH, p.getString("-m", 1));

		assertEquals(null, p.getString("-multi", 2));
	}

	@Test
	public void testSpecifiedMultipleTimes() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-q").asString(1).withAlias("-Q");
		assertFalse(p.parse("-q", "Foo", "-Q", "Bar"));
		String s = msg.getParseResult();
		assertTrue(s.contains("The argument '-q' is specified multiple times."));
	}

	@Test
	public void testMissingMandatory() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-q").asString(1).setMandatory();
		p.accept("-Q").asString(1);
		assertFalse(p.parse("-Q", "Bar"));
		assertTrue(msg.getParseResult().contains("The mandatory argument"));
	}

	@Test
	public void testLeftOvers() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-Q").asString(1);
		assertFalse(p.parse("-Q", "Bar", "some", "extra"));
		assertTrue(msg.getParseResult().contains("Unknown arguments on the command line"));
	}

	@Test
	public void testBoolean() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		p.accept("/bar").withAlias("/foo").asBoolean(1);
		assertTrue(p.parse("/b", "true", "1", "0", "false", "/foo", "false"));
		assertTrue(p.getBool("/b", 0));
		assertTrue(p.getBool("/b", 1));
		assertFalse(p.getBool("/b", 2));
		assertFalse(p.getBool("/b", 3));
		assertFalse(p.getBool("/bar", 0));
	}

	@Test
	public void testSingleBoolean() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("foo").asSingleBoolean();
		p.accept("bar").asSingleBoolean();
		assertTrue(p.parse("foo", "bar"));
		assertTrue(p.getBool("foo", 0));
		assertTrue(p.getBool("bar", 0));
		assertFalse(p.getBool("XXX", 0));
	}

	@Test
	public void testFailedParse() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b", "true", "1", "Nope", "false"));
		assertTrue(msg.getParseResult().contains("Parsing of argument"));
	}

	@Test
	public void testMissingParameters() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b", "true", "1", "Nope"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument"));
	}

	@Test
	public void testMissingParameters2() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument"));
	}

	@Test
	public void testNoInputWithMandatory() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(1).setMandatory();
		assertFalse(p.parse(""));
		assertTrue(msg.getParseResult().contains("The mandatory argument"));
	}

	@Test
	public void testDescription() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("single").asSingleBoolean().describedAs("AAA BBBBB CCCCCCCCCCC DDDDDDDDDDDDDDE EEEEEEEEEEEEEEEE FFFFFFFFFFF GGGGGGGGGGGGGGG HHHHHHHHHHHH");
		p.accept("/bool").asBoolean(1).withAlias("/B", "-B", "-b").describedAs("A Boolean value").setMandatory();
		p.accept("/string").asString(1).describedAs("A string argument");
		p.accept("/goo").asBoolean(1).setMandatory().describedAs("Something something");
		p.accept("/aaa").asString(1).describedAs("Jada Jada Jada");
		p.accept("/bbb").asString(1, Constructor.NO_PARAMETER_LIMIT).describedAs("A long non descriptive description without any meaning what so ever");
		assertTrue(p.parse("/bool", "1", "single", "/goo", "true", "/aaa", "AAA", "/bbb", "123", "456", "789"));
		assertEquals(true, p.getBool("/bool"));
		assertEquals(true, p.getBool("/goo"));
		assertEquals(true, p.getBool("single", 0));
		assertEquals("AAA", p.getString("/aaa", 0, "blah"));
		assertEquals("123", p.getString("/bbb", 0, "blah"));
		assertEquals("456", p.getString("/bbb", 1, "blah"));
		assertEquals("789", p.getString("/bbb", 2, "blah"));

		SystemOutputUsageFormatter usage = new SystemOutputUsageFormatter("application name");
		p.getUsage(usage);
		System.out.println(usage);
	}

	@Test
	public void testVariableParameterCount() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(1, Constructor.NO_PARAMETER_LIMIT);
		assertTrue(p.parse("/b", "1", "0", "1", "true", "false"));
		assertEquals(5, p.getAvailableBooleanParameterCount("/b"));
		assertTrue(p.getBool("/b", 0, false));
		assertFalse(p.getBool("/b", 1, true));
		assertTrue(p.getBool("/b", 2, false));
		assertTrue(p.getBool("/b", 3, false));
		assertFalse(p.getBool("/b", 4, true));
	}

	@Test
	public void testMultiArgumentInMiddle() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("single").asSingleBoolean().describedAs("AAA BBBBB CCCCCCCCCCC DDDDDDDDDDDDDDE EEEEEEEEEEEEEEEE FFFFFFFFFFF GGGGGGGGGGGGGGG HHHHHHHHHHHH");
		p.accept("/bool").asBoolean(1).withAlias("/B", "-B", "-b").describedAs("A Boolean value").setMandatory();
		p.accept("/string").asString(1).describedAs("A string argument");
		p.accept("/goo").asBoolean(1).setMandatory().describedAs("-gle?");
		p.accept("/aaa").asString(3).describedAs("Jada Jada Jada");
		p.accept("/bbb").asString(1, Constructor.NO_PARAMETER_LIMIT).describedAs("A long non descriptive description without any meaning what so ever");
		assertTrue(p.parse("/bool", "1", "single", "/goo", "true", "/bbb", "AAA", "BBB", "CCC", "/aaa", "123", "456", "789"));
		String s = msg.getParseResult();
		assertEquals("AAA", p.getString("/bbb", 0));
		assertEquals("BBB", p.getString("/bbb", 1));
		assertEquals("CCC", p.getString("/bbb", 2));
		assertEquals(null, p.getString("/bbb", 3));
	}

	@Test
	public void testNotEnoughParameters() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(2);
		p.accept("/c").asBoolean(1);
		assertFalse(p.parse("/b", "true", "/c", "false"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument /b, 2 wanted"));
	}

	@Test
	public void testNotEnoughParameters2() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/a").asBoolean(1);
		p.accept("/b").asBoolean(3);
		p.accept("/c").asBoolean(1);
		assertFalse(p.parse("/b", "true", "false", "/a", "true", "/c", "false"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument /b, 3 wanted"));
	}

	@Test
	public void testMultipleMulti() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/multi1").asBoolean(1, 3);
		p.accept("/multi2").asBoolean(1, 3);
		assertTrue(p.parse("/multi1", "1", "0", "/multi2", "0", "true"));
		assertEquals(true, p.getBool("/multi1", 0));
		assertEquals(false, p.getBool("/multi1", 1));
		assertEquals(false, p.getBool("/multi2", 0));
		assertEquals(true, p.getBool("/multi2", 1));
	}

	@Test
	public void testSameArgumentMultipleTimes() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/multi1").asBoolean(1, 3);
		p.accept("/multi2").asBoolean(1, 3);
		assertFalse(p.parse("/multi1", "1", "/multi1", "1"));
		String s = msg.getParseResult();
		assertTrue(s.contains("The argument '/multi1' is specified multiple times"));
	}

	@Test
	public void testDependsOnMissing() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").dependsOn("-second").asBoolean(1);
		p.accept("-second").asBoolean(1);
		assertFalse(p.parse("-first", "false"));
		assertTrue(msg.getParseResult().contains("Argument '-first' depends on '-second', but the latter is missing"));
	}

	@Test
	public void testDependsOnOk() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").dependsOn("-second").asBoolean(1);
		p.accept("-second").asBoolean(1);
		assertTrue(p.parse("-first", "false", "-second", "true"));
		assertEquals("", msg.getParseResult());
	}

	@Test
	public void testDependsTwoWay() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").dependsOn("-second").asBoolean(1);
		p.accept("-second").dependsOn("-first").asBoolean(1);
		assertTrue(p.parse("-first", "false", "-second", "true"));
		assertEquals("", msg.getParseResult());
	}

	@Test
	public void testDependsTwoWayFail() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").dependsOn("-second").asBoolean(1);
		p.accept("-second").dependsOn("-first").asBoolean(1);
		assertFalse(p.parse("-second", "true"));
		assertTrue(msg.getParseResult().contains("Argument '-second' depends on '-first', but the latter is missing"));
	}

	@Test
	public void testDependsOnProgrammingError() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").dependsOn("-second").asBoolean(1);
		assertFalse(p.parse("-first", "false"));
		assertTrue(msg.getParseResult().contains("Argument '-first' depends on '-second', but no such argument is defined - contact the author of the application"));
	}

	@Test
	public void testBlockedByOK() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").blockedBy("-second").asSingleBoolean();
		p.accept("-second").blockedBy("-first").asSingleBoolean();
		assertTrue(p.parse("-first"));
	}

	@Test
	public void testBlockedByFAIL() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").blockedBy("-second").asSingleBoolean();
		p.accept("-second").blockedBy("-first").asSingleBoolean();
		assertFalse(p.parse("-first", "-second"));
		assertTrue(msg.getParseResult().contains("mutually exclusive"));
		System.out.println(msg.getParseResult());
	}

	@Test
	public void testGitExample() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accept a single boolean parameter");
		p.accept("-multi").asString(1, 4).describedAs("An optional argument that accept one to four argument.");
		// The name of the argument, or any prefix characters, doesn't really matter, here we use double dash.
		p.accept("--otherArgument").withAlias("-o", "-O").asSingleBoolean().describedAs("An optional argument that takes no parameters");
		// Arguments with variable parameters are only accepted as the last argument on the commandline.
		assertTrue(p.parse("-argument", "true", "-O", "-multi", "parameter1", "parameter2", "parameter3"));
		// Verify the number of parameters that can be read for the different arguments.
		assertEquals(1, p.getAvailableBooleanParameterCount("--otherArgument"));
		assertEquals(3, p.getAvailableStringParameterCount("-multi"));
		assertEquals(1, p.getAvailableBooleanParameterCount("-argument"));

		// Read the values from the parser.
		assertEquals(true, p.getBool("-argument", 0));
		assertEquals("parameter1", p.getString("-multi", 0));
		assertEquals("parameter2", p.getString("-multi", 1));
		assertEquals("parameter3", p.getString("-multi", 2));
		assertEquals(null, p.getString("-multi", 4));

		SystemOutputUsageFormatter usage = new SystemOutputUsageFormatter("MyCmdString");
		p.getUsage(usage);

		System.out.println(usage);
	}

	@Test
	public void testGitExample2() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accept a single boolean parameter");
		p.accept("-multi").asString(1, 4).describedAs("An optional argument that accept one to four argument.");

		// Missing mandatory argument
		assertFalse(p.parse("-multi", "parameter1", "parameter2", "parameter3"));

		System.out.println(msg.getParseResult());
	}

	@Test
	public void testMissingArgumentTypee() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);

		p.accept("-first").describedAs("The first");
		assertFalse(p.parse("-first"));
		String s = msg.getParseResult();
		assert (s.contains("is missing type information"));
	}

	@Test
	public void testGarbageOnCommandLine() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);

		p.accept("-first").asSingleBoolean();
		assertFalse(p.parse("jada", "Jada"));
		String s = msg.getParseResult();
		assert (s.contains("jada, Jada"));
	}

	@Test
	public void testGarbageBeforeFirstCommand() throws Exception {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);

		p.accept("-first").asSingleBoolean();
		assertFalse(p.parse("jada", "-first"));
		String s = msg.getParseResult();
		assert (s.contains("jada"));
	}

}