package com.github.hornta.trollskogen_homes.events;

import com.github.hornta.trollskogen_homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestCloseHomeEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final Home home;

  public RequestCloseHomeEvent(Home home) {
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
