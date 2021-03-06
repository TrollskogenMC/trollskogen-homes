package com.github.hornta.trollskogen_homes.events;

import com.github.hornta.trollskogen_homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenHomeEvent extends Event {
  private final Home home;
  private static final HandlerList HANDLERS = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public OpenHomeEvent(Home home) {
    this.home = home;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public Home getHome() {
    return home;
  }
}
