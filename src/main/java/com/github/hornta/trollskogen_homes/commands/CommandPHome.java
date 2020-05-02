package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.Spawn;
import com.github.hornta.sassy_spawn.SpawnManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class CommandPHome implements ICommandHandler, Listener {
  private final HashMap<UUID, Long> immortals = new HashMap<>();
  private final HashMap<UUID, Boolean> immortalsTeleported = new HashMap<>();
  private final HashMap<UUID, String> immortalsHomeOwner = new HashMap<>();
  private static final HashSet<String> blockedCmds;

  static {
    blockedCmds = new HashSet<>();
    blockedCmds.add("sethome");
    blockedCmds.add("top");
    blockedCmds.add("etop");
    blockedCmds.add("tpahere");
    blockedCmds.add("etpahere");
    blockedCmds.add("tpaccept");
    blockedCmds.add("etpaccept");
    blockedCmds.add("tpyes");
    blockedCmds.add("etpyes");
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = TrollskogenCorePlugin.getUser(args[0]);
    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getHome(args[1], user.getId());

    UserObject currentUser = TrollskogenCorePlugin.getUser((Player) sender);
    if(currentUser == user) {
      return;
    }

    Player player = (Player) sender;
    immortals.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
    immortalsHomeOwner.put(player.getUniqueId(), user.getName());
    immortalsTeleported.put(player.getUniqueId(), false);
    PaperLib.teleportAsync(player, home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    String commandWithoutSlash = event.getMessage().trim().substring(1);
    int firstSpaceIndex = commandWithoutSlash.indexOf(" ");

    String command;
    if(firstSpaceIndex == -1) {
      command = commandWithoutSlash;
    } else {
      command = commandWithoutSlash.substring(0, firstSpaceIndex).toLowerCase();
    }

    if(!blockedCmds.contains(command)) {
      return;
    }

    UserObject user = TrollskogenCorePlugin.getUser(event.getPlayer());

    Home home = TrollskogenHomesPlugin.getInstance().getHomeManager().getNearestPublicHome(user, event.getPlayer().getLocation());
    if(home == null) {
      return;
    }

    UserObject owner = TrollskogenCorePlugin.getUser(home.getOwner());

    MessageManager.setValue("player_name", owner.getName());
    MessageManager.sendMessage(event.getPlayer(), MessageKey.PHOME_BLOCKED_COMMAND);
    event.setCancelled(true);
  }

  @EventHandler
  void onPlayerDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    if (immortals.containsKey(player.getUniqueId())) {
      long expire = immortals.get(player.getUniqueId());
      if (System.currentTimeMillis() >= expire) {
        immortals.remove(player.getUniqueId());
        immortalsHomeOwner.remove(player.getUniqueId());
      } else {
        event.setCancelled(true);
        player.setFireTicks(0);

        if (TrollskogenHomesPlugin.getInstance().getSassySpawn() != null && !immortalsTeleported.get(player.getUniqueId())) {
          Spawn spawn = SassySpawnPlugin.getSpawnManager().getSpawn(SpawnManager.DEFAULT_SPAWN);
          immortalsTeleported.put(player.getUniqueId(), true);
          Bukkit.getScheduler().runTaskLater(TrollskogenHomesPlugin.getInstance(), () -> {
            PaperLib.teleportAsync(player, spawn.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setFireTicks(0);
            immortals.remove(player.getUniqueId());
            MessageManager.setValue("player_name", immortalsHomeOwner.get(player.getUniqueId()));
            MessageManager.sendMessage(player, MessageKey.PHOME_SAFE_TELEPORT);
            immortalsHomeOwner.remove(player.getUniqueId());
          }, 1);
        }

      }
    }
  }
}
