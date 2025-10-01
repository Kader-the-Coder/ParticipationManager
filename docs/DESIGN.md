# ParticipationManager - Design Document

## Core Features

### 1. Daily Student Tracking
- Mark each student on a given day for the following criteria:
    - Participation
    - Camera
    - On Time
    - Behaviour
    - Attendance
- Add notes per criterion.
- Add reflections for the day.
- Automatically calculate **Daily Total Score** based on configurable weights.
- Save all information for the specific day.

### 2. Weeks and Quarters
- Each day belongs to a **week**.
- Weeks are automatically numbered relative to the **quarter start date**.
- Each **quarter** defines where week counting starts.
- When a new quarter is created, week numbering resets to 1.
- Calculate **weekly total score** for each student (average of daily totals).

### 3. Retrieval & Editing
- Select a week to view daily scores for all students.
- View weekly totals per student.
- Edit any daily score, notes, or reflections.
- Recalculate totals dynamically when edits are made.

### 4. Weights & Calculation
- Daily score calculated based on configurable weights for each criterion.
- Total score displayed as a percentage out of 100%.

## Database Tables
### students
| Column     | Type    | Notes                     |
|------------|---------|---------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT |
| name       | TEXT    | NOT NULL                  |

### quarters
| Column     | Type    | Notes                     |
|------------|---------|---------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT |
| name       | TEXT    | NOT NULL                  |
| start_date | DATE    | NOT NULL                  |

### weeks
| Column       | Type    | Notes                                  |
|--------------|---------|----------------------------------------|
| id           | INTEGER | PRIMARY KEY AUTOINCREMENT              |
| quarter_id   | INTEGER | FOREIGN KEY → quarters(id)             |
| week_number  | INTEGER | Week number within the quarter         |
| start_date   | DATE    |                                        |

### days
| Column     | Type    | Notes                                  |
|------------|---------|----------------------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT              |
| week_id    | INTEGER | FOREIGN KEY → weeks(id)                |
| date       | DATE    | UNIQUE within a week                   |

### daily_scores
| Column        | Type    | Notes                                  |
|---------------|---------|----------------------------------------|
| id            | INTEGER | PRIMARY KEY AUTOINCREMENT              |
| student_id    | INTEGER | FOREIGN KEY → students(id)             |
| day_id        | INTEGER | FOREIGN KEY → days(id)                 |
| participation | INTEGER | 0–100 (or weightable)                  |
| camera        | INTEGER | 0–100                                  |
| on_time       | INTEGER | 0–100                                  |
| behaviour     | INTEGER | 0–100                                  |
| attendance    | INTEGER | 0–100                                  |
| daily_total   | INTEGER | calculated field, cached for speed     |
| notes         | TEXT    | optional                               |
| reflections   | TEXT    | optional                               |

### criteria_weights
| Column        | Type    | Notes                                  |
|---------------|---------|----------------------------------------|
| id            | INTEGER | PRIMARY KEY AUTOINCREMENT              |
| participation | INTEGER | weight (percentage)                    |
| camera        | INTEGER | weight                                 |
| on_time       | INTEGER | weight                                 |
| behaviour     | INTEGER | weight                                 |
| attendance    | INTEGER | weight                                 |
| effective_date| DATE    | allows changing weights over time      |

### settings
| Column     | Type    | Notes                                  |
|------------|---------|----------------------------------------|
| key        | TEXT    | PRIMARY KEY (e.g. "window_x", "theme") |
| value      | TEXT    | Stored as string, parsed as needed     |
