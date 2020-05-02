package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import io.papermc.lib.PaperLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CommandAHome implements ICommandHandler {
  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = TrollskogenCorePlugin.getUser(args[0]);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[1], user.getId());
    PaperLib.teleportAsync((Player)sender, home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}
