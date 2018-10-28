package br.net.christiano322.PlayMoreSounds.api.events;

import br.net.christiano322.PlayMoreSounds.regions.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class ExitRegionEvent extends Event implements Cancellable {

    private Player player;
    private SoundRegion region;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public ExitRegionEvent(Player player, SoundRegion region) {
        this.player = player;
        this.region = region;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public SoundRegion getRegion() {
        return this.region;
    }
}
