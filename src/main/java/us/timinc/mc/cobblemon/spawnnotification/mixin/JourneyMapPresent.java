package us.timinc.mc.cobblemon.spawnnotification.mixin;

@org.spongepowered.asm.mixin.Mixin(journeymap.common.Journeymap.class)
public class JourneyMapPresent {
    @org.spongepowered.asm.mixin.injection.Inject(method = "serverStarted", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), remap = false)
    void JourneyMapInited(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.INSTANCE.onInitializeJourneyMap();
    }
}
