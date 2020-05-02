package com.github.hornta.trollskogen_homes.commands.argumentHandlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerHomeArgumentHandler implements IArgumentHandler {
  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    UserObject user = TrollskogenCorePlugin.getUser(prevArgs[0]);

    return TrollskogenHomesPlugin
      .getInstance()
      .getHomeManager()
      .getHomes(user.getId())
      .stream()
      .filter(h -> h.getName().toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
      .map(Home::getName)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.setValue("player_name", validationResult.getPrevArgs()[0]);
    MessageManager.setValue("home_name", validationResult.getValue());
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.PLAYER_HOME_NOT_FOUND);
  }
}
