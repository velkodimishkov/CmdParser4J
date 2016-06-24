// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

package cmdparser4j;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLConfigurationReader implements IConfigurationReader {

	public static class NodeMatcher {

		public NodeMatcher(String searchPath) {
			mySearchPath = searchPath == null ? "" : searchPath;
		}

		public NodeMatcher(String searchPath, String valueAttributeName) {
			this(searchPath);
			myValueName = valueAttributeName == null ? "" : valueAttributeName;
		}

		public NodeMatcher(String searchPath, String matchAttribute, String matchAttributeValue) {
			this(searchPath, null);
			myMatchAttribute = matchAttribute == null ? "" : matchAttribute;
			myMatchAttributeValue = matchAttributeValue == null ? "" : matchAttributeValue;
		}

		public NodeMatcher(String searchPath, String valueAttributeName, String matchAttribute, String matchAttributeValue) {
			this(searchPath, valueAttributeName);
			myMatchAttribute = matchAttribute == null ? "" : matchAttribute;
			myMatchAttributeValue = matchAttributeValue == null ? "" : matchAttributeValue;
		}

		void Match(Node node, List<String> output) {
			// Match using attribute name/value pair?
			if (myMatchAttribute.length() > 0) {
				NamedNodeMap attributes = node.getAttributes();
				Node attr = attributes.getNamedItem(myMatchAttribute);

				if (myMatchAttribute.equals(attr.getNodeName()) && myMatchAttributeValue.equals(attr.getNodeValue())) {
					// We found a matching attribute/value pair
					if (myValueName.length() == 0) {
						ReadTextValue(node, output);
					} else {
						ReadAttributeValue(node, myValueName, output);
					}
				}
			} else if (myValueName.length() == 0) {
				// Read the child data, e.g. <Node>THE DATA</Node>
				ReadTextValue(node, output);
			} else {
				// We want to read the value of the attribute
				ReadAttributeValue(node, myValueName, output);
			}
		}

		private void ReadAttributeValue(Node node, String name, List<String> output) {
			NamedNodeMap attributes = node.getAttributes();
			Node n = attributes.getNamedItem(name);
			if (n != null) {
				String value = n.getNodeValue();
				if (value != null) {
					output.add(value);
				}
			}
		}

		private void ReadTextValue(Node node, List<String> output) {
			String value = node.getTextContent();
			if (value != null && value.length() > 0) {
				output.add(value);
			}
		}

		String getPath() {
			return mySearchPath;
		}

		private String mySearchPath;
		private String myValueName = "";
		private String myMatchAttribute = "";
		private String myMatchAttributeValue = "";
	}

	public XMLConfigurationReader() {

	}

	public XMLConfigurationReader(String xmlData) {
		mySource = new InputSource(new StringReader(xmlData));
	}

	public void setMatcher(String primaryArgumentName, NodeMatcher matcher) {
		myMatcher.put(primaryArgumentName, matcher);
	}

	@Override
	public boolean fillFromConfiguration(Argument argument) {
		boolean res = true;

		NodeMatcher matcher = myMatcher.get(argument.getPrimaryName());
		if (matcher != null) {
			List<String> data = new ArrayList<String>();
			// The name of the argument must be the first item in array of items to parse
			data.add(argument.getPrimaryName());

			try {
				// Select the node in the XML tree
				XPath path = myXPathFactory.newXPath();
				NodeList nodes = (NodeList) path.evaluate(matcher.getPath(), mySource, XPathConstants.NODESET);

				// Loop each found node and let the matcher decide if it is a match.
				for (int i = 0; i < nodes.getLength(); ++i) {
					Node n = nodes.item(i);
					matcher.Match(n, data);
				}

				res = argument.parse(data);

			} catch (XPathExpressionException ex) {
				res = false;
			}
		}

		return res;
	}

	@Override
	public boolean loadFromFile( String pathToFile )
	{
		boolean res = false;

		FileInputStream input = null;

		try {
			File file = new File( pathToFile);
			if( file.exists() ) {
				input = new FileInputStream(pathToFile);
				byte[] data = new byte[(int)file.length()];
				input.read(data);
				mySource = new InputSource( new StringReader(new String( data, "UTF-8") ) );
				res = true;
			}
		}
		catch (Exception ex )
		{

		}
		finally {
			if( input != null) {
				try {
					input.close();
				}
				catch (Exception ex) {}
			}
		}

		return res;
	}

	private final HashMap<String, NodeMatcher> myMatcher = new HashMap<String, NodeMatcher>();
	private final XPathFactory myXPathFactory = XPathFactory.newInstance();
	private InputSource mySource = null;

}

