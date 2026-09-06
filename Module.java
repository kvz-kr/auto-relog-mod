package com.example.autorelog.gui;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public enum Category {
        COMBAT("⚔ COMBAT"),
        MISC("⊞ MISC"),
        DONUT("🍩 DONUT"),
        BASEFINDING("🧭 BASEFINDING"),
        RENDER("👁 RENDER"),
        CLIENT("⚙ CLIENT");

        public final String name;
        Category(String name) { this.name = name; }
    }

    public final String name;
    public final Category category;
    public boolean enabled;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public static List<Module> getAllModules() {
        List<Module> list = new ArrayList<>();

        // COMBAT
        list.add(new Module("AIM ASSIST", Category.COMBAT));
        list.add(new Module("ANCHOR MACRO", Category.COMBAT));
        list.add(new Module("AUTO CRYSTAL", Category.COMBAT));
        list.add(new Module("AUTO DOUBLE HAND", Category.COMBAT));
        list.add(new Module("AUTO HIT CRYSTAL", Category.COMBAT));
        list.add(new Module("AUTO INV TOTEM", Category.COMBAT));
        list.add(new Module("AUTO JUMP RESET", Category.COMBAT));
        list.add(new Module("AUTO TOTEM", Category.COMBAT));
        list.add(new Module("CRYSTAL OPTIMIZER", Category.COMBAT));
        list.add(new Module("DOUBLE ANCHOR", Category.COMBAT));
        list.add(new Module("ELYTRA SWAP", Category.COMBAT));
        list.add(new Module("HITBOX", Category.COMBAT));
        list.add(new Module("HOVER TOTEM", Category.COMBAT));
        list.add(new Module("MACE BOMBER", Category.COMBAT));
        list.add(new Module("MACE SWAP", Category.COMBAT));
        list.add(new Module("NO HIT DELAY", Category.COMBAT));
        list.add(new Module("SHIELD BREAKER", Category.COMBAT));
        list.add(new Module("SPEAR SWAP", Category.COMBAT));
        list.add(new Module("STATIC HITBOXES", Category.COMBAT));
        list.add(new Module("TOTEM OFFHAND", Category.COMBAT));
        list.add(new Module("TRIGGER BOT", Category.COMBAT));

        // MISC
        list.add(new Module("AUTO CLICKER", Category.MISC));
        list.add(new Module("AUTO EAT", Category.MISC));
        list.add(new Module("AUTO FIREWORK", Category.MISC));
        list.add(new Module("AUTO LOG", Category.MISC));
        list.add(new Module("AUTO LOOT", Category.MISC));
        list.add(new Module("AUTO MINE", Category.MISC));
        list.add(new Module("AUTO RECONNECT", Category.MISC));
        list.add(new Module("AUTO TOOL", Category.MISC));
        list.add(new Module("AUTO TPR", Category.MISC));
        list.add(new Module("AUTO WALK", Category.MISC));
        list.add(new Module("CORD SNAPPER", Category.MISC));
        list.add(new Module("ELYTRA GLIDE", Category.MISC));
        list.add(new Module("FAKEPLAYER", Category.MISC));
        list.add(new Module("FAST PLACE", Category.MISC));
        list.add(new Module("FREECAM", Category.MISC));
        list.add(new Module("KEY PEARL", Category.MISC));
        list.add(new Module("KEY WIND CHARGE", Category.MISC));
        list.add(new Module("NAME PROTECT", Category.MISC));
        list.add(new Module("SKIN PROTECT", Category.MISC));
        list.add(new Module("SPRINT", Category.MISC));
        list.add(new Module("WEATHER NOTIFIER", Category.MISC));

        // DONUT
        list.add(new Module("ANTI TRAP", Category.DONUT));
        list.add(new Module("AUTO SELL", Category.DONUT));
        list.add(new Module("AUTO SPAWNER SELL", Category.DONUT));
        list.add(new Module("CHUNK FINDER", Category.DONUT));
        list.add(new Module("FAKE PAY", Category.DONUT));
        list.add(new Module("FAKE STATS", Category.DONUT));
        list.add(new Module("ITEM DROPPER", Category.DONUT));
        list.add(new Module("NETHERITE FINDER", Category.DONUT));
        list.add(new Module("PLAYER CHUNKS", Category.DONUT));
        list.add(new Module("SPAWNER PROTECT", Category.DONUT));

        // BASEFINDING
        list.add(new Module("BLOCK ENTITY DEBUG", Category.BASEFINDING));
        list.add(new Module("HOLE ESP", Category.BASEFINDING));
        list.add(new Module("LIGHT FINDER", Category.BASEFINDING));
        list.add(new Module("PRIME CHUNK FINDER", Category.BASEFINDING));
        list.add(new Module("RTP BASE FINDER", Category.BASEFINDING));
        list.add(new Module("SUS CHUNK FINDER", Category.BASEFINDING));
        list.add(new Module("SUSPICIOUS ESP", Category.BASEFINDING));
        list.add(new Module("TUNNEL BASE FINDER", Category.BASEFINDING));
        list.add(new Module("SEED CHUNK FINDER", Category.BASEFINDING));

        // RENDER
        list.add(new Module("BLOCK ESP", Category.RENDER));
        list.add(new Module("BLOCK NOTIFIER", Category.RENDER));
        list.add(new Module("FREE LOOK", Category.RENDER));
        list.add(new Module("FULLBRIGHT", Category.RENDER));
        list.add(new Module("HUD", Category.RENDER));
        list.add(new Module("JUMP CIRCLES", Category.RENDER));
        list.add(new Module("MOB ESP", Category.RENDER));
        list.add(new Module("NAME TAGS", Category.RENDER));
        list.add(new Module("ORE SIM", Category.RENDER));
        list.add(new Module("PEARL TRAJECTORY", Category.RENDER));
        list.add(new Module("PLAYER ESP", Category.RENDER));
        list.add(new Module("REALHITBOX", Category.RENDER));
        list.add(new Module("MUSIC HUD", Category.RENDER));
        list.add(new Module("STORAGE ESP", Category.RENDER));
        list.add(new Module("SWINGSPEED", Category.RENDER));
        list.add(new Module("TARGET HUD", Category.RENDER));

        // CLIENT
        list.add(new Module("KRYPTON+", Category.CLIENT));
        list.add(new Module("CHAT MACRO", Category.CLIENT));
        list.add(new Module("DISCORD PRESENCE", Category.CLIENT));
        list.add(new Module("PROXY", Category.CLIENT));
        list.add(new Module("RADIO", Category.CLIENT));

        return list;
    }
}
