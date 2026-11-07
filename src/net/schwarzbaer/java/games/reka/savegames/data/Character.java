package net.schwarzbaer.java.games.reka.savegames.data;

import java.io.File;

import org.w3c.dom.Node;

import net.schwarzbaer.java.games.reka.savegames.data.SaveGame.DataFile;
import net.schwarzbaer.java.lib.system.XMLHelper;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues.NodeContainerType;

@SuppressWarnings("unused")
public class Character extends DataFile
{
	private static final KnownValues KV_Document = SaveGame.KVF
			.createFor(Character.class, " <DOC>")
			.addNodes(NodeContainerType.Nodes, "SaveableCharacter");
	
	private static final KnownValues KV = SaveGame.KVF
			.createFor(Character.class, " <SaveableCharacter>")
			.addAttrs(
					"xmlns:xsd",
					"xmlns:xsi"
			)
			.addNodes(
					NodeContainerType.Text,
					"saveGameVersionID",
					"maskIndex",
					"wearingMask"
			)
			.addNodes(
					NodeContainerType.Nodes,
					"materialFloatSettingDefinitions",
					"wearableMaterials",
					"wearableMeshes",
					"wearableTextures"
			);

	private final String saveGameVersionID;
	private final Long maskIndex;
	private final Boolean wearingMask;
	private final MaterialDef[] materialFloatSettingDefinitions;
	private final String[] wearableMaterials;
	private final String[] wearableMeshes;
	private final String[] wearableTextures;
	
	protected Character(File file)
	{
		super(file);
		
		if (document!=null)
			KV_Document.scan(document);
		
		Node base = document==null ? null : XMLHelper.getChild(document, "SaveableCharacter");
		if (base!=null)
			KV.scan(base);
		
		String maskIndexStr, wearingMaskStr;
		saveGameVersionID = XMLTools.getTextFromSubNode(base, "saveGameVersionID", KV);
		maskIndexStr      = XMLTools.getTextFromSubNode(base, "maskIndex"        , KV);
		wearingMaskStr    = XMLTools.getTextFromSubNode(base, "wearingMask"      , KV);
		maskIndex         = XMLTools.parseInt (maskIndexStr  , XMLTools.forSubNode(KV, "maskIndex"  ));
		wearingMask       = XMLTools.parseBool(wearingMaskStr, XMLTools.forSubNode(KV, "wearingMask"));
		
		Node materialFloatSettingDefinitionsNode = base==null ? null : XMLHelper.getChild(base, "materialFloatSettingDefinitions");
		Node wearableMaterialsNode               = base==null ? null : XMLHelper.getChild(base, "wearableMaterials"              );
		Node wearableMeshesNode                  = base==null ? null : XMLHelper.getChild(base, "wearableMeshes"                 );
		Node wearableTexturesNode                = base==null ? null : XMLHelper.getChild(base, "wearableTextures"               );
		
		materialFloatSettingDefinitions = XMLTools.parseSubNodeArray(
				materialFloatSettingDefinitionsNode,
				"MaterialFloatSettingDefinition",
				NodeContainerType.Nodes,
				MaterialDef[]::new, MaterialDef::new,
				Character.class, "materialFloatSettingDefinitions"
		);
		wearableMaterials = XMLTools.parseListOfStrings(wearableMaterialsNode, Character.class, "wearableMaterials");
		wearableMeshes    = XMLTools.parseListOfStrings(wearableMeshesNode   , Character.class, "wearableMeshes"   );
		wearableTextures  = XMLTools.parseListOfStrings(wearableTexturesNode , Character.class, "wearableTextures" );
	}
	
	private static class MaterialDef
	{
		private static final KnownValues KV = SaveGame.KVF
				.createFor(MaterialDef.class)
				.addNodes(NodeContainerType.Text ,"name")
				.addNodes(NodeContainerType.Nodes,"pfloats");
		
		private final String name;
		private final ParameterFloat[] parameters;
		
		MaterialDef(Node base)
		{
			if (base != null)
				KV.scan(base);
			
			name = XMLTools.getTextFromSubNode(base, "name", KV);
			
			Node pfloatsNode = base==null ? null : XMLHelper.getChild(base, "pfloats");
			
			parameters = XMLTools.parseSubNodeArray(
					pfloatsNode, "ParameterFloat", NodeContainerType.Nodes,
					ParameterFloat[]::new, ParameterFloat::new,
					MaterialDef.class, "pfloats"
			);
		}
	}
	
	private static class ParameterFloat
	{
		private static final KnownValues KV = SaveGame.KVF
				.createFor(ParameterFloat.class)
				.addNodes(NodeContainerType.Text, "parameterName", "value");
		
		private final String name;
		private final Double value;
		
		ParameterFloat(Node base)
		{
			if (base != null)
				KV.scan(base);
			
			String valueStr;
			name     = XMLTools.getTextFromSubNode(base, "parameterName", KV);
			valueStr = XMLTools.getTextFromSubNode(base, "value"        , KV);
			value    = XMLTools.parseFloat(valueStr, XMLTools.forSubNode(KV, "value"));
		}
	}
}
