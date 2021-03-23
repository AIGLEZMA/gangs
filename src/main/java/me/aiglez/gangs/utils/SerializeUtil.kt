package me.aiglez.gangs.utils

import com.google.gson.JsonObject
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.bukkit.BukkitUtil
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import me.lucko.helper.Helper
import me.lucko.helper.gson.JsonBuilder
import org.bukkit.Location
import org.bukkit.World

object SerializeUtil {

    fun serialize(location: Location) : JsonObject {
        return JsonBuilder.`object`()
            .add("x", location.x)
            .add("y", location.y)
            .add("z", location.z)
            .add("yaw", location.yaw)
            .add("pitch", location.pitch)
            .add("world", location.world.name)
            .build()
    }

    fun deserialize(json: JsonObject) : Location? {
        val world = Helper.world(json["world"].asString)
        if(!world.isPresent) {
            return null
        }
        return Location(
            world.get(), json["x"].asDouble, json["y"].asDouble, json["z"].asDouble, json["yaw"].asFloat, json["pitch"].asFloat
        )
    }

    fun serialize(region: Region): JsonObject {
        return JsonBuilder.`object`()
            .add("world", region.world?.name)
            .add("min", JsonBuilder.`object`()
                .add("x", region.minimumPoint.x)
                .add("y", region.minimumPoint.y)
                .add("z", region.minimumPoint.z)
                .build()
            )
            .add("max", JsonBuilder.`object`()
                .add("x", region.maximumPoint.x)
                .add("y", region.maximumPoint.y)
                .add("z", region.maximumPoint.z)
                .build()
            )
            .build()
    }

    fun deserializeRegion(json: JsonObject) : Region {
        val min = json.get("min").asJsonObject
        val minVector = Vector(min.get("x").asDouble, min.get("y").asDouble, min.get("z").asDouble)

        val max = json.get("max").asJsonObject
        val maxVector = Vector(max.get("x").asDouble, max.get("y").asDouble, max.get("z").asDouble)

        val worldName: String? = json.get("world")?.asString
        val world: World = Helper.worldNullable(worldName.orEmpty()) ?: return CuboidRegion(minVector, maxVector)

        return CuboidRegion(BukkitUtil.getLocalWorld(world), minVector, maxVector)
    }
}