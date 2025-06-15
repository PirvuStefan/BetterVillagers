package com.whyarewesoclever.betterVillagers;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public final class BetterVillagers extends JavaPlugin {

    public static List<String> keys = new ArrayList<>();
    public static List<String> worldsList = new ArrayList<>();
    public final static Map<String, VillagerTrade> villagerTrades = new HashMap<>();
    public static BetterVillagers getInstance() {
        return getPlugin(BetterVillagers.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            ReloadCommand reloadCommand = new ReloadCommand("bettervillagers");
            commandMap.register("bettervillagers", reloadCommand);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Ascii();

        File folder = getDataFolder();
        if (!folder.exists()) {
            if (folder.mkdir()) {
                getLogger().info("Folder 'BetterVillagers' created successfully!");
            } else {
                getLogger().info("Failed to create folder 'BetterVillagers'.");
            }
        }
        File folder2 = new File(getDataFolder(), "drops");
        if (!folder2.exists()) {
            if (folder2.mkdir()) {
                getLogger().info("Folder 'drops' created successfully!");
            } else {
                getLogger().info("Failed to create folder 'drops'.");
            }
        }
        if( !getConfig().getBoolean("Enable") ) {
            getLogger().info("BetterVillagers is disabled in the config file! The plugin will not register new trades for the villagers. Enable it in the config file to use the plugin.");
            getLogger().info("Or do as you wish, we are not your parents.");
            //return; - maybe return ?
        }

        initialiseKeys();
        initialiseMap();
        worldsList.addAll(getConfig().getStringList("Worlds")); // get the list of worlds from the config
        for (String word : worldsList) {
            getLogger().info("World: " + word);
        }
        int seconds = getConfig().getInt("CheckForUpdates");
        if( seconds < 3 ) seconds = 3;
        seconds = seconds * 20;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateVillagerTrades, 0L, seconds);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BetterVillagers has been disabled!");
    }

    public void initialiseKeys(){

        File folder = new File(getDataFolder(), "drops");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String name = file.getName();
                    String name_id = name.substring(0, name.length() - 4);
                    getLogger().info("File name: " + name);
                    keys.add(name_id);
                }
            }
        }
    }

    public void initialiseMap() {
        villagerTrades.clear();

        File folder = new File(getDataFolder(), "drops");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String name = file.getName();
                    String name_id = name.substring(0, name.length() - 4);
                    VillagerTrade villagerTrade = parseVillagerTrade(file);

                    villagerTrades.put(name_id, villagerTrade);

                }
            }
        }

    }

    private VillagerTrade parseVillagerTrade(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String material_input = null;
            String material_output = null;
            String material_optional = "none"; // default value
            String json_input = null;
            String json_output = null;
            String json_optional = "{}"; // default value
            int amount_input = 0;
            int amount_output = 0;
            int amount_optional = 0; // default value
            List<String> biomes = new ArrayList<>();
            List<String> bannedWorlds = new ArrayList<>();
            List<String> professions = new ArrayList<>();
            String day_night = "both"; // default value
            String weather = "any"; // default value
            String level = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("material_input: ")) {
                    material_input = line.substring(16);
                } else if (line.startsWith("material_output: ")) {
                    material_output = line.substring(17);
                } else if (line.startsWith("json_input: ")) {
                    json_input = line.substring(12);
                } else if (line.startsWith("json_output: ")) {
                    json_output = line.substring(13);
                } else if (line.startsWith("amount_input: ")) {
                    amount_input = Integer.parseInt(line.substring(14));
                } else if (line.startsWith("amount_output: ")) {
                    amount_output = Integer.parseInt(line.substring(15));
                } else if (line.startsWith("biomes: ")) {
                    biomes = Arrays.asList(line.substring(9, line.length() - 1).split(", "));
                } else if (line.startsWith("bannedWorlds: ")) {
                    if( 16 <= line.length() - 1) bannedWorlds = Arrays.asList(line.substring(16, line.length() - 1).split(", "));
                    else bannedWorlds = Arrays.asList(line.substring(16).split(", "));

                }  else if (line.startsWith("day_night: ")) {
                    day_night = line.substring(11);
                } else if (line.startsWith("weather: ")) {
                    weather = line.substring(9);
                } else if ( line.startsWith("professions:")){
                    professions = Arrays.asList(line.substring(14, line.length() - 1).split(",\\s*"));
                } else if ( line.startsWith("level:")){
                    level = line.substring(7);
                } else if (line.startsWith("material_optional: ")) {
                    material_optional = line.substring(18);
                } else if (line.startsWith("json_optional: ")) {
                    json_optional = line.substring(15);
                } else if (line.startsWith("amount_optional: ")) {
                    amount_optional = Integer.parseInt(line.substring(17));
                }
            }



            // Create a new VillagerTrade object with the parsed values
            VillagerTrade vil = new VillagerTrade(material_input, material_output, json_input, json_output, amount_input, amount_output, biomes, bannedWorlds, day_night, weather, professions, level);
            // If optional material and json are provided, set them
            if( material_optional.equals("none") )
                vil.setOptional(material_optional ,json_optional,amount_optional);


            return vil;

        } catch (IOException e) {
            getLogger().warning("Could not read file " + file.getName());
            throw new RuntimeException(e);
        }
    }

    private void updateVillagerTrades(){
        if( !getConfig().getBoolean("Enable") ) return;
        for( String world : worldsList) {
            for (Villager villagerNow : Bukkit.getWorld(world).getEntitiesByClass(Villager.class)) {

                if (!isVillagerEmployed(villagerNow)) continue;

                for (Map.Entry<String, VillagerTrade> entry : villagerTrades.entrySet()) {

                    VillagerTrade villagerTrade = entry.getValue();






                    // add trade logic if it meets the conditions, and detele the trade if they do not meet the criteria anymore
                    boolean biome = checkBiome(villagerNow, villagerTrade.getBiomes());
                    boolean bannedWorlds = checkBannedWorlds(villagerNow, villagerTrade.getBannedWorlds());
                    boolean day_night = checkDayNight(villagerNow, villagerTrade.getDayNight());
                    boolean weather = checkWeather(villagerNow, villagerTrade.getWeather());
                    boolean checkProfessions = checkProfession(villagerNow, villagerTrade.getProfessions());
                    boolean checkLevel = getVillagerLevel1(villagerTrade.getLevel()) <= villagerNow.getVillagerLevel();



                    if (biome && bannedWorlds && day_night && weather && checkProfessions && checkLevel && checkTrade(villagerNow, villagerTrade)) {
                        addCustomTrade(villagerNow, villagerTrade);
                    } else if (!biome || !bannedWorlds || !day_night || !weather || !checkProfessions) {
                        deleteCustomTrade(villagerNow, villagerTrade);
                    }
                    // checkProfessions might be a redundant check on deletion since a villager can only change profession on reset ( if the trading block is destroyed ) and it will always be the same profession ( might be good for updating )
                    // checkLevel might be a redundant check on deletion since a villager can only change level on reset ( if the trading block is destroyed ) and it will always be at that minimum level ( once a master always a master )
                }

            }
        }
    }

    private boolean checkProfession(Villager villagerNow, List<String> professions) {
        if (professions.isEmpty()) return true;
        if( professions.contains("ALL") ) return true;
        if ( professions.contains("all") ) return true;
        if( professions.contains("none") ) return false;
        if( professions.contains("NONE") ) return false;
        return professions.contains(villagerNow.getProfession().name());
    }

    private void addCustomTrade(Villager villager, VillagerTrade villagerTrade) {
        List<MerchantRecipe> trades = new ArrayList<>(villager.getRecipes());

        // Example: Add a custom trade that only appears at night
        ItemStack result = new ItemStack(Material.valueOf(villagerTrade.getMaterialOutput()), villagerTrade.getAmountOutput());
        ItemStack ingredient1 = new ItemStack(Material.valueOf(villagerTrade.getMaterialInput()), villagerTrade.getAmountInput());
        NBTItem nbtItem1 = new NBTItem(result);
        NBTItem nbtItem2 = new NBTItem(ingredient1);
        nbtItem1.mergeCompound(NBT.parseNBT(villagerTrade.getJsonOutput()));
        nbtItem2.mergeCompound(NBT.parseNBT(villagerTrade.getJsonInput()));
        result = nbtItem1.getItem();
        ingredient1 = nbtItem2.getItem();

        MerchantRecipe recipe = new MerchantRecipe(result, 0, 10, true);
        recipe.addIngredient(ingredient1);

        trades.add(recipe);
        villager.setRecipes(trades);
        //getLogger().info("Added custom trade to villager " + villager.getEntityId());
    }

    private void deleteCustomTrade(Villager villager, VillagerTrade villagerTrade){
        //getLogger().info("Deleting custom trade from villager " + villager.getEntityId());
        List<MerchantRecipe> trades = new ArrayList<>(villager.getRecipes());
        char found = 'a';
        boolean identical = false;
        MerchantRecipe recipe_delete = null;
        for (MerchantRecipe recipe : trades) {
            if (recipe.getResult().getType() == Material.valueOf(villagerTrade.getMaterialOutput()) &&
                    recipe.getIngredients().get(0).getType() == Material.valueOf(villagerTrade.getMaterialInput()) &&
                    recipe.getIngredients().get(0).getAmount() == villagerTrade.getAmountInput() &&
                    recipe.getResult().getAmount() == villagerTrade.getAmountOutput()) {
                identical = true;
            }
            if( identical && villagerTrade.getJsonInput().equals("{}") && villagerTrade.getJsonOutput().equals("{}") ) {

                trades.remove(recipe);
                break;
            }
            if( identical ){
                String json_input = villagerTrade.getJsonInput();
                String json_output = villagerTrade.getJsonOutput();
                NBTItem nbtItem = new NBTItem(recipe.getResult()); // result
                NBTItem nbtItem2 = new NBTItem(recipe.getIngredients().get(0)); // ingredient
                String json2 = nbtItem.toString();
                String json1 = nbtItem2.toString();
                if( !json_input.equals("{}") && json1.equals(json_input) )
                    found ++;
                if( !json_output.equals("{}") && json2.equals(json_output) )
                    found ++;
                if( found == 'c' ){
                    trades.remove(recipe);
                    break;
                }
            }
        }
        villager.setRecipes(trades);
    }

    private boolean checkTrade(Villager villager, VillagerTrade villagerTrade){
        // check if the villager already has the trade, to avoid duplicates
        boolean identical = false ;
        for (MerchantRecipe recipe : villager.getRecipes()) {
            // we need to check if the villager has the trade ( identical )
            // check if the villager has the trade, return false if it does
            if( recipe.getResult().getType() == Material.valueOf(villagerTrade.getMaterialOutput()) &&
                    recipe.getIngredients().get(0).getType() == Material.valueOf(villagerTrade.getMaterialInput()) &&
                    recipe.getIngredients().get(0).getAmount() == villagerTrade.getAmountInput() &&
                    recipe.getResult().getAmount() == villagerTrade.getAmountOutput() ) {
                identical = true;
                //return false; // return false if the villager has the trade
            }
            if( identical && villagerTrade.getJsonInput().equals("{}") && villagerTrade.getJsonOutput().equals("{}") ) {
                // getLogger().info("mancatiast");
                return false; // return false if the villager has the trade
            }
            if( identical ){
                String json_input = villagerTrade.getJsonInput();
                String json_output = villagerTrade.getJsonOutput();
                NBTItem nbtItem = new NBTItem(recipe.getResult()); // result
                NBTItem nbtItem2 = new NBTItem(recipe.getIngredients().get(0)); // ingredient
                String json2 = nbtItem.toString();
                String json1 = nbtItem2.toString();
//                getLogger().info("json1: " + json1);
//                getLogger().info("json2: " + json2);
//                getLogger().info("json_input: " + json_input);
//                getLogger().info("json_output: " + json_output);
                if( !json_input.equals("{}") && json1.equals(json_input) ){
                    return false;
                }
                if( !json_output.equals("{}") && json2.equals(json_output) ){
                    return false;
                }
//
            }


        }
        //getLogger().info("tare");
        return true; // return true if the villager does not have the trade
    }

    public static boolean isVillagerEmployed(Villager villager) {
        return villager.getProfession() != Villager.Profession.NONE && villager.getProfession() != Villager.Profession.NITWIT;
    } // check if the villager is employed ( not a nitwit, no unemployed villager, no baby villager )

    private boolean checkBiome(Villager villager, List< String > biomes){
        if (biomes.isEmpty()) return true;
        if( biomes.contains("ALL") ) return true;
        if ( biomes.contains("all") ) return true;
        return biomes.contains(villager.getLocation().getBlock().getBiome().name());
    } // return true if the villager is in that specific biome
    private boolean checkBannedWorlds(Villager villager, List< String > bannedWorlds){
        if (bannedWorlds.isEmpty()) return true;
        if( bannedWorlds.contains("ALL") ) return false;
        if ( bannedWorlds.contains("all") ) return false;
        if( bannedWorlds.contains("none") ) return true;
        if( bannedWorlds.contains("NONE") ) return true;
        return !bannedWorlds.contains(villager.getLocation().getWorld().getName());
    }
    private boolean checkDayNight(Villager villager, String day_night){
        if( day_night.equals("both") ) return true;
        if( day_night.equals("day") && villager.getWorld().getTime() < 13000 ) return true;
        if( day_night.equals("night") && villager.getWorld().getTime() > 13000 ) return true;
        return false;
    } // return true if the villager is in that 'specific time'
    private boolean checkWeather(Villager villager, String weather){
        if( weather.equals("any") ) return true;
        if( weather.equals("clear") && villager.getWorld().isClearWeather() ) return true;
        if( weather.equals("thunder") && villager.getWorld().hasStorm() ) return true;
        if( weather.equals("rain") && villager.getWorld().hasStorm() ) return true;
        return false;
    } // return true if the villager is in that 'specific weather'

    private int getVillagerLevel1(String s){
        return switch (s) {
            case "NOVICE" -> 1;
            case "APPRENTICE" -> 2;
            case "JOURNEYMAN" -> 3;
            case "EXPERT" -> 4;
            case "MASTER" -> 5;
            default -> 1;
        };
    }

    private void Ascii(){
    getLogger().info("____  _____ _____ _____ _____ ____  _     _  _     _     ____  _____ _____ ____  ____");
    getLogger().info("/  __\\/  __//__ __Y__ __Y  __//  __\\/ \\ |\\/ \\/ \\   / \\   /  _ \\/  __//  __//  __\\/ ___\\");
    getLogger().info("| | //|  \\    / \\   / \\ |  \\  |  \\/|| | //| || |   | |   | / \\|| |  _|  \\  |  \\/||    \\");
    getLogger().info("| |_\\\\|  /_   | |   | | |  /_ |    /| \\// | || |_\\/\\| |_\\/\\| |-||| |_//|  /_ |    /\\___ |");
    getLogger().info("\\____/\\____\\  \\_/   \\_/ \\____\\\\_/\\_\\\\__/  \\_/\\____/\\____/\\_/ \\|\\____\\\\____\\\\_/\\_\\\\____/");
    }

    public class WeatherListener implements Listener {

        @EventHandler
        public void onWeatherChange(WeatherChangeEvent event) {
            if (event.toWeatherState()) {
                // The weather is changing to rain or storm

                System.out.println("The weather is changing to rain or storm.");
            } else {
                // The weather is changing to clear
                System.out.println("The weather is changing to clear.");
            }
        }
    }


} //da s s