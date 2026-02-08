import asyncio
import sqlite3
from telegram import Update
from telegram.ext import Application, CommandHandler, ContextTypes
import matplotlib.pyplot as plt
from io import BytesIO

from config import TELEGRAM_BOT_TOKEN, DB_PATH, SEND_PERIOD_SECONDS

def get_latest_weather():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("""
        SELECT city, temperature_c, wind_speed_ms, weather_code, source_time, created_at
        FROM weather_records
        ORDER BY id DESC
        LIMIT 1
    """)
    row = cur.fetchone()
    conn.close()
    return row


def build_weather_plot():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("""
        SELECT COALESCE(created_at, source_time), temperature_c, wind_speed_ms
        FROM weather_records
        ORDER BY id DESC
        LIMIT 50
    """)
    rows = cur.fetchall()
    conn.close()

    rows.reverse()

    times = []
    temps = []
    winds = []

    for t, temp, wind in rows:
        ts = str(t)[:16].replace("T", " ")
        times.append(ts)
        temps.append(temp)
        winds.append(wind)
    
    plt.figure()
    plt.plot(times, temps, color="blue", marker="o", label="Температура (°C)")
    plt.plot(times, winds, color="red", marker="o", label="Скорость ветра (м/с)")

    plt.xlabel("Время")
    plt.ylabel("Значение")
    plt.title("История погоды")
    plt.xticks(rotation=45)
    plt.legend()
    plt.tight_layout()

    img = BytesIO()
    plt.savefig(img, format="png", dpi=300, bbox_inches="tight")
    plt.close()
    img.seek(0) 
    return img

async def cmd_plot(update: Update, context: ContextTypes.DEFAULT_TYPE):
    img = build_weather_plot()
    await update.message.reply_photo(photo=img, caption="График: температура (синий) и ветер (красный)")

def get_all_subscribers():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT chat_id FROM subscribers")
    rows = cur.fetchall()
    conn.close()
    return [r[0] for r in rows]

def add_subscriber(chat_id: int):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("INSERT OR IGNORE INTO subscribers(chat_id) VALUES (?)", (chat_id,))
    conn.commit()
    conn.close()

def remove_subscriber(chat_id: int):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("DELETE FROM subscribers WHERE chat_id = ?", (chat_id,))
    conn.commit()
    conn.close()

def format_weather(row):
    if not row:
        return "В базе пока нет данных о погоде. Запусти Java-сборщик."
    city, temp, wind, code, source_time, created_at = row
    return (
        f"Погода: {city}\n"
        f"Температура: {temp} °C\n"
        f"Ветер: {wind} m/s\n"
        f"Время: {source_time}\n"
    )

async def cmd_start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "Привет!\n"
        "/subscribe — подписаться\n"
        "/unsubscribe — отписаться\n"
        "/now — последняя погода"
    )

async def cmd_subscribe(update: Update, context: ContextTypes.DEFAULT_TYPE):
    add_subscriber(update.effective_chat.id)
    await update.message.reply_text("Ок, ты подписан ✅")

async def cmd_unsubscribe(update: Update, context: ContextTypes.DEFAULT_TYPE):
    remove_subscriber(update.effective_chat.id)
    await update.message.reply_text("Ок, ты отписан ❌")

async def cmd_now(update: Update, context: ContextTypes.DEFAULT_TYPE):
    row = get_latest_weather()
    await update.message.reply_text(format_weather(row))

async def push_loop(app: Application):
    while True:
        try:
            row = get_latest_weather()
            text = format_weather(row)
            for chat_id in get_all_subscribers():
                try:
                    await app.bot.send_message(chat_id=chat_id, text=text)
                except Exception:
                    pass
        except Exception:
            pass
        await asyncio.sleep(SEND_PERIOD_SECONDS)

async def on_startup(app: Application):
    app.create_task(push_loop(app))

def main():
    application = Application.builder().token(TELEGRAM_BOT_TOKEN).post_init(on_startup).build()
    application.add_handler(CommandHandler("start", cmd_start))
    application.add_handler(CommandHandler("subscribe", cmd_subscribe))
    application.add_handler(CommandHandler("unsubscribe", cmd_unsubscribe))
    application.add_handler(CommandHandler("now", cmd_now))
    application.add_handler(CommandHandler("plot", cmd_plot))
    application.run_polling()

if __name__ == "__main__":
    main()

