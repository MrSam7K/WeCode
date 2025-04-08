package org.zbinfinn.wecode.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.features.commands.SavedInventories;

@Mixin(PlayerInventory.class)
public class MPlayerInventory {

    @Inject(method = "setStack", at = @At("TAIL"), cancellable = true)
    public void render(int slot, ItemStack stack, CallbackInfo ci) {
        if(SavedInventories.setStack(slot, stack)) ci.cancel();
    }

}
