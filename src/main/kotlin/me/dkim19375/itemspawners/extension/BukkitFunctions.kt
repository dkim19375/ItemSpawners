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

import me.dkim19375.dkimbukkitcore.data.LocationWrapper
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bukkit.Location
import org.bukkit.Material

fun Location.getWrapper(): LocationWrapper = LocationWrapper(this)

fun Material.format(): String = WordUtils.capitalize(name.lowercase().replace("_", " "))

fun LocationWrapper.format(): String = "${world.name}, $x, $y, $z"