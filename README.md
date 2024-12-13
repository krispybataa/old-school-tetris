# 🎮 Old School Tetris

A classic implementation of Tetris with a retro twist, showcasing various Operating System concepts through its architecture.

## 🚀 Features

- Classic Tetris gameplay with retro-style graphics
- Progressive difficulty system
- Score tracking with high score persistence
- Responsive controls
- Background music and sound effects
- Pause/Resume functionality

## 🔧 Technical Implementation

### Threading and Concurrency
The game leverages multiple threads for smooth gameplay:

- **Animation Thread**: Handles the continuous rendering of game elements
- **AutoDown Thread**: Manages the automatic downward movement of pieces
- **Loading Thread**: Handles resource initialization

### Game Architecture

#### 🎮 Controller (`src/controller`)
- `Game.java`: Core game controller implementing `Runnable` interface
  - Manages game threads
  - Handles user input
  - Controls game state
  - Manages sound system

#### 🎨 View (`src/view`)
- `GameFrame.java`: Main window container
- `GameScreen.java`: Rendering engine
  - Double buffering for smooth graphics
  - Event handling for user interactions

#### 📊 Model (`src/model`)
- `GameLogic.java`: Singleton pattern implementation for game state
- Various piece classes (`TPiece.java`, `LPiece.java`, etc.)
- `Grid.java`: Game board management
- `CollisionManager.java`: Collision detection system

### Operating System Concepts Demonstrated

1. **Multi-threading**
   - Concurrent execution of game logic and rendering
   - Thread synchronization for game state management
   - Priority-based thread scheduling

2. **Resource Management**
   - Memory management through efficient grid system
   - Graphics resource handling with double buffering
   - Sound resource management

3. **Event Handling**
   - Keyboard input processing
   - Game state event management
   - Window system integration

## 🎯 Controls

- ⬅️ Left Arrow: Move piece left
- ➡️ Right Arrow: Move piece right
- ⬆️ Up Arrow: Rotate piece
- ⬇️ Down Arrow: Move piece down
- Space: Rotate piece
- P: Pause game
- M: Mute/Unmute
- R: Restart game
- Q: Quit game

## 🛠️ Technical Requirements

- Java Runtime Environment (JRE) 8 or higher
- Minimum 256MB RAM
- Graphics support for Java Swing
- Audio output capability

## 🚀 Getting Started

1. Download the latest release
2. Double-click the executable file
3. Start playing!

## 🎵 Sound Credits

Background music and sound effects included in the game.
## 🫂 Team Robby D.

Clark Rodriguez
James Dela Cruz
Jasper Perillo
---
*Built with ❤️ using Java and Swing*