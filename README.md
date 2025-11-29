# CBL Game - 2D Adventure Game

A 2D tile-based adventure game built in Java featuring intelligent enemy AI using Dijkstra's pathfinding algorithm and comprehensive collision detection system.

## Game Features

- **Tile-based World**: Navigate through dynamically loaded chunks with different terrain types
- **Intelligent Enemy AI**: Enemies use Dijkstra's algorithm to find the shortest path to the player
- **Combat System**: Attack enemies with cooldown-based combat mechanics
- **Collision Detection**: Accurate AABB (Axis-Aligned Bounding Box) collision system
- **Chunk Loading**: Seamless world exploration with dynamic chunk loading
- **Multiple Enemy Types**: Different enemy classes with varying stats and behaviors

## Controls

### Movement
- **WASD Keys** or **Arrow Keys**: Move the player character
  - W / Up Arrow: Move up
  - A / Left Arrow: Move left
  - S / Down Arrow: Move down
  - D / Right Arrow: Move right

### Combat
- **Spacebar**: Attack enemies within range

## How to Run

1. **Prerequisites**: Ensure you have Java Development Kit (JDK) installed on your system
2. **Open Project**: Open the `Game` folder in Visual Studio Code or your preferred IDE
3. **Run Game**: Execute the `Game.java` file
   ```bash
   javac *.java
   java Game
   ```

## Technologies Used

- **Java**: Core programming language
- **Java Swing**: GUI framework for game window and graphics
- **Java AWT**: Graphics and image handling
- **BufferedImage**: Sprite and tile image management
- **Multithreading**: Game loop and sprite animation handling

## Core Algorithms & Concepts

### Dijkstra's Pathfinding Algorithm
The game implements Dijkstra's algorithm for enemy AI pathfinding:
- Enemies calculate the shortest path to the player in real-time
- Uses a priority queue for efficient path calculation
- Accounts for terrain obstacles and walls
- Recalculates paths periodically for dynamic gameplay

### Collision Detection
Robust collision system featuring:
- **AABB (Axis-Aligned Bounding Box)** collision detection
- Wall collision prevention
- Boundary checking for chunk borders
- Entity-to-entity interaction detection

### Chunk Loading System
Dynamic world loading mechanism:
- **8x8 tile chunks** for memory efficiency
- Seamless transitions between world areas
- Text-based chunk data format for easy level editing
- Automatic enemy spawning per chunk

## Game Mechanics

### Combat System
- **Attack Range**: Each entity has a defined attack range
- **Cooldown System**: Prevents spam attacking
- **Health Points**: Both player and enemies have HP systems
- **Damage Dealing**: Different entities deal varying damage amounts

### Movement System
- **Velocity-based movement** with acceleration and deceleration
- **Smooth controls** with gradual speed changes
- **Collision-aware movement** preventing illegal positioning

### Entity System
- **Base Entity class** for shared functionality
- **Polymorphic design** for different entity types
- **Sprite animation system** for visual feedback
- **Health and damage management**

## Authors

- **Norbert Mazur**
- **Lars Wouters**

*Programming 2IP90 CBL Project*
