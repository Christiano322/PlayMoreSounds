package br.net.christiano322.PlayMoreSounds.regions.listeners;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.events.*;
import br.net.christiano322.PlayMoreSounds.regions.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.io.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerMove implements Listener {

	PMS main = PMS.plugin;

    @EventHandler
    public void MakeMove(PlayerMoveEvent e) {
        final Location from = e.getFrom();
        final Location to = e.getTo();
        if ((from.getBlockX() < to.getBlockX()) | (from.getBlockY() < to.getBlockY())
			| (from.getBlockZ() < to.getBlockZ()) | (from.getBlockX() > to.getBlockX())
			| (from.getBlockY() > to.getBlockY()) | (from.getBlockZ() > to.getBlockZ())) {
			if (to.getBlockY() > from.getBlockY()) {
				if (!e.getPlayer().isOnGround()) {
					if (!e.getPlayer().isFlying()) {
						if (!to.clone().add(0, -2, 0).getBlock().getType().equals(Material.AIR)) {
							if (e.isCancelled() ? !main.sounds.getBoolean("PlayerJump.Cancellable") : true) {
								System.out.println("........m");
								new SoundPlayer(main).playSound(EventName.PLAYER_JUMP, main.soundsFile, main.sounds, e.getPlayer(), "PlayerJump", null, null, false, null);
							}
						}
					}
				}
			}
            File folder = new File(PMS.plugin.getDataFolder(), "regions");
            if (folder.exists() ? folder.listFiles().length > 0 : false) {
                for (File file : folder.listFiles()) {
                    FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                    Location P1 = new Location(Bukkit.getWorld(conf.getString("World")),
											   conf.getDouble("Locations.P1.X"), conf.getDouble("Locations.P1.Y"),
											   conf.getDouble("Locations.P1.Z"));
                    Location P2 = new Location(Bukkit.getWorld(conf.getString("World")),
											   conf.getDouble("Locations.P2.X"), conf.getDouble("Locations.P2.Y"),
											   conf.getDouble("Locations.P2.Z"));
                    if ((!isInsideARegion(file, from))
						& (isInsideARegion(file, to))) {
                        final EnterRegionEvent event = new EnterRegionEvent(e.getPlayer(),
																			new SoundRegion(conf.getString("Name"), P1, P2, file));
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            e.setCancelled(true);
                        }
                    }
                    if ((isInsideARegion(file, from))
						& (!isInsideARegion(file, to))) {
                        final ExitRegionEvent event = new ExitRegionEvent(e.getPlayer(),
																		  new SoundRegion(conf.getString("Name"), P1, P2, file));
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

	boolean isInsideARegion(File file, Location loc) {
		FileConfiguration f = YamlConfiguration.loadConfiguration(file);
		Location l1 = new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P1.X"),
								   f.getDouble("Locations.P1.Y"), f.getDouble("Locations.P1.Z"));
		Location l2 = new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P2.X"),
								   f.getDouble("Locations.P2.Y"), f.getDouble("Locations.P2.Z"));
		int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

		return loc.getBlockX() >= x1 & loc.getBlockX() <= x2 & loc.getBlockY() >= y1 & loc.getBlockY() <= y2
			& loc.getBlockZ() >= z1 & loc.getBlockZ() <= z2;
	}
}

