package es.udc.ps.wikirym.app.presentation.main;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import es.udc.ps.wikirym.R;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.app.presentation.BaseActivity;
import es.udc.ps.wikirym.app.presentation.detail.EntryDetailFragment;
import es.udc.ps.wikirym.databinding.ActivityMainBinding;

// Single activity: it only hosts the fragments (list and detail) and manages the action bar
public class MainActivity extends BaseActivity<ActivityMainBinding>
        implements EntryListFragment.OnEntrySelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // After a rotation the FragmentManager restores the fragments by itself
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new EntryListFragment())
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateActionBar);
        updateActionBar();
    }

    @Override
    protected ActivityMainBinding inflateBinding(LayoutInflater inflater) {

        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    public void onEntrySelected(WikiEntry entry) {

        // The detail replaces the list and is added to the back stack: Back returns to the list
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.main_container, EntryDetailFragment.newInstance(entry))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {

        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    // Back arrow only in the detail; the detail sets its own title, the list shows the app name
    private void updateActionBar() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean showingDetail = fragmentManager.getBackStackEntryCount() > 0;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(showingDetail);
        }
        if (!showingDetail) {
            setTitle(R.string.app_name);
        }
    }
}
