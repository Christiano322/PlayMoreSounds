package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class LeaveServer implements Listener {

	PMS main;

	public LeaveServer(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeLeave(PlayerQuitEvent e) {
		if (e.getPlayer().isBanned()) {
			EventName event = EventName.PLAYER_BAN;
			new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), event.toName(), null, null, true, null);
		} else {
			try {
				EventName event = EventName.LEAVE_SERVER;
				new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), event.toName(), null, null, true, null);
				if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
					System.out.println("(Message)" + e.getQuitMessage());
				}
			} catch (Exception l) {
				if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
					l.printStackTrace();
				}
			}
		}
	}
}
