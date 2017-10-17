package com.chefless.ela.marvel100comics.comicdetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.chefless.ela.marvel100comics.Injection;
import com.chefless.ela.marvel100comics.R;
import com.chefless.ela.marvel100comics.data.source.ComicLoader;
import com.chefless.ela.marvel100comics.utils.ActivityUtils;

public class ComicDetailActivity extends AppCompatActivity {

    public static final String EXTRA_COMIC_ID = "comicId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String comicId = getIntent().getStringExtra(EXTRA_COMIC_ID);
        ComicDetailFragment comicDetailFragment = (ComicDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (comicDetailFragment == null) {
            comicDetailFragment = ComicDetailFragment.newInstance(comicId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    comicDetailFragment, R.id.contentFrame);
        }

        // Create the presenter
        new ComicDetailPresenter(
                comicId,
                Injection.provideComicsRepository(getApplicationContext()),
                comicDetailFragment,
                new ComicLoader(comicId, getApplicationContext()),
                getSupportLoaderManager());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
