// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

public interface IConfigurationReader {
	boolean fillFromConfiguration(Argument argument);
	boolean loadFromFile( String pathToFile );
}

