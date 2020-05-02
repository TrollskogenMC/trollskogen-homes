package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import io.papermc.lib.PaperLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CommandHome implements ICommandHandler {
  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = TrollskogenCorePlugin.getUser((Player) sender);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[0], user.getId());

    if(
      TrollskogenHomesPlugin.getInstance().getHomeManager().getHomes(user.getId()).indexOf(home) >= TrollskogenHomesPlugin.getInstance().getMaxHomes(user)
    ) {
      MessageManager.sendMessage(sender, MessageKey.HOME_MAXIMUM_USAGE);
      return;
    }

    PaperLib.teleportAsync((Player)sender, home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}
