package com.maneatingroblox.nightfall.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FieldNotesItem extends Item {
    private static final String PAGE_TAG = "nightfall_page";
    private static final int PAGE_COUNT = 3;

    public FieldNotesItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.nightfall.field_notes"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            int page = stack.getOrCreateTag().getInt(PAGE_TAG) % PAGE_COUNT;
            player.displayClientMessage(Component.translatable("item.nightfall.field_notes.page_" + (page + 1))
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
            stack.getOrCreateTag().putInt(PAGE_TAG, (page + 1) % PAGE_COUNT);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
