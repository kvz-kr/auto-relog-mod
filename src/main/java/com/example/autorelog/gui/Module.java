package com.example.autorelog.gui;

public class Module {
    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
