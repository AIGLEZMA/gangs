package me.aiglez.gangs.utils

import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.function.pattern.BlockPattern
import com.sk89q.worldedit.function.pattern.Pattern
import com.sk89q.worldedit.function.pattern.Patterns
import com.sk89q.worldedit.function.pattern.RandomPattern
import com.sk89q.worldedit.regions.Region
import me.aiglez.gangs.managers.MineManager
import me.lucko.helper.Services
import org.bukkit.Material

object WorldEditUtil {

    @JvmStatic
    fun fill(region: Region, materials: Map<Material, Double>?): Boolean {
        val localWorld = Services.load(MineManager::class.java).worldEditWorld
        val editSession = EditSessionBuilder(localWorld)
            .allowedRegionsEverywhere().limitUnlimited().fastmode(true).build()

        val pattern: Pattern
        if (materials != null) {
            pattern = RandomPattern()
            for ((material, chance) in materials) {
                pattern.add(BlockPattern(BaseBlock(material.id)), chance)
            }
        } else {
            pattern = BlockPattern(BaseBlock(0))
        }
        try {
            val changed = editSession.setBlocks(region, Patterns.wrap(pattern))
            Log.debug("Set blocks changed $changed ")
        } catch (e: MaxChangedBlocksException) {
            Log.warn("Couldn't fill a region, reached max change limit is ${e.blockLimit}")
            return false
        }

        editSession.flushQueue()
        return true
    }
}