package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.CaptureBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastCapture {
    fun handle(evt: PokemonCapturedEvent) {
        if (!config.broadcastCaptures) return

        val entity = evt.pokeBallEntity
        val coords = entity.blockPosition()
        val level = entity.level()
        if (level !is ServerLevel) return

        broadcast(
            evt.pokemon,
            coords,
            level.getBiome(coords).unwrapKey().get().location(),
            level.dimension().location(),
            level,
            evt.player
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: ResourceLocation,
        dimension: ResourceLocation,
        level: ServerLevel,
        player: ServerPlayer,
    ) {
        CaptureBroadcaster(
            pokemon,
            coords,
            biome,
            dimension,
            player
        ).getBroadcast()?.let { message ->
            if (config.announceCrossDimensions) {
                Broadcast.broadcastMessage(message)
            } else if (config.broadcastRangeEnabled) {
                Broadcast.broadcastMessage(getValidPlayers(level.dimension(), coords), message)
            } else {
                Broadcast.broadcastMessage(level, message)
            }
        }
    }
}