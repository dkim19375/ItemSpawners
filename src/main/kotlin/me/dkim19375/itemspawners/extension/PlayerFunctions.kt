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

package me.dkim19375.itemspawners.extension

import me.dkim19375.dkimbukkitcore.data.HelpMessage
import me.dkim19375.dkimbukkitcore.function.getMaxHelpPages
import me.dkim19375.dkimbukkitcore.function.showHelpMessage
import me.dkim19375.itemspawners.ItemSpawners
import me.dkim19375.itemspawners.enumclass.ErrorType
import me.dkim19375.itemspawners.enumclass.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permissible
import org.bukkit.plugin.java.JavaPlugin

private val plugin: ItemSpawners by lazy { JavaPlugin.getPlugin(ItemSpawners::class.java) }

private val commands = listOf(
    HelpMessage("help [page]", "Send help message", Permissions.COMMAND.perm),
    HelpMessage("reload", "Reload the plugin's config and data files", Permissions.RELOAD.perm),
    HelpMessage("list", "List all the spawners", Permissions.LIST.perm),
    HelpMessage(arg = "create <name> <delay - TICKS>",
        description = "Creates a spawner that drops the item you're holding every <delay> ticks (20 ticks = 1 second) at your location",
        permission = Permissions.EDIT.perm),
    HelpMessage("delete <name|all>", "Delete a spawner", Permissions.DELETE.perm),
    HelpMessage("enable <name|all>", "Enable a spawner", Permissions.ENABLE.perm),
    HelpMessage("disable <name|all>", "Disable a spawner", Permissions.DISABLE.perm),
    HelpMessage("teleport <name>", "Teleport to a spawner", Permissions.TELEPORT.perm),
    HelpMessage("item get <name>", "Get the item that is being spawned", Permissions.EDIT.perm),
    HelpMessage("item set <name>", "Set the item that is being spawned", Permissions.EDIT.perm),
    HelpMessage("setlocation <name>", "Set the location of a spawner", Permissions.EDIT.perm),
)

fun Permissible.hasPermission(permission: Permissions): Boolean = hasPermission(permission.perm)

fun Permissible.getMaxHelpPages(): Int = getMaxHelpPages(commands)

fun CommandSender.sendMessage(error: ErrorType) = sendMessage("${ChatColor.RED}${error.description}")

fun CommandSender.sendHelpMessage(label: String, error: ErrorType? = null, page: Int = 1) =
    showHelpMessage(label, error?.description, page, commands, plugin)