package com.possible_triangle.brazier.block.tile.render;

import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.block.tile.BrazierTile;
import com.possible_triangle.brazier.entity.render.CrazedFlameRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class BrazierRenderer extends BlockEntityRenderer<BrazierTile> {

    public static Sprite RUNES;
    public static String TEXTURE_KEY = "block/brazier_runes";
    public static final RenderLayer RENDER_TYPE;

    private static final int TEXTURE_HEIGHT = 9;

    static {
        RenderLayer.MultiPhaseParameters glState = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true))
                //.transparency(ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228515_g_"))
                .diffuseLighting(new RenderPhase.DiffuseLighting(true))
                .alpha(new RenderPhase.Alpha(0.004F))
                .lightmap(new RenderPhase.Lightmap(true))
                .build(true);
        RENDER_TYPE = RenderLayer.of(Content.MODID + ":runes", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, GL11.GL_QUADS, 128, glState);
    }

    /*
    TODO do I need this?
    @SubscribeEvent
    public static void atlasStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
            event.addSprite(new ResourceLocation(Brazier.MODID, TEXTURE_KEY));
        }
    }

    @SubscribeEvent
    public static void atlasStitch(TextureStitchEvent.Post event) {
        if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
            RUNES = event.getMap().getSprite(new ResourceLocation(Brazier.MODID, TEXTURE_KEY));
        }
    }
    */

    public BrazierRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private void renderTop(MatrixStack matrices, float alpha, VertexConsumerProvider buffer) {
        matrices.push();
        matrices.translate(0, 0.05F, 0);
        Matrix4f matrix = matrices.peek().getModel();
        VertexConsumer vertex = buffer.getBuffer(RENDER_TYPE);
        for (int i = 0; i < 4; i++) {
            float v = RUNES.getMaxU() - RUNES.getMinU();
            float start = v / 9F * i * 2;
            float minU = RUNES.getMinU() + start;
            float maxU = minU + v / 9F * 1.5F;

            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90F));

            vertex.vertex(matrix, 1.0F, 0, -0.25F).color(1F, 1F, 1F, alpha).texture(maxU, RUNES.getMinV()).light(0xF000F0).next();
            vertex.vertex(matrix, 1.0F, 0, +0.25F).color(1F, 1F, 1F, alpha).texture(maxU, RUNES.getMaxV()).light(0xF000F0).next();
            vertex.vertex(matrix, 2.5F, 0, +0.25F).color(1F, 1F, 1F, alpha).texture(minU, RUNES.getMaxV()).light(0xF000F0).next();
            vertex.vertex(matrix, 2.5F, 0, -0.25F).color(1F, 1F, 1F, alpha).texture(minU, RUNES.getMinV()).light(0xF000F0).next();

        }
        matrices.pop();
    }

    private void renderSide(MatrixStack matrices, float alpha, VertexConsumerProvider buffer, int height) {
        matrices.push();
        int times = height / TEXTURE_HEIGHT;

        for (int quarter = 0; quarter < 4; quarter++) {
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90F));

            for (int i = 0; i <= times; i++) {
                float segment = Math.min(TEXTURE_HEIGHT, height - i * TEXTURE_HEIGHT);
                float offset = i * TEXTURE_HEIGHT;

                matrices.push();
                matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(90F));
                matrices.translate(0, 2.55F, 0);

                Matrix4f matrix = matrices.peek().getModel();
                VertexConsumer vertex = buffer.getBuffer(RENDER_TYPE);
                float maxU = RUNES.getMinU() + segment * ((RUNES.getMaxU() - RUNES.getMinU()) / ((float) TEXTURE_HEIGHT));

                vertex.vertex(matrix, offset, 0, -0.25F).color(1F, 1F, 1F, alpha).texture(RUNES.getMinU(), RUNES.getMinV()).light(0xF000F0).next();
                vertex.vertex(matrix, offset, 0, +0.25F).color(1F, 1F, 1F, alpha).texture(RUNES.getMinU(), RUNES.getMaxV()).light(0xF000F0).next();
                vertex.vertex(matrix, offset + segment, 0, +0.25F).color(1F, 1F, 1F, alpha).texture(maxU, RUNES.getMaxV()).light(0xF000F0).next();
                vertex.vertex(matrix, offset + segment, 0, -0.25F).color(1F, 1F, 1F, alpha).texture(maxU, RUNES.getMinV()).light(0xF000F0).next();

                matrices.pop();
            }

        }

        matrices.pop();
    }

    private void renderFlame(MatrixStack matrices, VertexConsumerProvider buffer, int light) {
        CrazedFlameRenderer.renderFlame(matrices, MinecraftClient.getInstance().getEntityRenderDispatcher(), buffer, light);
    }

    @Override
    public void render(BrazierTile tile, float delta, MatrixStack matrices, VertexConsumerProvider buffer, int light, int overlay) {
        int height = tile.getHeight();
        float alpha = 1.0F;
        if (height > 0) {

            matrices.push();
            matrices.translate(0.5, 0, 0.5);

            // TODO use conig
            //if (BrazierConfig.CLIENT.RENDER_RUNES.get()) {
            renderTop(matrices, alpha, buffer);
            renderSide(matrices, alpha, buffer, height);
            //}

            matrices.push();
            matrices.translate(0, 1.4F, 0);
            renderFlame(matrices, buffer, light);
            matrices.pop();

            matrices.pop();

        }
    }
}
