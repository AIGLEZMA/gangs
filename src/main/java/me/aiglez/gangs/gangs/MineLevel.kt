package me.aiglez.gangs.gangs

import me.aiglez.gangs.managers.MineManager
import me.lucko.helper.Services
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class MineLevel(
    val ordinal: Int,
    val upgradeCost: Long,
    val blocks: Map<ItemStack, Double>,
    val lore: List<String>
) {

    fun isMinable(material: Material): Boolean {
        return blocks.map { entry -> entry.key.type }.contains(material)
    }

    companion object {

        fun calculateCost(start: MineLevel, end: MineLevel): Long {
            var cost = 0L
            for (level in Services.load(MineManager::class.java).levels) {
                if (level.ordinal > start.ordinal && level.ordinal <= end.ordinal) {
                    cost += level.upgradeCost
                }
            }
            return cost
        }

    }
}