@file:Suppress("MemberVisibilityCanBePrivate")

package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
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
        val level = entity.level()
        if (level !is ServerLevel) return

        val lastAttacker = entity.lastAttacker
        if (lastAttacker === null) {
            evt.pokemon.persistentData.putBoolean(FAINT_HAS_ENTITY, false)
        } else {
            evt.pokemon.persistentData.putBoolean(FAINT_HAS_ENTITY, true)
            evt.pokemon.persistentData.putUUID(FAINT_ENTITY, lastAttacker.uuid)
        }
    }

    fun handle(evt: BattleFaintedEvent) {
        if (!config.broadcastFaints) return
        if (!evt.killed.effectedPokemon.isWild()) return

        val entity = evt.killed.entity ?: return
        val level = entity.level()
        if (level !is ServerLevel) return

        evt.killed.effectedPokemon.persistentData.putBoolean(FAINT_HAS_ENTITY, true)
        evt.killed.facedOpponents.find { e -> e.effectedPokemon.getOwnerUUID() != null }?.effectedPokemon?.getOwnerPlayer()
            ?.let {
                evt.killed.effectedPokemon.persistentData.putUUID(FAINT_ENTITY, it.uuid)
            }
    }

    fun handle(entity: Entity, level: ServerLevel) {
        if (entity !is PokemonEntity) return
        if (!entity.pokemon.persistentData.contains(FAINT_HAS_ENTITY)) return

        val faintHasEntity = entity.pokemon.persistentData.getBoolean(FAINT_HAS_ENTITY)
        val attackingEntity = if (!faintHasEntity) null else entity.pokemon.persistentData.getUUID(
            FAINT_ENTITY
        )?.let { level.getEntity(it) }
        val coords = entity.blockPosition()

        broadcast(
            entity.pokemon,
            coords,
            level.getBiome(coords).unwrapKey().get().location(),
            level.dimension().location(),
            level,
            attackingEntity
        )
    }

    private fun broadcast(
        pokemon: Pokemon,
        coords: BlockPos,
        biome: ResourceLocation,
        dimension: ResourceLocation,
        level: ServerLevel,
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
                Broadcast.broadcastMessage(getValidPlayers(level.dimension(), coords), message)
            } else {
                Broadcast.broadcastMessage(level, message)
            }
        }
    }
}