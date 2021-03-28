package me.aiglez.gangs.listeners

import com.google.common.base.Preconditions
import com.sk89q.worldedit.bukkit.BukkitUtil
import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent
import me.aiglez.gangs.commands.admin.ToggleMinePlaceCommand
import me.aiglez.gangs.managers.MineManager
import me.aiglez.gangs.managers.UserManager
import me.lucko.helper.Services
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class MineListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player
        val block = e.block

        if (!isMineWorld(block)) return
        if (!ToggleMinePlaceCommand.canPlace(player)) {
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onBlockBreak(e: BlockBreakEvent) {
        val user = Services.load(UserManager::class.java).getUser(e.player.uniqueId)
        val block = e.block
        if (!user.hasGang() || !isMineWorld(block)) return
        val mine = user.gang.mine

        if (mine.level.blocks.contains(block.type) && mine.minableRegion.contains(BukkitUtil.toVector(block))) {
            mine.handle()
        } else {
            user.message("&c(Debug) You can't break that block")
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onTEBlockBreak(e: TEBlockExplodeEvent) {
        val user = Services.load(UserManager::class.java).getUser(e.player.uniqueId)
        val block = e.block
        if (!user.hasGang() || !isMineWorld(block)) return
        val mine = user.gang.mine

        for (exploded in e.blockList()) {
            if (mine.level.blocks.contains(block.type) && mine.minableRegion.contains(BukkitUtil.toVector(block))) {
                mine.handle()
            } else {
                user.message("&cYou can't break that block")
            }
        }
    }

    private fun isMineWorld(block: Block): Boolean {
        Preconditions.checkNotNull(block, "block may not be null")
        return block.location.world.uid == Services.load(MineManager::class.java).mineWorld.uid
    }
}