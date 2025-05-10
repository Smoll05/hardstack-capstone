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

Absolutely! Here's a polished and more professional version of your content, suitable for inclusion in a `README.md` file:

---

### Object-Oriented Programming (OOP) Principles in Our Codebase

Our project makes extensive use of core Object-Oriented Programming (OOP) principles to ensure maintainability, reusability, and clarity. Here's how each principle is applied:

#### **Encapsulation**

We **encapsulate** data by keeping most classes and fields private, exposing only necessary access through public getters and setters. This prevents unintended modifications from outside the class and keeps the internal workings hidden. For example, our **Model** classes act as data entities for the database and ViewModel state holders. They contain private fields accessed through well-defined getter and setter methods, ensuring controlled interaction.

#### **Inheritance**

We leverage **inheritance** to promote code reuse and establish a clear class hierarchy. Many of our classes extend base FXGL classes like `GameApplication`. Additionally, our `PlayerComponent` class serves as a superclass for both `PlayerComponent1` and `PlayerComponent2`, allowing shared logic while enabling player-specific behaviors.

#### **Polymorphism**

**Polymorphism** enables us to interact with different object types through a common interface or superclass, allowing flexible and scalable code. In our game, we treat game objects like players and platforms as instances of FXGL’s Entity class. This allows us to manage them generically without needing to know their specific types at every point.

#### **Abstraction**

**Abstraction** enables us to hide complex implementation details and expose only the relevant functionalities. We achieve this through abstract classes and interfaces. For example, the `PlayerComponent` abstract class defines a common contract for its subclasses (`PlayerComponent1` and `PlayerComponent2`). The ViewModel class is also an abstract class that has a GameProgressViewModel and SettingPreferenceViewModel. Interfaces are also used throughout the codebase to define consistent behavior while allowing multiple implementations.

---

## Java Generics

#### **Generic Classes and Methods**

Generics allow us to write flexible, reusable code by using type parameters. This helps reduce duplication and improves type safety at compile time.

In our project, we use generics extensively in our `ViewModel` implementations. For example, we define a base `ViewModel<T>` class that can work with different data types. This allows us to create specialized versions like `GameProgressViewModel` and `SettingPreferenceViewModel`, while still sharing common logic through the generic base.

We also use generic observers that can subscribe to any type of `ViewModel`, enabling a scalable and type-safe way to listen for changes across different parts of the application.

#### **Generic Collections**

We make use of Java’s built-in generic collections such as `ArrayList` and `HashMap` throughout the codebase. These collections allow us to store and manage objects of any type while preserving type safety. Using these generic collections helps ensure code flexibility and reduces the need for manual type casting, making our code cleaner and less error-prone.

---

Here’s a polished and more professional version of your **Multithreading and Concurrency** section, suitable for your `README.md` or project documentation:

---

## **Multithreading and Concurrency**

To maintain a smooth gameplay experience and prevent blocking the **JavaFX** and **FXGL** game loop, we offload long-running operations—such as database access—to separate threads. This ensures that the UI and game logic remain responsive.

For example, our game includes checkpoints throughout the map. When a player enters a checkpoint, a new thread is created to handle the associated database operations (such as saving progress). By running these tasks in the background, we avoid any noticeable lag or frame drops that could disrupt gameplay.

This use of concurrency helps us maintain real-time performance while still performing essential background tasks like saving data.

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
    * **SaveProgressDao**: This class specifically handles **Insert** and **Delete** operations. It is used to save new game progress to the database and delete old progress records when necessary.

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

Certainly! Here's a clearer and more polished version of your **Code Quality and Documentation** section, formatted in a more professional and readable way for a `README.md` or project documentation:

---

## **Code Quality and Documentation**

Our project follows a well-organized directory structure to maintain clarity, modularity, and ease of maintenance. Below is an overview of the resource and source code structure:

### **Project Directory Structure**

```
project-root/
├── src/                                		 # Source code directory
│   ├── assets/                      		# Game assets
│   │   ├── textures/             		 # Image files (.png, .jpg)
│   │   ├── sounds/              		 # Sound effects (.wav)
│   │   ├── music/                		  # Background music (.mp3)
│   │   ├── text/                    		 # Text files (.txt)
│   │   └── ui/
│   │       ├── css/                 		 # UI stylesheets
│   │       └── fonts/             		  # Fonts (.ttf, .otf)
│   └── com/yourpackage/           # Application source packages
│       ├── component/            	 # Player components (e.g., movement, swing logic)
│       ├── config/               		   # Constants (e.g., swing speed, jump force, rope length)
│       ├── controller/           		 # JavaFX controllers for UI screens
│       ├── data/                 			# DAOs, models, events, ViewModels, and database integration
│       ├── entity/               			# Entity type definitions and enums
│       ├── factory/              			# Entity factories for spawning game objects
│       ├── threading/            		 # Runnables and threading utilities
│       ├── ui/                   			   # UI layout and view components
│       └── utils/                	   	   # Utility classes (e.g., file choosers, serialization helpers)
```

This structure promotes separation of concerns, allowing different areas of the codebase (game logic, UI, data handling, etc.) to remain decoupled and easier to navigate. Each package serves a specific purpose, contributing to the overall maintainability and scalability of the project.

---


## Additional Features
### **Collaboration and Contribution to FXGL**

During development, our application encountered a critical issue involving the `RopeJoint` feature, which triggered an `ArrayIndexOutOfBoundsException` unnaturally. After investigating the problem, our team submitted a detailed bug report to the official FXGL GitHub repository.

Thanks to the prompt and helpful response from **Almas Baim**, the creator of FXGL, the root cause was quickly identified. Through this collaboration, we were able to contribute a fix to the FXGL codebase, enhancing its stability and functionality for all developers using the library.

Our contribution is documented in the following issue thread:
[**#1414 RopeJoint issue request**](https://github.com/AlmasB/FXGL/issues/1414#issuecomment-2837844306 "RopeJoint issue request")

---

### **ViewModel**

We implemented custom `ViewModel` classes using the **Observer Design Pattern**. This design allows the UI to automatically respond to changes in the underlying data models by observing updates from the `ViewModel`. As a result, the UI always reflects the latest state without requiring manual synchronization, improving both responsiveness and maintainability.

---
Certainly! Here's a more polished and professional version of your **MVVM Architecture** section:

---

### **MVVM Architecture**

Our program follows the **Model-View-ViewModel (MVVM)** architectural pattern to ensure a clean separation of concerns and improve code organization. This architecture divides the application into three core components:

* **Model**: Represents the application's data and business logic. It includes data classes, repositories, and database operations.
* **View**: Handles the user interface (UI), such as JavaFX screens, layouts, and user interaction.
* **ViewModel**: Acts as an intermediary between the Model and the View. It manages UI-related data in a lifecycle-aware way and exposes observable state to the View.


### **Database Connection Pooling with HikariCP**

We use **HikariCP**, a high-performance JDBC connection pool, to manage our database connections efficiently. A single, shared connection pool is initialized and reused throughout the application. This approach improves performance by:

* Minimizing the overhead of repeatedly opening and closing database connections.
* Caching and reusing `PreparedStatement` objects for faster execution.
* Reducing latency during high-load operations, such as saving game checkpoints or player progress.

This setup ensures robust, efficient, and scalable database access in both UI and background threads.

---

© 2025 HardStack. All rights reserved.

