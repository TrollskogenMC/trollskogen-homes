package com.github.hornta.trollskogen_homes.config;

import com.github.hornta.trollskogen_homes.ConfigKey;
import com.github.hornta.versioned_config.Configuration;
import com.github.hornta.versioned_config.IConfigVersion;
import com.github.hornta.versioned_config.Patch;
import com.github.hornta.versioned_config.Type;

public class InitialVersion implements IConfigVersion<ConfigKey> {
  @Override
  public int version() {
    return 1;
  }

  @Override
  public Patch<ConfigKey> migrate(Configuration<ConfigKey> configuration) {
    Patch<ConfigKey> patch = new Patch<>();
    patch.set(ConfigKey.LANGUAGE, "language", "swedish", Type.STRING);
    return patch;
  }
}
