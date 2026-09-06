package com.example.autorelog.gui;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class SusChunkFinderModule extends AbstractModule {
    public static final SusChunkFinderModule INSTANCE = new SusChunkFinderModule();

    // Module Configuration Settings
    public int simulationDistance = 4;
    public int sensitivity = 4; // Minimum count of target grown blocks to flag a chunk
    public boolean smartAdjustment = true;
    public int alpha = 30; // Red overlay transparency (0 - 100)

    // Block Category Toggles
    public boolean kelp = false;
    public boolean caveVines = false;
    public boolean vines = false;
    public boolean amethyst = true;
    public boolean bamboo = false;
    public boolean cocoa = false;
    public boolean beeNest = false;
    public boolean rotatedDeepslate = false;

    private final Set<ChunkPos> suspiciousChunks = new HashSet<>();

    public SusChunkFinderModule() {
        super("SUS CHUNK FINDER", Module.Category.RENDER);
    }

    @Override
    public void onTick() {
        if (!isEnabled()) {
            suspiciousChunks.clear();
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null || client.player == null) return;

        ChunkPos playerChunk = client.player.getChunkPos();
        suspiciousChunks.clear();

        // Scan chunks within simulation distance
        for (int dx = -simulationDistance; dx <= simulationDistance; dx++) {
            for (int dz = -simulationDistance; dz <= simulationDistance; dz++) {
                int chunkX = playerChunk.x + dx;
                int chunkZ = playerChunk.z + dz;

                if (world.isChunkLoaded(chunkX, chunkZ)) {
                    int count = countTargetsInChunk(world, chunkX, chunkZ);
                    if (count >= sensitivity) {
                        suspiciousChunks.add(new ChunkPos(chunkX, chunkZ));
                    }
                }
            }
        }
    }

    private int countTargetsInChunk(World world, int chunkX, int chunkZ) {
        int count = 0;
        int minX = chunkX * 16;
        int minZ = chunkZ * 16;
        int minY = world.getBottomY();
        int maxY = world.getTopYInclusive();

        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                for (int y = minY; y <= maxY; y += 2) { // Optimized vertical scanning step
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    if (amethyst && (block == Blocks.AMETHYST_CLUSTER || block == Blocks.MEDIUM_AMETHYST_BUD || block == Blocks.LARGE_AMETHYST_BUD)) count++;
                    if (kelp && (block == Blocks.KELP_PLANT || block == Blocks.KELP)) count++;
                    if (caveVines && (block == Blocks.CAVE_VINES || block == Blocks.CAVE_VINES_PLANT)) count++;
                    if (vines && block == Blocks.VINE) count++;
                    if (bamboo && block == Blocks.BAMBOO) count++;
                    if (cocoa && block == Blocks.COCOA) count++;
                    if (beeNest && (block == Blocks.BEE_NEST || block == Blocks.BEEHIVE)) count++;
                    if (rotatedDeepslate && block == Blocks.DEEPSLATE) count++;
                }
            }
        }
        return count;
    }

    public Set<ChunkPos> getSuspiciousChunks() {
        return suspiciousChunks;
    }

    /**
     * Call this inside your In-Game HUD / Screen render loop (e.g., ConfigScreen or HUD overlay handler)
     * to render the red highlight overlay for suspicious chunks.
     */
    public void renderChunkHighlights(DrawContext context) {
        if (!isEnabled() || suspiciousChunks.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;

        // Calculate ARGB color using custom alpha (0-100 mapped to 0-255)
        int redAlpha = Math.min(255, Math.max(30, (this.alpha * 255) / 100));
        int redChunkColor = (redAlpha << 24) | 0xFF0000;
        int borderColor = 0xFFFF0000; // Solid Red Outline

        int screenCenterX = client.getWindow().getScaledWidth() / 2;
        int screenCenterY = client.getWindow().getScaledHeight() / 2;

        for (ChunkPos chunk : suspiciousChunks) {
            int offsetX = (chunk.x - playerChunkX) * 12;
            int offsetZ = (chunk.z - playerChunkZ) * 12;

            int drawX = screenCenterX + offsetX;
            int drawY = screenCenterY + offsetZ;

            // Render Red Highlight Box for flagged chunk
            context.fill(drawX - 8, drawY - 8, drawX + 8, drawY + 8, redChunkColor);

            // Draw Red Border Frame
            context.fill(drawX - 8, drawY - 8, drawX + 8, drawY - 7, borderColor); // Top
            context.fill(drawX - 8, drawY + 7, drawX + 8, drawY + 8, borderColor); // Bottom
            context.fill(drawX - 8, drawY - 8, drawX - 7, drawY + 8, borderColor); // Left
            context.fill(drawX + 7, drawY - 8, drawX + 8, drawY + 8, borderColor); // Right

            // Label
            context.drawTextWithShadow(client.textRenderer, "SUS", drawX - 6, drawY - 3, 0xFFFFFFFF);
        }
    }
}
