package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerFlight implements Listener {

	PMS main;

	public PlayerFlight(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeFlyLevitate(PlayerToggleFlightEvent e) {
		try {
			Player p = e.getPlayer();
			if (p.isFlying()) {
				EventName ev = EventName.PLAYER_FLIGHT_STOP;
				if (e.isCancelled()) {
					if (main.sounds.getBoolean(ev.toName() + ".Cancellable")) {
						return;
					}
				}
				new SoundPlayer(main).playSound(ev, main.soundsFile, main.sounds, e.getPlayer(), ev.toName(), null, null, false, null);
			} else {
				EventName ev = EventName.PLAYER_FLIGHT;
				if (e.isCancelled()) {
					if (main.sounds.getBoolean(ev.toName() + ".Cancellable")) {
						return;
					}
				}
				new SoundPlayer(main).playSound(ev, main.soundsFile, main.sounds, e.getPlayer(), ev.toName(), null, null, false, null);
			}
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
			ErrorReport.errorReport(l, "PlayerFlight Exception:");
		}
	}
}
