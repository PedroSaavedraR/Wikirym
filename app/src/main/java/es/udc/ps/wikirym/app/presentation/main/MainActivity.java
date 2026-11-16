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

// Única actividad: aloja los fragmentos y gestiona la action bar
public class MainActivity extends BaseActivity<ActivityMainBinding>
        implements EntryListFragment.OnEntrySelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Tras rotar, el FragmentManager restaura los fragmentos
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

        // En la pila, para que Atrás vuelva a la lista
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

    // Flecha solo en el detalle; en la lista, el nombre de la app
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
