package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.trollskogen_homes.events.RequestSetHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommandSetHome implements ICommandHandler {
  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    Player player = (Player)sender;
    UserObject user = TrollskogenCorePlugin.getUser(player);
    Home existingHome = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[0], user.getId());

    int maxHomes = TrollskogenHomesPlugin.getInstance().getMaxHomes(user);
    if(existingHome == null && TrollskogenHomesPlugin.getInstance().getHomeManager().getHomes(user.getId()).size() == maxHomes) {
      MessageManager.setValue("num_homes", maxHomes);
      MessageManager.sendMessage(sender, MessageKey.MAX_HOMES);
      return;
    }

    Event event = new RequestSetHomeEvent(args[0], player.getLocation(), user);
    Bukkit.getPluginManager().callEvent(event);
    MessageManager.sendMessage(sender, MessageKey.HOME_SET);
  }
}
