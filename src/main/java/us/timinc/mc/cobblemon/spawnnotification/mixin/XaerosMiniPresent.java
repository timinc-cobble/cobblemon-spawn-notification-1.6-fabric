package us.timinc.mc.cobblemon.spawnnotification.mixin;

@org.spongepowered.asm.mixin.Mixin(xaero.minimap.XaeroMinimap.class)
public class XaerosMiniPresent {
    @org.spongepowered.asm.mixin.injection.Inject(method = "loadCommon", at=@org.spongepowered.asm.mixin.injection.At("HEAD"), remap = false)
    void xaerosInited(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.INSTANCE.onInitializeXaeros();
    }
}
