# 🎮 Tic-Tac-Toe Ultimate Client

<div align="center">

![JavaFX](https://img.shields.io/badge/JavaFX-UI-4285F4?style=for-the-badge&logo=java&logoColor=white)
![CSS](https://img.shields.io/badge/Style-Neon%20CSS-ff00ff?style=for-the-badge&logo=css3&logoColor=white)
![AI](https://img.shields.io/badge/AI-Minimax%20Algo-success?style=for-the-badge&logo=nvidia&logoColor=white)
![JSON](https://img.shields.io/badge/Protocol-JSON-lightgrey?style=for-the-badge&logo=json&logoColor=white)

<br />

### 🚀 A Modern, Feature-Packed Tic-Tac-Toe Experience
**Unbeatable AI · Real-Time Multiplayer · Cinematic Replays**

</div>

---

## ✨ Overview

The **Tic-Tac-Toe Ultimate Client** is a robust desktop application built with **JavaFX**. It offers a seamless gaming experience ranging from casual local play to competitive online matches.
With a **custom Neon UI**, **responsive animations**, and a **Minimax-powered AI**, it redefines the classic game.

> Built for **performance**, **interactivity**, and **visual appeal**.

---

## 📸 Visual Tour

<table align="center">
  <tr>
    <td align="center" width="50%">
      <img src="screenshots/login.png" alt="Login Screen" width="100%" style="border-radius:10px; box-shadow:0 0 15px rgba(0, 255, 240, 0.3);">
      <br>
      <b>🔐 Secure Authentication</b><br>
      <i>Modern login & registration with validation.</i>
    </td>
    <td align="center" width="50%">
      <img src="screenshots/mode_select.png" alt="Mode Selection" width="100%" style="border-radius:10px; box-shadow:0 0 15px rgba(255, 0, 127, 0.3);">
      <br>
      <b>🎛️ Game Modes</b><br>
      <i>Choose between AI, Local, or Online play.</i>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img src="screenshots/gameboard.png" alt="Neon Gameplay" width="100%" style="border-radius:10px; box-shadow:0 0 15px rgba(0, 255, 240, 0.3);">
      <br>
      <b>✨ Neon Gameplay</b><br>
      <i>Responsive grid with "Breathing" win animations.</i>
    </td>
    <td align="center" width="50%">
      <img src="screenshots/online.png" alt="Online Lobby" width="100%" style="border-radius:10px; box-shadow:0 0 15px rgba(255, 0, 127, 0.3);">
      <br>
      <b>🌐 Online Lobby</b><br>
      <i>Real-time list of players with live status.</i>
    </td>
  </tr>
</table>

---

## 🧠 Exclusive Features

### 🤖 The "Impossible" AI
Challenge our **Minimax Algorithm**. It recursively calculates every possible future move to ensure it *never* loses.
* **Easy:** Makes random mistakes.
* **Medium:** Plays defensively but misses winning opportunities.
* **Impossible:** Perfect play. Good luck!

### 🎥 Cinema Mode (Replays)
Every game you play (Offline or Online) can be recorded!
* **Save:** Games are serialized into JSON and stored locally.
* **Watch:** Enter the Replay Interface to watch past matches move-by-move.
* **Control:** Use **Play**, **Pause**, and **Reset** buttons to analyze your strategy.

### 🌐 Real-Time Multiplayer
* **Live Updates:** The player list updates automatically via server broadcasts.
* **Invitation System:** Send challenges to available players.
* **Smart Handling:** Graceful handling of opponent disconnects and game-over states.

---

## 🏗️ Architecture (MVC Pattern)

The project strictly follows the **Model-View-Controller (MVC)** design pattern for maintainability and scalability.

## 📂 Project Structure
```text
com.mycompany.client_xo_game
│
├── App.java                  # JavaFX Entry Point
├── controllers/              # 🎮 CONTROLLERS (Handle UI Logic)
│   ├── GameboardController.java
│   ├── LoginController.java
│   ├── OnlinePlayersController.java
│   └── ...
├── model/                    # 📦 MODELS (Data Structures)
│   ├── Player.java
│   ├── GameSession.java
│   └── Move.java
├── game_engine/              # ⚙️ LOGIC (The Brains)
│   ├── Board.java            # Grid state & Win checking
│   └── Minimax.java          # AI Algorithm
├── network/                  # 📡 NETWORKING
│   └── NetworkConnection.java # Socket Singleton
├── navigation/               # 🧭 NAVIGATION
│   └── Navigation.java       # Scene Switching Logic
└── resources/                # 🎨 ASSETS
    ├── fxml/                 # View Layouts
    ├── css/                  # Neon Stylesheets
    └── images/               # Icons & Avatars
```

## 🚀 Setup & Execution

### ✅ Prerequisites
Before running the server, ensure you have the following environment set up:
* **Java JDK 17+** installed.
* **Apache Derby** installed and running on port `1527`.
* **Build Tool:** Maven.
* **Required Libraries:**
    * `org.json`
    * `derbyclient.jar`
      
 ### 📥 Installation

**1️⃣ Clone Repository**
```bash
git clone [https://github.com/YourUsername/TicTacToe-Client.git](https://github.com/YourUsername/TicTacToe-Client.git)
```
### 2️⃣  Configure Network
Ensure the NetworkConnection.java points to your server's IP (Default: localhost).

### 3️⃣ Run Application
Navigate to the source package:
`com.mycompany.client_xo_game`

Run the main class:
`App.java` *(JavaFX Entry Point)*

---

## 👥 Contributors

Built by the **Group 1 (MAD Intake 46)**:

* **Ahmed Tayseer** 
* **Alaa Ayman** 
* **Mahmoud Tarek**  
* **Omar Amer** 
