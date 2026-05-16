package com.niazi.khatasystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.niazi.khatasystem.models.KhataRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper — SQLite Database Manager
 *
 * This class manages all database operations (CRUD) for the Niazi Khata System.
 * It extends SQLiteOpenHelper to create and manage the local SQLite database.
 *
 * Database: niazi_khata.db
 * Table: khata_records
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // ─── Database Configuration ────────────────────────────────────────────────
    private static final String DATABASE_NAME = "niazi_khata.db";
    private static final int DATABASE_VERSION = 1;

    // ─── Table & Column Names ──────────────────────────────────────────────────
    public static final String TABLE_KHATA = "khata_records";

    public static final String COL_ID           = "id";
    public static final String COL_CUSTOMER_NAME = "customer_name";
    public static final String COL_AMOUNT        = "amount";
    public static final String COL_DATE          = "date";
    public static final String COL_STATUS        = "status";
    public static final String COL_NOTES         = "notes";
    public static final String COL_CREATED_AT    = "created_at";

    // ─── SQL Statements ────────────────────────────────────────────────────────

    /** SQL to create the khata_records table */
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_KHATA + " (" +
            COL_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CUSTOMER_NAME + " TEXT NOT NULL, " +
            COL_AMOUNT        + " REAL NOT NULL, " +
            COL_DATE          + " TEXT NOT NULL, " +
            COL_STATUS        + " TEXT NOT NULL DEFAULT 'Unpaid', " +
            COL_NOTES         + " TEXT, " +
            COL_CREATED_AT    + " INTEGER" +
            ")";

    /** SQL to drop the table (used on upgrade) */
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_KHATA;

    // ─── Singleton Instance ────────────────────────────────────────────────────
    private static DatabaseHelper instance;

    /**
     * Get singleton instance of DatabaseHelper
     * Using singleton prevents multiple database connections
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // ─── Constructor ───────────────────────────────────────────────────────────
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ─── SQLiteOpenHelper Callbacks ────────────────────────────────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table when app is first installed
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table and recreate on version upgrade
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    // ─── CREATE — Insert a new record ──────────────────────────────────────────

    /**
     * Insert a new khata record into the database.
     * @param record KhataRecord object to insert
     * @return Row ID of the newly inserted record, or -1 if error
     */
    public long insertRecord(KhataRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_CUSTOMER_NAME, record.getCustomerName().trim());
        values.put(COL_AMOUNT,        record.getAmount());
        values.put(COL_DATE,          record.getDate());
        values.put(COL_STATUS,        record.getStatus());
        values.put(COL_NOTES,         record.getNotes() != null ? record.getNotes().trim() : "");
        values.put(COL_CREATED_AT,    System.currentTimeMillis());

        long rowId = db.insert(TABLE_KHATA, null, values);
        db.close();
        return rowId;
    }

    // ─── READ — Fetch all records ──────────────────────────────────────────────

    /**
     * Retrieve ALL khata records from the database.
     * Records are ordered by creation date (newest first).
     * @return List of all KhataRecord objects
     */
    public List<KhataRecord> getAllRecords() {
        List<KhataRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query all rows, ordered by created_at descending (newest first)
        String query = "SELECT * FROM " + TABLE_KHATA +
                       " ORDER BY " + COL_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        // Iterate through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                KhataRecord record = cursorToRecord(cursor);
                records.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return records;
    }

    // ─── READ — Get single record by ID ───────────────────────────────────────

    /**
     * Retrieve a single khata record by its ID.
     * @param id The record ID to look up
     * @return KhataRecord object, or null if not found
     */
    public KhataRecord getRecordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_KHATA,
                null,                          // All columns
                COL_ID + " = ?",              // WHERE clause
                new String[]{String.valueOf(id)}, // WHERE args
                null, null, null
        );

        KhataRecord record = null;
        if (cursor != null && cursor.moveToFirst()) {
            record = cursorToRecord(cursor);
            cursor.close();
        }

        db.close();
        return record;
    }

    // ─── READ — Search records by customer name ────────────────────────────────

    /**
     * Search khata records by customer name (case-insensitive partial match).
     * @param searchQuery The search term to look for in customer names
     * @return List of matching KhataRecord objects
     */
    public List<KhataRecord> searchByName(String searchQuery) {
        List<KhataRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Use LIKE with wildcards for partial matching
        String query = "SELECT * FROM " + TABLE_KHATA +
                       " WHERE " + COL_CUSTOMER_NAME + " LIKE ?" +
                       " ORDER BY " + COL_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchQuery + "%"});

        if (cursor.moveToFirst()) {
            do {
                KhataRecord record = cursorToRecord(cursor);
                records.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return records;
    }

    // ─── UPDATE — Edit an existing record ─────────────────────────────────────

    /**
     * Update an existing khata record in the database.
     * @param record KhataRecord with updated data (must have valid ID)
     * @return Number of rows affected (1 if success, 0 if not found)
     */
    public int updateRecord(KhataRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_CUSTOMER_NAME, record.getCustomerName().trim());
        values.put(COL_AMOUNT,        record.getAmount());
        values.put(COL_DATE,          record.getDate());
        values.put(COL_STATUS,        record.getStatus());
        values.put(COL_NOTES,         record.getNotes() != null ? record.getNotes().trim() : "");

        int rowsAffected = db.update(
                TABLE_KHATA,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(record.getId())}
        );

        db.close();
        return rowsAffected;
    }

    // ─── UPDATE — Toggle status between Paid/Unpaid ───────────────────────────

    /**
     * Quickly toggle the status of a record between Paid and Unpaid.
     * @param recordId The ID of the record to toggle
     * @param newStatus The new status ("Paid" or "Unpaid")
     * @return Number of rows affected
     */
    public int updateStatus(int recordId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_STATUS, newStatus);

        int rowsAffected = db.update(
                TABLE_KHATA,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(recordId)}
        );

        db.close();
        return rowsAffected;
    }

    // ─── DELETE — Remove a record ──────────────────────────────────────────────

    /**
     * Delete a khata record by its ID.
     * @param recordId The ID of the record to delete
     * @return Number of rows deleted (1 if success, 0 if not found)
     */
    public int deleteRecord(int recordId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_KHATA,
                COL_ID + " = ?",
                new String[]{String.valueOf(recordId)}
        );

        db.close();
        return rowsDeleted;
    }

    // ─── STATS — Summary calculations ─────────────────────────────────────────

    /**
     * Get total number of records in the database.
     */
    public int getTotalRecordCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KHATA, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Get the total paid amount (sum of all Paid records).
     */
    public double getTotalPaidAmount() {
        return getSumByStatus(KhataRecord.STATUS_PAID);
    }

    /**
     * Get the total unpaid/remaining amount (sum of all Unpaid records).
     */
    public double getTotalUnpaidAmount() {
        return getSumByStatus(KhataRecord.STATUS_UNPAID);
    }

    /**
     * Helper: sum the amount for records matching a given status.
     */
    private double getSumByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_KHATA +
                       " WHERE " + COL_STATUS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{status});
        double sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return sum;
    }

    // ─── Helper — Convert Cursor row to KhataRecord ───────────────────────────

    /**
     * Convert a database cursor row to a KhataRecord object.
     * This avoids code duplication when reading from cursor in multiple methods.
     */
    private KhataRecord cursorToRecord(Cursor cursor) {
        KhataRecord record = new KhataRecord();
        record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        record.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)));
        record.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)));
        record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
        record.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
        record.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)));
        record.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
        return record;
    }
}
