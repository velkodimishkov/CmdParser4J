// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import cmdparser4j.limits.NumericLimit;
import cmdparser4j.limits.StringLengthLimit;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class CmdParser4JTest {

	@Test
	public void testParse() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-m").asString(2);
		p.accept("-b").asBoolean(1);

		assertTrue(p.parse("-m", "", "one", "two", "-b", "1"));
		assertEquals(2, p.getAvailableStringParameterCount("-m"));
		assertEquals(1, p.getAvailableBooleanParameterCount("-b"));
		assertEquals("one", p.getString("-m"));
		assertEquals("two", p.getString("-m", 1));
		assertTrue(p.getBool("-b"));

		assertEquals(null, p.getString("-multi", 2));
	}

	@Test
	public void testWrongType() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-b").asSingleBoolean();

		assertTrue(p.parse("-b"));
		assertTrue(p.getBool("-b"));
		assertEquals("default", p.getString("-b", 0, "default"));
	}

	@Test
	public void testSpecifiedMultipleTimes() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-q").asString(1).withAlias("-Q");
		assertFalse(p.parse("-q", "Foo", "-Q", "Bar"));
		String s = msg.getParseResult();
		assertTrue(s.contains("The argument '-q' is specified multiple times."));
	}

	@Test
	public void testMissingMandatory() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-q").asString(1).setMandatory();
		p.accept("-Q").asString(1);
		assertFalse(p.parse("-Q", "Bar"));
		assertTrue(msg.getParseResult().contains("The mandatory argument"));
	}

	@Test
	public void testLeftOvers() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-Q").asString(1);
		assertFalse(p.parse("-Q", "Bar", "some", "extra"));
		assertTrue(msg.getParseResult().contains("Unknown arguments on the command line"));
	}

	@Test
	public void testBoolean() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		p.accept("/bar").asBoolean(1).withAlias("/foo");
		assertTrue(p.parse("/b", "true", "1", "0", "false", "/foo", "false"));
		assertTrue(p.getBool("/b", 0));
		assertTrue(p.getBool("/b", 1));
		assertFalse(p.getBool("/b", 2));
		assertFalse(p.getBool("/b", 3));
		assertFalse(p.getBool("/bar", 0));
	}

	@Test
	public void testSingleBoolean() {
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
	public void testInteger() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-a").asInteger(1);
		p.accept("-b").asInteger(2);
		assertTrue(p.parse("-a", "5", "-b", "1", "2"));
		assertTrue(p.getAvailableIntegerParameterCount("-b") == 2);
		assertEquals(8, p.getInteger("-a") + p.getInteger("-b", 0) + p.getInteger("-b", 1, 1000) + p.getInteger("-b", 10, 0));
	}

	@Test
	public void testIntegerInvalidData() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-a").asInteger(1);
		assertFalse(p.parse("-a", "AA"));
	}

	@Test
	public void testFailedParse() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b", "true", "1", "Nope", "false"));
		assertTrue(msg.getParseResult().contains("Parsing of argument"));
	}

	@Test
	public void testMissingParameters() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b", "true", "1", "Nope"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument"));
	}

	@Test
	public void testMissingParameters2() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(4);
		assertFalse(p.parse("/b"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument"));
	}

	@Test
	public void testNoInputWithMandatory() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(1).setMandatory();
		assertFalse(p.parse(""));
		assertTrue(msg.getParseResult().contains("The mandatory argument"));
	}

	@Test
	public void testDescription() {
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

		assertTrue( usage.toString().contains("/B") );
		assertTrue( usage.toString().contains("-B") );
		assertTrue( usage.toString().contains("-b") );
	}

	@Test
	public void testVariableParameterCount() {
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
	public void testMultiArgumentInMiddle() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("single").asSingleBoolean().describedAs("AAA BBBBB CCCCCCCCCCC DDDDDDDDDDDDDDE EEEEEEEEEEEEEEEE FFFFFFFFFFF GGGGGGGGGGGGGGG HHHHHHHHHHHH");
		p.accept("/bool").asBoolean(1).withAlias("/B", "-B", "-b").describedAs("A Boolean value").setMandatory();
		p.accept("/string").asString(1).describedAs("A string argument");
		p.accept("/goo").asBoolean(1).setMandatory().describedAs("-gle?");
		p.accept("/aaa").asString(3).describedAs("Jada Jada Jada");
		p.accept("/bbb").asString(1, Constructor.NO_PARAMETER_LIMIT).describedAs("A long non descriptive description without any meaning what so ever");
		assertTrue(p.parse("/bool", "1", "single", "/goo", "true", "/bbb", "AAA", "BBB", "CCC", "/aaa", "123", "456", "789"));
		assertEquals("AAA", p.getString("/bbb", 0));
		assertEquals("BBB", p.getString("/bbb", 1));
		assertEquals("CCC", p.getString("/bbb", 2));
		assertEquals(null, p.getString("/bbb", 3));
	}

	@Test
	public void testNotEnoughParameters() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/b").asBoolean(2);
		p.accept("/c").asBoolean(1);
		assertFalse(p.parse("/b", "true", "/c", "false"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument /b, 2 wanted"));
	}

	@Test
	public void testNotEnoughParameters2() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/a").asBoolean(1);
		p.accept("/b").asBoolean(3);
		p.accept("/c").asBoolean(1);
		assertFalse(p.parse("/b", "true", "false", "/a", "true", "/c", "false"));
		assertTrue(msg.getParseResult().contains("There are not enough parameters for the argument /b, 3 wanted"));
	}

	@Test
	public void testMultipleMulti() {
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
	public void testSameArgumentMultipleTimes() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("/multi1").asBoolean(1, 3);
		p.accept("/multi2").asBoolean(1, 3);
		assertFalse(p.parse("/multi1", "1", "/multi1", "1"));
		String s = msg.getParseResult();
		assertTrue(s.contains("The argument '/multi1' is specified multiple times"));
	}

	@Test
	public void testDependsOnMissing() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asBoolean(1).dependsOn("-second");
		p.accept("-second").asBoolean(1);
		assertFalse(p.parse("-first", "false"));
		assertTrue(msg.getParseResult().contains("Argument '-first' depends on '-second', but the latter is missing"));
	}

	@Test
	public void testDependsOnOk() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asBoolean(1).dependsOn("-second");
		p.accept("-second").asBoolean(1);
		assertTrue(p.parse("-first", "false", "-second", "true"));
		assertEquals("", msg.getParseResult());
	}

	@Test
	public void testDependsTwoWay() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asBoolean(1).dependsOn("-second");
		p.accept("-second").asBoolean(1).dependsOn("-first");
		assertTrue(p.parse("-first", "false", "-second", "true"));
		assertEquals("", msg.getParseResult());
	}

	@Test
	public void testDependsTwoWayFail() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asBoolean(1).dependsOn("-second");
		p.accept("-second").asBoolean(1).dependsOn("-first");
		assertFalse(p.parse("-second", "true"));
		assertTrue(msg.getParseResult().contains("Argument '-second' depends on '-first', but the latter is missing"));
	}

	@Test
	public void testDependsOnProgrammingError() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asBoolean(1).dependsOn("-second");
		assertFalse(p.parse("-first", "false"));
		assertTrue(msg.getParseResult().contains("Argument '-first' depends on '-second', but no such argument is defined - contact the author of the application"));
	}

	@Test
	public void testBlockedByOK() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asSingleBoolean().blockedBy("-second");
		p.accept("-second").asSingleBoolean().blockedBy("-first");
		assertTrue(p.parse("-first"));
	}

	@Test
	public void testBlockedByFAIL() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asSingleBoolean().blockedBy("-second");
		p.accept("-second").asSingleBoolean().blockedBy("-first");
		assertFalse(p.parse("-first", "-second"));
		assertTrue(msg.getParseResult().contains("mutually exclusive"));
	}

	@Test
	public void testBlockedByNoSuchDefined() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asBoolean(0).blockedBy("-doesnotexist");
		p.accept("-second").asBoolean(0).blockedBy("-first");
		assertFalse(p.parse("-first", "-second"));
		String error = msg.getParseResult();
		assertTrue(error.contains("doesnotexist"));
	}

	@Test
	public void testHiddenArgument() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("first").asSingleBoolean().setHidden();
		SystemOutputUsageFormatter usage = new SystemOutputUsageFormatter("test");
		p.getUsage(usage);
		assertFalse(usage.toString().contains("first"));
	}

	@Test
	public void testGitExample() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accept a single boolean parameter");
		p.accept("-multi").asString(1, 4).describedAs("An optional argument that accept one to four argument.");
		// The name of the argument, or any prefix characters, doesn't really matter, here we use double dash.
		p.accept("--otherArgument").asSingleBoolean().withAlias("-o", "-O").describedAs("An optional argument that takes no parameters");
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

		assertTrue( usage.toString().contains("-multi  <arg1> [... <arg4>]"));
	}

	@Test
	public void testGitExample2() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accept a single boolean parameter");
		p.accept("-multi").asString(1, 4).describedAs("An optional argument that accept one to four argument.");

		// Missing mandatory argument
		assertFalse(p.parse("-multi", "parameter1", "parameter2", "parameter3"));

		System.out.println(msg.getParseResult());
	}

	@Test
	public void testMissingArgumentTypee() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);

		p.accept("-first");
		assertFalse(p.parse("-first"));
		String s = msg.getParseResult();
		assertTrue (s.contains("is missing type information"));
	}

	@Test
	public void testGarbageOnCommandLine() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);

		p.accept("-first").asSingleBoolean();
		assertFalse(p.parse("jada", "Jada"));
		String s = msg.getParseResult();
		assertTrue (s.contains("jada, Jada"));
	}

	@Test
	public void testGarbageBeforeFirstCommand() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);

		p.accept("-first").asSingleBoolean();
		assertFalse(p.parse("jada", "-first"));
		String s = msg.getParseResult();
		assertTrue (s.contains("jada"));
	}

	@Test
	public void testReadConfigFromConfiguration_textformat() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);

		String cfgStr = "<Settings>" +
				"<First><![CDATA[40]]></First>" +
				"<First>41</First>" +
				"<First><![CDATA[42]]></First>" +
				"</Settings>";

		XMLConfigurationReader cfg = new XMLConfigurationReader(cfgStr, msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First"));

		assertTrue(p.parse(cfg));

		assertTrue(p.getInteger("-first") == 40);
		assertTrue(p.getInteger("-first", 1) == 41);
		assertTrue(p.getInteger("-first", 2) == 42);
	}

	@Test
	public void testReadConfigFromConfiguration_read_attribute_via_name_value_pair() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);

		String cfgStr = "<Settings>" +
				"<First Key=\"KeyName\" Value=\"1234\"/>" +
				"<First Key=\"KeyName\" Value=\"5678\"/>" +
				"<First Key=\"KeyName\" Value=\"9012\"/>" +
				"</Settings>";

		XMLConfigurationReader cfg = new XMLConfigurationReader(cfgStr, msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First", "Value", "Key", "KeyName"));

		assertTrue(p.parse(cfg));

		assertTrue(p.getInteger("-first") == 1234);
		assertTrue(p.getInteger("-first", 1) == 5678);
		assertTrue(p.getInteger("-first", 2) == 9012);
	}

	@Test
	public void testReadConfigFromConfiguration_read_attribute_don_not_care_about_matching_name_value_pair() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);

		String cfgStr = "<Settings>" +
				"<First Key=\"KeyName\" Value=\"70\"/>" +
				"<First Key=\"KeyName\" Value=\"80\"/>" +
				"<First Key=\"KeyName\" Value=\"90\"/>" +
				"</Settings>";

		XMLConfigurationReader cfg = new XMLConfigurationReader(cfgStr, msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First", "Value"));

		assertTrue(p.parse(cfg));

		assertTrue(p.getInteger("-first") == 70);
		assertTrue(p.getInteger("-first", 1) == 80);
		assertTrue(p.getInteger("-first", 2) == 90);
	}

	@Test
	public void testReadConfigFromConfiguration_read_text_data_via_name_value_pair() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);

		String cfgStr = "<Settings>" +
				"<First Key=\"KeyName\">100</First>" +
				"<First Key=\"KeyName\">200</First>" +
				"<First Key=\"KeyName\">300</First>" +
				"</Settings>";

		XMLConfigurationReader cfg = new XMLConfigurationReader(cfgStr, msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First", "Key", "KeyName"));

		assertTrue(p.parse(cfg));

		assertTrue(p.getInteger("-first") == 100);
		assertTrue(p.getInteger("-first", 1) == 200);
		assertTrue(p.getInteger("-first", 2) == 300);
	}

	@Test
	public void testReadConfigFromConfiguration_config_file_is_missing_entries() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);

		String cfgStr = "<Settings>" +
				"<First Key=\"KeyName\">55</First>" +
				"<First Key=\"KeyName\">56</First>" +
				"</Settings>";

		XMLConfigurationReader cfg = new XMLConfigurationReader(cfgStr, msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First"));

		assertFalse(p.parse(cfg));
	}

	@Test
	public void testReadConfigFromConfiguration_invalid_xpath() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);

		String cfgStr = "<Settings>" +
				"<First Key=\"KeyName\">55</First>" +
				"<First Key=\"KeyName\">56</First>" +
				"<First Key=\"KeyName\">57</First>" +
				"</Settings>";

		XMLConfigurationReader cfg = new XMLConfigurationReader(cfgStr, msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("%#%&##%#"));

		assertFalse(p.parse(cfg));
	}

	@Test
	public void testReadConfigFromConfiguration_specified_on_commandline() {
		try {
			IParseResult msg = new SystemOutputParseResult();
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("-first").asInteger(3);
			p.accept("-config").asString(1).setMandatory();

			String fileName = "config.xml";

			String cfgStr = "<Settings>" +
					"<First>88</First>" +
					"<First>89</First>" +
					"<First>90</First>" +
					"</Settings>";

			File f = new File(fileName);
			if (f.exists()) {
				assertTrue(f.delete());
			}

			FileOutputStream fs = new FileOutputStream(fileName);
			fs.write(cfgStr.getBytes());
			fs.close();

			assertTrue(f.exists());
			f.deleteOnExit();

			XMLConfigurationReader cfg = new XMLConfigurationReader( msg );
			cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First"));

			assertTrue(p.parse("-config", cfg, "-config", fileName));

			assertTrue(p.getInteger("-first") == 88);
			assertTrue(p.getInteger("-first", 1) == 89);
			assertTrue(p.getInteger("-first", 2) == 90);
		}
		catch (FileNotFoundException ex )
		{
			Assert.fail(ex.toString());
		}
		catch (IOException ex)
		{
			Assert.fail(ex.toString());
		}
	}

	@Test
	public void testReadConfigFromConfiguration_config_file_missing() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("-first").asInteger(3);
		p.accept("-config").asString(1).setMandatory();

		XMLConfigurationReader cfg = new XMLConfigurationReader(msg);
		cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First"));

		assertFalse(p.parse("-config", cfg, "-config", "non-existingFile.xml"));
		assertTrue(msg.getParseResult().contains("Could not load the configuration specified by argument"));
	}

	@Test
	public void testReadConfigFromConfiguration_read_to_settings() {
		try {
			IParseResult msg = new SystemOutputParseResult();
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("-first").asInteger(3);
			p.accept("-second").asSingleBoolean();
			p.accept("-config").asString(1).setMandatory();

			String fileName = "config.xml";

			String cfgStr = "<Settings>" +
					"<First>88</First>" +
					"<First>89</First>" +
					"<First>90</First>" +
					"<Second><Awesome>42</Awesome></Second>" +
					"</Settings>";

			File f = new File(fileName);
			if (f.exists()) {
				assertTrue( f.delete() );
			}

			FileOutputStream fs = new FileOutputStream(fileName);
			fs.write(cfgStr.getBytes());
			fs.close();

			assertTrue(f.exists());

			XMLConfigurationReader cfg = new XMLConfigurationReader(msg);
			cfg.setMatcher("-first", new XMLConfigurationReader.NodeMatcher("/Settings/First"));
			cfg.setMatcher("-second", new XMLConfigurationReader.NodeMatcher("/Settings/Second/Awesome"));

			assertTrue(p.parse("-config", cfg, "-config", fileName, "-second"));

			assertTrue(p.getInteger("-first") == 88);
			assertTrue(p.getInteger("-first", 1) == 89);
			assertTrue(p.getInteger("-first", 2) == 90);
			assertTrue(p.getBool("-second"));

			if (f.exists()) {
				assertTrue( f.delete() );
			}
		}
		catch (FileNotFoundException ex )
		{
			Assert.fail(ex.toString());
		}
		catch (IOException ex )
		{
			Assert.fail(ex.toString());
		}
	}

	@Test
	public void testIntegerLimitsOk() {
		IParseResult msg = new SystemOutputParseResult();
		for (Integer i = 4; i < 6; ++i) {
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("--first").asInteger(1, new NumericLimit<Integer>(4, 5));
			assertTrue(p.parse("--first", i.toString()));
		}
	}

	@Test
	public void testIntegerLimitsFail() {
		IParseResult msg = new SystemOutputParseResult();
		for (Integer i = 1; i < 4; ++i) {
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("--first").asInteger(1, new NumericLimit<Integer>(4, 5));
			assertFalse(p.parse("--first", i.toString()));
			assertTrue(msg.getParseResult().contains("is outside allowed limits of"));

		}

		for (Integer i = 6; i < 9; ++i) {
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("--first").asInteger(1, new NumericLimit<Integer>(4, 5));
			assertFalse(p.parse("--first", i.toString()));
			assertTrue(msg.getParseResult().contains("is outside allowed limits of"));
		}
	}

	@Test
	public void testIStringLimitsOk() {
		IParseResult msg = new SystemOutputParseResult();
		for (Integer i = 4; i < 6; ++i) {
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("--first").asString(1, new StringLengthLimit(4, 5));
			assertTrue(p.parse("--first", new String(new char[i]).replace("\0", "A")));
		}
	}

	@Test
	public void testStringLimitsFail() {
		IParseResult msg = new SystemOutputParseResult();
		for (Integer i = 1; i < 4; ++i) {
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("--first").asString(1, new StringLengthLimit(4, 5));
			assertFalse(p.parse("--first", new String(new char[i]).replace("\0", "A")));
			assertTrue(msg.getParseResult().contains("is outside allowed lengths of 4 - 5"));

		}

		for (Integer i = 6; i < 9; ++i) {
			CmdParser4J p = new CmdParser4J(msg);
			p.accept("--first").asString(1, new StringLengthLimit(4, 5));
			assertFalse(p.parse("--first", new String(new char[i]).replace("\0", "A")));
			assertTrue(msg.getParseResult().contains("is outside allowed lengths of 4 - 5"));
		}
	}

	@Test
	public void testDependsOnFormatterOk() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("--D").asString(1, Constructor.NO_PARAMETER_LIMIT).dependsOn("--A").describedAs("Command D.");
		p.accept("--E").asBoolean(1).dependsOn("--A").describedAs("Command E.");
		p.accept("--A").asBoolean(1).withAlias("-a").dependsOn("--F").dependsOn("--B").dependsOn("--C").describedAs("Command A.");
		p.accept("--F").asString(1, 5).dependsOn("--B").dependsOn("--E").describedAs("Command F.");
		p.accept("--B").asBoolean(1).withAlias("-b").describedAs("Command B.");
		p.accept("--C").asString(1, 5).describedAs("Command C.");

		SystemOutputUsageFormatter usage = new SystemOutputUsageFormatter("Application name");
		p.getUsage(usage);

		assertTrue(p.parse("--A", "true", "--E", "true", "--D", "myStr2", "--F", "myStr3", "--B", "false", "--C", "myStr1"));
		assertEquals("", msg.getParseResult());

		System.out.println(usage);
	}

	@Test
	public void testDependsOnFormatterNok() {
		IParseResult msg = new SystemOutputParseResult();
		CmdParser4J p = new CmdParser4J(msg);
		p.accept("--D").asString(1, Constructor.NO_PARAMETER_LIMIT).dependsOn("--A").describedAs("Command D.");
		p.accept("--E").asBoolean(1).dependsOn("--A").describedAs("Command E.");
		p.accept("--A").asBoolean(1).withAlias("-a").dependsOn("--F").dependsOn("--B").dependsOn("--C").describedAs("Command A.");
		p.accept("--F").asString(1, 5).dependsOn("--B").dependsOn("--E").describedAs("Command F.");
		p.accept("--B").asBoolean(1).withAlias("-b").describedAs("Command B.");
		p.accept("--C").asString(1, 5).describedAs("Command C.");

		SystemOutputUsageFormatter usage = new SystemOutputUsageFormatter("Application name");
		p.getUsage(usage);

		assertFalse(p.parse("--A", "true", "--E", "true", "--D", "myStr2", "--B", "false"));

		System.out.println(usage);
	}
}