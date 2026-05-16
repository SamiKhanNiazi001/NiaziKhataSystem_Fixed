package com.niazi.khatasystem.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.niazi.khatasystem.R;
import com.niazi.khatasystem.models.KhataRecord;

import java.util.List;

/**
 * KhataAdapter — RecyclerView Adapter
 *
 * This adapter binds KhataRecord data to each card in the RecyclerView list.
 * It also handles click events for each card item.
 */
public class KhataAdapter extends RecyclerView.Adapter<KhataAdapter.KhataViewHolder> {

    // ─── Interface for item click callbacks ────────────────────────────────────
    public interface OnItemClickListener {
        void onItemClick(KhataRecord record);          // Full card click → view detail
        void onEditClick(KhataRecord record);          // Edit button click
        void onDeleteClick(KhataRecord record);        // Delete button click
        void onStatusToggle(KhataRecord record);       // Status badge click → toggle paid/unpaid
    }

    // ─── Adapter fields ────────────────────────────────────────────────────────
    private Context context;
    private List<KhataRecord> recordList;
    private OnItemClickListener listener;

    // ─── Constructor ───────────────────────────────────────────────────────────
    public KhataAdapter(Context context, List<KhataRecord> recordList, OnItemClickListener listener) {
        this.context = context;
        this.recordList = recordList;
        this.listener = listener;
    }

    // ─── RecyclerView.Adapter methods ─────────────────────────────────────────

    @NonNull
    @Override
    public KhataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card layout for each item
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_khata_record, parent, false);
        return new KhataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KhataViewHolder holder, int position) {
        // Get the record at this position
        KhataRecord record = recordList.get(position);

        // Bind data to views
        holder.tvCustomerName.setText(record.getCustomerName());
        holder.tvAmount.setText("Rs. " + String.format("%.0f", record.getAmount()));
        holder.tvDate.setText(record.getDate());

        // Show notes if available, hide if empty
        if (record.getNotes() != null && !record.getNotes().trim().isEmpty()) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText(record.getNotes());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }

        // ── Status badge coloring ──────────────────────────────────────────────
        if (record.isPaid()) {
            // Green badge for Paid
            holder.tvStatus.setText("✓ Paid");
            holder.tvStatus.setBackgroundResource(R.drawable.badge_paid);
            holder.tvStatus.setTextColor(Color.parseColor("#1B5E20"));
            // Amount in green for paid
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            // Red badge for Unpaid
            holder.tvStatus.setText("⚠ Unpaid");
            holder.tvStatus.setBackgroundResource(R.drawable.badge_unpaid);
            holder.tvStatus.setTextColor(Color.parseColor("#B71C1C"));
            // Amount in accent orange for unpaid
            holder.tvAmount.setTextColor(Color.parseColor("#FF6B35"));
        }

        // ── Click Listeners ────────────────────────────────────────────────────

        // Full card click → show detail
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(record);
        });

        // Edit button click
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(record);
        });

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(record);
        });

        // Status badge click → toggle status
        holder.tvStatus.setOnClickListener(v -> {
            if (listener != null) listener.onStatusToggle(record);
        });
    }

    @Override
    public int getItemCount() {
        return recordList != null ? recordList.size() : 0;
    }

    // ─── Update list data (called after search or refresh) ────────────────────

    /**
     * Update the adapter's data and refresh the RecyclerView.
     * @param newList New list of records to display
     */
    public void updateList(List<KhataRecord> newList) {
        this.recordList = newList;
        notifyDataSetChanged();
    }

    // ─── ViewHolder — Holds references to each card's views ───────────────────

    public static class KhataViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCustomerName;
        TextView tvAmount;
        TextView tvDate;
        TextView tvStatus;
        TextView tvNotes;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public KhataViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find all views in the item card layout
            cardView       = itemView.findViewById(R.id.cardView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAmount       = itemView.findViewById(R.id.tvAmount);
            tvDate         = itemView.findViewById(R.id.tvDate);
            tvStatus       = itemView.findViewById(R.id.tvStatus);
            tvNotes        = itemView.findViewById(R.id.tvNotes);
            btnEdit        = itemView.findViewById(R.id.btnEdit);
            btnDelete      = itemView.findViewById(R.id.btnDelete);
        }
    }
}
