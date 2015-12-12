# CmdParser4J - Command Line Parser for Java

This is a rewrite of my original [JavaCommandLineParser](https://github.com/PerMalmberg/JavaCommandLineParser) with support for multiple parameters to an argument.

## Features
* Written with support for Java6 in mind so 7 and 8 shouldn't be a problem.
* Arguments that takes none, single, multiple and unlimited number of parameters.
* Mandatory and optional arguments.
* Dependencies between arguments, i.e. if entered, an argument may require another argument to be entered too.
* Constraint validation; same argument not allowed twice, only allow a single argument with variable number of parameters and placement of such an argument at the end of the command line.

## Usage
This example is taken from the test cases.

```Java
CmdParser4J p = new CmdParser4J("-");
p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accepts a single boolean parameter");
p.accept("-multi").asString(1, 4).describedAs("An optional argument that accepts one to four argument.");

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
```

The parser also supports printing usage text;
```Java
p.getUsage("myApp")
```
which return the following string, based on the above example.
```
Usage: myApp -argument <arg1> [-multi <arg1>  <arg2>  <arg3>  <arg4>] [--otherArgument] 
             -argument	 An argument that accepts a single boolean parameter
             -multi	 An optional argument that accepts one to four argument.
             --otherArgument	[-o] An optional argument that takes no parameters
```

If ```CmdParser4J.parse()``` returns false, you can easily see what went wrong by calling the method ```getParseResult()```, like so:

```Java
CmdParser4J p = new CmdParser4J("-");
p.accept("-argument").asBoolean(1).setMandatory().describedAs("An argument that accepts a single boolean parameter");
p.accept("-multi").asString(1, 4).describedAs("An optional argument that accepts one to four argument.");

// Missing mandatory argument
assertFalse(p.parse("-multi", "parameter1", "parameter2", "parameter3"));

System.out.println(p.getParseResult());
```
which yields the following message
```
The mandatory argument '-argument' is missing
```
