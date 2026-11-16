package es.udc.ps.wikirym.app.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<B extends ViewBinding> extends Fragment {

    // Only valid between onCreateView and onDestroyView: the fragment can outlive its view (back stack)
    protected B mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = inflateBinding(inflater, container);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        mBinding = null;
    }

    protected abstract B inflateBinding(LayoutInflater inflater,
                                        ViewGroup container);
}
