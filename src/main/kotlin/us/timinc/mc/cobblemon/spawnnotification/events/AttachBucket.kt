package us.timinc.mc.cobblemon.spawnnotification.events

import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.BUCKET

object AttachBucket {
    fun handle(evt: SpawnEvent<PokemonEntity>) {
        evt.entity.pokemon.persistentData.putString(BUCKET, evt.ctx.cause.bucket.name)
    }
}