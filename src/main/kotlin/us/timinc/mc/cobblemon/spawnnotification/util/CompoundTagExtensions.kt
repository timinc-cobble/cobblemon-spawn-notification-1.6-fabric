package us.timinc.mc.cobblemon.spawnnotification.util

import net.minecraft.nbt.CompoundTag

fun CompoundTag.getUuidOrNull(key: String) = if (this.contains(key)) this.getUUID(key) else null