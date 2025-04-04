package com.whyarewesoclever.betterVillagers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BetterVillagers has been disabled!");
    }

    private void initialiseKeys(){

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
                    String[] biomeArray = line.substring(8).split(", ");
                    for (String biome : biomeArray) {
                        biomes.add(biome);
                    }
                } else if (line.startsWith("banned_worlds: ")) {
                    String[] bannedWorldsArray = line.substring(16).split(", ");
                    for (String world : bannedWorldsArray) {
                        bannedWorlds.add(world);
                    }
                } else if (line.startsWith("day_night: ")) {
                    day_night = line.substring(11);
                } else if (line.startsWith("weather: ")) {
                    weather = line.substring(9);
                }
            }


            return new VillagerTrade(material_input, material_output, json_input, json_output, amount_input, amount_output, biomes, bannedWorlds, day_night, weather);

        } catch (IOException e) {
            getLogger().warning("Could not read file " + file.getName());
            throw new RuntimeException(e);
        }
    }
}