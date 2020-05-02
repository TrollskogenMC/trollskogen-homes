package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CommandHomes implements ICommandHandler {
  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = TrollskogenCorePlugin.getUser((Player) sender);
    String homes = TrollskogenHomesPlugin.getInstance().getHomeManager().getHomes(user.getId())
      .stream()
      .map((Home h) -> {
        MessageManager.setValue("home_name", h.getName());

        MessageKey messageType = MessageKey.HOMES_HOME;
        if(
          TrollskogenHomesPlugin.getInstance().getHomeManager().getHomes(user.getId()).indexOf(h) >= TrollskogenHomesPlugin.getInstance().getMaxHomes(user)
        ) {
          messageType = MessageKey.HOMES_INACTIVE_HOME;
        }
        String home = MessageManager.getMessage(messageType);

        if(h.isPublic()) {
          home += "§f(" + MessageManager.getMessage(MessageKey.HOME_PUBLIC);

          if(!h.isAllowCommands()) {
            home += "§f, " + MessageManager.getMessage(MessageKey.HOME_PUBLIC_DISALLOW_COMMANDS);
          }

          home += "§f)";
        }

        return home;
      })
      .collect(Collectors.joining("§f, "));

    MessageManager.setValue("homes", homes);
    MessageManager.setValue("num_homes", TrollskogenHomesPlugin.getInstance().getHomeManager().getHomes(user.getId()).size());
    MessageManager.setValue("max_homes", TrollskogenHomesPlugin.getInstance().getMaxHomes(user));
    MessageManager.sendMessage(sender, MessageKey.HOMES);
  }
}
