package com.whyarewesoclever.betterVillagers;

import org.bukkit.plugin.java.JavaPlugin;

public final class BetterVillagers extends JavaPlugin {

    public static BetterVillagers getInstance() {
        return getPlugin(BetterVillagers.class);
    }
    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
