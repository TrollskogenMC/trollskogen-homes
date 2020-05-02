package com.github.hornta.trollskogen_homes.deserializers;

import com.github.hornta.trollskogen_homes.Home;
import com.github.hornta.trollskogen_homes.HomeManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class PostedHomeDeserializer implements JsonDeserializer<Home> {
  @Override
  public Home deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    JsonObject json = elem.getAsJsonObject().get("posted").getAsJsonObject();
    return HomeManager.parseHome(json);
  }
}
