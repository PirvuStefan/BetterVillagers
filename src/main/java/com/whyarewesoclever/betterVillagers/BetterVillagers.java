package com.whyarewesoclever.betterVillagers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BetterVillagers extends JavaPlugin {

    public static List<String> keys;

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


        //getServer().getPluginManager().registerEvents(new ReloadCommand("bettervillagers").onInventoryClick(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BetterVillagers has been disabled!");
    }

    private void initialiseKeys(){
        File folder = new File(getDataFolder(), "drops");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String key = file.getName().replace(".yml", "");
                getLogger().info("Key: " + key);
                keys.add(key);
            }
        }
    }
}




