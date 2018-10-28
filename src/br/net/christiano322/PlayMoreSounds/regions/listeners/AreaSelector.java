package br.net.christiano322.PlayMoreSounds.regions.listeners;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.regions.listeners.MC1_13.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.Event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

public class AreaSelector implements Listener {

    public static HashMap<String, Location> p1 = new HashMap<String, Location>();
    public static HashMap<String, Location> p2 = new HashMap<String, Location>();

    private PMS main;

    public AreaSelector(PMS main) {
        this.main = main;
    }

    @EventHandler
    public void MakeRegion(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("playmoresounds.region.wand.select")) {
            return;
        }
        ItemStack i = null;
		if (main.bukkitVersion.contains("1.7") | main.bukkitVersion.contains("1.8")) {
			i = p.getItemInHand();
		} else {
			i = ItemInHand.getItemInMainHand(p);
		}
		if (i != null) {
			if (i.getType() == Material.valueOf(main.getConfig().getString("SoundRegions.WandTool.Material"))) {
				if (main.getConfig().getBoolean("SoundRegions.WandTool.Glowing")) {
					if (!i.getItemMeta().hasEnchants()) {
						return;
					}
				} else {
					if (i.getItemMeta().hasEnchants()) {
						return;
					}
				}
				String m = i.getItemMeta().getDisplayName();
				if (m != null) { 
					if (m.equals(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("SoundRegions.WandTool.Name")))) {
						if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
							Location clicked = e.getClickedBlock().getLocation();
							e.setUseInteractedBlock(Result.DENY);
							if (p1.containsKey(p.getName())) {
								if (p1.get(p.getName()) == clicked) {
									return;
								}
							}
							p1.put(p.getName(), clicked);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageSender.string("Prefix") + " "
																				 + MessageSender.string("Region.PositionSelected1").replaceAll("<coordinates>",
																																			   "[X:" + clicked.getBlockX() + ", Y:" + clicked.getBlockY() + ", Z:"
																																			   + clicked.getBlockZ() + ", W:" + clicked.getWorld().getName() + "]")));
						} else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
							Location clicked = e.getClickedBlock().getLocation();
							e.setUseInteractedBlock(Result.DENY);
							if (p2.containsKey(p.getName())) {
								if (p2.get(p.getName()) == clicked) {
									return;
								}
							}
							p2.put(p.getName(), clicked);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageSender.string("Prefix") + " "
																				 + MessageSender.string("Region.PositionSelected2").replaceAll("<coordinates>",
																																			   "[X:" + clicked.getBlockX() + ", Y:" + clicked.getBlockY() + ", Z:"
																																			   + clicked.getBlockZ() + ", W:" + clicked.getWorld().getName() + "]")));
						}
					}
				}
			}
		}
	}
}
