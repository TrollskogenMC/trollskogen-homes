package com.github.hornta.trollskogen_homes.events;

import com.github.hornta.trollskogen_homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloseHomeEvent extends Event {
  private Home home;
  private static final HandlerList HANDLERS = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public CloseHomeEvent(Home home) {
    this.home = home;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public Home getHome() {
    return home;
  }
}
