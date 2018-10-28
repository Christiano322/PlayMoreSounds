package br.net.christiano322.PlayMoreSounds.api.events;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class ToggleSoundsEvent extends Event implements Cancellable {

    private Player affected;
    private Player controller;
    private boolean result;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public ToggleSoundsEvent(Player affected, Player controller, boolean result) {
        this.affected = affected;
        this.controller = controller;
        this.result = result;
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

    public Player getAffected() {
        return this.affected;
    }

    public Player getController() {
        return this.controller;
    }

    public boolean getToggleResult() {
        return this.result;
    }
}
