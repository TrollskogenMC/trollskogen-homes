package com.github.hornta.trollskogen_homes.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.MessengerException;
import com.github.hornta.messenger.Translation;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.trollskogen_homes.ConfigKey;
import com.github.hornta.trollskogen_homes.MessageKey;
import com.github.hornta.trollskogen_homes.TrollskogenHomesPlugin;
import com.github.hornta.versioned_config.ConfigurationException;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class CommandReload implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    try {
      TrollskogenHomesPlugin.getConfiguration().reload();
    } catch (ConfigurationException e) {
      TrollskogenHomesPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to reload configuration. Reason: " + e.getMessage(), e);
      return;
    }
    Translation translation;
    try {
      translation = TrollskogenHomesPlugin.getInstance().getTranslations().createTranslation(TrollskogenHomesPlugin.getConfiguration().get(ConfigKey.LANGUAGE));
    } catch (MessengerException e) {
      TrollskogenHomesPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to reload translations. Reason: " + e.getMessage(), e);
      return;
    }
    MessageManager.getInstance().setTranslation(translation);
    MessageManager.sendMessage(commandSender, MessageKey.RELOADED);
  }
}
