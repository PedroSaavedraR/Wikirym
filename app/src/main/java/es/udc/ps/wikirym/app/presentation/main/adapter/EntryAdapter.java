package es.udc.ps.wikirym.app.presentation.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.app.presentation.EntryTypeLabels;
import es.udc.ps.wikirym.databinding.RowEntryBinding;

public class EntryAdapter extends Adapter<EntryAdapter.EntryHolder> {

    public interface OnEntryClickListener {

        void onEntryClick(WikiEntry entry);
    }

    private List<WikiEntry> mItems;

    private final OnEntryClickListener mListener;

    public EntryAdapter(OnEntryClickListener listener) {

        mItems = new ArrayList<>();
        mListener = listener;
    }

    public void setItems(List<WikiEntry> items) {

        mItems = items;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {

        return mItems.isEmpty();
    }

    @NonNull
    @Override
    public EntryAdapter.EntryHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {

        RowEntryBinding binding = RowEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EntryHolder(binding);
    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull EntryAdapter.EntryHolder holder,
                                 int position) {

        WikiEntry entry = mItems.get(position);
        holder.bind(entry);
        holder.itemView.setOnClickListener(v -> mListener.onEntryClick(entry));
    }

    static class EntryHolder extends RecyclerView.ViewHolder {

        private final RowEntryBinding mBinding;

        private EntryHolder(RowEntryBinding binding) {

            super(binding.getRoot());
            mBinding = binding;
        }

        private void bind(WikiEntry entry) {

            mBinding.rowEntryTitle.setText(entry.getDisplayTitle());
            mBinding.rowEntryType.setText(EntryTypeLabels.getLabel(entry.getType()));
        }
    }

}
