package br.com.henriplugins.database;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class DatabaseManager {

    private Connection connection;

    public void connect(String host, String port, String database, String username, String password, boolean useMySQL) throws Exception {
        if (useMySQL) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, username, password
            );
        } else {
            connection = DriverManager.getConnection("jdbc:sqlite:terrenos.db");
        }
        initializeTables();
    }

    private void initializeTables() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS terrenos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "owner_uuid TEXT, "
                + "size INTEGER, "
                + "location TEXT"
                + ");";
        connection.createStatement().execute(sql);
    }

    public double getBalance(Player player) {
        return LIZTerrenos.getInstance().getEconomy().getBalance(player);
    }
    public void saveTerreno(Terreno terreno) throws Exception {
        String sql = "INSERT INTO terrenos (owner_uuid, size, location) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, terreno.getOwner().toString()); // UUID para String
            statement.setInt(2, terreno.getSize());
            statement.setString(3, terreno.getLocation());
            statement.executeUpdate();
        }
    }
}
