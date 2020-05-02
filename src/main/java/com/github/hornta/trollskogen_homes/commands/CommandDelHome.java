package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.trollskogen_homes.events.RequestDeleteHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDelHome implements ICommandHandler {
  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = TrollskogenCorePlugin.getUser((Player)sender);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[0], user.getId());
    Bukkit.getPluginManager().callEvent(new RequestDeleteHomeEvent(home));
    MessageManager.setValue("home_name", home.getName());
    MessageManager.sendMessage(sender, MessageKey.HOME_DELETED);
  }
}
