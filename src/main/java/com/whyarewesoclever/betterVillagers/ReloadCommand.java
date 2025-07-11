package com.whyarewesoclever.betterVillagers;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
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
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.whyarewesoclever.betterVillagers.BetterVillagers.keys;
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
                getLogger().info("No argument provided. Try /bettervillagers reload");

            }
            return false;
        }
        tabComplete(sender, s, strings);
        if (sender instanceof Player) {
            boolean permission = sender.hasPermission("bettervillagers.commands");
            if (!permission) {
                if (strings[0].equals("reload")) {
                    if (sender.hasPermission("bettervillagers.reload"))
                        permission = true;
                }
                if (strings[0].equals("create") || strings[0].equals("set")) {
                    if (sender.hasPermission("bettervillagers.create"))
                        permission = true;
                }
            }
            if (!permission) {
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A00D0D") + "You do not have permission to use this command!");
                return false;
            }
        } // we do have permission to use the command
        if( strings.length > 1 && strings[0].equals("create") ){
            sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Too many arguments provided. Try /bettervillagers reload or /bettervillagers create");
            return false;
        }
        if( strings[0].equals("reload") ){
            sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Reloading config file .");
            BetterVillagers.getInstance().reloadConfig();
            BetterVillagers.getInstance().initialiseKeys();
            BetterVillagers.getInstance().initialiseMap();
            BetterVillagers.worldsList.addAll(BetterVillagers.getInstance().getConfig().getStringList("Worlds"));
            return true;
        }
        if( !strings[0].equals("create") && !strings[0].equals("set") ){
            sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Invalid argument provided. Try /bettervillagers reload or /bettervillagers create");
            return false;
        }
        if( !(sender instanceof Player) && strings[0].equals("create") ){
            getLogger().info("This command can only be run by a player .");
            return false;
        }

        // bettervillagers set name.yml whether rain


        // now we know that the argument is "create"


        // bettervillagers create
        if( strings.length == 4 ){ // right now strings[0].equals("set") is reddundant
            if( strings[2].equals("weather") ){
                WriteWheaterToFile(strings[1], strings[3]);
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Weather set to " + strings[3] + " for the trade " + strings[1]);
            }
            if( strings[2].equals("day_night") ){
                WriteDay_NightToFile(strings[1], strings[3]);
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Day/Night set to " + strings[3] + " for the trade " + strings[1]);
            }
            if( strings[2].equals("biomes")){
                // write to the file
                WriteBiomes(strings[1], strings[3]);
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Biomes set to " + strings[3] + " for the trade " + strings[1]);
            }
            if( strings[2].equals("professions")){
                // write to the file
                WriteProfessions(strings[1], strings[3]);
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Professions set to " + strings[3] + " for the trade " + strings[1]);
            }
            if( strings[2].equals("level")){
                // write to the file
                strings[3] = reglateLevel(strings[3]);
                WriteLevel(strings[1], strings[3]);
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Level set to " + strings[3] + " for the trade " + strings[1]);
                sender.sendMessage(net.md_5.bungee.api.ChatColor.of("#A9DE18") + "The villager needs to be at least " + strings[3] + " to trade with you .");
            }
        }
        if( strings[0].equals("create")) createCommand((Player) sender); // we do have the create command here



        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1 && args[0].startsWith("r"))
            return Collections.singletonList("reload");
        if (args.length == 1 && args[0].startsWith("c"))
            return Collections.singletonList("create");
        if (args.length == 1 && args[0].startsWith("s"))
            return Collections.singletonList("set");
        if (args.length == 2 && args[0].equals("set"))
            return keys; // here we should return the list of files in the Drops folder
        if (args.length == 3 && args[2].startsWith("w"))
            return Collections.singletonList("weather");
        if( args.length == 3 && args[2].startsWith("b"))
            return Collections.singletonList("biomes");
        if( args.length == 3 && args[2].startsWith("p"))
            return Collections.singletonList("professions");
        if (args.length >= 3 && args[2].equals("weather")) {
            if (args.length == 4 && args[3].startsWith("a"))
                return Collections.singletonList("any");
            if (args.length == 4 && args[3].startsWith("r"))
                return Collections.singletonList("rain");
            if (args.length == 4 && args[3].startsWith("c"))
                return Collections.singletonList("clear");
        }
