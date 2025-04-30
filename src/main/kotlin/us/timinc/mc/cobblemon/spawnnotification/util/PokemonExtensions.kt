package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.pokemon.Pokemon

fun Pokemon.isReallyWild() = this.isWild() && this.originalTrainer === null