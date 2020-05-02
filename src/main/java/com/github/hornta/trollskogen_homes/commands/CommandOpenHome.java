package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.trollskogen_homes.events.RequestOpenHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommandOpenHome implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    UserObject user = TrollskogenCorePlugin.getUser((Player) commandSender);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[0], user.getId());

    if(home.isPublic()) {
      MessageManager.setValue("home_name", home.getName());
      MessageManager.sendMessage(commandSender, MessageKey.OPEN_HOME_OPENED);
      return;
    }

    if(TrollskogenHomesPlugin.getInstance().getHomeManager().getHomes(user.getId()).stream().filter(Home::isPublic).count() == 1) {
      MessageManager.setValue("max_open_homes", 1);
      MessageManager.sendMessage(commandSender, MessageKey.OPEN_HOME_RESTRICTION);
      return;
    }

    Event event = new RequestOpenHomeEvent(home);
    Bukkit.getPluginManager().callEvent(event);

    // TODO: Callback for messages
    MessageManager.setValue("home_name", home.getName());
    MessageManager.sendMessage(commandSender, MessageKey.OPEN_HOME);
  }
}