//        if( args.length == 4 && args[2].equals("professions") && args[3].startsWith("a"))
//            return Collections.singletonList("all");
        if (args.length == 3 && args[2].startsWith("d"))
            return Collections.singletonList("day_night");

        if (args.length >= 3 && args[2].equals("day_night")) {
            if (args.length == 4 && args[3].startsWith("b"))
                return Collections.singletonList("both");
            if (args.length == 4 && args[3].startsWith("d"))
                return Collections.singletonList("day");
            if (args.length == 4 && args[3].startsWith("n"))
                return Collections.singletonList("night");
        }
        return Collections.emptyList();
    }

    public void createCommand(Player player){

        player.playSound(player.getLocation(), org.bukkit.Sound.UI_LOOM_TAKE_RESULT, 10, 1);
        // create the command
        Inventory inventory = Bukkit.createInventory(player, 9 * 6, ChatColor.DARK_AQUA + "ᴄʀᴇᴀᴛᴇ ᴄᴜꜱᴛᴏᴍ ᴛʀᴀᴅᴇꜱ");
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        NBTItem nbtItem_black = new NBTItem(item);
        nbtItem_black.mergeCompound(NBT.parseNBT("{display:{Lore:['[\"\",{\"text\":\".\",\"italic\":false,\"color\":\"black\"}]']}}"));

        for( int i = 0; i < 9 * 6; i++){
            inventory.setItem(i, nbtItem_black.getItem());
        }
        item = new ItemStack(Material.BARRIER);
        NBTItem nbtItem1 = new NBTItem(item);

        nbtItem1.mergeCompound(NBT.parseNBT("{display:{Name:'[\"\",{\"text\":\"Close\",\"italic\":false}]',Lore:['[\"\"]','[\"\",{\"text\":\"Leave the menu\",\"italic\":false}]']}}"));
        inventory.setItem(49, nbtItem1.getItem());


        item = new ItemStack(Material.AIR);
        inventory.setItem(28, item);
        inventory.setItem(29, item); // for the second item, if the player wants to trade two items with the villager ( a book and a diamond for a enchanted book )
        inventory.setItem(34, item);
        item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        NBTItem nbtItem2 = new NBTItem(item);
        nbtItem2.mergeCompound(NBT.parseNBT("{display:{Name:'[\"\",{\"text\":\"Create Trade\",\"italic\":false,\"color\":\"yellow\"}]',Lore:['[\"\"]','[\"\",{\"text\":\"Create a trade configuration for the items selected.\",\"italic\":false,\"color\":\"white\"}]','[\"\",{\"text\":\"Check the folder \\'BetterVillagers\\' to see the .yml file .\",\"italic\":false,\"color\":\"white\"}]']}}"));
        inventory.setItem(31, nbtItem2.getItem());

        getServer().getPluginManager().registerEvents(this, BetterVillagers.getInstance());
        //inventory.setItem(3, item );
        player.openInventory(inventory);
        // if ( ( player.getOpenInventory().getTopInventory().equals(inventory) ))
        //  BetterVillagers.glass++;
    }

    boolean checkInventoryComplete(Inventory inventory){
        int i =  28;
        boolean flag1 = true;
        boolean flag2 = true;
        if( inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            flag1 = false;
        i = 29;
        if( inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            flag2 = false;
        i = 34;
        if( !(flag1 || flag2) ) // if neither slot 28 nor 29 is filled, the trade cannot be created
            return false;
        return inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {



        Player player = (Player) event.getWhoClicked();
        InventoryView view = player.getOpenInventory();
        Inventory inventory = event.getInventory();
        //boolean block = !view.getTitle().equals(ChatColor.DARK_AQUA + "ᴄʀᴇᴀᴛᴇ ᴄᴜꜱᴛᴏᴍ ᴛʀᴀᴅᴇꜱ");
        boolean block = view.getTitle().equals(ChatColor.DARK_AQUA + "ᴄʀᴇᴀᴛᴇ ᴄᴜꜱᴛᴏᴍ ᴛʀᴀᴅᴇꜱ");

        if( !block) return;

        if (event.getInventory().getHolder() instanceof Player) {

            NBTItem nbtItem_now = new NBTItem(event.getCurrentItem());
            String json_now = nbtItem_now.toString();
            String compare = "{display:{Lore:['[\"\",{\"text\":\".\",\"italic\":false,\"color\":\"black\"}]']}}";

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE && json_now.equals(compare)) {
                event.setCancelled(true);
            }
            if( event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER && json_now.contains("Leave the menu") ){
                event.setCancelled(true);
                player.closeInventory();
                HandlerList.unregisterAll(this);
            }
            if( event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EXPERIENCE_BOTTLE && json_now.contains("Create Trade") ){

                event.setCancelled(true);

                if( !checkInventoryComplete(inventory) ){
                    player.closeInventory();
                    player.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Please fill all the air slots with the items you want to create a trade .\n First one is what you give the villager, second one is what you get .");
                }
                else{
                    player.sendMessage(net.md_5.bungee.api.ChatColor.of("#00FF00") + "[BetterVillagers] : " + net.md_5.bungee.api.ChatColor.of("#A9DE18") + "Trade created successfully .");
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_BREAK, 10, 1);
                    // here we should create the trade file and save it to the plugin's directory
                    int count = 0;

                    // get the item from index 28 and 34
                    ItemStack item1 = inventory.getItem(28);
                    ItemStack item2 = inventory.getItem(34);
                    ItemStack iitemoptional = inventory.getItem(29); // this is the second item, if the player wants to trade two items with the villager ( a book and a diamond for a enchanted book )

                    if( item1 != null && item1.getType() != Material.AIR )
                        count++;
                    if( iitemoptional != null && iitemoptional.getType() != Material.AIR )
                        count++;
                    if( item1 == null || item1.getType() == Material.AIR) item1 = iitemoptional; // this is to hande if the items is put in either of the two slots, but we only have one item to trade
                    if( iitemoptional == null || iitemoptional.getType() == Material.AIR) iitemoptional = item1; // this is to hande if the items is put in either of the two slots, but we only have one item to trade

                    NBTItem nbtItem1 = new NBTItem(item1);
                    NBTItem nbtItem2 = new NBTItem(item2);
                    NBTItem nbtItemOptional = new NBTItem(iitemoptional);
                    String json1 = nbtItem1.toString();
                    String json2 = nbtItem2.toString();
                    String jsonOptional ;
                    jsonOptional = (count == 2) ? nbtItemOptional.toString() : "{}"; // if we have two items, we will use the second item, otherwise we will use an empty json object

                    // save the json to the config file
                    player.closeInventory();
                    HandlerList.unregisterAll(this);
                    // logic to parse the object and save it to the config file ( json for each item )
                    String name_id = item1.getType().name() + "_" + item2.getType().name(); // name of the file
                    name_id = generateUniqueId(name_id);
                    int amount1 = item1.getAmount();
                    int amount2 = item2.getAmount();
                    int amountOptional ;
                    amountOptional = (count == 2) ? iitemoptional.getAmount() : 0;
                    try {
                        java.nio.file.Files.createFile(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + name_id + ".yml").toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    WriteToFile(name_id, item1.getType().name(), item2.getType().name(), json1, json2, amount1, amount2, jsonOptional,amountOptional, iitemoptional.getType().name() );
                }



            }
        }
    }

    private String generateUniqueId(String name_id){
        File folder = new File(BetterVillagers.getInstance().getDataFolder(), "Drops");
        int i = 0;
        while (true) {
            File[] files = folder.listFiles();
            boolean found = false;
            for (File file : files) {
                if (file.getName().equals(name_id + (i == 0 ? "" : i )+ ".yml") ) {
                    i++;
                    found = true;
                    break;
                }
            }
            if (!found) return name_id + (i == 0 ? "" : i);
        }
    }

    private void WriteToFile(String fileName,String mat1, String mat2, String json1, String json2, int amount1, int amount2, String jsonOptional, int amountOptional, String matOptional) {
        // write the json to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml"))) {
            writer.write("material_input: " + mat1 + "\n");
            writer.write("amount_input: " + amount1 + "\n");
            writer.write("json_input: " + json1 + "\n");
            if( amountOptional > 0 ) {
                writer.write("material_input_optional: " + matOptional + "\n");
                writer.write("amount_input_optional: " + amountOptional + "\n");
                writer.write("json_input_optional: " + jsonOptional + "\n");
            }
            else{
                writer.write("material_input_optional: none\n");
                writer.write("amount_input_optional: 0\n");
                writer.write("json_input_optional: {}\n");
            }
            writer.write("material_output: " + mat2 + "\n");
            writer.write("amount_output: " + amount2 + "\n");
            writer.write("json_output: " + json2 + "\n");
            writer.write("biomes: [all]\n"); // default value is empty ( that means all biomes )
            writer.write("bannedWorlds: [none]\n"); // default value is empty ( that means no worlds are banned )
            writer.write("day_night: both\n"); // default value is both ( that means it can be traded at any time )
            writer.write("weather: any\n"); // default value is any ( that means it can be traded at any weather )
            writer.write("professions: [all]\n"); // default value is all ( that means it can be traded by any villager )
            writer.write("level: NOVICE\n"); // default value is novice ( that means it can be traded by any villager )
          //  writer.write("level: NOVICE\n"); // any level is fine by default

        } catch (IOException e) {
            getLogger().warning("Could not write to file " + fileName + ".yml");
            throw new RuntimeException(e);
        }
    }

    private void WriteWheaterToFile(String fileName, String weather) {
        // write the json to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml"), true)) {

            List<String> lines = java.nio.file.Files.readAllLines(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath());

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("weather: ")) {
                    lines.set(i, "weather: " + weather);
                    //getLogger().info(lines.get(i));
                }

            } // da
            java.nio.file.Files.write(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath(), lines);

            // this is decent, but it does delete the file and create a new one with the updated content ( not ideal )
            // since we are using the same file, we should just update the content of the file cause we lose the 'date created' and 'last modified' attributes


        } catch (IOException e) {
            getLogger().warning("Could not write to file " + fileName + ".yml");
            throw new RuntimeException(e);
        }
    }

    private void WriteDay_NightToFile(String fileName, String day_night){
        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml"), true)) {

            List<String> lines = java.nio.file.Files.readAllLines(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath());

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("day_night: ")) {
                    lines.set(i, "day_night: " + day_night );
                    //getLogger().info(lines.get(i));
                }

            }
            java.nio.file.Files.write(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath(), lines);



        } catch (IOException e) {
            getLogger().warning("Could not write to file " + fileName + ".yml");
            throw new RuntimeException(e);
        }
    }

    private void WriteBiomes(String fileName, String biomes){

        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml"), true)) {

            List<String> lines = java.nio.file.Files.readAllLines(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath());

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("biomes: ")) {
                    lines.set(i, "biomes: [" + biomes + "]");
                    //getLogger().info(lines.get(i));
                }

            }
            java.nio.file.Files.write(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath(), lines);



        } catch (IOException e) {
            getLogger().warning("Could not write to file " + fileName + ".yml");
            throw new RuntimeException(e);
        }

    }//da

    private void WriteProfessions(String fileName, String professions) {

        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml"), true)) {

            List<String> lines = java.nio.file.Files.readAllLines(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath());

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("professions: ")) {
                    lines.set(i, "professions: [" + professions + "]");
                    //getLogger().info(lines.get(i));
                }

            }
            java.nio.file.Files.write(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath(), lines);



        } catch (IOException e) {
            getLogger().warning("Could not write to file " + fileName + ".yml");
            throw new RuntimeException(e);
        }

    }

    private void WriteLevel(String fileName, String level){

        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml"), true)) {

            List<String> lines = java.nio.file.Files.readAllLines(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath());

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("level:")) {
                    lines.set(i, "level: " + level );
                    //getLogger().info(lines.get(i));
                }

            }
            java.nio.file.Files.write(new java.io.File(BetterVillagers.getInstance().getDataFolder(), "Drops/" + fileName + ".yml").toPath(), lines);



        } catch (IOException e) {
            getLogger().warning("Could not write to file " + fileName + ".yml");
            throw new RuntimeException(e);
        }

    }
    
    private String reglateLevel(String level){
        return switch (level) {
            case "2" -> "APPRENTICE";
            case "3" -> "JOURNEYMAN";
            case "4" -> "MASTER";
            case "5" -> "EXPERT";
            default -> level;
        };

    } 



}