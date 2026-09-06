package com.example.autorelog.gui;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SusChunkFinderModule extends AbstractModule {
    public static final SusChunkFinderModule INSTANCE = new SusChunkFinderModule();

    // Module Settings
    public int simulationDistance = 4;
    public int sensitivity = 4;
    public boolean smartAdjustment = true;
    public int alpha = 30;

    // Block Toggles
    public boolean kelp = false;
    public boolean caveVines = false;
    public boolean vines = false;
    public boolean amethyst = true;
    public boolean bamboo = false;
    public boolean cocoa = false;
    public boolean beeNest = false;
    public boolean rotatedDeepslate = false;

    private final Set<ChunkPos> suspiciousChunks = ConcurrentHashMap.newKeySet();
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor();
    private int tickCounter = 0;
    private boolean isScanning = false;

    public SusChunkFinderModule() {
        super("SUS CHUNK FINDER", Module.Category.RENDER);
    }

    @Override
    public void onTick() {
        if (!isEnabled()) {
            suspiciousChunks.clear();
            return;
        }

        tickCounter++;
        if (tickCounter < 20 || isScanning) return;
        tickCounter = 0;

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null || client.player == null) return;

        ChunkPos playerChunk = client.player.getChunkPos();

        isScanning = true;
        scanExecutor.submit(() -> {
            try {
                Set<ChunkPos> detected = new HashSet<>();

                for (int dx = -simulationDistance; dx <= simulationDistance; dx++) {
                    for (int dz = -simulationDistance; dz <= simulationDistance; dz++) {
                        int chunkX = playerChunk.x + dx;
                        int chunkZ = playerChunk.z + dz;

                        if (world.isChunkLoaded(chunkX, chunkZ)) {
                            int count = countTargetsInChunk(world, chunkX, chunkZ);
                            if (count >= sensitivity) {
                                detected.add(new ChunkPos(chunkX, chunkZ));
                            }
                        }
                    }
                }

                suspiciousChunks.clear();
                suspiciousChunks.addAll(detected);
            } finally {
                isScanning = false;
            }
        });
    }

    private int countTargetsInChunk(World world, int chunkX, int chunkZ) {
        int count = 0;
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        if (chunk == null) return 0;

        int minX = chunkX * 16;
        int minZ = chunkZ * 16;
        int minY = world.getBottomY();
        int maxY = world.getTopYInclusive();

        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                for (int y = minY; y <= maxY; y += 2) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    if (amethyst && (block == Blocks.AMETHYST_CLUSTER || block == Blocks.MEDIUM_AMETHYST_BUD || block == Blocks.LARGE_AMETHYST_BUD)) count++;
                    else if (kelp && (block == Blocks.KELP_PLANT || block == Blocks.KELP)) count++;
                    else if (caveVines && (block == Blocks.CAVE_VINES || block == Blocks.CAVE_VINES_PLANT)) count++;
                    else if (vines && block == Blocks.VINE) count++;
                    else if (bamboo && block == Blocks.BAMBOO) count++;
                    else if (cocoa && block == Blocks.COCOA) count++;
                    else if (beeNest && (block == Blocks.BEE_NEST || block == Blocks.BEEHIVE)) count++;
                    else if (rotatedDeepslate && block == Blocks.DEEPSLATE) count++;

                    if (count >= sensitivity) return count;
                }
            }
        }
        return count;
    }

    public Set<ChunkPos> getSuspiciousChunks() {
        return suspiciousChunks;
    }

    public void renderChunkHighlights(DrawContext context) {
        if (!isEnabled() || suspiciousChunks.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;

        int redAlpha = Math.min(255, Math.max(30, (this.alpha * 255) / 100));
        int redChunkColor = (redAlpha << 24) | 0xFF0000;
        int borderColor = 0xFFFF0000;

        int screenCenterX = client.getWindow().getScaledWidth() / 2;
        int screenCenterY = client.getWindow().getScaledHeight() / 2;

        for (ChunkPos chunk : suspiciousChunks) {
            int offsetX = (chunk.x - playerChunkX) * 12;
            int offsetZ = (chunk.z - playerChunkZ) * 12;

            int drawX = screenCenterX + offsetX;
            int drawY = screenCenterY + offsetZ;

            context.fill(drawX - 8, drawY - 8, drawX + 8, drawY + 8, redChunkColor);
            context.fill(drawX - 8, drawY - 8, drawX + 8, drawY - 7, borderColor);
            context.fill(drawX - 8, drawY + 7, drawX + 8, drawY + 8, borderColor);
            context.fill(drawX - 8, drawY - 8, drawX - 7, drawY + 8, borderColor);
            context.fill(drawX + 7, drawY - 8, drawX + 8, drawY + 8, borderColor);

            context.drawTextWithShadow(client.textRenderer, "SUS", drawX - 6, drawY - 3, 0xFFFFFFFF);
        }
    }
}
