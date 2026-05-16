# 📒 Niazi Khata System
### Offline Udhaar / Khata Management App for Android

---

## 📱 App Overview

**Niazi Khata System** is a fully offline Android app to manage udhaar (credit/debit) records for friends and customers. No internet required — all data is stored locally on the device using **SQLite database**.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| ➕ Add Record | Add new khata with name, amount, date, status, notes |
| ✏️ Edit Record | Edit any existing record |
| 🗑️ Delete Record | Delete with confirmation dialog |
| 📋 View All | See all records in a clean card list |
| 🔍 Search | Search records by customer name in real-time |
| ✅ Mark Paid/Unpaid | Toggle status with one tap |
| 📊 Dashboard | See Total Records, Paid Amount, Remaining Amount |
| 💾 Offline | Works 100% without internet |

---

## 🏗️ Project Structure

```
NiaziKhataSystem/
├── app/
│   └── src/main/
│       ├── java/com/niazi/khatasystem/
│       │   ├── activities/
│       │   │   ├── MainActivity.java          ← Dashboard + List
│       │   │   ├── AddEditKhataActivity.java  ← Add / Edit Form
│       │   │   └── RecordDetailActivity.java  ← Full Detail View
│       │   ├── adapters/
│       │   │   └── KhataAdapter.java          ← RecyclerView Adapter
│       │   ├── database/
│       │   │   └── DatabaseHelper.java        ← SQLite CRUD Operations
│       │   └── models/
│       │       └── KhataRecord.java           ← Data Model
│       ├── res/
│       │   ├── layout/
│       │   │   ├── activity_main.xml          ← Dashboard Layout
│       │   │   ├── activity_add_edit_khata.xml← Form Layout
│       │   │   ├── activity_record_detail.xml ← Detail Layout
│       │   │   └── item_khata_record.xml      ← Card Item Layout
│       │   ├── drawable/                      ← Buttons, Badges, Backgrounds
│       │   └── values/
│       │       ├── colors.xml                 ← Dark Theme Colors
│       │       ├── strings.xml                ← App Strings
│       │       └── themes.xml                 ← Material Dark Theme
│       └── AndroidManifest.xml
├── build.gradle
└── settings.gradle
```

---

## 🚀 How to Open in Android Studio

### Step 1 — Open the Project
1. Download/extract the `NiaziKhataSystem` folder
2. Open **Android Studio**
3. Click **"Open"** → Navigate to the `NiaziKhataSystem` folder → Click OK
4. Wait for **Gradle sync** to complete (may take 1-2 minutes on first open)

### Step 2 — Sync Gradle
- If prompted, click **"Sync Now"** in the notification bar
- Android Studio will download all required dependencies automatically

### Step 3 — Generate Proper App Icon (Optional)
1. Right-click on `res/` folder → **New → Image Asset**
2. Select **Launcher Icons (Adaptive and Legacy)**
3. Choose your icon image → Click **Next → Finish**

### Step 4 — Run the App
**On Emulator:**
- Click **Run ▶** or press `Shift+F10`
- Select an AVD (create one if needed: Tools → Device Manager)

**On Real Device:**
1. Enable **Developer Options** on your Android phone
2. Enable **USB Debugging**
3. Connect via USB → Select your device → Click **Run ▶**

### Step 5 — Build APK (to install on any phone)
1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`
3. Transfer to phone and install (enable "Install from Unknown Sources")

---

## 🔧 Technical Details

| Property | Value |
|----------|-------|
| Language | Java |
| Min SDK | API 21 (Android 5.0 Lollipop) |
| Target SDK | API 34 (Android 14) |
| Database | SQLite (local, offline) |
| UI Library | Material Components |
| Architecture | Activity-based MVC |
| Storage | SQLite — `niazi_khata.db` |

---

## 🗄️ Database Schema

**Table: `khata_records`**

| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER PK | Auto-incremented unique ID |
| `customer_name` | TEXT | Customer/friend name |
| `amount` | REAL | Udhaar amount in Rs. |
| `date` | TEXT | Date (DD/MM/YYYY format) |
| `status` | TEXT | "Paid" or "Unpaid" |
| `notes` | TEXT | Optional notes |
| `created_at` | INTEGER | Unix timestamp |

---

## 🎨 Design

- **Theme:** Dark professional (#121212 background)
- **Accent:** Orange (#FF6B35) — inspired by traditional ledger/khata books
- **Cards:** Rounded (16dp corners) with subtle elevation
- **Status:** Green badges for Paid, Red badges for Unpaid

---

## 📞 Troubleshooting

**Gradle sync fails:**
→ Go to File → Invalidate Caches → Restart

**App not installing on device:**
→ Check that "Install from Unknown Sources" is enabled in phone settings

**Build errors:**
→ Make sure you have Android SDK 34 installed (SDK Manager → SDK Platforms)

---

*Built for offline daily use — No internet, No cloud, No Firebase — Just pure local storage.*
