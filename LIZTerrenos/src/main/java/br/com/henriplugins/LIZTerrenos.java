package br.com.henriplugins;

import br.com.henriplugins.commands.ComprarCommand;
import br.com.henriplugins.database.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LIZTerrenos extends JavaPlugin {

    private static LIZTerrenos instance;
    private Economy economy;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        // Configuração padrão
        saveDefaultConfig();

        // Configurar Vault
        if (!setupEconomy()) {
            getLogger().severe("Vault não encontrado! Desabilitando o plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Inicializar banco de dados
        databaseManager = new DatabaseManager();
        try {
            boolean useMySQL = getConfig().getBoolean("MySQL");
            String host = getConfig().getString("mysql.host");
            String port = getConfig().getString("mysql.port");
            String database = getConfig().getString("mysql.database");
            String username = getConfig().getString("mysql.username");
            String password = getConfig().getString("mysql.password");

            databaseManager.connect(host, port, database, username, password, useMySQL);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Erro ao conectar ao banco de dados!");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Registrar comandos
        getCommand("comprar").setExecutor(new ComprarCommand());

        getLogger().info("LIZTerrenos habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LIZTerrenos desabilitado!");
    }

    public static LIZTerrenos getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
