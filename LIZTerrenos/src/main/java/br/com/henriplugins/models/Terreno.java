package br.com.henriplugins.models;

import org.bukkit.Location;
import java.util.List;
import java.util.UUID;

public class Terreno {
    private int id;
    private String owner;
    private int size;
    private Location corner1;
    private Location corner2;
    private List<String> trustedPlayers;
    private double cost;

    public Terreno(int id, String owner, int size, Location corner1, Location corner2, List<String> trustedPlayers, double cost) {
        this.id = id;
        this.owner = owner;
        this.size = size;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.trustedPlayers = trustedPlayers;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public Location getCenter() {
        if (corner1 == null || corner2 == null) {
            return null;
        }

        double centerX = (corner1.getX() + corner2.getX()) / 2;
        double centerY = (corner1.getY() + corner2.getY()) / 2;
        double centerZ = (corner1.getZ() + corner2.getZ()) / 2;

        return new Location(corner1.getWorld(), centerX, centerY, centerZ);
    }

    public int getSize() {
        return size;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public List<String> getTrustedPlayers() {
        return trustedPlayers;
    }

    public boolean isTrusted(UUID uuid) {
        return trustedPlayers.contains(uuid.toString());
    }

    public void addTrustedPlayer(UUID uuid) {
        if (!isTrusted(uuid)) {
            trustedPlayers.add(uuid.toString());
        }
    }

    public void setTrustedPlayers(List<String> trustedPlayers) {
        this.trustedPlayers = trustedPlayers;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
