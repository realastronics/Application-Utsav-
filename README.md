# Utsav

> Connect event hosts with the managers who bring their vision to life.

Utsav is an Android application that bridges the gap between people planning events (like weddings, corporate functions, birthdays, conferences), and the professional event managers equipped to execute them. Hosts browse curated manager profiles, filter by location and budget, and coordinate directly through in-app chat. Managers build a portfolio, define their services, and manage incoming event requests from a dedicated dashboard.

----

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Branch Strategy](#branch-strategy)
- [Team](#team)
- [Roadmap](#roadmap)

---

## Overview

| | |
|---|---|
| **Platform** | Android (API 24+) |
| **Language** | Java |
| **IDE** | Android Studio |
| **Status** | In development — Phase 2 |

---

## Features

### Host (User) side
- Browse and filter event managers by location, rating, and event type
- View detailed manager profiles with portfolio images and service breakdown
- Save / bookmark managers for later
- Initiate and manage event requests
- In-app chat with managers

### Manager side
- Create and manage a public profile with portfolio media
- Define service offerings, event types, and availability
- Accept or reject incoming event requests
- Negotiate pricing within the chat window
- View basic performance insights *(premium feature)*

---

## Tech Stack

```
Language        Java
UI              XML layouts — ConstraintLayout, RecyclerView
Navigation      Activity + Fragment stack, BottomNavigationView
Data            Local (DataProvider) → Firebase (planned)
Chat            UI-complete, real-time backend planned via Firebase
Auth            Local flow now, Firebase Auth planned
```

---

## Project Structure

```
app/src/main/
│
├── java/.../utsav/
│   ├── activities/        # One Activity per screen
│   ├── fragments/         # Tab and sub-screen fragments
│   ├── adapters/          # RecyclerView adapters
│   ├── models/            # Shared data models — write these first
│   └── utils/             # DataProvider, Constants
│
└── res/
    ├── layout/            # One XML per Activity, Fragment, or list item
    ├── drawable/          # Icons, backgrounds, shape drawables
    ├── menu/              # Bottom navigation menu
    └── values/            # colors.xml, strings.xml, themes.xml
```

### Key models (shared across both flows)

| File | Purpose |
|---|---|
| `Manager.java` | Manager profile data |
| `Event.java` | Event request details |
| `ChatMessage.java` | Individual chat message |
| `User.java` | Host profile data |

> **Rule:** `models/` and `utils/` are shared contracts. Neither team modifies them without a group discussion first.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11+
- Android SDK API 24+

### Setup

```bash
# Clone the repository
git clone https://github.com/your-org/utsav.git

# Open in Android Studio
# File → Open → select the cloned folder

# Let Gradle sync, then run on emulator or device
```

No API keys or external services are required to run the current build. All data is served locally via `DataProvider.java`.

---

## Branch Strategy

```
main                    ← stable, reviewed code only
 └── develop            ← integration branch — both teams merge here first
      ├── feature/user-flow/mitali & parth      
      └── feature/manager-flow/Farhan & Mehak
```

**Rules**
- No direct pushes to `main`
- All work goes to your feature branch → PR into `develop` → reviewed → merged to `main`
- Commit format: `[area] Short description` — e.g. `[user] Add ManagerListActivity` or `[manager] Wire RequestsFragment adapter`

---

## Team

| Name | Role |
|---|---|
| Mitali | User flow |
| Parth | User flow |
| Farhan | Manager flow |
| Mehak | Manager flow |

---

## Roadmap

- [x] Phase 0 — Idea, scope, and feature definition
- [x] Phase 1 — Figma UI design and full prototype
- [ ] Phase 2 — Android implementation (current)
- [ ] Phase 3 — Firebase Auth + Firestore integration
- [ ] Phase 4 — Real-time chat via Firebase
- [ ] Phase 5 — Location-based manager sorting
- [ ] Phase 6 — Premium stats dashboard + monetisation layer

---

*Built as part of a Mobile Application Development course. Designed to be commercially viable and Play Store-ready.*
