package com.whyarewesoclever.betterVillagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class ReloadCommand extends BukkitCommand implements Listener {


        public ReloadCommand(String name) {
            super(name);
            this.setDescription("Create custom villager trades .\n Run the command /bettervillagers create .\nReload the config file to apply changes . \n Requires permission bettervillagers.commands");
            this.setUsage("\n/bettervillagers reload\n/bettervillagers create");
            this.setPermission("bettervillagers.commands");
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] strings){
            if (strings.length == 0) {
                if (!(sender instanceof Player)) {
                    getLogger().info("No argument provided. Try /bettersniffer reload");

                }
                return false;
            }
            tabComplete(sender, s, strings);
            if (sender instanceof Player) {
                boolean permission = sender.hasPermission("bettervillagers.commands");
                if (!permission) {
                    if (s.equals("reload")) {
                        if (sender.hasPermission("bettervillagers.reload"))
                            permission = true;
                    }
                    if (s.equals("create")) {
                        if (sender.hasPermission("bettervillagers.create"))
                            permission = true;
                    }
                }
                if (!permission) {
                    sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A00D0D") + "You do not have permission to use this command!");
                    return false;
                }
            } // we do have permission to use the command
            if( strings.length > 1) {
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Too many arguments provided. Try /bettervillagers reload or /bettervillagers create");
                return false;
            }
            if( strings[0].equals("reload") ){
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Reloading config file .");
                BetterVillagers.getInstance().reloadConfig();
                return true;
            }
            if( !strings[0].equals("create") ){
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Invalid argument provided. Try /bettervillagers reload or /bettervillagers create");
                return false;
            }
            if( !(sender instanceof Player)){
                getLogger().info("This command can only be run by a player .");
                return false;
            }

            // now we know that the argument is "create"
            createCommand((Player) sender);
            //BetterVillagers.glass++;
            getLogger().info("BetterVillagers.glass = " + BetterVillagers.glass);
            return true;
        }

        @Override
        public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {

            if (args.length == 1 && args[0].startsWith("r")) {
                return Collections.singletonList("reload");
            }
            if( args.length == 1 && args[0].startsWith("c") ){
                return Collections.singletonList("create");
            }
            return Collections.emptyList();
        }

        public void createCommand(Player player){
            // create the command
             Inventory inventory = Bukkit.createInventory(player, 9 * 6, ChatColor.DARK_AQUA + "ᴄʀᴇᴀᴛᴇ ᴄᴜꜱᴛᴏᴍ ᴛʀᴀᴅᴇꜱ");
             ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
             for( int i = 0; i < 9 * 6; i++){
                 inventory.setItem(i, item);
             }

             getServer().getPluginManager().registerEvents(this, BetterVillagers.getInstance());
             //inventory.setItem(3, item );
             player.openInventory(inventory);
            if ( ( player.getOpenInventory().getTopInventory().equals(inventory) ))
                BetterVillagers.glass++;
        }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

            getLogger().info("BetterVillagers.glass = " + BetterVillagers.glass);

            Player player = (Player) event.getWhoClicked();
            InventoryView view = player.getOpenInventory();
            boolean block = !view.getTitle().equals(ChatColor.DARK_AQUA + "ᴄʀᴇᴀᴛᴇ ᴄᴜꜱᴛᴏᴍ ᴛʀᴀᴅᴇꜱ");
        if (event.getInventory().getHolder() instanceof Player && block) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        }
    }

}
