package bloomery.dtrmc.client.mixin;

import bloomery.dtrmc.client.ClientNicknameData;
import bloomery.dtrmc.client.KeyInputHandler;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void dtrmc$controlNametagVisibility(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity) {
            if (ClientNicknameData.isGlobalNametagsHidden()) {
                if (KeyInputHandler.isHoldingRevealKey()) {
                    cir.setReturnValue(true);
                    return;
                }
                cir.setReturnValue(false);
            }
        }
    }
}