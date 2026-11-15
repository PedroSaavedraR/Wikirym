package es.udc.ps.wikirym.app.presentation.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import es.udc.ps.wikirym.BuildConfig;
import es.udc.ps.wikirym.R;
import es.udc.ps.wikirym.app.data.UespAPI;
import es.udc.ps.wikirym.app.data.UespCategories;
import es.udc.ps.wikirym.app.data.interceptor.UespApiInterceptor;
import es.udc.ps.wikirym.app.domain.CategoryMembersResponse;
import es.udc.ps.wikirym.app.domain.EntryType;
import es.udc.ps.wikirym.app.domain.SearchResponse;
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

    private static final int LIMIT = 50;
    // Waiting time after the last key before searching, so that not every letter launches a request
    private static final long SEARCH_DELAY_MS = 400;

    private EntryAdapter mAdapter;
    private UespAPI mApi;
    private Call<?> mCurrentCall;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mSearchRunnable = this::loadEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setUpApi();
        setUpView();
        loadEntries();
    }

    @Override
    protected ActivityMainBinding inflateBinding(LayoutInflater inflater) {

        return ActivityMainBinding.inflate(inflater);
    }

    private void setUpApi() {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        List<Interceptor> interceptors = okHttpClient.interceptors();
        // Added before the logging interceptor so that the log shows the final request
        interceptors.add(new UespApiInterceptor());
        interceptors.add(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://en.uesp.net/w/").client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        mApi = retrofit.create(UespAPI.class);
    }

    private void setUpView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.mainList.setLayoutManager(linearLayoutManager);

        mAdapter = new EntryAdapter();
        mBinding.mainList.setAdapter(mAdapter);

        mBinding.mainSearchButton.setOnClickListener(v -> onSearch());
        mBinding.mainSearchText.setOnEditorActionListener((v, actionId, event) -> {
            // A hardware Enter key sends the action twice (key down and key up): only the first one searches
            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                return true;
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null) {
                onSearch();
                return true;
            }
            return false;
        });

        // The list is filtered while the user types
        mBinding.mainSearchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mHandler.removeCallbacks(mSearchRunnable);
                mHandler.postDelayed(mSearchRunnable, SEARCH_DELAY_MS);
            }
        });

        mBinding.mainFilter.setOnCheckedStateChangeListener((group, checkedIds) -> loadEntries());
    }

    @Override
    protected void onDestroy() {

        mHandler.removeCallbacks(mSearchRunnable);
        if (mCurrentCall != null) {
            mCurrentCall.cancel();
        }
        super.onDestroy();
    }

    private void onSearch() {

        hideKeyboard();
        loadEntries();
    }

    // Without text, the whole category of the selected chip is listed; with text, it is searched inside it
    private void loadEntries() {

        mHandler.removeCallbacks(mSearchRunnable);

        EntryType type = getSelectedType();
        String textToSearch = getTextToSearch();

        // Only the last request matters: a slower previous response must not replace it
        if (mCurrentCall != null) {
            mCurrentCall.cancel();
        }

        showLoading();

        if (textToSearch.isEmpty()) {
            getCategoryMembers(type);
        } else {
            searchEntries(textToSearch, type);
        }
    }

    private void getCategoryMembers(EntryType type) {

        Call<CategoryMembersResponse> call = mApi.getCategoryMembers(UespCategories.getCategory(type), LIMIT);
        mCurrentCall = call;

        call.enqueue(new Callback<CategoryMembersResponse>() {

            @Override
            public void onResponse(@NonNull Call<CategoryMembersResponse> call,
                                   @NonNull Response<CategoryMembersResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getQuery() != null) {

                    Log.i(TAG, "Response OK: " + response.code());
                    showEntries(response.body().getQuery().getCategoryMembers(), type);

                } else {

                    Log.e(TAG, "Response fails: " + response.code());
                    showError();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryMembersResponse> call,
                                  @NonNull Throwable t) {

                if (call.isCanceled()) {
                    return;
                }
                Log.e(TAG, "Response fails: " + t.getMessage());
                showError();
            }
        });
    }

    private void searchEntries(String textToSearch,
                               EntryType type) {

        Call<SearchResponse> call = mApi.searchPages(buildSearch(textToSearch, type), LIMIT);
        mCurrentCall = call;

        call.enqueue(new Callback<SearchResponse>() {

            @Override
            public void onResponse(@NonNull Call<SearchResponse> call,
                                   @NonNull Response<SearchResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Log.i(TAG, "Response OK: " + response.code());
                    // Without results MediaWiki may omit the "query" object
                    SearchResponse.Query query = response.body().getQuery();
                    showEntries(query != null ? query.getSearch() : null, type);

                } else {

                    Log.e(TAG, "Response fails: " + response.code());
                    showError();
                }

            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call,
                                  @NonNull Throwable t) {

                if (call.isCanceled()) {
                    return;
                }
                Log.e(TAG, "Response fails: " + t.getMessage());
                showError();
            }
        });
    }

    // CirrusSearch query of MediaWiki: every word must be at the start of a word of the title
    // ("af" finds "Afflicted", "aela t" finds "Aela the Huntress"), inside the categories of the selected type
    private String buildSearch(String textToSearch,
                               EntryType type) {

        StringBuilder search = new StringBuilder();

        for (String word : textToSearch.split("\\s+")) {
            // Keep only letters, digits, apostrophes and hyphens: the rest is search syntax
            String cleanWord = word.replaceAll("[^\\p{L}\\p{N}'-]", "");
            if (!cleanWord.isEmpty()) {
                search.append("intitle:").append(cleanWord).append("* ");
            }
        }

        return search + "incategory:" + UespCategories.getSearchCategories(type);
    }

    private EntryType getSelectedType() {

        int checkedId = mBinding.mainFilter.getCheckedChipId();

        if (checkedId == R.id.main_filter_quests) {
            return EntryType.QUEST;
        } else if (checkedId == R.id.main_filter_items) {
            return EntryType.ITEM;
        } else if (checkedId == R.id.main_filter_places) {
            return EntryType.PLACE;
        }
        return EntryType.CHARACTER;
    }

    private String getTextToSearch() {

        Editable text = mBinding.mainSearchText.getText();
        return text != null ? text.toString().trim() : "";
    }

    private void showLoading() {

        mBinding.mainProgress.setVisibility(View.VISIBLE);
        mBinding.mainEmptyView.setVisibility(View.GONE);
        mBinding.mainList.setVisibility(View.GONE);
    }

    private void showEntries(List<WikiEntry> entries,
                             EntryType type) {

        if (entries == null || entries.isEmpty()) {
            showEmptyView(R.string.entries_no_results);
            return;
        }

        // The type is not in the JSON: every entry belongs to the requested category
        for (WikiEntry entry : entries) {
            entry.setType(type);
        }

        mAdapter.setItems(entries);
        mBinding.mainList.scrollToPosition(0);
        mBinding.mainProgress.setVisibility(View.GONE);
        mBinding.mainEmptyView.setVisibility(View.GONE);
        mBinding.mainList.setVisibility(View.VISIBLE);
    }

    private void showEmptyView(@StringRes int message) {

        mBinding.mainEmptyView.setText(message);
        mBinding.mainProgress.setVisibility(View.GONE);
        mBinding.mainEmptyView.setVisibility(View.VISIBLE);
        mBinding.mainList.setVisibility(View.GONE);
    }

    private void showError() {

        showEmptyView(R.string.entries_no_results);
        Toast.makeText(this, R.string.error_general, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {

        WindowCompat.getInsetsController(getWindow(), mBinding.mainSearchText).hide(WindowInsetsCompat.Type.ime());
    }
}
