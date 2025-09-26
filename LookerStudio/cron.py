from pymongo import MongoClient
import gspread
from google.oauth2.service_account import Credentials

# MongoDB connection
MONGO_URI = "MONGO_LINK"
DB_NAME = "SearchEngg"
COLLECTION = "SearchEngg"

client = MongoClient(MONGO_URI)
db = client[DB_NAME]
logs = list(db[COLLECTION].find())

# Google Sheets connection
SCOPES = ["https://www.googleapis.com/auth/spreadsheets"]
CREDS = Credentials.from_service_account_file("service_account.json", scopes=SCOPES)
gc = gspread.authorize(CREDS)

# Open sheet by name
SHEET_NAME = "SearchLogsExport"
sh = gc.open(SHEET_NAME).sheet1

# Clear old data
sh.clear()

# Write header
header = ["query_text", "timestamp", "latency_ms", "num_results", "clicked_doc_id", "user_id"]
sh.append_row(header)

# Write logs
for log in logs:
    row = [
        log.get("query_text", ""),
        log.get("timestamp", ""),
        log.get("latency_ms", ""),
        log.get("num_results", ""),
        log.get("clicked_doc_id", ""),
        log.get("user_id", "")
    ]
    sh.append_row(row)

print(f"Exported {len(logs)} rows to Google Sheets!")
