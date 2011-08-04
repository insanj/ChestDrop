/*
 Created by Julian Weiss (insanj), updates frequent on Google+ (and sometimes Twitter)!

 Please do not modify or decompile at any date, but feel free to distribute with credit.
 Production began on Thursday, August 4th, 2011.
 Last edited on: 8/4/11

 ChestDrop 1.0!
 Special thanks to: 
 		CainFoool, for the idea and feature designs!

 		
 Works with the current CraftBukkit Build (#1000).
 All other information should be available at bukkit.org under ChestDrop.

 Currently supports:
		Permissions plugin, version 3.1.6!

 THIS VERSION CURRENT HAS ONE CLAS:
			ChestDrop.java

*/

package me.insanj.ChestDrop;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ChestDrop extends JavaPlugin
{
	private static final Logger log = Logger.getLogger("Minecraft");
	private static final String version = "1.0";
	
	public static boolean permissions;
	public PermissionHandler permissionHandler;
	
	@Override
	public void onEnable(){		
		log.info("{ChestDrop} plugin version " + version + " has successfully started.");
		setupPermissions();
	}//end method onEnable()
	
	@Override
	public void onDisable() {
		log.info("{ChestDrop} plugin version " + version + " disabled.");
	}//end method onDisable()
	
	private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	      
	      if (this.permissionHandler == null) {
	          if (permissionsPlugin != null) 
	              this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	          
	          else {
	              log.warning("{ChestDrop} could not detect a Permissions system, defaulting to OP usage.");
	              permissions = false;
	          } 
	      }//end if
	 }//end setupPermissions();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
	{
		if( commandLabel.equalsIgnoreCase("store") ){
			if( sender.isOp() || (permissions == true && permissionHandler.has((Player) sender, "ChestDrop.use")) ){
				sender.sendMessage(ChatColor.GOLD + "Sorting inventory...");
				storeInventory((Player) sender);
			}
			
			else
				sender.sendMessage(ChatColor.RED + "You do not have the permissions to use that command!");
		}//end if
			
		return true;
		
	}//end method onCommand()
	
	void storeInventory(Player player){
		
		ItemStack[] preInventory = player.getInventory().getContents();
		Block target = player.getTargetBlock(null, 100);
		Location freeTarget = new Location(target.getWorld(), target.getX(), target.getY() + 1, target.getZ());		
		Block targetBlock = freeTarget.getBlock();

		if( targetBlock.getTypeId() != 0 ){
			player.sendMessage(ChatColor.RED + "Sorry, the location you are looking at is already occupied by a block!");
			return;
		}
		
		targetBlock.setType(Material.CHEST);
		Chest chest = (Chest) targetBlock.getState();
		
		//Clears any air in the inventory array...
		ArrayList<ItemStack> airedInv = new ArrayList<ItemStack>();
		for(int i = 0; i < preInventory.length; i++)
			airedInv.add(preInventory[i]);
		
		while(airedInv.contains(null))
			airedInv.remove(null);
		
		ItemStack[] inventory = new ItemStack[airedInv.size()];
		for(int i = 0; i < inventory.length; i++)
			inventory[i] = airedInv.get(i);
		
		//If the inventory size of the player is NOT greater than the inventory size of a chest...
		if( !(inventory.length > chest.getInventory().getSize()) ){
			for(int i = 0; i < inventory.length; i++)
				chest.getInventory().addItem(inventory[i]);
		}
		
		else{
						
			int i = 0;
			Location freeTarget2;
			targetBlock.setType(Material.CHEST);
			
			if(player.getLocation().getX() == freeTarget.getX())
				freeTarget2 = new Location(target.getWorld(), target.getX(), target.getY() + 1, target.getZ() + 1);
			else
				freeTarget2 = new Location(target.getWorld(), target.getX() + 1, target.getY() + 1, target.getZ());

			if( freeTarget2.getBlock().getTypeId() != 0 ){
				player.sendMessage(ChatColor.RED + "Sorry, the location you are looking at is already occupied by a block!");
				return;
			}
			
			Block targetBlock2 = freeTarget2.getBlock();
			targetBlock2.setType(Material.CHEST);
			Chest chest2 = (Chest) targetBlock2.getState();

			for( ; i < chest.getInventory().getSize(); i++)
				chest.getInventory().addItem(inventory[i]);
			for( ; i < chest2.getInventory().getSize(); i++)
				chest.getInventory().addItem(inventory[i]);
		
		}//end else
		
		player.getInventory().clear();
		player.sendMessage(ChatColor.GREEN + "Your inventory has been stored successfully!");
				
	}//end storeInventory
	
}//end class TreeOctopus


/***********************************Contents of "plugin.yml":*******************************
name: ChestDrop
version: 1.0
author: insanj
main: me.insanj.ChestDrop.ChestDrop
description: Stores the users inventory into a newly generated chest where they are looking.
website:

commands:
  store:
    permissions: ChestDrop.use
    description: Stores inventory in chest.
    usage: /<command>

******************************************************************************************/