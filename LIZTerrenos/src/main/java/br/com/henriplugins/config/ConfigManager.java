package br.com.henriplugins.config;

import br.com.henriplugins.LIZTerrenos;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final LIZTerrenos plugin;

    public ConfigManager(LIZTerrenos plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}