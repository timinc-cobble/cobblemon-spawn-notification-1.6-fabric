package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.SHOULD_BROADCAST_FAINT
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.DespawnBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastDespawn {
    fun handle(entity: Entity, level: ServerLevel) {
        if (!config.broadcastVolatileDespawns) return
        if (entity !is PokemonEntity) return
        if (entity.pokemon.persistentData.contains(SHOULD_BROADCAST_FAINT)) return

        val coords = entity.blockPosition()

        broadcast(
            entity.pokemon,
            coords,
            level.getBiome(coords).unwrapKey().get().location(),
            level.dimension().location(),
            level,
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: ResourceLocation,
        dimension: ResourceLocation,
        level: ServerLevel,
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
                Broadcast.broadcastMessage(getValidPlayers(level.dimension(), coords), message)
            } else {
                Broadcast.broadcastMessage(level, message)
            }
        }
    }
}