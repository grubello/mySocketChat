package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import pl.statisfy.socketchat.project.utils.Utils;

public class BaseFragment extends Fragment {

    protected CompositeDisposable mCompositeDisposable;
    public SharedPreferences sharedPrefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeDisposable = new CompositeDisposable();
        sharedPrefs = getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
