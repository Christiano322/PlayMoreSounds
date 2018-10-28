package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;

public class PlayerDeath implements Listener {

	PMS main;

	public PlayerDeath(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeDeath(PlayerDeathEvent e) {
		try {
			EventName event = EventName.PLAYER_DEATH;
				String name = event.toName();
				new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getEntity(), name, null, null, false, null);
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}
}
