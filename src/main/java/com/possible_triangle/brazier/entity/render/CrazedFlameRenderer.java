package com.possible_triangle.brazier.entity.render;

import com.possible_triangle.brazier.Brazier;
import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.entity.CrazedFlame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CrazedFlameRenderer extends EntityRenderer<CrazedFlame> {

    public static final ModelIdentifier MODEL = new ModelIdentifier(new Identifier(Content.MODID, "living_flame"), "inventory");

    public CrazedFlameRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected int getBlockLight(CrazedFlame entity, BlockPos partialTicks) {
        return 10;
    }

    @Override
    public Identifier getTexture(CrazedFlame entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }


    @Override
    public void render(CrazedFlame entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider buffer, int light) {
        matrices.push();
        matrices.translate(0, 0.5D, 0);
        float scale = 1.5F;
        matrices.scale(scale, scale, scale);
        renderFlame(matrices, this.dispatcher, buffer, light);
        matrices.pop();
    }

    public static void renderFlame(MatrixStack matrices, EntityRenderDispatcher rendererManager, VertexConsumerProvider buffer, int light) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockRenderManager dispatcher = mc.getBlockRenderManager();
        BakedModelManager modelManager = mc.getBakedModelManager();
        float scale = 0.6F;

        matrices.push();
        matrices.scale(scale, scale, scale);
        matrices.multiply(rendererManager.camera.getRotation());
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrices.translate(-0.5F, -0.5F, -0.5F);
        dispatcher.getModelRenderer().render(
                matrices.peek(), buffer.getBuffer(RenderLayer.getCutout()),
                null, modelManager.getModel(MODEL), 1.0F, 1.0F, 1.0F,
                light, 0
        );
        matrices.pop();
    }

}
