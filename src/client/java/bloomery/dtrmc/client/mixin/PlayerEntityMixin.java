package bloomery.dtrmc.client.mixin;

import bloomery.dtrmc.client.KeyInputHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void dtrmc$revealRealNametag(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        // ถ้าผู้เล่นกำลังกดปุ่มค้างไว้ ให้แสดงชื่อจริงบนหัวทันที
        if (KeyInputHandler.isHoldingRevealKey()) {
            cir.setReturnValue(Text.literal(player.getGameProfile().getName()));
        }
    }
}