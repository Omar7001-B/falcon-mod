package net.omar.tutorial.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.omar.tutorial.classes.DEBUG;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ExampleMixin {
	private static final Logger LOGGER = LogManager.getLogger("Mixin");

	@Inject(method = "sendPacket", at = @At("HEAD"))
	private void onSendPacket (Packet < ? > packet, CallbackInfo ci){
		if(packet instanceof PlayPongC2SPacket || packet instanceof PlayerMoveC2SPacket) return;
		//DEBUG.Chat("Sent packet: {}", packet);
		if (packet instanceof ClickSlotC2SPacket) {
			ClickSlotC2SPacket clickPacket = (ClickSlotC2SPacket) packet;
			Int2ObjectMap<ItemStack> x = clickPacket.getModifiedStacks();
			DEBUG.Mixin("ClickSlotC2SPacket - Sync ID: " + clickPacket.getSyncId() + ", Slot: " + clickPacket.getSlot() + ", Button: " + clickPacket.getButton() + ", SlotActionType: " + clickPacket.getActionType() + ", getStack: " + clickPacket.getStack());
		} else if (packet instanceof ButtonClickC2SPacket) {
			ButtonClickC2SPacket buttonPacket = (ButtonClickC2SPacket) packet;
			DEBUG.Mixin("ButtonClickC2SPacket - Sync ID: " + buttonPacket.getSyncId() + ", Button ID: " + buttonPacket.getButtonId());
		} else if (packet instanceof SelectMerchantTradeC2SPacket) {
			SelectMerchantTradeC2SPacket tradePack = (SelectMerchantTradeC2SPacket) packet;
			DEBUG.Mixin("Trade id is: " + tradePack.getTradeId());
		} else if (packet instanceof CloseHandledScreenC2SPacket) {
			DEBUG.Mixin("Close screen id is: " + ((CloseHandledScreenC2SPacket) packet).getSyncId());
		}
	}

}