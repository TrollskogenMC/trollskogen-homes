package com.github.hornta.trollskogen_homes.commands.argumentHandlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.PrefixMatcher;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.trollskogen_homes.events.CloseHomeEvent;
import com.github.hornta.trollskogen_homes.events.LoadHomesEvent;
import com.github.hornta.trollskogen_homes.events.OpenHomeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class OpenHomePlayersArgumentHandler implements IArgumentHandler, Listener {
  private final PrefixMatcher allUsernames;

  public OpenHomePlayersArgumentHandler() {
    this.allUsernames = new PrefixMatcher();
  }

  @EventHandler
  void onLoadHomes(LoadHomesEvent event) {
    for(UserObject user : TrollskogenCorePlugin.getUserManager().getUsers()) {
      if(TrollskogenHomesPlugin.getInstance().getHomeManager().hasOpenHomes(user)) {
        allUsernames.insert(user.getName());
      }
    }
  }

  @EventHandler
  void onOpenHome(OpenHomeEvent event) {
    UserObject user = TrollskogenCorePlugin.getUser(event.getHome().getOwner());
    allUsernames.insert(user.getName());
  }

  @EventHandler
  void onCloseHome(CloseHomeEvent event) {
    UserObject user = TrollskogenCorePlugin.getUser(event.getHome().getOwner());
    if(!TrollskogenHomesPlugin.getInstance().getHomeManager().hasOpenHomes(user)) {
      allUsernames.delete(user.getName());
    }
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return new LinkedHashSet<>(allUsernames.find(argument));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.PLAYER_OPEN_HOME_NOT_FOUND);
  }
}
