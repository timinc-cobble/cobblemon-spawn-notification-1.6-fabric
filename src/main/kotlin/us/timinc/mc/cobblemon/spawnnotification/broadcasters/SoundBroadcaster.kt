package us.timinc.mc.cobblemon.spawnnotification.broadcasters

import com.cobblemon.mod.common.util.playSoundServer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SoundBroadcaster(
    val level: World,
    val pos: BlockPos,
    val sound: SoundEvent
) {
    fun playShinySound() {
        level.playSoundServer(pos.toCenterPos(), sound, SoundCategory.NEUTRAL, 10f, 1f)
    }

    fun playShinySoundClient(player: PlayerEntity) {
        player.playSound(sound, 10f, 1f)
    }
}