package com.github.hornta.trollskogen_homes.events;

import com.github.hornta.trollskogen_homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestOpenHomeEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private Home home;

  public RequestOpenHomeEvent(Home home) {
    this.home = home;
  }

  public Home getHome() {
    return home;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
