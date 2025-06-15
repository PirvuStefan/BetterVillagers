package com.whyarewesoclever.betterVillagers;

import java.util.ArrayList;
import java.util.List;

public class VillagerTrade {
    String material_input, material_output, json_input, json_output;
    int amount_input, amount_output;
    public List< String > biomes;
    public List < String > bannedWorlds;
    public List < String > professions = new ArrayList<>();
    String day_night = "both"; // default value
    // modifiers for it to be day, night, or both
    String weather = "any"; // default value
    // modifiers for it to be clear, rain, thunder or any
    String level = "NOVICE" ;
    int amount_optional = 0; // default value, no optional item
    String materialOptioanal = "none";
    String jsonOptional = "{}";


    public VillagerTrade(String material1, String material2, String json1,String json2){
        this.material_input = material1;
        this.material_output = material2;
        this.json_input = json1;
        this.json_output = json2;
    }
    public VillagerTrade(String material1, String material2, String json1,String json2, int number1, int number2, List<String> biomes, List<String> bannedWorlds, String day_night, String weather, List<String> professions, String level){
        this.material_input = material1;
        this.material_output = material2;
        this.json_input = json1;
        this.json_output = json2;
        this.amount_input = number1;
        this.amount_output = number2;
        this.biomes = biomes;
        this.bannedWorlds = bannedWorlds;
        this.day_night = day_night;
        this.weather = weather;
        this.professions = professions;
        this.level = level;
    }
    public VillagerTrade(int number1, int number2){
        this.amount_input = number1;
        this.amount_output = number2;
    }
    public VillagerTrade(List<String> biomes) {
        this.biomes = biomes;
    }
    public VillagerTrade(List<String> biomes, List<String> bannedWorlds) {
        this.biomes = biomes;
        this.bannedWorlds = bannedWorlds;
    }

    public VillagerTrade(String example){
        this.day_night = example;
    }
    public VillagerTrade(String example, String example2){
        this.day_night = example;
        this.weather = example2;
    }
    public VillagerTrade(String material,String json, int n){
        this.materialOptioanal = material;
        this.jsonOptional = json;
        this.amount_optional = n;
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
    public String getDayNight() {
        return day_night;
    }
    public String getWeather() {
        return weather;
    }
    public int getAmountInput() {
        return amount_input;
    }
    public int getAmountOutput() {
        return amount_output;
    }//da
    public List<String> getProfessions() {
        return professions;
    }
    public String getLevel(){
        return level;
    }

} // write once, debug everywhere