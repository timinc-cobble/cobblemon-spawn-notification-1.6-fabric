package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.SPAWN_BROADCASTED
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.SpawnBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers
import us.timinc.mc.cobblemon.spawnnotification.util.isReallyWild

object BroadcastUnnaturalSpawn {
    fun handle(entity: Entity, world: ServerLevel) {
        if (entity !is PokemonEntity) return
        val pokemon = entity.pokemon

        if (!pokemon.isReallyWild()) return

        afterOnServer(1, world) {
            if (pokemon.persistentData.contains(SPAWN_BROADCASTED)) return@afterOnServer
            pokemon.persistentData.putBoolean(SPAWN_BROADCASTED, true)

            val pos = entity.blockPosition()

            val messages = SpawnBroadcaster(
                pokemon,
                pos,
                world.biomeManager.getBiome(pos).unwrapKey().get().location(),
                world.dimension().location(),
                null
            ).getBroadcast()
            messages.forEach { message ->
                if (config.announceCrossDimensions) {
                    Broadcast.broadcastMessage(message)
                } else if (config.broadcastRangeEnabled) {
                    Broadcast.broadcastMessage(getValidPlayers(world.dimension(), pos), message)
                } else {
                    Broadcast.broadcastMessage(world, message)
                }
            }
        }
    }
}