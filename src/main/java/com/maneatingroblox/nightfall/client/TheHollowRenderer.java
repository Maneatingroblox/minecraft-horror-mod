package com.maneatingroblox.nightfall.client;

import com.maneatingroblox.nightfall.Nightfall;
import com.maneatingroblox.nightfall.entity.TheHollowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TheHollowRenderer extends MobRenderer<TheHollowEntity, TheHollowModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Nightfall.MOD_ID, "textures/entity/the_hollow.png");

    public TheHollowRenderer(EntityRendererProvider.Context context) {
        super(context, new TheHollowModel(context.bakeLayer(TheHollowModel.LAYER_LOCATION)), 0.38F);
    }

    @Override
    public ResourceLocation getTextureLocation(TheHollowEntity entity) {
        return TEXTURE;
    }
}
