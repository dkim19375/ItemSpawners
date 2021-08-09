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

package me.dkim19375.itemspawners.command

import me.dkim19375.dkimbukkitcore.function.getMaxHelpPages
import me.dkim19375.dkimcore.extension.containsIgnoreCase
import me.dkim19375.itemspawners.ItemSpawners
import me.dkim19375.itemspawners.data.SpawnerData
import me.dkim19375.itemspawners.enumclass.Permissions
import me.dkim19375.itemspawners.extension.getMaxHelpPages
import me.dkim19375.itemspawners.extension.hasPermission
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.permissions.Permissible
import org.bukkit.util.StringUtil

class ItemSpawnerTab(private val plugin: ItemSpawners) : TabCompleter {
    private fun getPartial(token: String, collection: Iterable<String>): List<String> =
        StringUtil.copyPartialMatches(token, collection, mutableListOf())

    private fun getPartialPerm(
        token: String,
        collection: Iterable<String>,
        sender: Permissible,
        perm: Permissions = Permissions.EDIT,
    ): List<String>? {
        if (!sender.hasPermission(perm)) {
            return null
        }
        return getPartial(token, collection)
    }

    private fun getBaseCommands(sender: Permissible): List<String> {
        val list = mutableListOf("help")
        if (sender.hasPermission(Permissions.RELOAD)) {
            list.add("reload")
        }
        if (sender.hasPermission(Permissions.LIST)) {
            list.add("list")
        }
        if (sender.hasPermission(Permissions.DELETE)) {
            list.add("delete")
        }
        if (sender.hasPermission(Permissions.ENABLE)) {
            list.add("enable")
        }
        if (sender.hasPermission(Permissions.DISABLE)) {
            list.add("disable")
        }
        if (sender.hasPermission(Permissions.TELEPORT)) {
            list.add("teleport")
        }
        if (!sender.hasPermission(Permissions.EDIT)) {
            return list
        }
        list.addAll(listOf("create", "item", "setlocation"))
        return list
    }

    private fun getSpawners(): List<String> = plugin.spawnersManager.spawners.map(SpawnerData::name)

    private fun getListPerm(
        sender: Permissible,
        list: List<String>,
    ): List<String> = if (sender.hasPermission(Permissions.EDIT)) {
        list
    } else {
        emptyList()
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String>? {
        if (!sender.hasPermission(Permissions.COMMAND)) {
            return null
        }
        return when (args.size) {
            0 -> getBaseCommands(sender)
            1 -> getPartial(args[0], getBaseCommands(sender))
            2 -> {
                return when (args[0].lowercase()) {
                    "help" -> getPartial(args[1], (1..sender.getMaxHelpPages()).map(Int::toString))
                    "create" -> getListPerm(sender, listOf("<name>"))
                    "delete" -> getPartialPerm(args[1], getSpawners().plus("all"), sender, Permissions.DELETE)
                    "enable" -> getPartialPerm(args[1], getSpawners().plus("all"), sender, Permissions.ENABLE)
                    "disable" -> getPartialPerm(args[1], getSpawners().plus("all"), sender, Permissions.DISABLE)
                    "teleport" -> getPartialPerm(args[1], getSpawners(), sender, Permissions.TELEPORT)
                    "item" -> getPartialPerm(args[1], listOf("get", "set"), sender)
                    "setlocation" -> getPartialPerm(args[1], getSpawners(), sender)
                    else -> emptyList()
                }
            }
            3 -> {
                return when (args[0].lowercase()) {
                    "create" -> getListPerm(sender, listOf("<delay - ticks (20 ticks = 1 second)>"))
                    "item" -> {
                        if (!listOf("get", "set").containsIgnoreCase(args[1])) {
                            return emptyList()
                        }
                        return getPartialPerm(args[2], getSpawners(), sender)
                    }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
    }
}