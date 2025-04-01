package com.whyarewesoclever.betterVillagers;

import java.util.List;

public class VillagerTrade {
    String material_input, material_output, json_input, json_output;
    public List< String > biomes;
    public List < String > bannedWorlds;
    String day_night = "both"; // default value
    // modifiers for it to be day, night, or both
    String weather = "any"; // default value
    // modifiers for it to be clear, rain, thunder or any

    public VillagerTrade(String material1, String material2, String json1,String json2){
        this.material_input = material1;
        this.material_output = material2;
        this.json_input = json1;
        this.json_output = json2;
    }
    public VillagerTrade(List<String> biomes) {
        this.biomes = biomes;
    }
    public VillagerTrade(List<String> biomes, List<String> bannedWorlds) {
        this.biomes = biomes;
        this.bannedWorlds = bannedWorlds;
    }

    public String getMaterialInput() {
        return material_input;
    }
    public String getMaterialOutput() {
        return material_output;
    }
    public String getJsonInput() {
        return json_input;
    }
    public String getJsonOutput() {
        return json_output;
    }
    public List<String> getBiomes() {
        return biomes;
    }
    public List<String> getBannedWorlds() {
        return bannedWorlds;
    }

} // write once, debug everywhere
