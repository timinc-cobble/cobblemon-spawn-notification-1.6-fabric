package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.DespawnBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.DespawnReason
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastDespawn {
    fun handle(evt: PokemonCapturedEvent) {
        if (!config.broadcastDespawns) return

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
            DespawnReason.CAPTURED
        )
    }

    fun handle(evt: PokemonFaintedEvent) {
        if (!config.broadcastDespawns) return
        if (!evt.pokemon.isWild()) return

        val entity = evt.pokemon.entity ?: return
        val coords = entity.blockPos
        val level = entity.world
        if (level !is ServerWorld) return

        broadcast(
            evt.pokemon,
            coords,
            level.getBiome(coords).key.get().value,
            level.dimensionEntry.key.get().value,
            level,
            DespawnReason.FAINTED
        )
    }

    fun handle(entity: Entity, level: ServerWorld) {
        if (!config.broadcastVolatileDespawns) return
        if (entity !is PokemonEntity) return

        val coords = entity.blockPos

        broadcast(
            entity.pokemon,
            coords,
            level.getBiome(coords).key.get().value,
            level.dimensionEntry.key.get().value,
            level,
            DespawnReason.DESPAWNED
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: Identifier,
        dimension: Identifier,
        level: ServerWorld,
        reason: DespawnReason,
    ) {
        DespawnBroadcaster(
            pokemon,
            CobblemonSpawnPools.WORLD_SPAWN_POOL,
            coords,
            biome,
            dimension,
            reason
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