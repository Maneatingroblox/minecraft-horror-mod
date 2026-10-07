package com.maneatingroblox.nightfall.client;

import com.maneatingroblox.nightfall.Nightfall;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class TheHollowRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Nightfall.MOD_ID, "textures/entity/the_hollow.png");

    public TheHollowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie entity) {
        return TEXTURE;
    }
}
