package com.github.hornta.trollskogen_homes;

import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_homes.events.CloseHomeEvent;
import com.github.hornta.trollskogen_homes.events.DeleteHomeEvent;
import com.github.hornta.trollskogen_homes.events.LoadHomesEvent;
import com.github.hornta.trollskogen_homes.events.OpenHomeEvent;
import com.github.hornta.trollskogen_homes.events.SetHomeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.util.HashMap;
import java.util.Map;

public class DynmapIntegration implements Listener {
  private MarkerSet markerSet;
  private MarkerIcon markerIcon;
  private final Map<Home, Marker> homeMarkers;
  private static final String MARKER_SET_ID = "trollskogen_homes";
  private static final String MARKER_SET_LABEL = "Homes";
  private final boolean isLoaded;

  DynmapIntegration(JavaPlugin plugin) {
    homeMarkers = new HashMap<>();

    Plugin dynmapPlugin = plugin.getServer().getPluginManager().getPlugin("dynmap");
    if(dynmapPlugin == null || !dynmapPlugin.isEnabled()) {
      isLoaded = false;
      return;
    }

    DynmapAPI api = (DynmapAPI) dynmapPlugin;

    MarkerAPI markerApi = api.getMarkerAPI();
    if(markerApi == null) {
      isLoaded = false;
      return;
    }

    isLoaded = true;

    markerSet = markerApi.getMarkerSet(MARKER_SET_ID);
    if(markerSet == null) {
      markerSet = markerApi.createMarkerSet(MARKER_SET_ID, MARKER_SET_LABEL, null, false);
    }

    markerIcon = markerApi.getMarkerIcon("house");
    if(markerIcon == null) {
      markerIcon = markerApi.getMarkerIcon(MarkerIcon.DEFAULT);
    }
  }

  public void dispose() {
    if(markerSet != null) {
      markerSet.deleteMarkerSet();
    }
    markerSet = null;
    markerIcon = null;

    homeMarkers.clear();
  }

  private void updateHomeMarker(Home home) {
    if(!isLoaded) {
      return;
    }

    if(!home.isPublic()) {
      return;
    }

    Marker marker = homeMarkers.get(home);

    if (marker == null) {
      UserObject owner = TrollskogenCorePlugin.getUser(home.getOwner());
      marker = markerSet.createMarker(
        home.getId() + "",
        "<strong>Public home</strong><br /><em>" + owner.getName() + "</em>",
        true,
        home.getLocation().getWorld().getName(),
        home.getLocation().getX(),
        home.getLocation().getY(),
        home.getLocation().getZ(),
        markerIcon,
        false
      );
      homeMarkers.put(home, marker);
    } else {
      marker.setLocation(
        home.getLocation().getWorld().getName(),
        home.getLocation().getX(),
        home.getLocation().getY(),
        home.getLocation().getZ()
      );
      marker.setLabel(home.getName());
    }
  }

  private void deleteHomeMarker(Home home) {
    if(!isLoaded) {
      return;
    }

    Marker homeMarker = homeMarkers.get(home);
    if(homeMarker != null) {
      homeMarker.deleteMarker();
    }
  }

  @EventHandler
  void onLoadHomes(LoadHomesEvent event) {
    for(Home home : event.getHomeManager().getHomes()) {
      updateHomeMarker(home);
    }
  }

  @EventHandler
  void onOpenHome(OpenHomeEvent event) {
    updateHomeMarker(event.getHome());
  }

  @EventHandler
  void onCloseHome(CloseHomeEvent event) {
    deleteHomeMarker(event.getHome());
  }

  @EventHandler
  void onSetHome(SetHomeEvent event) {
    updateHomeMarker(event.getHome());
  }

  @EventHandler
  void onDeleteHome(DeleteHomeEvent event) {
    deleteHomeMarker(event.getHome());
  }
}
