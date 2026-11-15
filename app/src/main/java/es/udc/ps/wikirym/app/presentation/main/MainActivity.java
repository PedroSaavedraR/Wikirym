package es.udc.ps.wikirym.app.presentation.main;

import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import es.udc.ps.wikirym.app.domain.EntryType;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.app.presentation.BaseActivity;
import es.udc.ps.wikirym.app.presentation.main.adapter.EntryAdapter;
import es.udc.ps.wikirym.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setUpView();
    }

    @Override
    protected ActivityMainBinding inflateBinding(LayoutInflater inflater) {

        return ActivityMainBinding.inflate(inflater);
    }

    private void setUpView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.mainList.setLayoutManager(linearLayoutManager);

        EntryAdapter adapter = new EntryAdapter(getEntries());
        mBinding.mainList.setAdapter(adapter);
    }

    private List<WikiEntry> getEntries() {

        List<WikiEntry> entries = new ArrayList<>();

        entries.add(new WikiEntry(1, "Lydia", EntryType.CHARACTER));
        entries.add(new WikiEntry(2, "Ulfric Stormcloak", EntryType.CHARACTER));
        entries.add(new WikiEntry(3, "Paarthurnax", EntryType.CHARACTER));
        entries.add(new WikiEntry(4, "Unbound", EntryType.QUEST));
        entries.add(new WikiEntry(5, "The Golden Claw", EntryType.QUEST));
        entries.add(new WikiEntry(6, "Dawnbreaker", EntryType.ITEM));
        entries.add(new WikiEntry(7, "Ebony Blade", EntryType.ITEM));
        entries.add(new WikiEntry(8, "Whiterun", EntryType.PLACE));

        return entries;
    }
}
