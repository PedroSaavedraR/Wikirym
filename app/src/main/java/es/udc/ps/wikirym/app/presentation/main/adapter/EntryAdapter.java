package es.udc.ps.wikirym.app.presentation.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import es.udc.ps.wikirym.R;
import es.udc.ps.wikirym.app.domain.EntryType;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.databinding.RowEntryBinding;

public class EntryAdapter extends Adapter<EntryAdapter.EntryHolder> {

    private List<WikiEntry> mItems;

    public EntryAdapter() {

        mItems = new ArrayList<>();
    }

    public void setItems(List<WikiEntry> items) {

        mItems = items;
        notifyDataSetChanged();
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

        holder.bind(mItems.get(position));
    }

    static class EntryHolder extends RecyclerView.ViewHolder {

        private final RowEntryBinding mBinding;

        private EntryHolder(RowEntryBinding binding) {

            super(binding.getRoot());
            mBinding = binding;
        }

        private void bind(WikiEntry entry) {

            mBinding.rowEntryTitle.setText(entry.getDisplayTitle());
            mBinding.rowEntryType.setText(getTypeLabel(entry.getType()));
        }

        @StringRes
        private static int getTypeLabel(EntryType type) {

            switch (type) {
                case CHARACTER:
                    return R.string.entry_type_character;
                case QUEST:
                    return R.string.entry_type_quest;
                case ITEM:
                    return R.string.entry_type_item;
                case PLACE:
                default:
                    return R.string.entry_type_place;
            }
        }
    }

}
