package com.niazi.khatasystem.models;

/**
 * KhataRecord Model Class
 * Represents a single udhaar/khata entry in the system.
 * This is a simple POJO (Plain Old Java Object) used to hold record data.
 */
public class KhataRecord {

    // Database column names as constants (used in DatabaseHelper)
    public static final String STATUS_PAID = "Paid";
    public static final String STATUS_UNPAID = "Unpaid";

    // Record fields
    private int id;             // Unique auto-incremented ID from SQLite
    private String customerName; // Name of the customer/friend
    private double amount;       // Amount of udhaar/khata
    private String date;         // Date of the record (stored as String)
    private String status;       // Paid or Unpaid
    private String notes;        // Optional notes about the record
    private long createdAt;      // Timestamp when record was created

    // ─── Constructors ──────────────────────────────────────────────────────────

    /**
     * Empty constructor — needed for creating empty objects
     */
    public KhataRecord() {}

    /**
     * Full constructor — used when fetching records from database
     */
    public KhataRecord(int id, String customerName, double amount,
                       String date, String status, String notes, long createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    /**
     * Constructor without ID — used when inserting new records
     */
    public KhataRecord(String customerName, double amount,
                       String date, String status, String notes) {
        this.customerName = customerName;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.createdAt = System.currentTimeMillis();
    }

    // ─── Getters and Setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * Helper method to check if this record is paid
     */
    public boolean isPaid() {
        return STATUS_PAID.equals(this.status);
    }

    @Override
    public String toString() {
        return "KhataRecord{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
