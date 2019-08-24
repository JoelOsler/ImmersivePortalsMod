package com.qouteall.immersive_portals.mixin_optifine;

import com.qouteall.immersive_portals.optifine_compatibility.OFGlobal;
import net.optifine.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Config.class)
public class MOConfig {
    @Inject(method = "isShaders", at = @At("HEAD"), cancellable = true)
    private static void onIsShaders(CallbackInfoReturnable<Boolean> cir) {
        if (OFGlobal.shaderContextManager.isContextSwitched()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}