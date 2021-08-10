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

package me.dkim19375.itemspawners

import io.github.slimjar.app.builder.ApplicationBuilder
import me.dkim19375.dkimbukkitcore.checker.UpdateChecker
import me.dkim19375.dkimbukkitcore.config.ConfigFile
import me.dkim19375.dkimbukkitcore.function.logInfo
import me.dkim19375.dkimbukkitcore.javaplugin.CoreJavaPlugin
import me.dkim19375.itemspawners.command.ItemSpawnerCmd
import me.dkim19375.itemspawners.command.ItemSpawnerTab
import me.dkim19375.itemspawners.data.SpawnerData
import me.dkim19375.itemspawners.manager.SpawnersManager
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.util.logging.Level
import kotlin.system.measureTimeMillis

class ItemSpawners : CoreJavaPlugin() {
    override val defaultConfig: Boolean = false
    private val updateChecker = UpdateChecker("95173", null, this)
    val spawnersFile by lazy { ConfigFile(this, "data/spawners.yml") }
    val spawnersManager = SpawnersManager(this)
    var currentTick = 0L
        private set

    override fun onLoad() {
        logInfo("Loading libraries... (This may take a few seconds up to a minute)")
        logInfo(
            "Finished loading libraries in ${
                measureTimeMillis {
                    ApplicationBuilder.appending(description.name).build()
                }
            }ms!"
        )
    }

    override fun onEnable() {
        registerConfigs()
        reloadConfig()
        registerCommand("itemspawners", ItemSpawnerCmd(this), ItemSpawnerTab(this))
        Bukkit.getScheduler().runTask(this) {
            checkForUpdates()
            currentTick++
            Bukkit.getScheduler().runTaskTimer(this, { currentTick++ }, 1L, 1L)
        }
    }

    override fun onDisable() {
        unregisterConfig(spawnersFile)
        ConfigurationSerialization.unregisterClass(SpawnerData::class.java)
    }

    override fun reloadConfig() {
        super.reloadConfig()
        spawnersManager.reload()
    }

    private fun registerConfigs() {
        ConfigurationSerialization.registerClass(SpawnerData::class.java)
        registerConfig(spawnersFile)
    }

    private fun checkForUpdates() {
        updateChecker.getSpigotVersion({ version ->
            if (version == description.version) {
                logInfo("${description.name} is up to date! ($version)")
                return@getSpigotVersion
            }
            logInfo("${description.name} is outdated!", Level.WARNING)
            logInfo("Your version: ${description.version}", Level.WARNING)
            logInfo("Newest version: $version", Level.WARNING)
            logInfo("Please update here: ${description.website}", Level.WARNING)
        }) {
            logInfo("Could not get latest version!", Level.SEVERE)
            it.printStackTrace()
        }
    }
}