package com.example.autorelog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("autorelog.json").toFile();

    public static ModConfig INSTANCE = new ModConfig();

    // Persistent Settings
    public boolean enabled = true;
    public double yThreshold = -5.0;
    public boolean autoReconnect = true;
    public int selectedSlot = 0;
    public int reconnectDelayTicks = 3; // Default 3 ticks (~150ms)

    // Session Runtime Flags
    public transient boolean isReconnecting = false;
    public transient boolean shouldChangeSlotOnJoin = false;

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
