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

enum class ErrorType(val description: String) {
    NO_PERMISSION("You do not have permission!"),
    INVALID_ARG("Invalid argument!"),
    MUST_BE_PLAYER("You must be a player!"),
    SPAWNER_EXISTS("The spawner already exists!"),
    INVALID_SPAWNER("The spawner doesn't exist!"),
    NOT_ENOUGH_ARGS("Not enough arguments!"),
    DELAY_NUMBER("The delay must be a number!"),
    MUST_HAVE_ITEM("You must have an item in your hand!"),
    SPAWNER_ENABLED("The spawner is already enabled!"),
    SPAWNER_DISABLED("The spawner is already disabled!")
}