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

### 2. Quarters and Weeks
- Each day belongs to a **quarter**.
- **Weeks** are **calculated dynamically** based on the quarter’s start date.
- Week numbering **resets to 1** at the start of each new quarter.
- Users can **customize week length** and optionally set offsets or start days within a quarter.
- Calculate **weekly total score** for each student by averaging daily totals **per dynamically generated week**.

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
| Column     | Type    | Notes                                 |
|------------|---------|---------------------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT             |
| name       | TEXT    | NOT NULL                              |
| grade_id   | INTEGER | FOREIGN KEY → grades(id)              |

### grades
| Column     | Type    | Notes                     |
|------------|---------|---------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT |
| name       | TEXT    | NOT NULL, UNIQUE          |

### subjects
| Column     | Type    | Notes                     |
|------------|---------|---------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT |
| name       | TEXT    | NOT NULL, UNIQUE          |

### student_subject
| Column      | Type                     | Notes                           |
|-------------|--------------------------|---------------------------------|
| student_id  | INTEGER                  | FOREIGN KEY → students(id)      |
| subject_id  | INTEGER                  | FOREIGN KEY → subjects(id)      |
| PRIMARY KEY | (student_id, subject_id) | ensures no duplicate enrollment |

### quarters
| Column     | Type    | Notes                     |
|------------|---------|---------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT |
| name       | TEXT    | NOT NULL                  |
| start_date | DATE    | NOT NULL                  |

### days
| Column | Type    | Notes                             |
|--------|---------|-----------------------------------|
| id     | INTEGER | PRIMARY KEY AUTOINCREMENT         |
| date   | DATE    | Unique; represents a specific day |


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
| Column         | Type    | Notes                             |
|----------------|---------|-----------------------------------|
| id             | INTEGER | PRIMARY KEY AUTOINCREMENT         |
| participation  | INTEGER | weight (percentage)               |
| camera         | INTEGER | weight                            |
| on_time        | INTEGER | weight                            |
| behaviour      | INTEGER | weight                            |
| attendance     | INTEGER | weight                            |
| effective_date | DATE    | allows changing weights over time |

### settings
| Column     | Type    | Notes                                  |
|------------|---------|----------------------------------------|
| key        | TEXT    | PRIMARY KEY (e.g. "window_x", "theme") |
| value      | TEXT    | Stored as string, parsed as needed     |
