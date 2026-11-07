package net.schwarzbaer.java.games.reka.savegames.data;

import java.io.File;

import org.w3c.dom.Node;

import net.schwarzbaer.java.games.reka.savegames.data.SaveGame.DataFile;
import net.schwarzbaer.java.games.reka.savegames.views.FilePanel;
import net.schwarzbaer.java.lib.gui.ValueListOutput;
import net.schwarzbaer.java.lib.system.XMLHelper;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues.NodeContainerType;

public class Character extends DataFile implements FilePanel.SimpleValueOutputter
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
	
	@Override
	public boolean canShowInstantly() { return true; }

	@Override
	public void writeTo(ValueListOutput out)
	{
		out.add(0, "Character");
		out.add(1, "saveGameVersionID", saveGameVersionID);
		out.add(1, "maskIndex"        , maskIndex);
		out.add(1, "wearingMask"      , wearingMask, "Yes", "No");
		if (materialFloatSettingDefinitions==null)
			out.add(1, "Material Definitions", "%s", "NULL");
		else
		{
			out.add(1, "Material Definitions", "%d", materialFloatSettingDefinitions.length);
			for (int i=0; i<materialFloatSettingDefinitions.length; i++)
				MaterialDef.writeTo(out, 2, i, materialFloatSettingDefinitions[i]);
		}
		FilePanel.SimpleValueOutputter.writeTo(out, 1, "Wearable Materials", wearableMaterials);
		FilePanel.SimpleValueOutputter.writeTo(out, 1, "Wearable Meshes"   , wearableMeshes   );
		FilePanel.SimpleValueOutputter.writeTo(out, 1, "Wearable Textures" , wearableTextures );
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
		
		static void writeTo(ValueListOutput out, int indentLevel, int arrIndex, MaterialDef materialDef)
		{
			if (materialDef==null)
				out.add(indentLevel, "[%d] NULL".formatted(arrIndex+1));
			else
			{
				out.add(indentLevel, "[%d]".formatted(arrIndex+1), materialDef.name);
				if (materialDef.parameters==null)
					out.add(indentLevel+1, "parameters == NULL");
				else
					for (int i=0; i<materialDef.parameters.length; i++)
						ParameterFloat.writeTo(out, indentLevel+1, i, materialDef.parameters[i]);
			}
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
		
		static void writeTo(ValueListOutput out, int indentLevel, int arrIndex, ParameterFloat parameter)
		{
			if (parameter==null)
				out.add(indentLevel, "[%d] NULL".formatted(arrIndex+1));
			else
			{
				String nameStr = parameter.name==null ? "{name==NULL}" : "\"%s\"".formatted(parameter.name);
				out.add(indentLevel, "[%d] %s".formatted(arrIndex+1, nameStr), parameter.value);
			}
			
		}
	}
}
