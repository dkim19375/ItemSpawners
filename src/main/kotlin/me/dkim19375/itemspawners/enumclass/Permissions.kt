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

package me.dkim19375.itemspawners.enumclass

private const val BASE = "itemspawners"

enum class Permissions(val perm: String) {
    COMMAND("$BASE.command"),
    RELOAD("$BASE.reload"),
    EDIT("$BASE.edit"),
    LIST("$BASE.list"),
    DELETE("$BASE.delete"),
    ENABLE("$BASE.enable"),
    DISABLE("$BASE.disable"),
    TELEPORT("$BASE.teleport")
}