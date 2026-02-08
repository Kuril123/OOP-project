# OOP Weather Project (Java + SQLite + optional Python bot)

## What it does
- Java app fetches current weather from Open-Meteo (no API key).
- Saves results into SQLite database file `weather.db`.
- Runs on a scheduler (default: every 30 minutes).

## Run (Java)
1) Install JDK 11+ and Maven
2) In project folder:
   mvn -q clean package
   mvn -q exec:java

SQLite file will be created in the project root: `weather.db`.

## Change city
Edit: `src/main/java/oop/weather/AppConfig.java`
- CITY, LAT, LON
- PERIOD_SECONDS

## Optional (Python Telegram bot)
Folder: `python_bot/`
- `create_db.py` (creates tables)
- `bot.py` (reads latest weather and sends to subscribers)

Install:
  pip install -r python_bot/requirements.txt

Set token in `python_bot/config.py` then run:
  python python_bot/create_db.py
  python python_bot/bot.py
