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
		CmdParser4J p = new CmdParser4J();
		p.accept("-m").asString(2);
		assertTrue(p.parse("-m", VAR_OPT_SOME_FOLDER_FOO, OTHER_PATH));
		assertEquals(2, p.getAvailableStringParameterCount("-m"));
		assertEquals(VAR_OPT_SOME_FOLDER_FOO, p.getString("-m"));
		assertEquals(OTHER_PATH, p.getString("-m", 1));

		assertEquals(null, p.getString("-multi", 2));
	}

	@Test
	public void testSpecifiedMultipleTimes() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("-q").asString(1).withAlias("-Q");
		assertFalse(p.parse("-q", "Foo", "-Q", "Bar"));
		String s = p.getParseResult();
		assertTrue(s.contains("The argument '-q' is specified multiple times."));
	}

	@Test
	public void testMissingMandatory() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("-q").asString(1).setMandatory();
		p.accept("-Q").asString(1);
		assertFalse(p.parse("-Q", "Bar"));
		assertTrue(p.getParseResult().contains("The mandatory argument"));
	}

	@Test
	public void testLeftOvers() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("-Q").asString(1);
		assertFalse(p.parse("-Q", "Bar", "some", "extra"));
		assertTrue(p.getParseResult().contains("Unknown arguments on the command line"));
	}

	@Test
	public void testBoolean() throws Exception {
		CmdParser4J p = new CmdParser4J();
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
		CmdParser4J p = new CmdParser4J();
		p.accept("foo").asSingleBoolean();
		p.accept("bar").asSingleBoolean();
		assertTrue(p.parse("foo", "bar"));
		assertTrue(p.getBool("foo", 0));
		assertTrue(p.getBool("bar", 0));
		assertFalse(p.getBool("XXX", 0));
	}

	@Test
	public void testFailedParse() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b", "true", "1", "Nope", "false"));
		assertTrue(p.getParseResult().contains("Parsing of argument"));
	}

	@Test
	public void testMissingParameters() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b", "true", "1", "Nope"));
		assertTrue(p.getParseResult().contains("There are not enough parameters for the argument"));
	}

	@Test
	public void testMissingParameters2() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b"));
		assertTrue(p.getParseResult().contains("There are not enough parameters for the argument"));
	}

	@Test
	public void testNoInputWithMandatory() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("/b").asBoolean(1).setMandatory();
		assertFalse(p.parse(""));
		assertTrue(p.getParseResult().contains("The mandatory argument"));
	}

	@Test
	public void testDescription() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("single").asSingleBoolean().describedAs("AAA BBBBB CCCCCCCCCCC DDDDDDDDDDDDDDE EEEEEEEEEEEEEEEE FFFFFFFFFFF GGGGGGGGGGGGGGG HHHHHHHHHHHH");
		p.accept("/bool").asBoolean(1).withAlias("/B", "-B", "-b").describedAs("A Boolean value").setMandatory();
		p.accept("/string").asString(1).describedAs("A string argument");
		p.accept("/goo").asBoolean(1).setMandatory().describedAs("-gle?");
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
		String s = p.getUsage("application name");
		System.out.println(s);
	}

	@Test
	public void testVariableParameterCount() throws Exception {
		CmdParser4J p = new CmdParser4J();
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
	public void testMultiArgumentAtEnd() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("single").asSingleBoolean().describedAs("AAA BBBBB CCCCCCCCCCC DDDDDDDDDDDDDDE EEEEEEEEEEEEEEEE FFFFFFFFFFF GGGGGGGGGGGGGGG HHHHHHHHHHHH");
		p.accept("/bool").asBoolean(1).withAlias("/B", "-B", "-b").describedAs("A Boolean value").setMandatory();
		p.accept("/string").asString(1).describedAs("A string argument");
		p.accept("/goo").asBoolean(1).setMandatory().describedAs("-gle?");
		p.accept("/aaa").asString(1).describedAs("Jada Jada Jada");
		p.accept("/bbb").asString(1, Constructor.NO_PARAMETER_LIMIT).describedAs("A long non descriptive description without any meaning what so ever");
		assertFalse(p.parse("/bool", "1", "single", "/goo", "true", "/bbb", "AAA", "/aaa", "123", "456", "789"));
		assertTrue(p.getParseResult().contains("An argument that allows variable number of parameters must be places last on the command line"));
	}

	@Test
	public void testMultipleMulti() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("/multi1").asBoolean(1, 3);
		p.accept("/multi2").asBoolean(1, 3);
		assertFalse(p.parse("/multi1", "1", "/multi2", "1"));
		assertTrue(p.getParseResult().contains("Multiple arguments which allows for variable parameter count are specified on the command line"));
	}

	@Test
	public void testSameArgumentMultipleTimes() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("/multi1").asBoolean(1, 3);
		p.accept("/multi2").asBoolean(1, 3);
		assertFalse(p.parse("/multi1", "1", "/multi1", "1"));
		String s = p.getParseResult();
		assertTrue(s.contains("The argument '/multi1' is specified multiple times"));
	}

	@Test
	public void testGitExample() throws Exception {
		CmdParser4J p = new CmdParser4J();
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

		System.out.println(p.getUsage("myApp"));
	}

	@Test
	public void testGitExample2() throws Exception {
		CmdParser4J p = new CmdParser4J();
		p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accept a single boolean parameter");
		p.accept("-multi").asString(1, 4).describedAs("An optional argument that accept one to four argument.");

		// Missing mandatory argument
		assertFalse(p.parse("-multi", "parameter1", "parameter2", "parameter3"));

		System.out.println(p.getParseResult());
	}

}