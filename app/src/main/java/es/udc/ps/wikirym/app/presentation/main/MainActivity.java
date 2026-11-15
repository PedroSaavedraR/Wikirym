package es.udc.ps.wikirym.app.presentation.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import es.udc.ps.wikirym.app.data.UespAPI;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.app.presentation.BaseActivity;
import es.udc.ps.wikirym.app.presentation.main.adapter.EntryAdapter;
import es.udc.ps.wikirym.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final String TAG = MainActivity.class.getSimpleName();

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

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://en.uesp.net/w/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        UespAPI api = retrofit.create(UespAPI.class);

        String category = "Category:Skyrim-Quests";
        int limit = 50;
        String format = "json";
        int formatVersion = 2;
        Call<List<WikiEntry>> call = api.getCategoryMembers(category, limit, format, formatVersion);

        try {

            // Synchronous call on the main thread: Android throws a NetworkOnMainThreadException.
            // It is solved in the next feature with an asynchronous call.
            return call.execute().body();

        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "Response fails", e);
        }

        return new ArrayList<>();
    }
}
