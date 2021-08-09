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

package me.dkim19375.itemspawners.manager

import me.dkim19375.dkimbukkitcore.function.logInfo
import me.dkim19375.dkimcore.extension.removeIf
import me.dkim19375.itemspawners.ItemSpawners
import me.dkim19375.itemspawners.data.SpawnerData
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import kotlin.math.min

class SpawnersManager(private val plugin: ItemSpawners) {
    private val tasks = mutableMapOf<String, Pair<Long, BukkitTask>>()
    val spawners = mutableSetOf<SpawnerData>()

    fun reload() {
        val newSet = mutableSetOf<SpawnerData>()
        val section = plugin.spawnersFile.config.getConfigurationSection("spawners") ?: return
        for (key in section.getKeys(false)) {
            val spawner = section.get(key) as? SpawnerData
            if (spawner == null) {
                logInfo("Invalid spawner data: $key")
                continue
            }
            newSet.add(spawner)
        }
        spawners.clear()
        spawners.addAll(newSet)
        startTasks()
    }

    fun save() {
        for (spawner in spawners.toSet()) {
            plugin.spawnersFile.config.set("spawners.${spawner.name}", spawner)
        }
        plugin.spawnersFile.save()
    }

    private fun startTasks() {
        val savedTasks = tasks.map { it.key to it.value.first }.toMap()
        tasks.forEach { (_, task) -> task.second.cancel() }
        tasks.clear()
        for (spawner in spawners.toSet()) {
            start(spawner, savedTasks[spawner.name])
        }
    }

    fun getSpawner(name: String): SpawnerData? =
        spawners.firstOrNull { it.name == name } ?: spawners.firstOrNull { it.name.equals(name, true) }

    fun start(spawner: SpawnerData, firstDelay: Long? = null) {
        spawners.remove(spawner)
        spawners.add(spawner.copy(enabled = true))
        val spawnerName = spawner.name
        val newFirstDelay = firstDelay?.let { min(spawner.delay, plugin.currentTick - it) } ?: 1L
        tasks[spawnerName] = plugin.currentTick to Bukkit.getScheduler().runTaskTimer(plugin, {
            tasks[spawnerName]?.let {
                tasks[spawnerName] = plugin.currentTick to it.second
            }
            val newSpawner = getSpawner(spawnerName) ?: return@runTaskTimer
            newSpawner.location.world.dropItem(newSpawner.location.getLocation(), newSpawner.item.clone())
        }, newFirstDelay, spawner.delay)
        save()
    }

    fun disable(spawner: SpawnerData, save: Boolean = true) {
        tasks[spawner.name]?.second?.cancel()
        tasks.remove(spawner.name)
        spawners.remove(spawner)
        spawners.add(spawner.copy(enabled = false))
        if (!save) {
            return
        }
        save()
    }
}