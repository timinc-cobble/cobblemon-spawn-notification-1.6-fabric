package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.SPAWN_BROADCASTED
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.SpawnBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastUnnaturalSpawn {
    fun handle(entity: Entity, world: ServerWorld) {
        if (entity !is PokemonEntity) return
        val pokemon = entity.pokemon

        if (pokemon.isPlayerOwned()) return

        afterOnServer(1, world) {
            if (pokemon.persistentData.contains(SPAWN_BROADCASTED)) return@afterOnServer

            val pos = entity.blockPos

            val messages = SpawnBroadcaster(
                pokemon,
                pos,
                world.biomeAccess.getBiome(pos).key.get().value,
                world.dimensionEntry.key.get().value,
                null
            ).getBroadcast()
            messages.forEach { message ->
                if (config.announceCrossDimensions) {
                    Broadcast.broadcastMessage(message)
                } else if (config.broadcastRangeEnabled) {
                    Broadcast.broadcastMessage(getValidPlayers(world.dimensionEntry.key.get(), pos), message)
                } else {
                    Broadcast.broadcastMessage(world, message)
                }
            }
        }
    }
}