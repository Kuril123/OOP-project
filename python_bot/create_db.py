import sqlite3
from pathlib import Path
from config import DB_PATH

def main():
    Path(DB_PATH).touch(exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()

    cur.execute("""
    CREATE TABLE IF NOT EXISTS weather_records (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        city TEXT NOT NULL,
        temperature_c REAL,
        wind_speed_ms REAL,
        weather_code INTEGER,
        source_time TEXT,
        created_at TEXT DEFAULT (datetime('now'))
    );
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS subscribers (
        chat_id INTEGER PRIMARY KEY
    );
    """)

    conn.commit()
    conn.close()
    print("DB initialized:", DB_PATH)

if __name__ == "__main__":
    main()
