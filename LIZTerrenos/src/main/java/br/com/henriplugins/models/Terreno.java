package br.com.henriplugins.models;

import java.util.UUID;

public class Terreno {
    private final UUID owner;
    private final int size;
    private final String location;

    public Terreno(UUID owner, int size, String location) {
        this.owner = owner;
        this.size = size;
        this.location = location;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }
}
