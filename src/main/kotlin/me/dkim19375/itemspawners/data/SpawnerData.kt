/*
 *     ItemSpawners, A spigot plugin that creates item spawners
 *     Copyright (C) 2021  dkim19375
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.dkim19375.itemspawners.data

import me.dkim19375.dkimbukkitcore.data.LocationWrapper
import me.dkim19375.dkimbukkitcore.function.logInfo
import me.dkim19375.itemspawners.extension.format
import me.dkim19375.itemspawners.extension.getWrapper
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack

data class SpawnerData(
    val name: String,
    val location: LocationWrapper,
    val item: ItemStack,
    val delay: Long,
    val enabled: Boolean = true
) : ConfigurationSerializable {
    override fun serialize(): Map<String, Any> = mapOf(
        "name" to name,
        "location" to location.getLocation(),
        "item" to item,
        "delay" to delay,
        "enabled" to enabled
    )

    override fun toString(): String {
        return "SpawnerData(name='$name', location=${location.format()}, item=$item, delay=$delay, enabled=$enabled)"
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): SpawnerData? {
            val name = map["name"] as? String ?: run {
                logInfo("Invalid data: name")
                return null
            }
            val location = (map["location"] as? Location)?.getWrapper() ?: run {
                logInfo("Invalid data for $name: location")
                return null
            }
            val item = map["item"] as? ItemStack ?: run {
                logInfo("Invalid data for $name: item")
                return null
            }
            val delay = (map["delay"] as? Number)?.toLong() ?: run {
                logInfo("Invalid data for $name: delay")
                return null
            }
            val enabled = map["enabled"] as? Boolean ?: run {
                logInfo("Invalid data for $name: enabled")
                return null
            }
            return SpawnerData(name, location, item, delay, enabled)
        }
    }


}