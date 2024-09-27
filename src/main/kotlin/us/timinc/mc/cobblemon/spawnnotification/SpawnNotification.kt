package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import us.timinc.mc.cobblemon.spawnnotification.config.ConfigBuilder
import us.timinc.mc.cobblemon.spawnnotification.config.SpawnNotificationConfig
import us.timinc.mc.cobblemon.spawnnotification.events.BroadcastDespawn
import us.timinc.mc.cobblemon.spawnnotification.events.BroadcastSpawn
import us.timinc.mc.cobblemon.spawnnotification.events.PlayShinyPlayerSound
import us.timinc.mc.cobblemon.spawnnotification.events.PlayShinySound

object SpawnNotification : ModInitializer {
    const val MOD_ID = "spawn_notification"
    var config: SpawnNotificationConfig = ConfigBuilder.load(SpawnNotificationConfig::class.java, MOD_ID)

    @JvmStatic
    var SHINY_SOUND_ID: Identifier = Identifier("$MOD_ID:pla_shiny")

    @JvmStatic
    var SHINY_SOUND_EVENT: SoundEvent = SoundEvent.of(SHINY_SOUND_ID)

    override fun onInitialize() {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, BroadcastSpawn::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, PlayShinySound::handle)
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.LOWEST, PlayShinyPlayerSound::handle)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, BroadcastDespawn::handle)
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.LOWEST, BroadcastDespawn::handle)
        ServerEntityEvents.ENTITY_UNLOAD.register(BroadcastDespawn::handle)
    }
}