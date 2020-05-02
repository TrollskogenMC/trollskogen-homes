package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.trollskogen_homes.events.RequestToggleCommandsEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommandHomeToggleCommands implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    UserObject user = TrollskogenCorePlugin.getUser((Player) commandSender);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[0], user.getId());
    Event event = new RequestToggleCommandsEvent(home);
    Bukkit.getPluginManager().callEvent(event);
    if(home.isAllowCommands()) {
      MessageManager.sendMessage(commandSender, MessageKey.TOGGLE_HOME_COMMANDS_ALLOW);
    } else {
      MessageManager.sendMessage(commandSender, MessageKey.TOGGLE_HOME_COMMANDS_DENY);
    }
  }
}
