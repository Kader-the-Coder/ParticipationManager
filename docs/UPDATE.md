# ParticipationManager - Implementation Steps

## Phase 1: Database Schema
- [X] Create database schema with the following tables:
  - [X] students
  - [X] quarters
  - [X] weeks
  - [X] days
  - [X] daily_scores
  - [X] criteria_weights
  - [X] settings

## Phase 1.5: Settings System
- [x] Implement `settings` table CRUD
- [x] Add methods to save and load settings (e.g. window size, position, last opened week)
- [x] Integrate settings into GUI initialization (window restores last position/size)

## Phase 2: Student Basics
- [ ] Implement CRUD operations for students
- [ ] Create simple GUI for adding/editing/removing students
- [ ] Display student list in GUI

## Phase 3: Quarters & Weeks
- [ ] Implement CRUD for quarters
- [ ] Implement automatic week numbering logic (resets each quarter)
- [ ] GUI for selecting quarter and week

## Phase 4: Daily Tracking
- [ ] Implement saving of daily scores per student
- [ ] Implement changeable criteria weights
- [ ] GUI grid for entering Participation, Camera, On Time, Behaviour, Attendance
- [ ] Auto-calculation of Daily Total
- [ ] Notes per criteria and reflections per day

## Phase 5: Retrieval & Editing
- [ ] View scores per week/day for each student
- [ ] Edit saved scores, notes, and reflections
- [ ] Ensure recalculations reflect updated weights

## Phase 6: Polishing
- [ ] Add validation and error handling
- [ ] Improve GUI layout and usability
- [ ] Add export/import options (CSV or backup)
