package net.schwarzbaer.java.games.reka.savegames.data;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.schwarzbaer.java.lib.system.XMLHelper;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues.NodeContainerType;

class XMLTools
{
	private interface StatementConsumer
	{
		void add(String format, Object... args);
		
		default StatementConsumer forSubNode(String subNodeName)
		{
			return (format, args) -> add("Subnode \"%s\" : %s", subNodeName, format.formatted(args));
		}
	}

	static KnownValues createKVforSubNode(Class<?> dataClass, String subNodeName)
	{
		return SaveGame.KVF
			.createFor(dataClass, " <%s>".formatted(subNodeName));
	}

	static StatementConsumer forSubNode(KnownValues KV, String subNodeName)
	{
		return (format, args) -> KV.addStatement("Subnode \"%s\" : %s", subNodeName, format.formatted(args));
	}

	static String getTextFromSubNode(Node node, String subNodeName, KnownValues KV)
	{
		return getTextFromSubNode(node, subNodeName, forSubNode(KV, subNodeName));
	}

	static String getTextFromSubNode(Node node, String subNodeName, StatementConsumer statementConsumer)
	{
		if (node==null)
			return null;
		
		Node subNode = XMLHelper.getChild(node, subNodeName);
		if (subNode != null)
			return getTextFromNode(subNode, statementConsumer.forSubNode(subNodeName));
		
		if (statementConsumer!=null)
			statementConsumer.add("No subnode \"%s\"", subNodeName);
		return null;
	}
	
	static String getTextFromNode(Node node, StatementConsumer statementConsumer)
	{
		if (node==null)
			return null;
		
		NodeList childNodes = node.getChildNodes();
		int n = childNodes.getLength();
		if (n!=1)
		{
			if (statementConsumer!=null)
				statementConsumer.add("Expected Text containing node hasn't exact 1 child (n=%d)", n);
			return null;
		}
		
		Node subNode = childNodes.item(0);
		if (subNode==null)
		{
			if (statementConsumer!=null)
				statementConsumer.add("Expected Text containing node has a NULL child");
			return null;
		}
		
		short subNodeType = subNode.getNodeType();
		if (subNodeType!=Node.TEXT_NODE)
		{
			if (statementConsumer!=null)
				statementConsumer.add("Expected Text containing node has a child, that is not of type TEXT_NODE : %s", XMLHelper.getNodeTypeLabel(subNodeType));
			return null;
		}
		
		String subNodeValue = subNode.getNodeValue();
		if (subNodeValue==null)
		{
			if (statementConsumer!=null)
				statementConsumer.add("Expected Text containing node has a child, that is of type TEXT_NODE but has a NULL value");
		}
		
		return subNodeValue;
	}
	
	static String getAttribute(Node node, String attrName)
	{
		if (node==null)
			return null;
		
		NamedNodeMap attributes = node.getAttributes();
		Node attrNode = attributes.getNamedItem(attrName);
		
		if (attrNode.getNodeType() != Node.ATTRIBUTE_NODE)
			throw new IllegalStateException();
		
		return attrNode.getNodeValue();
	}
	
	static Long parseInt(String str, StatementConsumer statementConsumer)
	{
		if (str==null)
			return null;
		
		try
		{
			return Long.parseLong(str);
		}
		catch (NumberFormatException e)
		{
			// e.printStackTrace();
			if (statementConsumer!=null)
				statementConsumer.add("Can't parse Number from \"%s\" : ", str, e.getMessage());
			return null;
		}
	}

	static Double parseFloat(String str, StatementConsumer statementConsumer)
	{
		if (str==null)
			return null;
		
		try
		{
			double value = Double.parseDouble(str);
			if (!Double.isFinite(value))
			{
				if (statementConsumer!=null)
					statementConsumer.add("Can't parse Number from \"%s\"", str);
				return null;
			}
			return value;
		}
		catch (NumberFormatException e)
		{
			// e.printStackTrace();
			if (statementConsumer!=null)
				statementConsumer.add("Can't parse Number from \"%s\" : ", str, e.getMessage());
			return null;
		}
	}
	
	static Boolean parseBool(String str, StatementConsumer statementConsumer)
	{
		if (str==null)
			return null;
		
		if ("true" .equalsIgnoreCase(str)) return true;
		if ("false".equalsIgnoreCase(str)) return false;
		
		if (statementConsumer!=null)
			statementConsumer.add("Can't parse Boolean from \"%s\"", str);
		return null;
	}

	static String[] parseListOfStrings(
			Node node,
			Class<?> dataClass, String arraySubNodeName
	) {
		return parseListOfStrings(
				node, "string",
				dataClass, arraySubNodeName
		);
	}

	static String[] parseListOfStrings(
			Node node, String subNodeName,
			Class<?> dataClass, String arraySubNodeName
	) {
		return parseSubNodeArray(
				node, subNodeName, NodeContainerType.Text,
				String[]::new,
				subNode -> XMLTools.getTextFromNode(subNode, forSubNode(createKVforSubNode(dataClass, arraySubNodeName), subNodeName) ),
				dataClass, arraySubNodeName
		);
	}

	static <T> T[] parseSubNodeArray(
			Node node, String subNodeName,
			IntFunction<T[]> createArray,
			Function<Node,T> itemConstructor,
			Class<?> dataClass, String arraySubNodeName
	) {
		return parseSubNodeArray(
				node, subNodeName, NodeContainerType.Unspecific,
				createArray, itemConstructor,
				dataClass, arraySubNodeName
		);
	}

	static <T> T[] parseSubNodeArray(
			Node node, String subNodeName, NodeContainerType nodeContainerType,
			IntFunction<T[]> createArray,
			Function<Node,T> itemConstructor,
			Class<?> dataClass, String arraySubNodeName
	) {
		if (node==null)
			return null;
		
		createKVforSubNode(dataClass, arraySubNodeName)
			.addNodes(nodeContainerType, subNodeName)
			.scan(node);
		
		ArrayList<T> list = new ArrayList<>();
		XMLHelper.forEachChild(node, (subNode, tag) -> {
			if (subNodeName.equals(tag))
				list.add(itemConstructor.apply(subNode));
		});
		
		return list.toArray(createArray);
	}
}
