package es.udc.ps.wikirym.app.presentation.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import es.udc.ps.wikirym.BuildConfig;
import es.udc.ps.wikirym.R;
import es.udc.ps.wikirym.app.data.UespAPI;
import es.udc.ps.wikirym.app.data.interceptor.UespApiInterceptor;
import es.udc.ps.wikirym.app.domain.CategoryMembersResponse;
import es.udc.ps.wikirym.app.domain.EntryType;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.app.presentation.BaseActivity;
import es.udc.ps.wikirym.app.presentation.main.adapter.EntryAdapter;
import es.udc.ps.wikirym.databinding.ActivityMainBinding;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EntryAdapter mAdapter;

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

        mAdapter = new EntryAdapter();
        mBinding.mainList.setAdapter(mAdapter);

        getEntries();
    }

    private void getEntries() {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        List<Interceptor> interceptors = okHttpClient.interceptors();
        // Added before the logging interceptor so that the log shows the final request
        interceptors.add(new UespApiInterceptor());
        interceptors.add(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://en.uesp.net/w/").client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        UespAPI api = retrofit.create(UespAPI.class);

        EntryType type = EntryType.CHARACTER;
        String category = "Category:Skyrim-NPCs";
        int limit = 50;
        Call<CategoryMembersResponse> call = api.getCategoryMembers(category, limit);

        call.enqueue(new Callback<CategoryMembersResponse>() {

            @Override
            public void onResponse(@NonNull Call<CategoryMembersResponse> call,
                                   @NonNull Response<CategoryMembersResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getQuery() != null) {

                    Log.i(TAG, "Response OK: " + response.code());

                    // The type is not in the JSON: every entry belongs to the requested category
                    List<WikiEntry> entries = response.body().getQuery().getCategoryMembers();
                    for (WikiEntry entry : entries) {
                        entry.setType(type);
                    }
                    mAdapter.setItems(entries);

                } else {

                    Log.e(TAG, "Response fails: " + response.code());
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryMembersResponse> call,
                                  @NonNull Throwable t) {

                Log.e(TAG, "Response fails: " + t.getMessage());
                Toast.makeText(MainActivity.this, R.string.error_general, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
