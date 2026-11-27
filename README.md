# ⚽ DOPO Soccer

![Java](https://img.shields.io/badge/Java-21-orange) ![Swing](https://img.shields.io/badge/GUI-Swing-blue) ![dyn4j](https://img.shields.io/badge/physics-dyn4j%205.0.2-green) ![JUnit](https://img.shields.io/badge/tests-JUnit%205-red)

A 2D **big-head soccer** game made with Java Swing. Two players face each other on a side-view field, each one defending a goal. They run, jump, kick and fire their team's **power shot** to score.

Built for the **DOPO** course (Object-Oriented Development) at Escuela Colombiana de Ingeniería Julio Garavito.

**Authors:** Elian Ibarra · Oscar Poveda

---

## 📑 Contents
- [Preview](#-preview)
- [Screenshots](#-screenshots)
- [Features](#-features)
- [Controls](#-controls)
- [Game modes](#-game-modes)
- [Characters and power shots](#-characters-and-power-shots)
- [Progression and saving](#-progression-and-saving)
- [Getting started](#-getting-started)
- [Tests, reports and docs](#-tests-reports-and-docs)
- [Project structure](#-project-structure)
- [Design](#-design)

---

## 🎥 Preview

https://github.com/user-attachments/assets/2ae1aff4-c814-44fc-aa84-138a429ec3b9

---

## 📸 Screenshots

<!--
  To add a screenshot: edit this file on GitHub, delete the "Paste a screenshot here" text of a cell
  and drag the image there (or paste it with Ctrl+V). GitHub uploads it and writes the link for you.
  Keep each image on ONE line, or the table breaks.
-->

| Main menu | Character selection |
|:---:|:---:|
| <i><img width="1278" height="926" alt="image" src="https://github.com/user-attachments/assets/1eefd960-21ba-42f8-a767-7adf49da05ef" /></i> | <i><img width="1278" height="927" alt="image" src="https://github.com/user-attachments/assets/3144ceb9-2271-44d9-ac97-ef375816549d" />
</i> |
| **Arcade match** | **Power shots and effects** |
| <i><img width="1278" height="927" alt="image" src="https://github.com/user-attachments/assets/bf1433a7-9f62-4b9d-8612-acf6fb63f709" /></i> | <i><img width="1278" height="929" alt="image" src="https://github.com/user-attachments/assets/971b9043-92e3-4890-8c7c-f2c95d7e6398" /></i> |
| **Tournament bracket** | **Store** |
| <i><img width="1277" height="929" alt="image" src="https://github.com/user-attachments/assets/45fc22bd-a0f0-4479-bbec-504b28467bae" /></i> | <i><img width="1281" height="928" alt="image" src="https://github.com/user-attachments/assets/6a2c3b76-a1fc-4c43-98e4-1ade672e420b" /></i> |
| **Survival** | **Pause, save and open a game** |
| <i><img width="1276" height="928" alt="image" src="https://github.com/user-attachments/assets/cd141478-d4f7-4f93-b289-e737cb2a58b9" /></i> | <i><img width="1278" height="929" alt="image" src="https://github.com/user-attachments/assets/ba1d2c5e-13d0-4974-8a72-0305d2a635f7" /></i> |

---

## ✨ Features
- **2 modalities:** player vs player on one keyboard, or player vs machine.
- **2 game modes:** Arcade and an 8-team Tournament.
- **3 match types:** by goals, by time (with sudden death) and survival.
- **8 national teams** sharing **4 power shots**, each one with its own effect on the rival.
- **4 AI profiles:** Clumsy, Offensive, Defensive and Expert.
- **Real 2D physics** (collisions, bounces, gravity) with dyn4j.
- **Store:** unlock characters, buy balls and upgrade 4 stats.
- **9 stadiums**, **3 resolutions**, music and sound effects with adjustable volume.
- **Automatic progress saving** and **manual save/open** of a game in progress (`.ds` files).
- **170 unit tests**, plus coverage (JaCoCo), static analysis (PMD) and Javadoc reports.

---

## 🎮 Controls

| Action | Player 1 (left) | Player 2 (right) |
|--------|:---:|:---:|
| Move | `A` / `D` | `←` / `→` |
| Jump | `W` | `↑` |
| Kick | `S` | `↓` |
| Power shot | `Q` | Right `Ctrl` |
| Pause / resume | `Esc` | `Esc` |

---

## 🏟️ Game modes

| Mode | What it is | Modalities | Match types |
|------|------------|------------|-------------|
| **Arcade** | One match between the chosen characters, in the chosen stadium. In survival, a series of matches | PVP · PVM | Goals · Time · Survival (PVM only) |
| **Tournament** | 8 teams in a random bracket: quarterfinals, semifinal and final against the Expert AI. The other matches are simulated, and each of yours is played in a random stadium. The champion wins **+500 points** | PVM only | Goals · Time |

| Match type | Rule |
|------------|------|
| **By goals** | The first to reach the goal target wins |
| **By time** | Most goals when time runs out. A tie goes to **sudden death**: the next goal wins |
| **Survival** | Each player has 3 balls and loses one per goal conceded. You keep your balls from one match to the next, while the machine starts with 3 every match. The series ends when you run out |

**AI profiles:** *Clumsy* (chases the ball only 40 % of the time) · *Offensive* (always attacks) · *Defensive* (guards its goal) · *Expert* (reacts twice as fast and switches between attack and defense).

---

## 🌎 Characters and power shots

| Power shot | Teams | Effect on the rival it hits |
|------------|-------|-----------------------------|
| **Blue Aura**: straight and very fast | South Korea · Spain *(800 pts)* | **Pushed** back toward their own goal |
| **Lightning**: straight, the highest damage | Cameroon · Argentina | **Electrocuted** for 3 s: can't jump, half speed |
| **Tornado**: bounces along the ground | Nigeria · Japan *(1200 pts)* | **Knocked out** for 3 s: thrown into the air |
| **Illusion**: the ball splits into 8, and 7 of them are fake | United States · Colombia | **Confused** for 2.5 s |

- The **power bar** fills in 10 s and gets **+25 %** when you concede a goal. Press the power key with a full bar to light up the **aura**, then touch the ball to fire.
- Kicks and power shots deal **damage**. At 0 life a player is **stunned** for 3 s, then gets full life back.
- The damage scales with the **Kick** stat of the player who shoots.

---

## 💰 Progression and saving
- **Points:** 100 for a win, 25 for a loss. Against the machine only player 1 gets points, and a match ended by hand gives none. The tournament champion gets +500.
- **Store:** unlock characters and balls, and upgrade **Speed, Jump, Kick and Survival** from 1 to 10 (+10 % per level). Each player has their own profile.
- **Progress** (both profiles) is saved **automatically** in `DOPOSoccer.progress` and loaded at start-up.
- **Saved games:** pause a match and press *GUARDAR PARTIDA* to save it as a `.ds` file. Open it later from the home screen with *ABRIR PARTIDA*: it comes back paused, exactly as it was (score, time, bracket or survival series).
- Errors are shown in a message and written to `DOPOSoccer.log`.

---

## 🚀 Getting started

**Requirements:** JDK 21+. The libraries are already in [`lib/`](lib/) (`dyn4j-5.0.2.jar` and the JUnit console launcher).

Run every command from the **project root**, or the game won't find its images and sounds.

```powershell
# Windows (PowerShell): compile and run
javac -encoding UTF-8 -cp "lib/*" -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName
java -cp "bin;lib/*" presentation.DOPOSoccerGUI
```

```bash
# Linux / macOS: compile and run  (in Git Bash, use ";" instead of ":")
javac -encoding UTF-8 -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" presentation.DOPOSoccerGUI
```

**From an IDE:** add both `.jar` files in `lib/` as libraries, mark `src` as the source folder, and run `presentation.DOPOSoccerGUI` with the project root as the working directory. The project can also be imported as Maven (`pom.xml`).

---

## 🧪 Tests, reports and docs

```powershell
# Unit tests (after compiling; on Linux/macOS use ":" instead of ";")
java -jar lib/junit-platform-console-standalone-1.11.4.jar execute -cp "bin;lib/dyn4j-5.0.2.jar" --scan-classpath
```

```bash
# Tests + reports + Javadoc (needs Maven 3.9+ and internet on the first run)
mvn clean verify
```

| Output | Tool | Open |
|--------|------|------|
| Test coverage of `domain` | JaCoCo | `docs/informes/jacoco/index.html` |
| Good practices | PMD | `docs/informes/pmd/pmd.html` |
| Code documentation | Javadoc | `docs/apidocs/index.html` |

---

## 📁 Project structure
```
DOPO-HeadSoccer/
├── src/
│   ├── domain/         Game logic: DOPOSoccer facade, modes, match types, players, physics, AI, shots, effects
│   │   └── enums/      Shared names (Side, Action, MatchEvent, StatType, Team...)
│   ├── presentation/   Swing GUI: DOPOSoccerGUI and the views (home, arcade, match, tournament)
│   └── tests/          JUnit 5 tests of the domain
├── assets/             Images (players, balls, flags, stadiums), font and sounds
├── lib/                dyn4j and JUnit
├── design/             UML model in Astah (DOPOSoccer.asta)
├── docs/               Guides, retrospectives, reports (informes/) and Javadoc (apidocs/)
└── pom.xml             Maven build: tests, JaCoCo, PMD and Javadoc
```

---

## 🧩 Design

### 🎨 UI design (Figma)
<!-- Replace FIGMA_URL with the link to the Figma file (Share → Copy link) -->
[![Figma](https://img.shields.io/badge/Figma-UI%20design-F24E1E?logo=figma&logoColor=white)](FIGMA_URL)

<i>🔗 [Figma Design](https://www.figma.com/design/aYCP8sc0w1dV1e8FlRpfEZ/DOPO-HeadSoccer--Final-?node-id=0-1&p=f)</i>

### 🏗️ Code design

| Pattern | Where |
|---------|-------|
| **Facade** | The GUI only talks to `domain.DOPOSoccer` |
| **Strategy** | `PlayerController` (human or AI), `PowerShot` (each team's shot), `MatchType` (match rules) |
| **Template Method** | `Effect` (what a player can do while affected), `GameMode` (`ArcadeMode`, `TournamentMode`), `AIController` (`decide`) |
| **Factory method** | `PowerShot.createEffect()`: each shot creates its own effect |
| **Composition** | `TournamentMode` ◆── `BracketMatch`, `GameCharacter` ──► `PowerShot` |
| **MVC** | Domain = model, views = view, `DOPOSoccerGUI` = controller |

- **Physics** is delegated to **dyn4j**, and only `PhysicsEngine` uses it. The domain works in pixels and dyn4j in meters.
- **Persistence** uses Java serialization: the `PlayerProfile[]` goes in the progress file, and the whole `DOPOSoccer` goes in a `.ds` file.
- **Errors** are `DOPOSoccerException`s with a message for the user, which the GUI shows in a dialog.
