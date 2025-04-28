@file:Suppress("MemberVisibilityCanBePrivate")

package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.FAINT_ENTITY
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.FAINT_HAS_ENTITY
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.broadcasters.FaintBroadcaster
import us.timinc.mc.cobblemon.spawnnotification.util.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.util.PlayerUtil.getValidPlayers

object BroadcastFaint {

    fun handle(evt: PokemonFaintedEvent) {
        if (!config.broadcastFaints) return
        if (!evt.pokemon.isWild()) return

        val entity = evt.pokemon.entity ?: return
        val level = entity.world
        if (level !is ServerWorld) return

        val lastAttacker = entity.lastAttacker
        if (lastAttacker === null) {
            evt.pokemon.persistentData.putBoolean(FAINT_HAS_ENTITY, false)
        } else {
            evt.pokemon.persistentData.putBoolean(FAINT_HAS_ENTITY, true)
            evt.pokemon.persistentData.putUuid(FAINT_ENTITY, lastAttacker.uuid)
        }
    }

    fun handle(evt: BattleFaintedEvent) {
        if (!config.broadcastFaints) return
        if (!evt.killed.effectedPokemon.isWild()) return

        val entity = evt.killed.entity ?: return
        val level = entity.world
        if (level !is ServerWorld) return

        evt.killed.effectedPokemon.persistentData.putBoolean(FAINT_HAS_ENTITY, true)
        evt.killed.facedOpponents.find { e -> e.effectedPokemon.getOwnerUUID() != null }?.effectedPokemon?.getOwnerPlayer()
            ?.let {
                evt.killed.effectedPokemon.persistentData.putUuid(FAINT_ENTITY, it.uuid)
            }
    }

    fun handle(entity: Entity, level: ServerWorld) {
        if (entity !is PokemonEntity) return
        if (!entity.pokemon.persistentData.contains(FAINT_HAS_ENTITY)) return

        val faintHasEntity = entity.pokemon.persistentData.getBoolean(FAINT_HAS_ENTITY)
        val attackingEntity = if (!faintHasEntity) null else entity.pokemon.persistentData.getUuid(
            FAINT_ENTITY
        )?.let { level.getEntity(it) }
        val coords = entity.blockPos

        broadcast(
            entity.pokemon,
            coords,
            level.getBiome(coords).key.get().value,
            level.dimensionEntry.key.get().value,
            level,
            attackingEntity
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: Identifier,
        dimension: Identifier,
        level: ServerWorld,
        player: Entity? = null,
    ) {
        FaintBroadcaster(
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