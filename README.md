# JavaFX Geometric Drawing Application

A desktop application built with JavaFX that allows users to draw geometric shapes on a canvas, persist drawings to a MySQL database, and log user actions through interchangeable logging strategies. The architecture is designed around established object-oriented design patterns to ensure modularity, extensibility, and maintainability.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Design Patterns](#design-patterns)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Technologies](#technologies)

---

## Overview

This project was developed as part of the MASI module (Modelisation et Architecture des Systemes d'Information) at IIT. The goal is to demonstrate the practical application of design patterns in a real JavaFX desktop application.

---

## Features

- Draw rectangles, circles, and lines on an interactive canvas using mouse drag gestures
- Select the active shape type from a toolbar palette
- Save the current drawing to a MySQL database with a custom name
- Load any previously saved drawing from the database
- Clear all shapes from the canvas
- Switch logging strategy at runtime between three modes: console, file, and database
- View file log content inline after each draw action when file logging is active
- Clear the database log table via a dedicated button

---

## Design Patterns

### Strategy — Logging System

The logging system is built around the Strategy pattern. The `Logger` interface defines a single `log(String message)` method. Three concrete implementations are provided:

- `ConsoleLogger` — writes to standard output
- `FileLogger` — appends to `app.log` in the working directory
- `DBLogger` — inserts records into the `logs` table in MySQL

The `LoggerContext` class holds a reference to the active strategy and delegates all log calls to it. The strategy can be swapped at runtime without any change to the calling code.

### Strategy — Shape Representation

The `ShapeModel` interface abstracts the representation of geometric shapes through a single `getShape()` method returning a JavaFX `Shape` object. Concrete implementations include `RectangleShape`, `CircleShape`, and `LineShape`. The `Drawing` class operates on a list of `ShapeModel` instances, remaining completely decoupled from the specific shape types.

### Factory — Shape Creation

`ShapeFactory` centralizes the instantiation logic for all shape types. Given a `ShapeType` enum value and coordinate parameters, it returns the appropriate `ShapeModel` implementation. This isolates construction logic from the controller and makes adding new shape types straightforward.

### Singleton (Partial) — Database Utility

`DBUtil` provides a single static entry point for obtaining JDBC connections. It encapsulates the connection URL, credentials, and driver initialization, ensuring that database configuration is managed in one place across the entire application.

---

## Project Structure

```
src/
  main/
    java/
      app/
        HelloFX.java                  # Application entry point
      controller/
        DrawingController.java        # Main UI controller and event handling
      factory/
        ShapeFactory.java             # Factory for shape instantiation
      logger/
        Logger.java                   # Logger interface (Strategy)
        LoggerContext.java            # Strategy context
        ConsoleLogger.java
        FileLogger.java
        DBLogger.java
      model/
        ShapeModel.java               # Shape interface (Strategy)
        Drawing.java                  # Drawing context, holds list of shapes
        ShapeType.java                # Enum for shape types
        RectangleShape.java
        CircleShape.java
        LineShape.java
        DrawingDAO.java               # Data access object for drawings
        DAO.java
      util/
        DBUtil.java                   # JDBC connection utility
```

---

## Prerequisites

- Java 21 (OpenJDK 21 or equivalent)
- Maven 3.8+
- MySQL 8.0+
- IntelliJ IDEA or any IDE with Maven support

---

## Database Setup

Create the database and required tables before running the application:

```sql
CREATE DATABASE IF NOT EXISTS drawingapp;
USE drawingapp;

CREATE TABLE IF NOT EXISTS drawings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS shapes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    drawing_id INT,
    type VARCHAR(50),
    x1 DOUBLE,
    y1 DOUBLE,
    x2 DOUBLE,
    y2 DOUBLE,
    FOREIGN KEY (drawing_id) REFERENCES drawings(id)
);
```

The `logs` table is created automatically on first launch by `DBUtil`.

If your MySQL credentials differ from the defaults, update the following constants in `src/main/java/util/DBUtil.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/drawingapp";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

---

## Running the Application

Clone the repository and run via Maven:

```bash
git clone https://github.com/your-username/drawingapp-modelisation.git
cd drawingapp-modelisation
mvn javafx:run
```

Alternatively, open the project in IntelliJ IDEA, let Maven resolve dependencies, and run `mvn javafx:run` from the built-in terminal.

---

## Technologies

| Technology | Version |
|---|---|
| Java | 21 |
| JavaFX | 21.0.6 |
| MySQL Connector/J | 9.7.0 |
| Maven | 3.8+ |
| MySQL | 8.0+ |
