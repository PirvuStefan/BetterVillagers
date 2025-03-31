package com.whyarewesoclever.betterVillagers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class BetterVillagers extends JavaPlugin {


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

        //getServer().getPluginManager().registerEvents(new ReloadCommand("bettervillagers").onInventoryClick(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BetterVillagers has been disabled!");
    }
}
