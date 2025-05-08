# Capstone Project: *The Adventures Of Joe And Marie*

## Table of Contents

1. [Project Overview](#project-overview)
2. [Object-Oriented Programming Principles](#object-oriented-programming-principles)
3. [Java Generics](#java-generics)
4. [Multithreading and Concurrency](#multithreading-and-concurrency)
5. [Graphical User Interface](#graphical-user-interface)
6. [Database Connectivity](#database-connectivity)
7. [Unified Modeling Language (UML)](#unified-modeling-language-uml)
8. [Design Patterns](#design-patterns)
9. [Code Quality and Documentation](#code-quality-and-documentation)
10. [Additional Features](#additional-features)
11. [Setup and Run Instructions](#setup-and-run-instructions)

---

## Project Overview

**Project Title**: *The Adventures of Joe and Marie*
**Description**:
"The Adventures of Joe and Marie” is a fun and challenging platformer game where two characters, Joe and Marie, work together to explore tricky levels. Inspired by Bread and Fred, players are tied together and must jump, swing, and climb as a team. You can play with a friend or control both characters on your own. The game is filled with funny moments, cute art, and puzzles that need teamwork to solve. It’s great for players who enjoy co-op games and fun adventures.

**Members**
- Carl Angelo T. Pepino
- Nathanael Jedd N. Del Castillo
- Mary Avriel Rainne M. Borromeo
- Brent Micheal S. Tolentino
- Ivann James M. Paradero

**Tech Stack**:
- **Java**
- **JavaFX**
- **FXGL**
- JDBC with **MySQL Connector/J**
- JDBC Connection Pool with **HikariCP**

---

## Object-Oriented Programming Principles

* **Encapsulation**: Lorem

* **Inheritance**: Lorem

* **Polymorphism**: Lorem

* **Abstraction**: Lorem

---

## Java Generics

* **Generic Classes/Methods**:
* **Generic Collections**:
* **Purpose**:

---

## Multithreading and Concurrency

Lorem

---
## Graphical User Interface

* **Built With**: JavaFX and FXGL

    * The user interface of the application is built using **JavaFX** for rendering and handling UI components, while **FXGL** manages game-related interactions and entities.

* **Features**:

    * **Intro Scene**: The application starts with a dynamic **intro scene** built with FXGL, where introductory visuals, logos, and game information are displayed before transitioning to the main menu.
    * **Main Menu**: The main menu offers options like starting a new game, loading a saved game, viewing settings, or quitting. The layout and buttons are created with **FXML** for modular and flexible UI design.
    * **FXGL HUD (Heads-Up Display)**: A real-time **HUD** is displayed during gameplay, showing the score, health, time remaining, and other in-game stats. This is implemented using **FXGL’s built-in HUD support**, ensuring seamless display updates during game events.

* **Event Handling**:

    * **Game Control Events**: Player actions such as movement, jumping, and attacking are handled using **FXGL’s input system**, which listens for key presses and triggers the corresponding actions in the game logic.

* **User Experience Design**:

    * **Layout**: The game’s layout uses  **JavaFX layout panes** like **AnchorPane** and **VBox**, ensuring elements scale and reposition appropriately for various resolutions.
    * **FXML**: The UI design is structured using **FXML** files, making it easier to separate the UI logic from game logic. This allows for better maintainability and customization of the interface.
    * **Accessibility**: The game includes mouse navigation and proper accessibility practices, allowing players to navigate the interface efficiently using just the mouse.

---

## Database Connectivity

* **Database Used**: MySQL

    * The application uses **MySQL** and **XAMPP** as the relational database to store game-related data, including player progress and settings preferences.

* **Integration**:

    * The database is integrated using the MySQL Connector/J, which enables the application to interact with MySQL through JDBC (Java Database Connectivity).
    * The application utilizes a HikariCP connection pool to efficiently manage database connections, ensuring they are reused for optimal performance during CRUD operations.

* **CRUD Operations**:

    * **GameProgressDao**: This class implements all **CRUD operations** (Create, Read, Update, Delete) for managing game progress, such as saving and retrieving player scores, levels, and other game-specific data.
    * **SettingPreferenceDao**: Handles CRUD operations for managing user settings, including preferences like sound volume, difficulty level, and display settings.
    * **SaveProgress**: This class specifically handles **Insert** and **Delete** operations. It is used to save new game progress to the database and delete old progress records when necessary.

---

## Unified Modeling Language (UML)

* **Diagrams Submitted**: Class Diagram
* **Tools Used**: [draw.io](https://draw.io/ "draw.io")

---

## Design Patterns
### Creational Design Pattern
#### - Singleton Design Pattern
- **GameWorld Management**
  The **GameWorld** in FXGL is a central component that manages all the entities and their components. It is essentially a container where entities are added, removed, and updated during the game's lifecycle.
  When using `getSingleton()` in FXGL, it interacts with the GameWorld's underlying management system, which ensures that only a single instance of an entity (or component) is created and exists at a time.

- **Singleton Connection Pool**
  The project only uses one single connection pool with the help of **HikariCP** connection pool library. This ensures that the application can manage database connections efficiently, optimize resource usage, and improve performance, all while maintaining stability and scalability.

- **Singleton ViewModels**
  The application utilizes the Singleton design pattern to ensure that core classes such as **ViewModels** are instantiated only once. This guarantees a single, consistent state of the model throughout the application's lifecycle, promoting centralized state management and avoiding redundant object creation.

#### - Builder Design Pattern
- **FXGL Entity Builder API**
  The application leverages the Builder design pattern through FXGL’s Entities.builder() API to construct game entities in a readable, step-by-step manner. This pattern enhances code clarity and maintainability by allowing fluent configuration of entity properties such as type, position, components, and visuals before finalizing the build.

#### - Factory Design Pattern
- **Entity Factory**
  The application employs the Factory design pattern in conjunction with the Builder pattern to encapsulate the creation logic of various game entities. Dedicated factory methods are used to instantiate players, platforms, and other game elements, promoting separation of concerns and improving scalability by decoupling entity creation from the main game logic.

### Structural Design Pattern
#### - Composite Design Pattern
- The application reflects the Composite design pattern through **FXGL’s entity-component architecture**, where each game entity is composed of multiple modular components (e.g., physics, control, visual). This allows individual components to be treated uniformly and managed as part of a larger whole, promoting flexibility and reusability in defining complex entity behaviors.

#### - Facade Design Pattern
- The Facade design pattern is evident in FXGL’s API design, which exposes a simplified and unified interface **(FXGL class)** to interact with various subsystems such as game world, input, audio, and UI. This abstraction hides the complexity of underlying systems and allows developers to perform high-level tasks through concise, readable calls, improving productivity and reducing boilerplate.

### Behavioral Design Pattern
#### - Observer Design Pattern
- **Key Input Handler**
  FXGL’s input system uses the Observer pattern to listen for key events. Actions are registered as listeners and triggered when the corresponding input is detected.

- **ViewModel Observer**
  In cases where UI updates depend on changes in game or player state, observers are notified automatically to reflect the latest data in the view, promoting loose coupling.

#### - State Design Pattern
- **Entity State**
  Player entities maintain internal state (like IDLE, JUMP, FALL, etc.) which changes based on interactions. This is a classic use of the State pattern, enabling dynamic behavior changes at runtime without modifying the entity’s structure.
---

## Code Quality and Documentation

* **Modular Code**: \[Describe class/module structure and separation of concerns]
* **Naming Conventions**: \[State that standard Java conventions were followed]

---

## Additional Features

The application encountered a RopJoint bug that led to an ArrayIndexOutOfBoundsException. Our team reported the issue on the official FXGL GitHub repository. Thanks to the swift support of Almas Baim, the author of FXGL, the root cause of the problem was identified and a solution was provided. This collaboration allowed our team to contribute a fix to the FXGL library, improving its functionality for all users.

[#1414 RopeJoint issue request](https://github.com/AlmasB/FXGL/issues/1414#issuecomment-2837844306 "RopeJoint issue request")

---

© 2025 HardStack. All rights reserved.

