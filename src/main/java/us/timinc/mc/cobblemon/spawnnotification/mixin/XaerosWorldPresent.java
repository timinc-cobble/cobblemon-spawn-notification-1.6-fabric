package us.timinc.mc.cobblemon.spawnnotification.mixin;

@org.spongepowered.asm.mixin.Mixin(xaero.map.core.XaeroWorldMapCore.class)
public class XaerosWorldPresent {
    @org.spongepowered.asm.mixin.injection.Inject(method = "onMinecraftRunTick", at=@org.spongepowered.asm.mixin.injection.At("TAIL"), remap = false)
    private static void xaerosWorldPresent(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.INSTANCE.getXaerosPresent()) return;
        us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.INSTANCE.onInitializeXaeros();
    }
}
