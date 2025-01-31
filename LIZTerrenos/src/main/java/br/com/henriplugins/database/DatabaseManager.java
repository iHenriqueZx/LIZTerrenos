package br.com.henriplugins.database;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DatabaseManager {

    private Connection connection;

    public void connect(String host, String port, String database, String username, String password, boolean useMySQL) throws Exception {
        if (useMySQL) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, username, password
            );
        } else {
            String pluginDataFolder = LIZTerrenos.getInstance().getDataFolder().getPath();
            String dbPath = pluginDataFolder + "/terrenos.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
        initializeTables();
    }

    private void initializeTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS terrenos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "owner_uuid TEXT, " +
                "size INTEGER, " +
                "location TEXT, " +
                "trusted_players TEXT, " +
                "cost DOUBLE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public double getBalance(Player player) {
        return LIZTerrenos.getInstance().getEconomy().getBalance(player);
    }

    public void saveTerreno(Player player, Terreno terreno) {
        String sql = "INSERT INTO terrenos (owner_uuid, size, location, trusted_players, cost) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, terreno.getSize());

            Location center = player.getLocation();
            int halfSize = terreno.getSize() / 2;

            Location corner1 = new Location(center.getWorld(),
                    center.getX() - halfSize, center.getY(), center.getZ() - halfSize);
            Location corner2 = new Location(center.getWorld(),
                    center.getX() + halfSize, center.getY(), center.getZ() + halfSize);

            String location = String.format(
                    "%s,%f,%f,%f,%f,%f,%f",
                    center.getWorld().getName(),
                    corner1.getX(), corner1.getY(), corner1.getZ(),
                    corner2.getX(), corner2.getY(), corner2.getZ()
            );
            statement.setString(3, location);

            statement.setString(4, String.join(",", terreno.getTrustedPlayers()));
            statement.setDouble(5, terreno.getCost());

            statement.executeUpdate();
        } catch (SQLException e) {
            LIZTerrenos.getInstance().getLogger().log(Level.SEVERE, "Erro ao salvar terreno", e);
        }
    }

    public void updateTerreno(Terreno terreno) {
        String sql = "UPDATE terrenos SET trusted_players = ? WHERE owner_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.join(",", terreno.getTrustedPlayers()));
            statement.setString(2, terreno.getOwner().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            LIZTerrenos.getInstance().getLogger().log(Level.SEVERE, "Erro ao atualizar terreno", e);
        }
    }

    public Terreno getTerrenoById(int id) {
        String query = "SELECT * FROM terrenos WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToTerreno(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTerrenoById(int id) {
        String query = "DELETE FROM terrenos WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Terreno getTerrenoByOwner(String ownerUUID) {
        String sql = "SELECT * FROM terrenos WHERE owner_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ownerUUID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToTerreno(resultSet);
                }
            }
        } catch (SQLException e) {
            LIZTerrenos.getInstance().getLogger().log(Level.SEVERE, "Erro ao buscar terreno por dono", e);
        }
        return null;
    }

    public Terreno getTerrenoByLocation(Location location) {
        String sql = "SELECT * FROM terrenos";
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String[] locParts = resultSet.getString("location").split(",");
                String worldName = locParts[0];
                double x1 = Double.parseDouble(locParts[1]);
                double y1 = Double.parseDouble(locParts[2]);
                double z1 = Double.parseDouble(locParts[3]);
                double x2 = Double.parseDouble(locParts[4]);
                double y2 = Double.parseDouble(locParts[5]);
                double z2 = Double.parseDouble(locParts[6]);

                World world = LIZTerrenos.getInstance().getServer().getWorld(worldName);

                if (world != null && isInside(location, x1, z1, x2, z2, world)) {
                    return mapResultSetToTerreno(resultSet);
                }
            }
        } catch (SQLException e) {
            LIZTerrenos.getInstance().getLogger().log(Level.SEVERE, "Erro ao buscar terreno por localização", e);
        }
        return null;
    }

    public void deleteTerreno(Terreno terreno) {
        String sql = "DELETE FROM terrenos WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, terreno.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LIZTerrenos.getInstance().getLogger().log(Level.SEVERE, "Erro ao excluir terreno", e);
        }
    }

    private boolean isInside(Location loc, double x1, double z1, double x2, double z2, World world) {
        if (!loc.getWorld().equals(world)) {
            return false;
        }
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);

        double locX = loc.getX();
        double locZ = loc.getZ();

        return locX >= minX && locX <= maxX && locZ >= minZ && locZ <= maxZ;
    }

    private Terreno mapResultSetToTerreno(ResultSet resultSet) throws SQLException {
        String ownerUUID = resultSet.getString("owner_uuid");
        int size = resultSet.getInt("size");
        String location = resultSet.getString("location");
        String trustedPlayersStr = resultSet.getString("trusted_players");
        List<String> trustedPlayers = trustedPlayersStr != null ?
                new ArrayList<>(List.of(trustedPlayersStr.split(","))) : new ArrayList<>();

        String[] locParts = location.split(",");
        String worldName = locParts[0];
        double x1 = Double.parseDouble(locParts[1]);
        double y1 = Double.parseDouble(locParts[2]);
        double z1 = Double.parseDouble(locParts[3]);
        double x2 = Double.parseDouble(locParts[4]);
        double y2 = Double.parseDouble(locParts[5]);
        double z2 = Double.parseDouble(locParts[6]);

        World world = LIZTerrenos.getInstance().getServer().getWorld(worldName);
        Location corner1 = world != null ? new Location(world, x1, y1, z1) : null;
        Location corner2 = world != null ? new Location(world, x2, y2, z2) : null;

        double cost = resultSet.getDouble("cost");

        int id = resultSet.getInt("id");

        return new Terreno(id, ownerUUID, size, corner1, corner2, trustedPlayers, cost);
    }
}
