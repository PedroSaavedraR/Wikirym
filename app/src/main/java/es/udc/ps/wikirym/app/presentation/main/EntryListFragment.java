package es.udc.ps.wikirym.app.presentation.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import es.udc.ps.wikirym.app.presentation.BaseFragment;
import es.udc.ps.wikirym.app.presentation.main.adapter.EntryAdapter;
import es.udc.ps.wikirym.databinding.FragmentEntryListBinding;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntryListFragment extends BaseFragment<FragmentEntryListBinding> {

    // La implementa la actividad, que decide cómo mostrar el detalle
    public interface OnEntrySelectedListener {

        void onEntrySelected(WikiEntry entry);
    }

    private static final String TAG = EntryListFragment.class.getSimpleName();

    private static final int LIMIT = 50;
    // Espera tras la última tecla antes de buscar
    private static final long SEARCH_DELAY_MS = 400;

    private OnEntrySelectedListener mListener;
    // Del fragmento, no de la vista: conserva la lista al volver del detalle
    private EntryAdapter mAdapter;
    private UespAPI mApi;
    private Call<?> mCurrentCall;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mSearchRunnable = this::loadEntries;

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        if (!(context instanceof OnEntrySelectedListener)) {
            throw new IllegalStateException(context + " must implement OnEntrySelectedListener");
        }
        mListener = (OnEntrySelectedListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setUpApi();
        mAdapter = new EntryAdapter(entry -> mListener.onEntrySelected(entry));
    }

    @Override
    protected FragmentEntryListBinding inflateBinding(LayoutInflater inflater,
                                                      ViewGroup container) {

        return FragmentEntryListBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setUpView();
    }

    // Listeners tras restaurar texto y chip, para no lanzar peticiones de más
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);

        setUpListeners();

        if (mAdapter.isEmpty()) {
            loadEntries();
        } else {
            showList();
        }
    }

    @Override
    public void onDestroyView() {

        mHandler.removeCallbacks(mSearchRunnable);
        if (mCurrentCall != null) {
            mCurrentCall.cancel();
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    private void setUpApi() {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        List<Interceptor> interceptors = okHttpClient.interceptors();
        // Antes que el de logging, para que el log muestre la petición final
        interceptors.add(new UespApiInterceptor());
        interceptors.add(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://en.uesp.net/w/").client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        mApi = retrofit.create(UespAPI.class);
    }

    private void setUpView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.mainList.setLayoutManager(linearLayoutManager);
        mBinding.mainList.setAdapter(mAdapter);
    }

    private void setUpListeners() {

        mBinding.mainSearchButton.setOnClickListener(v -> onSearch());
        mBinding.mainSearchText.setOnEditorActionListener((v, actionId, event) -> {
            // Intro llega dos veces (pulsar y soltar): solo busca la primera
            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                return true;
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null) {
                onSearch();
                return true;
            }
            return false;
        });

        // Filtra mientras se escribe
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

    private void onSearch() {

        hideKeyboard();
        loadEntries();
    }

    // Sin texto lista la categoría del chip; con texto busca dentro de ella
    private void loadEntries() {

        mHandler.removeCallbacks(mSearchRunnable);

        if (mBinding == null) {
            return;
        }

        EntryType type = getSelectedType();
        String textToSearch = getTextToSearch();

        // Solo vale la última petición
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
                    // Sin resultados puede faltar "query"
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

    // Prefijo en el título: "af" encuentra "Afflicted", "aela t" encuentra "Aela the Huntress"
    private String buildSearch(String textToSearch,
                               EntryType type) {

        StringBuilder search = new StringBuilder();

        for (String word : textToSearch.split("\\s+")) {
            // Solo letras, números, apóstrofos y guiones: lo demás es sintaxis de búsqueda
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

        // La respuesta puede llegar con la vista ya destruida
        if (mBinding == null) {
            return;
        }

        if (entries == null || entries.isEmpty()) {
            showEmptyView(R.string.entries_no_results);
            return;
        }

        // El tipo no viene en el JSON: es el de la categoría pedida
        for (WikiEntry entry : entries) {
            entry.setType(type);
        }

        mAdapter.setItems(entries);
        mBinding.mainList.scrollToPosition(0);
        showList();
    }

    private void showList() {

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

        if (mBinding == null) {
            return;
        }
        showEmptyView(R.string.entries_no_results);
        Toast.makeText(requireContext(), R.string.error_general, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {

        WindowCompat.getInsetsController(requireActivity().getWindow(), mBinding.mainSearchText)
                .hide(WindowInsetsCompat.Type.ime());
    }
}
