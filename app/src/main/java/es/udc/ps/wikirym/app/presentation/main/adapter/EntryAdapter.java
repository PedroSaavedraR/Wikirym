package es.udc.ps.wikirym.app.presentation.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import es.udc.ps.wikirym.databinding.RowEntryBinding;

public class EntryAdapter extends Adapter<EntryAdapter.EntryHolder> {

    private List<String> mItems;

    public EntryAdapter(List<String> items) {

        mItems = items;
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

        holder.mBinding.rowEntryName.setText(mItems.get(position));
    }

    public static class EntryHolder extends RecyclerView.ViewHolder {

        private final RowEntryBinding mBinding;

        public EntryHolder(RowEntryBinding binding) {

            super(binding.getRoot());
            mBinding = binding;
        }
    }

}
