package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity

object AttachBucket {
    const val BUCKET = "spawn_notification:bucket"
    fun handle(evt: SpawnEvent<PokemonEntity>) {
        evt.entity.pokemon.persistentData.putString(BUCKET, evt.ctx.cause.bucket.name)
    }
}