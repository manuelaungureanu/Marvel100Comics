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

package com.chefless.ela.marvel100comics.data.source;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.chefless.ela.marvel100comics.Injection;
import com.chefless.ela.marvel100comics.data.models.Comic;
/**
 * Custom {@link android.content.Loader} for a {@link com.chefless.ela.marvel100comics.data.models.Comic}, using the
 * {@link ComicsRepository} as its source. This Loader is a {@link AsyncTaskLoader} so it queries
 * the data asynchronously.
 */
public class ComicLoader extends AsyncTaskLoader<Comic>
        implements ComicsRepository.ComicsRepositoryObserver{

    private final String mComicId;
    private ComicsRepository mRepository;

    public ComicLoader(String comicId, Context context) {
        super(context);
        this.mComicId = comicId;
        mRepository = Injection.provideComicsRepository(context);
    }

    @Override
    public Comic loadInBackground() {
        return mRepository.getComic(mComicId);
    }

    @Override
    public void deliverResult(Comic data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {

        // Deliver any previously loaded data immediately if available.
        if (mRepository.cachedComicsAvailable()) {
            deliverResult(mRepository.getCachedComic(mComicId));
        }

        // Begin monitoring the underlying data source.
        mRepository.addContentObserver(this);

        if (takeContentChanged() || !mRepository.cachedComicsAvailable()) {
            // When a change has  been delivered or the repository cache isn't available, we force
            // a load.
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mRepository.removeContentObserver(this);
    }

    @Override
    public void onComicsChanged() {
        if (isStarted()) {
            forceLoad();
        }
    }
}
