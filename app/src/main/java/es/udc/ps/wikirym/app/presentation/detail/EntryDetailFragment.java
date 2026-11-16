package es.udc.ps.wikirym.app.presentation.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import es.udc.ps.wikirym.BuildConfig;
import es.udc.ps.wikirym.R;
import es.udc.ps.wikirym.app.data.UespAPI;
import es.udc.ps.wikirym.app.data.interceptor.UespApiInterceptor;
import es.udc.ps.wikirym.app.domain.EntryType;
import es.udc.ps.wikirym.app.domain.PageDetail;
import es.udc.ps.wikirym.app.domain.PageDetailResponse;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import es.udc.ps.wikirym.app.presentation.BaseFragment;
import es.udc.ps.wikirym.app.presentation.EntryTypeLabels;
import es.udc.ps.wikirym.databinding.FragmentEntryDetailBinding;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntryDetailFragment extends BaseFragment<FragmentEntryDetailBinding> {

    private static final String TAG = EntryDetailFragment.class.getSimpleName();

    private static final String ARG_PAGE_ID = "arg_page_id";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_TYPE = "arg_type";

    private Call<PageDetailResponse> mCurrentCall;

    // Los arguments sobreviven a la recreación del fragmento
    public static EntryDetailFragment newInstance(WikiEntry entry) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_ID, entry.getPageId());
        args.putString(ARG_TITLE, entry.getDisplayTitle());
        args.putString(ARG_TYPE, entry.getType() != null ? entry.getType().name() : EntryType.CHARACTER.name());

        EntryDetailFragment fragment = new EntryDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentEntryDetailBinding inflateBinding(LayoutInflater inflater,
                                                        ViewGroup container) {

        return FragmentEntryDetailBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Bundle args = requireArguments();
        String title = args.getString(ARG_TITLE);
        EntryType type = EntryType.valueOf(args.getString(ARG_TYPE));

        // Título en la action bar
        requireActivity().setTitle(title);

        mBinding.detailTitle.setText(title);
        mBinding.detailType.setText(EntryTypeLabels.getLabel(type));

        getPageDetail(args.getInt(ARG_PAGE_ID));
    }

    @Override
    public void onDestroyView() {

        if (mCurrentCall != null) {
            mCurrentCall.cancel();
        }
        super.onDestroyView();
    }

    private void getPageDetail(int pageId) {

        showLoading();

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        List<Interceptor> interceptors = okHttpClient.interceptors();
        interceptors.add(new UespApiInterceptor());
        interceptors.add(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://en.uesp.net/w/").client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        UespAPI api = retrofit.create(UespAPI.class);

        mCurrentCall = api.getPageDetail(pageId);

        mCurrentCall.enqueue(new Callback<PageDetailResponse>() {

            @Override
            public void onResponse(@NonNull Call<PageDetailResponse> call,
                                   @NonNull Response<PageDetailResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getQuery() != null) {

                    Log.i(TAG, "Response OK: " + response.code());
                    List<PageDetail> pages = response.body().getQuery().getPages();
                    PageDetail page = pages != null && !pages.isEmpty() ? pages.get(0) : null;

                    if (page == null || page.isMissing()) {
                        showEmptyView(R.string.detail_not_found);
                    } else {
                        showDetail(page);
                    }

                } else {

                    Log.e(TAG, "Response fails: " + response.code());
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageDetailResponse> call,
                                  @NonNull Throwable t) {

                if (call.isCanceled()) {
                    return;
                }
                Log.e(TAG, "Response fails: " + t.getMessage());
                showError();
            }
        });
    }

    private void showLoading() {

        mBinding.detailProgress.setVisibility(View.VISIBLE);
        mBinding.detailEmptyView.setVisibility(View.GONE);
        mBinding.detailContent.setVisibility(View.GONE);
    }

    private void showDetail(PageDetail page) {

        // La respuesta puede llegar con la vista ya destruida
        if (mBinding == null) {
            return;
        }

        if (TextUtils.isEmpty(page.getExtract())) {
            mBinding.detailExtract.setText(R.string.detail_no_extract);
        } else {
            mBinding.detailExtract.setText(page.getExtract());
        }

        PageDetail.Thumbnail thumbnail = page.getThumbnail();
        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getSource())) {
            mBinding.detailImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(thumbnail.getSource()).into(mBinding.detailImage);
        } else {
            mBinding.detailImage.setVisibility(View.GONE);
        }

        mBinding.detailProgress.setVisibility(View.GONE);
        mBinding.detailEmptyView.setVisibility(View.GONE);
        mBinding.detailContent.setVisibility(View.VISIBLE);
    }

    private void showEmptyView(@StringRes int message) {

        if (mBinding == null) {
            return;
        }
        mBinding.detailEmptyView.setText(message);
        mBinding.detailProgress.setVisibility(View.GONE);
        mBinding.detailEmptyView.setVisibility(View.VISIBLE);
        mBinding.detailContent.setVisibility(View.GONE);
    }

    private void showError() {

        if (mBinding == null) {
            return;
        }
        showEmptyView(R.string.detail_not_found);
        Toast.makeText(requireContext(), R.string.error_general, Toast.LENGTH_SHORT).show();
    }
}
