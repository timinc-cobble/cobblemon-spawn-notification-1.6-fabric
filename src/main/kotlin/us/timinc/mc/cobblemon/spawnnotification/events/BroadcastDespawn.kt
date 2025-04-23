package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.FAINT_HAS_ENTITY
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.DespawnBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastDespawn {
    fun handle(entity: Entity, level: ServerWorld) {
        if (!config.broadcastVolatileDespawns) return
        if (entity !is PokemonEntity) return
        if (entity.pokemon.persistentData.contains(FAINT_HAS_ENTITY)) return

        val coords = entity.blockPos

        broadcast(
            entity.pokemon,
            coords,
            level.getBiome(coords).key.get().value,
            level.dimensionEntry.key.get().value,
            level,
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: Identifier,
        dimension: Identifier,
        level: ServerWorld,
    ) {
        DespawnBroadcaster(
            pokemon,
            coords,
            biome,
            dimension
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