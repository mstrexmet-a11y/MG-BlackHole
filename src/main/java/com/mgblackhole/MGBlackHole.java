package com.mgblackhole;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(MGBlackHole.MOD_ID)
public class MGBlackHole {
    public static final String MOD_ID = "mgblackhole";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> BLACK_HOLE_ITEM = ITEMS.register("black_hole",
            () -> new BlackHoleItem(new Item.Properties().stacksTo(1).durability(16)));

    public MGBlackHole() {
        IEventBus modBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(BLACK_HOLE_ITEM);
        }
    }

    public static class BlackHoleItem extends Item {
        public BlackHoleItem(Properties properties) { super(properties); }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide) {
                Vec3 target = player.getEyePosition().add(player.getLookAngle().scale(6.0));
                BlackHoleField.create(level, target, player);
                player.getCooldowns().addCooldown(this, 30);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                Vec3 target = Vec3.atCenterOf(context.getClickedPos().relative(context.getClickedFace()));
                BlackHoleField.create(level, target, context.getPlayer());
                if (context.getPlayer() != null) {
                    context.getPlayer().getCooldowns().addCooldown(this, 30);
                    context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }
}
