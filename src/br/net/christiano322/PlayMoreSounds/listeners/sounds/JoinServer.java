package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.*;

public class JoinServer implements Listener {

	PMS main;

	public JoinServer(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeJoin(PlayerLoginEvent e) {
		final Player p = e.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (p.isOnline()) {
					if (p.hasPlayedBefore()) {
						EventName event = EventName.JOIN_SERVER;
						String name = event.toName();
						new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, name, null, null, true, null);
					} else {
						EventName event = EventName.FIRST_JOIN;
						String name = event.toName();
						new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, name, null, null, true, null);
					}
				}
			}
		}.runTaskLater(main, 1);
		if (main.updateFound) {
			if (p.hasPermission("playmoresounds.update.joinmessage")) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
																	 "&a* PlayMoreSounds has a new update available! *\n > " + main.updateLink));
			}
		}
		if (main.getConfig().getBoolean("EnableSoundsAfterRelog")) {
			if (main.ignoredPlayers.contains(p.getName())) {
				main.ignoredPlayers.remove(p.getName());
			}
		}
	}
}
