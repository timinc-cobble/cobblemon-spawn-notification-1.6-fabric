package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.SpawnBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastSpawn {
    fun handle(evt: SpawnEvent<PokemonEntity>) {
        val world = evt.ctx.world
        val pos = evt.ctx.position
        val pokemon = evt.entity.pokemon

        if (world.isClient) return
        if (pokemon.isPlayerOwned()) return

        val messages = SpawnBroadcaster(
            evt.entity.pokemon,
            evt.ctx.spawner.getSpawnPool(),
            evt.ctx.position,
            evt.ctx.biomeName,
            evt.ctx.world.dimensionEntry.key.get().value,
            if (evt.ctx.spawner is PlayerSpawner) (evt.ctx.spawner as PlayerSpawner).getCauseEntity() else null
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