package me.aiglez.gangs.helpers

import me.aiglez.gangs.managers.ConfigurationManager
import me.lucko.helper.Services
import me.lucko.helper.config.ConfigurationNode

object Configuration {

    @JvmStatic
    fun getString(vararg path: Any) : String {
        return getNode(*path).getString("null")
    }

    @JvmStatic
    fun getBoolean(vararg path: Any) : Boolean {
        return getNode(*path).getBoolean(false)
    }

    @JvmStatic
    fun getLong(vararg path: Any) : Long {
        return getNode(*path).getLong(1L)
    }

    @JvmStatic
    fun getInteger(vararg path: Any) : Int {
        return getNode(*path).getInt(-1)
    }

    @JvmStatic
    fun getDouble(vararg path: Any) : Double {
        return getNode(*path).getDouble(-1.0)
    }

    @JvmStatic
    fun getList(vararg path: Any) : List<String> {
        return getNode(*path).getList { t -> t.toString() }
    }

    @JvmStatic
    fun getNode(vararg path: Any) : ConfigurationNode {
        return Services.load(ConfigurationManager::class.java).configNode.getNode(*path)
    }
}