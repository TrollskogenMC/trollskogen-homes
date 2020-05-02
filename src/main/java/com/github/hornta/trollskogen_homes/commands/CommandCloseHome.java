package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.trollskogen_homes.events.RequestCloseHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommandCloseHome implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    UserObject user = TrollskogenCorePlugin.getUser((Player) commandSender);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[0], user.getId());

    if(!home.isPublic()) {
      MessageManager.setValue("home_name", home.getName());
      MessageManager.sendMessage(commandSender, MessageKey.CLOSE_HOME_CLOSED);
      return;
    }

    Event event = new RequestCloseHomeEvent(home);
    Bukkit.getPluginManager().callEvent(event);

    MessageManager.setValue("home_name", home.getName());
    MessageManager.sendMessage(commandSender, MessageKey.CLOSE_HOME);
  }
}
