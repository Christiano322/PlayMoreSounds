package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerDrop implements Listener {

	PMS main;

	public PlayerDrop(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeDrop(PlayerDropItemEvent e) {
		try {
			EventName event = EventName.PLAYER_DROP;
			String name = event.toName();
			if (e.isCancelled()) {
				if (main.sounds.getBoolean(name + ".Cancellable")) {
					return;
				}
			}
			new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}
}
