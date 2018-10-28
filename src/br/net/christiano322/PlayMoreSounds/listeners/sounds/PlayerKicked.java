package br.net.christiano322.PlayMoreSounds.listeners.sounds;
import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerKicked implements Listener {
	PMS main;

	public PlayerKicked(PMS main) {
		this.main = main;
	}

	@EventHandler
	public void MakeKicked(PlayerKickEvent e) {
		if (e.isCancelled() ? !main.sounds.getBoolean("PlayerKicked.Cancellable") : true) {
			new SoundPlayer(main).playSound(EventName.PLAYER_KICKED, main.soundsFile, main.sounds, e.getPlayer(), "PlayerKicked", null, null, false, null);
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				main.logger.log("PlayerKicked (Player)" + e.getPlayer().getName() + " / (Reason) " + e.getReason() + " / (Message) " + e.getLeaveMessage());
			}
		}
	}
}
