package com.possible_triangle.brazier.entity.render;

import com.possible_triangle.brazier.Brazier;
import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.entity.Crazed;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EvokerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CrazedRender extends EvokerEntityRenderer<Crazed> {

    private static final Identifier TEXTURE = new Identifier(Content.MODID, "textures/entity/crazed.png");

    @Override
    public void render(Crazed entity, float f, float g, MatrixStack matrices, VertexConsumerProvider buffer, int i) {
        model.getHat().visible = true;
        super.render(entity, f, g, matrices, buffer, i);
    }

    public CrazedRender(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        model.getHat().visible = true;
    }

    @Override
    public Identifier getTexture(Crazed entity) {
        return TEXTURE;
    }
}
