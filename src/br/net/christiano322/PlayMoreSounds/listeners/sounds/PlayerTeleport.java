package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.*;
import org.bukkit.scheduler.*;
import br.net.christiano322.PlayMoreSounds.api.*;
import br.net.christiano322.PlayMoreSounds.api.events.*;
import br.net.christiano322.PlayMoreSounds.regions.*;

public class PlayerTeleport implements Listener {

	private PMS main;

	public PlayerTeleport(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeTeleport(final PlayerTeleportEvent e) {
		try {
			new BukkitRunnable() {
				@Override
				public void run() {
					boolean from = PlayMoreSounds.getRegionManager().isInsideARegion(e.getFrom());
					boolean to = PlayMoreSounds.getRegionManager().isInsideARegion(e.getTo());
					SoundRegion rgTo = PlayMoreSounds.getRegionManager().getRegionByLocation(e.getTo());
					SoundRegion rgFrom = PlayMoreSounds.getRegionManager().getRegionByLocation(e.getFrom());

					if (from) {
						if (!to) {
							final ExitRegionEvent event = new ExitRegionEvent(e.getPlayer(), rgFrom);
							Bukkit.getPluginManager().callEvent(event);
							if (event.isCancelled()) {
								e.setCancelled(true);
							}
						}
					} else {
						if (to) {
							final EnterRegionEvent event = new EnterRegionEvent(e.getPlayer(), rgTo);
							Bukkit.getPluginManager().callEvent(event);
							if (event.isCancelled()) {
								e.setCancelled(true);
							}
						}
					}
					if (e.getCause().equals(TeleportCause.COMMAND)) {
						Location loc = e.getTo().clone();
						if (e.isCancelled() ? main.sounds.getBoolean("PlayerTeleport.Cancellable") : true) {
							new SoundPlayer(main).playSound(EventName.PLAYER_TELEPORT, main.soundsFile, main.sounds, e.getPlayer(), "PlayerTeleport", null, loc, false, null);
						}
					}
				}
			}.runTaskLater(main, 1);
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}
}
