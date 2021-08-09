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

import me.dkim19375.itemspawners.ItemSpawners
import me.dkim19375.itemspawners.data.SpawnerData
import me.dkim19375.itemspawners.enumclass.ErrorType
import me.dkim19375.itemspawners.enumclass.Permissions
import me.dkim19375.itemspawners.extension.*
import org.bukkit.ChatColor
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ItemSpawnerCmd(private val plugin: ItemSpawners) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (!sender.hasPermission(Permissions.COMMAND)) {
            sender.sendMessage(ErrorType.NO_PERMISSION)
            return true
        }
        if (args.isEmpty()) {
            sender.sendHelpMessage(label)
            return true
        }
        when (args[0].lowercase()) {
            "help" -> {
                sender.sendHelpMessage(label = label, page = args.getOrNull(1)?.toIntOrNull() ?: 1)
                return true
            }
            "reload" -> {
                if (!sender.hasPermission(Permissions.RELOAD)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                plugin.reloadConfig()
                sender.sendMessage("${GREEN}Successfully reloaded config and data files!")
                return true
            }
            "list" -> {
                if (!sender.hasPermission(Permissions.LIST)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                sender.sendMessage("${GREEN}Spawners:")
                for (spawner in plugin.spawnersManager.spawners.toSet()) {
                    sender.sendMessage("${AQUA}${spawner.name}: ${
                        spawner.enabled.color("Enabled",
                            "Disabled")
                    }$GOLD, Item: ${spawner.item.type.format()}, Location: ${spawner.location.format()}")
                }
                if (plugin.spawnersManager.spawners.isEmpty()) {
                    sender.sendMessage("${AQUA}None")
                }
                return true
            }
            "create" -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                if (sender !is Player) {
                    sender.sendMessage(ErrorType.MUST_BE_PLAYER)
                    return true
                }
                if (sender.itemInHand.type == Material.AIR) {
                    sender.sendMessage(ErrorType.MUST_HAVE_ITEM)
                    return true
                }
                if (args.size < 3) {
                    sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                    return true
                }
                val name = args[1]
                if (plugin.spawnersManager.getSpawner(name) != null) {
                    sender.sendMessage(ErrorType.SPAWNER_EXISTS)
                    return true
                }
                val delay = args[2].toLongOrNull()
                if (delay == null) {
                    sender.sendMessage(ErrorType.DELAY_NUMBER)
                    return true
                }
                val spawner = SpawnerData(
                    name = name,
                    location = sender.location.getWrapper(),
                    item = sender.itemInHand.clone(),
                    delay = delay
                )
                plugin.spawnersManager.spawners.add(spawner)
                plugin.spawnersManager.start(spawner)
                sender.sendMessage("${GREEN}Successfully created a spawner!")
                return true
            }
            "delete" -> {
                val spawner = getSpawner(sender, args, Permissions.DELETE) {
                    for (newSpawner in it) {
                        plugin.spawnersManager.disable(newSpawner, false)
                        plugin.spawnersManager.spawners.remove(newSpawner)
                    }
                    plugin.spawnersManager.save()
                    sender.sendMessage("${GREEN}Successfully removed all spawners!")
                } ?: return true
                plugin.spawnersManager.disable(spawner)
                plugin.spawnersManager.spawners.remove(spawner)
                plugin.spawnersManager.save()
                sender.sendMessage("${GREEN}Successfully removed the spawner!")
                return true
            }
            "enable" -> {
                val spawner = getSpawner(sender, args, Permissions.ENABLE) {
                    for (newSpawner in it.filterNot(SpawnerData::enabled)) {
                        plugin.spawnersManager.start(newSpawner)
                    }
                    sender.sendMessage("${GREEN}Successfully enabled all spawners!")
                } ?: return true
                if (spawner.enabled) {
                    sender.sendMessage(ErrorType.SPAWNER_ENABLED)
                    return true
                }
                plugin.spawnersManager.start(spawner)
                sender.sendMessage("${GREEN}Successfully enabled the spawner!")
                return true
            }
            "disable" -> {
                val spawner = getSpawner(sender, args, Permissions.ENABLE) {
                    for (newSpawner in it.filter(SpawnerData::enabled)) {
                        plugin.spawnersManager.disable(newSpawner, false)
                    }
                    plugin.spawnersManager.save()
                    sender.sendMessage("${GREEN}Successfully disabled all spawners!")
                } ?: return true
                if (!spawner.enabled) {
                    sender.sendMessage(ErrorType.SPAWNER_DISABLED)
                    return true
                }
                plugin.spawnersManager.disable(spawner)
                sender.sendMessage("${GREEN}Successfully disabled the spawner!")
                return true
            }
            "teleport" -> {
                if (sender !is Player) {
                    sender.sendMessage(ErrorType.MUST_BE_PLAYER)
                    return true
                }
                val spawner = getSpawner(sender, args, Permissions.TELEPORT) ?: return true
                sender.teleport(spawner.location.getLocation())
                sender.sendMessage("${GREEN}Successfully teleported to the spawner!")
                return true
            }
            "item" -> {
                if (sender !is Player) {
                    sender.sendMessage(ErrorType.MUST_BE_PLAYER)
                    return true
                }
                val spawner = getSpawner(sender, args, minArgs = 3) ?: return true
                if (args[1].equals("get", true)) {
                    sender.inventory.addItem(spawner.item.clone())
                    sender.sendMessage("${GREEN}Successfully gave the item!")
                    return true
                }
                if (!args[1].equals("set", true)) {
                    sender.sendMessage(ErrorType.INVALID_ARG)
                    return true
                }
                if (sender.itemInHand.type == Material.AIR) {
                    sender.sendMessage(ErrorType.MUST_HAVE_ITEM)
                    return true
                }
                plugin.spawnersManager.spawners.remove(spawner)
                plugin.spawnersManager.spawners.add(spawner.copy(item = sender.itemInHand.clone()))
                plugin.spawnersManager.save()
                sender.sendMessage("${GREEN}Successfully set the item!")
                return true
            }
            "setlocation" -> {
                if (sender !is Player) {
                    sender.sendMessage(ErrorType.MUST_BE_PLAYER)
                    return true
                }
                val spawner = getSpawner(sender, args) ?: return true
                plugin.spawnersManager.spawners.remove(spawner)
                plugin.spawnersManager.spawners.add(spawner.copy(location = sender.location.getWrapper()))
                plugin.spawnersManager.save()
                sender.sendMessage("${GREEN}Successfully set the location!")
                return true
            }
            else -> {
                sender.sendMessage(ErrorType.INVALID_ARG)
                return true
            }
        }
    }

    private fun getSpawner(
        sender: CommandSender,
        args: Array<out String>,
        permission: Permissions = Permissions.EDIT,
        minArgs: Int = 2,
        runIfAll: ((Set<SpawnerData>) -> Unit)? = null
    ): SpawnerData? {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ErrorType.NO_PERMISSION)
            return null
        }
        if (args.size < minArgs) {
            sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
            return null
        }
        if (args[1].equals("all", true) && runIfAll != null) {
            runIfAll(plugin.spawnersManager.spawners.toSet())
            return null
        }
        val spawner = plugin.spawnersManager.getSpawner(args[minArgs - 1])
        if (spawner == null) {
            sender.sendMessage(ErrorType.INVALID_SPAWNER)
            return null
        }
        return spawner
    }
}