package com.github.hornta.trollskogen_homes;

import com.github.hornta.commando.CarbonArgument;
import com.github.hornta.commando.CarbonCommand;
import com.github.hornta.commando.Commando;
import com.github.hornta.commando.ICarbonArgument;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.MessagesBuilder;
import com.github.hornta.messenger.Translation;
import com.github.hornta.messenger.Translations;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import com.github.hornta.trollskogen_homes.commands.*;
import com.github.hornta.trollskogen_homes.commands.argumentHandlers.HomeArgumentHandler;
import com.github.hornta.trollskogen_homes.commands.argumentHandlers.OpenHomePlayersArgumentHandler;
import com.github.hornta.trollskogen_homes.commands.argumentHandlers.PlayerHomeArgumentHandler;
import com.github.hornta.trollskogen_homes.commands.argumentHandlers.PlayerOpenHomeArgumentHandler;
import com.github.hornta.trollskogen_homes.config.InitialVersion;
import com.github.hornta.versioned_config.Configuration;
import com.github.hornta.versioned_config.ConfigurationBuilder;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class TrollskogenHomesPlugin extends JavaPlugin {
  private static TrollskogenHomesPlugin instance;
  private Configuration<ConfigKey> configuration;
  private Commando commando;
  private SassySpawnPlugin sassySpawn;
  private HomeManager homeManager;
  private LuckPerms luckPerms;
  private Translations translations;

  @Override
  public void onEnable() {
    instance = this;
    luckPerms = LuckPermsProvider.get();
    TrollskogenCorePlugin.getServerReady().waitFor(this);

    File cfgFile = new File(getDataFolder(), "config.yml");
    ConfigurationBuilder<ConfigKey> cb = new ConfigurationBuilder<>(this, cfgFile);
    cb.addVersion(new InitialVersion());
    configuration = cb.run();

    MessageManager messageManager = new MessagesBuilder()
      .add(MessageKey.NO_PERMISSION_COMMAND, "no_permission_command")
      .add(MessageKey.MISSING_ARGUMENTS_COMMAND, "missing_arguments_command")
      .add(MessageKey.COMMAND_NOT_FOUND, "command_not_found")
      .add(MessageKey.PLAYER_OPEN_HOME_NOT_FOUND, "player_open_home_not_found")
      .add(MessageKey.MAX_HOMES, "max_homes")
      .add(MessageKey.HOME_MAXIMUM_USAGE, "home_maximum_usage")
      .add(MessageKey.HOME_SET, "home_set")
      .add(MessageKey.SETHOME_BAD_CHARS, "sethome_bad_chars")
      .add(MessageKey.PLAYER_NOT_SET_HOME, "player_not_set_home")
      .add(MessageKey.HOME_NOT_FOUND, "home_not_found")
      .add(MessageKey.PLAYER_HOME_NOT_FOUND, "player_home_not_found")
      .add(MessageKey.OPEN_HOME_NOT_FOUND, "open_home_not_found")
      .add(MessageKey.OPEN_HOME_HOMELESS_NOT_FOUND, "open_home_homeless_not_found")
      .add(MessageKey.HOME_DELETED, "home_deleted")
      .add(MessageKey.HOMES, "homes")
      .add(MessageKey.HOMES_HOME, "homes_home")
      .add(MessageKey.HOMES_INACTIVE_HOME, "homes_inactive_home")
      .add(MessageKey.HOME_PUBLIC, "home_public")
      .add(MessageKey.HOME_PUBLIC_DISALLOW_COMMANDS, "home_public_disallow_cmds")
      .add(MessageKey.TOGGLE_HOME_COMMANDS_ALLOW, "toggle_home_cmds_allow")
      .add(MessageKey.TOGGLE_HOME_COMMANDS_DENY, "toggle_home_cmds_deny")
      .add(MessageKey.OPEN_HOME_OPENED, "open_home_opened")
      .add(MessageKey.OPEN_HOME, "open_home")
      .add(MessageKey.OPEN_HOME_RESTRICTION, "open_home_restriction")
      .add(MessageKey.CLOSE_HOME, "close_home")
      .add(MessageKey.CLOSE_HOME_CLOSED, "close_home_closed")
      .add(MessageKey.PHOME_SAFE_TELEPORT, "phome_safe_teleport")
      .add(MessageKey.PHOME_BLOCKED_COMMAND, "phome_blocked_cmd")
      .add(MessageKey.RELOADED, "reloaded")
      .build();

    translations = new Translations(this, messageManager);
    Translation translation = translations.createTranslation(configuration.get(ConfigKey.LANGUAGE));
    messageManager.setTranslation(translation);

    setupCommands();

    homeManager = new HomeManager();
    if(getServer().getPluginManager().getPlugin("SassySpawn") != null) {
      sassySpawn = (SassySpawnPlugin) getServer().getPluginManager().getPlugin("SassySpawn");
    }

    getServer().getPluginManager().registerEvents(homeManager, this);
  }

  private void setupCommands() {
    commando = new Commando();
    commando.setNoPermissionHandler((CommandSender commandSender, CarbonCommand command) -> MessageManager.sendMessage(commandSender, MessageKey.NO_PERMISSION_COMMAND));

    commando.setMissingArgumentHandler((CommandSender commandSender, CarbonCommand command) -> {
      MessageManager.setValue("usage", command.getHelpText());
      MessageManager.sendMessage(commandSender, MessageKey.MISSING_ARGUMENTS_COMMAND);
    });

    commando.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
      MessageManager.setValue("suggestions", suggestions.stream()
        .map(CarbonCommand::getHelpText)
        .collect(Collectors.joining("\n")));
      MessageManager.sendMessage(sender, MessageKey.COMMAND_NOT_FOUND);
    });

    HomeArgumentHandler homeArgumentHandler = new HomeArgumentHandler();
    ICarbonArgument homeArgument = new CarbonArgument.Builder("home")
      .setHandler(homeArgumentHandler)
      .setDefaultValue(Player.class, (CommandSender sender, String[] args) -> {
        UserObject user = TrollskogenCorePlugin.getUser(((Player)sender).getUniqueId());
        List<Home> homes = homeManager.getHomes(user.getId());
        if(homes.size() > 0) {
          return homes.get(0).getName();
        }
        return "";
      })
      .create();

    commando
      .addCommand("home")
      .withArgument(homeArgument)
      .withHandler(new CommandHome())
      .requiresPermission("ts.home")
      .preventConsoleCommandSender();

    commando
      .addCommand("sethome")
      .withArgument(
        new CarbonArgument.Builder("home")
          .setPattern(Pattern.compile("[a-z0-9_]+", Pattern.CASE_INSENSITIVE))
          .setDefaultValue(Player.class, Home.DEFAULT_HOME_NAME)
          .create()
      )
      .withHandler(new CommandSetHome())
      .requiresPermission("ts.sethome")
      .preventConsoleCommandSender();

    commando
      .addCommand("delhome")
      .withArgument(
        new CarbonArgument.Builder("home")
          .setHandler(homeArgumentHandler)
          .create()
      )
      .withHandler(new CommandDelHome())
      .requiresPermission("ts.delhome")
      .preventConsoleCommandSender();

    commando
      .addCommand("homes")
      .withHandler(new CommandHomes())
      .requiresPermission("ts.homes")
      .preventConsoleCommandSender();

    ICarbonArgument playerHomeArgumentHandler = new CarbonArgument.Builder("home")
      .setHandler(new PlayerHomeArgumentHandler())
      .dependsOn(TrollskogenCorePlugin.getPlayerArg())
      .create();

    commando
      .addCommand("ahome")
      .withHandler(new CommandAHome())
      .withArgument(TrollskogenCorePlugin.getPlayerArg())
      .withArgument(playerHomeArgumentHandler)
      .requiresPermission("ts.ahome")
      .preventConsoleCommandSender();

    commando
      .addCommand("openhome")
      .withHandler(new CommandOpenHome())
      .withArgument(homeArgument)
      .requiresPermission("ts.openhome")
      .preventConsoleCommandSender();

    commando
      .addCommand("closehome")
      .withHandler(new CommandCloseHome())
      .withArgument(homeArgument)
      .requiresPermission("ts.closehome")
      .preventConsoleCommandSender();

    commando
      .addCommand("togglehomecmds")
      .withHandler(new CommandHomeToggleCommands())
      .withArgument(homeArgument)
      .requiresPermission("ts.togglehomecmds")
      .preventConsoleCommandSender();

    OpenHomePlayersArgumentHandler openHomePlayersArgumentHandler = new OpenHomePlayersArgumentHandler();
    Bukkit.getPluginManager().registerEvents(openHomePlayersArgumentHandler, this);

    ICarbonArgument openHomePlayerArgument = new CarbonArgument.Builder("player")
      .setHandler(openHomePlayersArgumentHandler)
      .create();


    CommandPHome phome = new CommandPHome();
    Bukkit.getPluginManager().registerEvents(phome, this);
    commando
      .addCommand("phome")
      .withHandler(phome)
      .withArgument(openHomePlayerArgument)
      .withArgument(
        new CarbonArgument.Builder("home")
          .dependsOn(openHomePlayerArgument)
          .setHandler(new PlayerOpenHomeArgumentHandler())
          .setDefaultValue(CommandSender.class, (CommandSender sender, String[] args) -> {
            UserObject user = TrollskogenCorePlugin.getUser(args[0]);
            List<Home> userHomes = homeManager.getHomes(user.getId());
            for(Home home : userHomes) {
              if(home.isPublic()) {
                return home.getName();
              }
            }
            return "";
          })
          .create()
      )
      .requiresPermission("ts.phome")
      .preventConsoleCommandSender();
  }

  public Commando getCommando() {
    return commando;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return commando.handleCommand(sender, command, args);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return commando.handleAutoComplete(sender, command, args);
  }

  public HomeManager getHomeManager() {
    return homeManager;
  }

  public int getMaxHomes(UserObject user) {
    User luckUser = luckPerms
      .getUserManager()
      .getUser(user.getUuid());

    if(luckUser == null) {
      return 0;
    }

    String numHomesString = luckUser
      .getCachedData()
      .getMetaData(QueryOptions.nonContextual())
      .getMetaValue("numHomes");

    if(numHomesString == null) {
      return 0;
    }

    int numHomes = 0;
    try {
      numHomes += Integer.parseInt(numHomesString);
    } catch (NumberFormatException e) {
      getLogger().severe("`numHomes` meta value can not be parsed as an integer");
    }

    return numHomes;
  }

  public static Configuration<ConfigKey> getConfiguration() {
    return instance.configuration;
  }

  public Translations getTranslations() {
    return translations;
  }

  public static TrollskogenHomesPlugin getInstance() {
    return instance;
  }

  public SassySpawnPlugin getSassySpawn() {
    return sassySpawn;
  }
}