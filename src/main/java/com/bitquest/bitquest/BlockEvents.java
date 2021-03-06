package com.bitquest.bitquest;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Created by explodi on 11/1/15.
 * Edited by Xeyler on 11/2/15
 */
public class BlockEvents implements Listener {
	
	BitQuest bitQuest;
	
	public BlockEvents(BitQuest plugin) {
		
		bitQuest = plugin;
		
	}
	
    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
    	// If block is bedrock, cancel the event
    	if(event.getBlock().getType().equals(Material.BEDROCK)) {
    		bitQuest.error(event.getPlayer(), "Removing bedrock is not allowed!");
    		event.setCancelled(true);
    	// If player is in a no-build zone, cancel the event
    	} else if (bitQuest.canBuild(event.getBlock().getLocation(), event.getPlayer()) == false) {
			event.setCancelled(true);

			bitQuest.error(event.getPlayer(), "You may not break blocks here!");
        }
    }
	@EventHandler
	void onBlockPlace(BlockPlaceEvent event) {
		// set clan
		// first, we check if the player has permission to build
		if (bitQuest.canBuild(event.getBlock().getLocation(), event.getPlayer()) == false) {
			event.setCancelled(true);
            bitQuest.error(event.getPlayer(), "You may not place blocks here!");
		} else if (event.getBlock().getType() == Material.STANDING_BANNER) {

			if (bitQuest.areaForLocation(event.getBlock().getLocation()) != null) {
				// Banner banner=(Banner)e.getBlock();
				Block belowBlock = event.getBlock().getRelative(0, -1, 0);
				Sign sign = null;
				if (belowBlock.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
					sign = (Sign) belowBlock.getRelative(BlockFace.EAST).getState();
				}
				if (belowBlock.getRelative(BlockFace.WEST).getType().equals(Material.WALL_SIGN)) {
					sign = (Sign) belowBlock.getRelative(BlockFace.WEST).getState();
				}
				if (belowBlock.getRelative(BlockFace.NORTH).getType().equals(Material.WALL_SIGN)) {
					sign = (Sign) belowBlock.getRelative(BlockFace.NORTH).getState();
				}
				if (belowBlock.getRelative(BlockFace.SOUTH).getType().equals(Material.WALL_SIGN)) {
					sign = (Sign) belowBlock.getRelative(BlockFace.SOUTH).getState();
				}
				if (sign != null) {
					String tag = sign.getLine(0).toLowerCase();
					if (tag.length() > 12) {
						bitQuest.error(event.getPlayer(), "Clan names are limited to a maximum of 12 characters.");
					} else if (tag.length() > 0) {
						// TODO: Find out if the clan name already exists and check player is invited
						// TODO: Add a clan property to area key
						// TODO: Add a clan property to user data
					} else {
						bitQuest.error(event.getPlayer(), "Please write the name of the clan on the first line of the sign.");
					}


				}
			} else {
				bitQuest.error(event.getPlayer(), "You can only place banners in your home land.");
				event.setCancelled(true);
			}
		} else if(event.getBlock().getType().equals(Material.BEDROCK)) {
			bitQuest.error(event.getPlayer(), "Placing bedrock is not allowed!");
    		event.setCancelled(true);
		}

        
	}
    // Make a sweet block regeneration ^_^
	// TODO: Some blocks are still affected in this method...
    @EventHandler
	void onExplode(EntityExplodeEvent event) {
		
		for(Block block : event.blockList()) {
			
			if(block.getType() != Material.AIR && block.getType() != Material.CHEST) {
			
				final BlockState state = block.getState();
				
				int delay;
				
				if(block.getType().hasGravity()) {
					
					delay = 141;
					
				} else {
					
					delay = bitQuest.rand(80, 140);
					
				}
				
				// Set block to air so that no blocks drop
				block.setType(Material.AIR);
			
				// Regenerate all the blocks in a random order
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(bitQuest, new Runnable() {
				
					public void run() {

						state.update(true, false);
					
					}
				
				}, delay);
			
			}
			
		}
		
	}

	@EventHandler
	void onBlockExp(BlockExpEvent event) {
		event.setExpToDrop(0);
	}
    
}
