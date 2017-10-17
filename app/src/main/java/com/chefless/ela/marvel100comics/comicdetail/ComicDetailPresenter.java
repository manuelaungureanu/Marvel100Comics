/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chefless.ela.marvel100comics.comicdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chefless.ela.marvel100comics.BasePresenter;
import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.data.source.ComicLoader;
import com.chefless.ela.marvel100comics.data.source.ComicsRepository;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link ComicDetailFragment}), retrieves the data and updates
 * the UI as required.
 */
public class ComicDetailPresenter implements BasePresenter,
        LoaderManager.LoaderCallbacks<Comic> {

    private static final int COMIC_QUERY = 3;

    private ComicsRepository mComicsRepository;

    private ComicDetailContract.View mDetailView;

    private ComicLoader mComicLoader;

    private LoaderManager mLoaderManager;

    @Nullable
    private String mComicId;

    public ComicDetailPresenter(@Nullable String taskId,
                                @NonNull ComicsRepository tasksRepository,
                                @NonNull ComicDetailContract.View taskDetailView,
                                @NonNull ComicLoader taskLoader,
                                @NonNull LoaderManager loaderManager) {
        mComicId = taskId;
        mComicsRepository = checkNotNull(tasksRepository, "comicsRepository cannot be null!");
        mDetailView = checkNotNull(taskDetailView, "comicsDetailView cannot be null!");
        mComicLoader = checkNotNull(taskLoader, "comicsLoader cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");

        mDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(COMIC_QUERY, null, this);
    }


    private void showTask(@NonNull Comic comic) {
        mDetailView.setModel(comic);
        mDetailView.setLoadingIndicator(false);
    }

    @Override
    public Loader<Comic> onCreateLoader(int id, Bundle args) {
        if (mComicId == null) {
            return null;
        }
        mDetailView.setLoadingIndicator(true);
        return mComicLoader;
    }

    @Override
    public void onLoadFinished(Loader<Comic> loader, Comic data) {
        if (data != null) {
            showTask(data);
        } else {
            mDetailView.showMissingComic();
        }
    }

    @Override
    public void onLoaderReset(Loader<Comic> loader) {
        // no-op
    }

}
