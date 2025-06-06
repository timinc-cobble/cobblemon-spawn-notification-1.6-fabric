package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import kotlin.math.sqrt

object PlayerUtil {
    /**
     * Get a list of valid players.
     *
     * @param pos The center to block to search from.
     * @param range The distance (in block radius) to search out to.
     * @param dimensionKey The dimension to search in.
     * @param playerLimit The maximum number of players to accept (prioritized by least distance).
     *
     * @return The filtered list of players.
     */
    fun getValidPlayers(
        pos: BlockPos, range: Int, dimensionKey: ResourceKey<Level>, playerLimit: Int,
    ): List<ServerPlayer> {
        return getValidPlayers(pos, range, dimensionKey).sortedBy { sqrt(pos.distSqr(it.blockPosition())) }
            .take(playerLimit)
    }

    /**
     * Get a list of valid players.
     *
     * @param pos The center to block to search from.
     * @param range The distance (in block radius) to search out to.
     * @param dimensionKey The dimension to search in.
     *
     * @return The filtered list of players.
     */
    fun getValidPlayers(
        pos: BlockPos, range: Int, dimensionKey: ResourceKey<Level>,
    ): List<ServerPlayer> {
        val serverInstance = server() ?: return emptyList()

        return serverInstance.playerList.players.filter {
            val distance = sqrt(pos.distSqr(it.blockPosition()))
            return@filter distance <= range && dimensionKey == it.level().dimension()
        }
    }

    fun getValidPlayers(level: ResourceKey<Level>, pos: BlockPos): List<ServerPlayer> {
        return if (config.playerLimitEnabled) getValidPlayers(
            pos, config.broadcastRange, level, config.playerLimit
        ) else getValidPlayers(pos, config.broadcastRange, level)
    }
}