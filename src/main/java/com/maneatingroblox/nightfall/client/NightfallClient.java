package com.maneatingroblox.nightfall.client;

import com.maneatingroblox.nightfall.Nightfall;
import com.maneatingroblox.nightfall.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Nightfall.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NightfallClient {
    private NightfallClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> EntityRenderers.register(ModEntities.THE_HOLLOW.get(), TheHollowRenderer::new));
    }
}
