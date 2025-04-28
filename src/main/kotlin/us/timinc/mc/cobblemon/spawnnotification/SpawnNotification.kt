package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import us.timinc.mc.cobblemon.spawnnotification.config.ConfigBuilder
import us.timinc.mc.cobblemon.spawnnotification.config.SpawnNotificationConfig
import us.timinc.mc.cobblemon.spawnnotification.events.*

object SpawnNotification : ModInitializer {
    const val MOD_ID = "spawn_notification"
    const val SPAWN_BROADCASTED = "spawn_notification:spawn_broadcasted"
    const val BUCKET = "spawn_notification:bucket"
    const val FAINT_HAS_ENTITY = "spawn_notification:faint_reason"
    const val FAINT_ENTITY = "spawn_notification:faint_entity"
    var config: SpawnNotificationConfig = ConfigBuilder.load(SpawnNotificationConfig::class.java, MOD_ID)
    var journeyMapPresent: Boolean = false
    var xaerosPresent: Boolean = false

    @JvmStatic
    var SHINY_SOUND_ID: Identifier = Identifier.of("$MOD_ID:pla_shiny")

    @JvmStatic
    var SHINY_SOUND_EVENT: SoundEvent = SoundEvent.of(SHINY_SOUND_ID)

    override fun onInitialize() {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, AttachBucket::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, BroadcastSpawn::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, PlayShinySound::handle)
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.LOWEST, PlayShinyPlayerSound::handle)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, BroadcastCapture::handle)
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.LOWEST, BroadcastFaint::handle)
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, BroadcastFaint::handle)
        ServerEntityEvents.ENTITY_LOAD.register(BroadcastUnnaturalSpawn::handle)
        ServerEntityEvents.ENTITY_UNLOAD.register(BroadcastFaint::handle)
        ServerEntityEvents.ENTITY_UNLOAD.register(BroadcastDespawn::handle)
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { _, _, _ ->
            config = ConfigBuilder.load(SpawnNotificationConfig::class.java, MOD_ID)
        }
    }

    fun onInitializeJourneyMap() {
        journeyMapPresent = true
    }

    fun onInitializeXaeros() {
        xaerosPresent = true
    }
}