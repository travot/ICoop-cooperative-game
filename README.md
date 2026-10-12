# ICoop: A Two-Player Cooperative Action-Puzzle Game

A top-down, Zelda-style game for two players on one keyboard. One plays a fire hero, the other a water hero. Neither can finish the dungeon alone: fire and water walls hurt the hero of the wrong element, pressure plates only hold walls open while someone stands on them, and each hero must collect the orb, staff and key of its own element.

> Built by Alban Thèves and Tom Ravot for **EPFL CS-107 (Introduction to Programming)**, autumn semester 2024. Java 21.

## About this repository

The project was developed during the autumn 2024 semester on **EPFL's internal Git platform**, which is why this GitHub account shows no activity for that period. This repository is a curated copy of the code we wrote, published afterwards. It does not include the course-provided game engine, the game assets (sprites, maps, fonts) or the submission tools, which belong to the course. See [Build](#build) for what is needed to run it.

## The game

The two heroes must escape a trap laid by the Queen, crossing four areas:

| Area | What happens there |
|---|---|
| **Spawn** | Hub area with doors to the other zones and the manor door that ends the game |
| **OrbWay** | Each hero collects an orb that makes them immune to their own element. Pressure plates open elemental walls |
| **Maze** | A long corridor full of enemies, bombs and elemental walls, where the heroes find their staffs |
| **Arena** | Each hero collects a key. Together the keys unlock the portal back to Spawn |

**Controls**

| | Fire hero | Water hero |
|---|---|---|
| Move | W A S D | I J K L |
| Switch item | Q | U |
| Use item | E | O |

Space advances dialogue, `R` resets the game and `T` resets the current area.

## What we built

- **Two players, one world.** Each hero is a state machine (idle, sword attack, staff attack) with its own health bar, inventory, key bindings and damage-specific invulnerability timers (fire, water, explosion, physical). The camera follows the midpoint between the two heroes.
- **Elemental mechanics.** Walls, orbs, staffs, keys and projectiles carry an element (fire or water). Elemental walls damage the hero who touches them, a hero becomes immune to its own element by collecting its orb, and orbs, staffs and keys can only be picked up by the matching hero.
- **Items and combat.** A sword, fire and water staffs that shoot elemental balls, collectable explosives with a timed fuse, hearts that heal, and rocks that staffs can destroy.
- **Enemies.** `BombFoe` wanders randomly, chases a hero, plants a bomb when within range two, then protects itself. `HellSkull` is stationary and spits fire at random intervals. `BombFoe` is immune to water and explosions, `HellSkull` to fire and explosions.
- **Level logic.** A pressure plate holds a group of walls open through a `Logic` signal while a hero stands on it, so mechanisms are wired by passing objects rather than hard-coded. The two keys together unlock a portal, and doors carry both players to the next area at chosen coordinates.
- **Dialogue.** Heroes display messages from the engine's dialogue class (welcome, orb, locked door, victory), and a hero's controls are frozen while its message is on screen.

## How it works

The game is built on the course's 2D engine (`areagame`: areas, actors, cells and a game loop) and organised in four packages:

| Package | Role |
|---|---|
| `icoop` | Game class, key bindings, cell behaviour, collectables |
| `icoop.actor` | Players, enemies, items, walls, doors, portals, projectiles |
| `icoop.area` | The four areas and their layout |
| `icoop.handler` | Inventory, items, player status display, interaction visitor |

Design choices worth noting:

- **Visitor pattern for interactions.** Every actor implements `Interactable` and/or `Interactor`, and `ICoopInteractionVisitor` declares one `interactWith` method per actor type. When a player touches a rock, a key or an enemy, double dispatch picks the right behaviour without a chain of `instanceof` checks. Each actor with reactions has its own inner handler.
- **Maps drawn as images.** `ICoopBehavior` reads the pixel colours of a behaviour image to build the grid of cells (wall, walkable, door, rock, obstacle), so a level layout can be edited in an image editor.
- **Composition for shared parts.** `Health` is a reusable component used by players and enemies. Elemental behaviour is shared through the `ElementalEntity` interface and the `ElementalItem` and `ElementalWall` base classes.
- **Documentation.** About 5,900 lines of Java across 37 files, with Javadoc on public classes and methods.

## Build

The code compiles against the course's game engine (package `ch.epfl.cs107.play`) and expects its resources (sprites, behaviour maps, fonts and dialogues under `resources/`), neither of which is included here. With them in place, the project compiles with Java 21 and starts from `ch.epfl.cs107.Play`. No build file is included, because the project was built inside the course's own setup.

## Authors

Alban Thèves and Tom Ravot
