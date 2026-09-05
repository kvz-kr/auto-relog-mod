package com.example.autorelog;

public class ModConfig {
    public static final ModConfig INSTANCE = new ModConfig();

    public boolean enabled = true;
    public double yThreshold = -5.0;
    public boolean autoReconnect = true;
    public boolean isReconnecting = false;
    public int selectedSlot = 0;
    public boolean shouldChangeSlotOnJoin = false;
}
