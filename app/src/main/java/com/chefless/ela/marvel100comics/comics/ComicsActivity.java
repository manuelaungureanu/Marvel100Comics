package com.chefless.ela.marvel100comics.comics;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.chefless.ela.marvel100comics.Injection;
import com.chefless.ela.marvel100comics.R;
import com.chefless.ela.marvel100comics.data.source.ComicsLoader;
import com.chefless.ela.marvel100comics.data.source.ComicsRepository;
import com.chefless.ela.marvel100comics.databinding.ActivityComicsBinding;
import com.chefless.ela.marvel100comics.utils.ActivityUtils;

public class ComicsActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private ComicsPresenter mComicsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComicsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_comics);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        ComicsFragment comicsFragment =
                (ComicsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (comicsFragment == null) {
            // Create the fragment
            comicsFragment = ComicsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), comicsFragment, R.id.contentFrame);
        }


        // Create the presenter
        ComicsRepository repository = Injection.provideComicsRepository(getApplicationContext());
        ComicsLoader comicsLoader = new ComicsLoader(getApplicationContext(), repository);

        mComicsPresenter = new ComicsPresenter(
                comicsLoader,
                getSupportLoaderManager(),
                repository,
                comicsFragment
        );

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            ComicsFilterType currentFiltering =
                    (ComicsFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mComicsPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mComicsPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }
}
