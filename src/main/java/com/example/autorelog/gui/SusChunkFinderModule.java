package com.example.autorelog.gui;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class SusChunkFinderModule extends AbstractModule {
    public static final SusChunkFinderModule INSTANCE = new SusChunkFinderModule();

    public int simulationDistance = 4;
    public int sensitivity = 4; // Minimum count of grown target blocks to flag a chunk
    public boolean smartAdjustment = true;
    public int alpha = 30;

    // Target block toggles
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
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onRenderWorld);
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
                for (int y = minY; y <= maxY; y++) {
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

    private void onRenderWorld(WorldRenderContext context) {
        if (!isEnabled() || suspiciousChunks.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        Vec3d cameraPos = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        float r = 1.0f, g = 0.2f, b = 0.2f, a = Math.max(0.05f, alpha / 100.0f);

        for (ChunkPos chunk : suspiciousChunks) {
            double x1 = chunk.getStartX();
            double z1 = chunk.getStartZ();
            double x2 = chunk.getEndX() + 1;
            double z2 = chunk.getEndZ() + 1;
            double y1 = client.world.getBottomY();
            double y2 = client.world.getTopYInclusive() + 1;

            // Highlight chunk bounding quad
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)x1, (float)y1, (float)z1).color(r, g, b, a);
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)x2, (float)y1, (float)z1).color(r, g, b, a);
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)x2, (float)y1, (float)z2).color(r, g, b, a);
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)x1, (float)y1, (float)z2).color(r, g, b, a);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }
}
