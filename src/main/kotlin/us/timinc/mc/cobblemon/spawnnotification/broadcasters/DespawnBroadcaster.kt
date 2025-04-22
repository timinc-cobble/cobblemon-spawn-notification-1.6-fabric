package us.timinc.mc.cobblemon.spawnnotification.broadcasters

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config
import us.timinc.mc.cobblemon.spawnnotification.events.AttachBucket.BUCKET

class DespawnBroadcaster(
    val pokemon: Pokemon,
    val coords: BlockPos,
    val biome: Identifier,
    val dimension: Identifier,
) {
    private val shiny
        get() = pokemon.shiny
    private val blacklisted
        get() = config.blacklistForBroadcast.any {
            PokemonProperties.parse(
                it
            ).matches(pokemon)
        }
    private val label
        get() = if (blacklisted) null else pokemon.form.labels.firstOrNull { it in config.labelsForBroadcast }
    private val bucket
        get() = if (blacklisted || !pokemon.persistentData.contains(BUCKET)) null else config.bucketsForBroadcast.firstOrNull {
            it == pokemon.persistentData.getString(
                BUCKET
            )
        }
    private val shouldBroadcast
        get() = ((shiny && config.broadcastShiny) || label != null || bucket != null) && config.blacklistForBroadcastEvenIfShiny.none {
            PokemonProperties.parse(
                it
            ).matches(pokemon)
        }

    fun getBroadcast(): Text? {
        if (!shouldBroadcast) return null

        return config.getComponent(
            "notification.despawn",
            if (shiny && config.broadcastShiny) config.getComponent(
                "notification.shiny",
                config.getComponent("shiny")
            ) else "",
            if (label != null) config.getComponent(
                "notification.label",
                config.getComponent("label.$label")
            ) else "",
            if (bucket != null) config.getComponent(
                "notification.bucket",
                config.getComponent("bucket.$bucket")
            ) else "",
            pokemon.species.translatedName,
            if (config.broadcastBiome) config.getComponent(
                "notification.biome",
                config.getRawComponent("biome.${biome.toTranslationKey()}")
            ) else "",
            if (config.broadcastCoords) config.getComponent(
                "notification.coords",
                coords.x,
                coords.y,
                coords.z
            ) else "",
            if (config.announceCrossDimensions) config.getComponent(
                "notification.dimension",
                config.getRawComponent("dimension.${dimension.toTranslationKey()}")
            ) else ""
        )
    }
}