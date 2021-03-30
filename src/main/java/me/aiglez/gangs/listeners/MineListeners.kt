package me.aiglez.gangs.listeners

import com.google.common.base.Preconditions
import com.sk89q.worldedit.bukkit.BukkitUtil
import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent
import me.aiglez.gangs.commands.admin.ToggleMinePlaceCommand
import me.aiglez.gangs.managers.MineManager
import me.aiglez.gangs.managers.UserManager
import me.aiglez.gangs.users.User
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
        val user = User.get(e.player)
        val block = e.block
        if (!user.hasGang() || !isMineWorld(block)) return
        val mine = user.gang.mine

        if (mine.level.isMinable(block.type) && mine.minableRegion.contains(BukkitUtil.toVector(block))) {
            mine.handle()
        } else {
            user.message("&c(Debug) You have broken a block that is not minable")
        }
    }

    @EventHandler
    fun onTEBlockBreak(e: TEBlockExplodeEvent) {
        val user = User.get(e.player)
        val block = e.block
        if (!user.hasGang() || !isMineWorld(block)) return
        val mine = user.gang.mine

        for (exploded in e.blockList()) {
            if (mine.level.isMinable(block.type) && mine.minableRegion.contains(BukkitUtil.toVector(block))) {
                mine.handle()
            } else {
                user.message("&c(Debug TE) You have broken a block that is not minable")
            }
        }
    }

    private fun isMineWorld(block: Block): Boolean {
        Preconditions.checkNotNull(block, "block may not be null")
        return block.location.world.uid == Services.load(MineManager::class.java).mineWorld.uid
    }
}