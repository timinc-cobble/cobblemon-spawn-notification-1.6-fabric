package us.timinc.mc.cobblemon.spawnnotification.broadcasters

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnPool
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.config

class SpawnBroadcaster(
    val pokemon: Pokemon,
    val spawnPool: SpawnPool,
    val coords: BlockPos,
    val biome: Identifier,
    val dimension: Identifier,
    val player: ServerPlayerEntity?,
) {
    private val shiny
        get() = pokemon.shiny
    private val label
        get() = pokemon.form.labels.firstOrNull { it in config.labelsForBroadcast }
    private val buckets
        get() = spawnPool
            .mapNotNull { if (it is PokemonSpawnDetail) it else null }
            .filter { it.pokemon.matches(pokemon) }
            .map { it.bucket.name }
    private val bucket
        get() = config.bucketsForBroadcast.firstOrNull { it in buckets }
    private val shouldBroadcast
        get() = ((shiny && config.broadcastShiny) || label != null || bucket != null) && config.blacklistForBroadcast.none {
            PokemonProperties.parse(
                it
            ).matches(pokemon)
        }

    fun getBroadcast(): List<Text> {
        if (!shouldBroadcast) return emptyList()

        val list = mutableListOf<Text>()
        list.add(
            config.getComponent(
                "notification.spawn",
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
                if (config.broadcastSpeciesName) pokemon.species.translatedName else Text.translatable("cobblemon.entity.pokemon"),
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
                ) else "",
                if (config.broadcastPlayerSpawnedOn && player != null) config.getComponent(
                    "notification.player",
                    player.name
                ) else "",
                if (config.broadcastJourneyMapWaypoints) buildJourneyMapWaypoint() else ""
            )
        )

        if (config.broadcastXaerosWaypoints) {
            list.add(buildXaerosWaypoint())
        }

        return list
    }

    private fun buildXaerosWaypoint() = config.getComponent(
        "notification.waypoints.xaeros",
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
        if (config.broadcastSpeciesName) pokemon.species.translatedName else Text.translatable("cobblemon.entity.pokemon"),
        coords.x,
        coords.y,
        coords.z,
        dimension.path
    )

    private fun buildJourneyMapWaypoint() = config.getComponent(
        "notification.waypoints.journeymap",
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
        if (config.broadcastSpeciesName) pokemon.species.translatedName else Text.translatable("cobblemon.entity.pokemon"),
        coords.x,
        coords.y,
        coords.z,
        "${dimension.namespace}:${dimension.path}"
    )
}
