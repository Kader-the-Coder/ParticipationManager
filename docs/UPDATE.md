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
- [X] Implement CRUD operations for students
- [X] Create simple GUI for adding/editing/removing students
- [X] Display student list in GUI

## Phase 2.5: Main Menu - Changelog
- [X] Implement navigation system (e.g. CardLayout or MainFrame.showPanel(name)) for switching between sections
- [X] Centralize shared state (e.g. selected quarter/week) in main controller or frame
- [X] Add dynamic title/header bar that updates with current section name
- [X] Create button to manage settings
- [X] Create button to manage students
- [X] Create button managing quarters
- [X] Ensure code allows for adding more buttons in the future
- [X] Add “Back to Menu” button/action in each subpanel
- [X] Reserve space for future sections (e.g. Reports, Analytics)

## Phase 3.1: Daily Tracking UI - Core
- [X] Create main panel for daily tracking
  - [X] Add table/grid to display students and their daily scores
  - [X] Add columns for Participation, Camera, On Time, Behaviour, Attendance, Daily Total
  - [X] Add buttons to mark each field

## Phase 3.2: Daily Tracking UI - Additional
- [X] Allow for determining the day
- [X] Allow for removing a all data in a given day

## Phase 4: Daily Tracking Filters
- [X] Implement logic to filter out students by grade and subject
- [X] Persist selected filters between sessions

## Phase 5.1: View weekly scores
- [X] Create new button in main panel for viewing scores
- [X] When selecting view scores -> select quarter
- [X] When selecting a quarter -> select week
- [ ] When selecting a week -> display weekly scores of all students
  - [ ] Make scores filterable by grade and subject
  - [ ] Include button to go to a specific day in that week 

## Phase 5.2: Daily Tracking Logic
- [X] Implement saving of daily scores per student (linked to `days`)
- [ ] Implement changeable participation weight
- [ ] Implement changeable camera weight
- [ ] Implement changeable on-time weight
- [ ] Implement changeable behaviour weight
- [ ] Implement changeable attendance weight
- [ ] Implement logic to ensure changed weights only apply to later dates
- [ ] Implement logic to allow custom application of weights for a given date
- [ ] Implement auto-calculation of Daily Total

## Phase 6: Quarters & Filters
- [X] Implement CRUD for quarters (used as filters; start dates are saved)
- [ ] Implement GUI for selecting quarter (week selection derived dynamically from days)
- [ ] Implement dynamic week numbering logic based on quarter start date (programmatic, not stored)

## Phase 7: Retrieval & Editing
- [ ] View scores per week for each student
- [ ] View scores per day for each student
- [ ] Edit saved scores for each criterion
- [ ] Edit notes per criterion
- [ ] Edit reflections per day
- [ ] Ensure recalculations reflect updated weights

## Phase 8: Daily Tracking UI - Non-functional
- [ ] Add a way to write Notes per student
- [ ] Add a way to write Reflections per student
- [ ] Add a way to write Notes per day
- [ ] Add a way to write Reflections per day
- [ ] Implement notes per criterion
- [ ] Implement reflections per day

## Phase 9: Polishing
- [ ] Add validation for user input
- [ ] Add error handling for invalid data
- [ ] Improve GUI layout
- [ ] Improve GUI usability
- [ ] Ensure consistency across GUI elements
- [ ] Add export to CSV option
- [ ] Add import from CSV option
- [ ] Add backup and restore functionality
