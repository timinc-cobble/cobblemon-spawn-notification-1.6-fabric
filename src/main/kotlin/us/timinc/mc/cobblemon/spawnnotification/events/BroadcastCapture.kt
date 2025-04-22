package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.CaptureBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastCapture {
    fun handle(evt: PokemonCapturedEvent) {
        if (!config.broadcastCaptures) return

        val entity = evt.pokeBallEntity
        val coords = entity.blockPos
        val level = entity.world
        if (level !is ServerWorld) return

        broadcast(
            evt.pokemon,
            coords,
            level.getBiome(coords).key.get().value,
            level.dimensionEntry.key.get().value,
            level,
            evt.player
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: Identifier,
        dimension: Identifier,
        level: ServerWorld,
        player: ServerPlayerEntity,
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
                Broadcast.broadcastMessage(getValidPlayers(level.dimensionEntry.key.get(), coords), message)
            } else {
                Broadcast.broadcastMessage(level, message)
            }
        }
    }
}