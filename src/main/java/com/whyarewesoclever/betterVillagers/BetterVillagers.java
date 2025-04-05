package com.whyarewesoclever.betterVillagers;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public final class BetterVillagers extends JavaPlugin {

    public static List<String> keys = new ArrayList<>();

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

        initialiseKeys();
        initialiseMap();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateVillagerTrades, 0L, 100L);
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
            String json_input = null;
            String json_output = null;
            int amount_input = 0;
            int amount_output = 0;
            List<String> biomes = new ArrayList<>();
            List<String> bannedWorlds = new ArrayList<>();
            String day_night = "both"; // default value
            String weather = "any"; // default value

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
                }
            }

//            getLogger().info("Parsed file: " + file.getName());
//            getLogger().info("material_input: " + material_input);
//            getLogger().info("material_output: " + material_output);
//            getLogger().info("json_input: " + json_input);
//            getLogger().info("json_output: " + json_output);
//            getLogger().info("amount_input: " + amount_input);
//            getLogger().info("amount_output: " + amount_output);
//            getLogger().info("biomes: " + biomes);
//            getLogger().info("biomes is empty: " + biomes.size());
//            getLogger().info("biomes first element: " + (biomes.isEmpty() ? "none" : biomes.get(0)));
//            getLogger().info("banned_worlds: " + bannedWorlds);
//            getLogger().info("banned_worlds is empty: " + bannedWorlds.size());
//            getLogger().info("day_night: " + day_night);
//            getLogger().info("weather: " + weather);


            // Create a new VillagerTrade object with the parsed values
            return new VillagerTrade(material_input, material_output, json_input, json_output, amount_input, amount_output, biomes, bannedWorlds, day_night, weather);

        } catch (IOException e) {
            getLogger().warning("Could not read file " + file.getName());
            throw new RuntimeException(e);
        }
    }

    private void updateVillagerTrades(){
        if( !getConfig().getBoolean("Enable") ) return;
        for (Villager villagerNow : Bukkit.getWorld("world").getEntitiesByClass(Villager.class)) {

            for (Map.Entry<String, VillagerTrade> entry : villagerTrades.entrySet()) {
                VillagerTrade villagerTrade = entry.getValue();
               // Bukkit.getLogger().info("Biomes: " + villagerTrade.biomes);
                    addCustomTrade(villagerNow, villagerTrade);

            }

        }
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
        getLogger().info("Added custom trade to villager " + villager.getEntityId());
    }

    private boolean checkBiome(Villager villager, List< String > biomes){
        if (biomes.isEmpty()) return true;
        if( biomes.contains("ALL") ) return true;
        if ( biomes.contains("all") ) return true;
        return biomes.contains(villager.getLocation().getBlock().getBiome().name());
    }
    private boolean checkBannedWorlds(Villager villager, List< String > bannedWorlds){
        if (bannedWorlds.isEmpty()) return true;
        if( bannedWorlds.contains("ALL") ) return false;
        if ( bannedWorlds.contains("all") ) return false;
        return !bannedWorlds.contains(villager.getLocation().getWorld().getName());
    }
}