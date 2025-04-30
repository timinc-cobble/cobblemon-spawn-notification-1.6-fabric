package us.timinc.mc.cobblemon.spawnnotification.broadcasters

import com.cobblemon.mod.common.util.playSoundServer
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class SoundBroadcaster(
    val level: Level,
    val pos: BlockPos,
    val sound: SoundEvent,
) {
    fun playShinySound() {
        level.playSoundServer(pos.center, sound, SoundSource.NEUTRAL, 10f, 1f)
    }

    fun playShinySoundClient(player: Player) {
        player.playSound(sound, 10f, 1f)
    }
}