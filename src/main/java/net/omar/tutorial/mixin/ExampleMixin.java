package net.omar.tutorial.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.omar.tutorial.Managers.Inventorying;
import net.omar.tutorial.Managers.Clicking;
import net.omar.tutorial.Managers.Trading;
import net.omar.tutorial.Managers.Debugging;
import net.omar.tutorial.Data.Indexes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientPlayNetworkHandler.class)
public class ExampleMixin {
	private static final Logger LOGGER = LogManager.getLogger("Mixin");

	@Inject(method = "sendPacket", at = @At("HEAD"))
	private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
		if(packet instanceof PlayPongC2SPacket || packet instanceof PlayerMoveC2SPacket) return;

		if (packet instanceof ClickSlotC2SPacket) {
			ClickSlotC2SPacket clickPacket = (ClickSlotC2SPacket) packet;
			Int2ObjectMap<ItemStack> x = clickPacket.getModifiedStacks();
			Debugging.Mixin("ClickSlotC2SPacket - Sync ID: " + clickPacket.getSyncId() + ", Slot: " + clickPacket.getSlot() + ", Button: " + clickPacket.getButton() + ", SlotActionType: " + clickPacket.getActionType() + ", getStack: " + clickPacket.getStack());
		}

		if (packet instanceof ButtonClickC2SPacket) {
			ButtonClickC2SPacket buttonPacket = (ButtonClickC2SPacket) packet;
			Debugging.Mixin("ButtonClickC2SPacket - Sync ID: " + buttonPacket.getSyncId() + ", Button ID: " + buttonPacket.getButtonId());
		}

		if (packet instanceof SelectMerchantTradeC2SPacket) {
			SelectMerchantTradeC2SPacket tradePack = (SelectMerchantTradeC2SPacket) packet;
			Debugging.Mixin("Trade id is: " + tradePack.getTradeId());

			if(!Trading.isAutomatedTrade){
				int shiftKeyCode = GLFW.GLFW_KEY_LEFT_SHIFT;
				if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), shiftKeyCode)) {
					Debugging.Mixin("Shift click on trade ID: " + tradePack.getTradeId());
					Clicking.slotShiftLeftClick(Indexes.Trade.RESULT_SLOT);
				} else {
					// Normal click on trade ID
					Debugging.Mixin("Normal click on trade ID: " + tradePack.getTradeId());
					Inventorying.moveCompleteItem(Indexes.Trade.RESULT_SLOT, Indexes.Trade.TOTAL_INVENTORY);
				}

			}
		}

		if (packet instanceof CloseHandledScreenC2SPacket) {
			Debugging.Mixin("Close screen id is: " + ((CloseHandledScreenC2SPacket) packet).getSyncId());
		}
	}
}
