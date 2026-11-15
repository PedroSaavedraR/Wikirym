package es.udc.ps.wikirym.app.presentation.main;

import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
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

    private List<String> getEntries() {

        List<String> entries = new ArrayList<>();

        entries.add("Lydia");
        entries.add("Ulfric Stormcloak");
        entries.add("Paarthurnax");
        entries.add("Unbound");
        entries.add("The Golden Claw");
        entries.add("Dawnbreaker");
        entries.add("Ebony Blade");
        entries.add("Whiterun");

        return entries;
    }
}
