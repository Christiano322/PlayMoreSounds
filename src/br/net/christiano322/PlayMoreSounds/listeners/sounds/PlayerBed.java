package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerBed implements Listener {

	PMS main;

	public PlayerBed(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeBedEnter(PlayerBedEnterEvent e) {
		try {
			EventName event = EventName.BED_ENTER;
			String name = event.toName();
			if (e.isCancelled()) {
				if (main.sounds.getBoolean(name + ".Cancellable")) {
					return;
				}
			}
			new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, e.getPlayer().getLocation(), false,  null);
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				System.out.println("(Bed) " + e.getBed());
			}
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeBedLeave(PlayerBedLeaveEvent e) {
		try {
			EventName event = EventName.BED_LEAVE;
			String name = event.toName();
			new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				System.out.println("(Bed) " + e.getBed());
			}
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}
}
