package org.prank;

import org.prank.config.ConfigCategory;
import org.prank.config.Configuration;
import org.prank.config.Property;

import java.io.File;
import java.util.*;

public class ConfigReader {
    private static List<String> gtOreList = new ArrayList<String>() {{
        add("naquadah");
		add("lignite");
		add("coal");
		add("magnetite");
		add("gold");
		add("iron");
		add("cassiterite");
		add("tetrahedrite");
		add("netherquartz");
		add("sulfur");
		add("copper");
		add("bauxite");
		add("salts");
		add("redstone");
		add("soapstone");
		add("nickel");
		add("platinum");
		add("pitchblende");
		add("monazite");
		add("molybdenum");
		add("tungstate");
		add("sapphire");
		add("manganese");
		add("quartz");
		add("diamond");
		add("olivine");
		add("apatite");
		add("galena");
		add("lapis");
		add("beryllium");
		add("uranium");
		add("oilsand");
    }};

    public static void readWorldGenConfig() {
        File file = new File("WorldGeneration.cfg");
        if (!file.exists() || !file.canRead())
            return;

        OreVein.clear();
        Configuration cfg = new Configuration(file);
        ConfigCategory wg = cfg.getCategory("worldgen");

        for (int i = 0, j = wg.properties.get("AmountOfCustomLargeVeinSlots_16").getInt(); i < j; i++) {
            gtOreList.add("custom." + (i < 10 ? "0" : "") + i);
        }

        for (String veinName : gtOreList) {
            Property tr = wg.properties.get("ore.mix." + veinName + "_true");
            Property fl = wg.properties.get("ore.mix." + veinName + "_false");
            if (tr != null && !tr.getBoolean() || fl != null && !fl.getBoolean()) continue;
            OreVein vein = new OreVein(veinName);
            ConfigCategory cc = cfg.getCategory("worldgen.ore.mix." + veinName);
            for (String name : cc.properties.keySet()) {
                Property prop = cc.properties.get(name);
                if (name.startsWith("MinHeight")) {
                    vein.min = prop.getInt();
                } else if (name.startsWith("MaxHeight")) {
                    vein.max = prop.getInt();
                } else if (name.startsWith("RandomWeight")) {
                    vein.weight = prop.getInt();
                } else if (prop.getBoolean()) {
                	String dimName = name.substring(0, name.lastIndexOf('_')).toLowerCase();
                	//������� ��� ������ ��������� ���� � ����������� ������
                	if(dimName.contains("theend") || dimName.contains("the end") || dimName.contains("endasteroid"))
                		dimName = "theend";
                	if(!vein.dims.contains(dimName))
                		vein.dims.add(dimName);
                }
            }
            ConfigCategory dims = cfg.getCategory("worldgen.dimensions.ore.mix." + veinName);
            for (String nn : dims.properties.keySet()){
            	Property p = dims.properties.get(nn);
            	String propName = nn.substring(0, nn.lastIndexOf('_'));
            	String dimName= propName.toLowerCase();
            	//������� ��� ������ ��������� ���� � ����������� ������
            	if(dimName.contains("theend") || dimName.contains("the end") || dimName.contains("endasteroid"))
            		dimName = "theend";
            	if(!p.getBoolean()){
            		if(vein.dims.contains(dimName)){
            				vein.dims.remove(dimName);
            		}
            		continue;
            	}
            	else{
            		Property falseP=dims.properties.get(propName+"_false");
            		if(falseP==null || falseP.getBoolean())
            			vein.dims.add(dimName);
            	}
            }
            
            OreVein.add(vein);
        }
    }
}
