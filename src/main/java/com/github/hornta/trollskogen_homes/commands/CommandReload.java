package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.Translation;
import com.github.hornta.trollskogen_homes.ConfigKey;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import org.bukkit.command.CommandSender;

public class CommandReload implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    TrollskogenHomesPlugin.getConfiguration().reload();
    Translation translation = TrollskogenHomesPlugin.getInstance().getTranslations().createTranslation(TrollskogenHomesPlugin.getConfiguration().get(ConfigKey.LANGUAGE));
    MessageManager.getInstance().setTranslation(translation);
    MessageManager.sendMessage(commandSender, MessageKey.RELOADED);
  }
}
