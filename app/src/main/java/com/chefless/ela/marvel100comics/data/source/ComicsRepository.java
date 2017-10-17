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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chefless.ela.marvel100comics.data.models.Comic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load comics from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class ComicsRepository implements ComicsDataSource {

    private static ComicsRepository INSTANCE = null;

    private final ComicsDataSource mComicsRemoteDataSource;

    private List<ComicsRepositoryObserver> mObservers = new ArrayList<ComicsRepositoryObserver>();

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Comic> mCachedComics;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty;

    public static ComicsRepository getInstance(ComicsDataSource comicsRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ComicsRepository(comicsRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    // Prevent direct instantiation.
    private ComicsRepository(@NonNull ComicsDataSource comicsRemoteDataSource) {
        mComicsRemoteDataSource = checkNotNull(comicsRemoteDataSource);
    }

    public void addContentObserver(ComicsRepositoryObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(ComicsRepositoryObserver observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    private void notifyContentObserver() {
        for (ComicsRepositoryObserver observer : mObservers) {
            observer.onComicsChanged();
        }
    }

    /**
     *
     * Gets Comics from cache, local data source (SQLite) or remote data source, whichever is
     * available first. This is done synchronously because it's used by the {@link ComicsLoader},
     * which implements the async mechanism.
     */
    @Nullable
    @Override
    public List<Comic> getComics() {
        List<Comic> comics;

        if (!mCacheIsDirty) {
            // Respond immediately with cache if available and not dirty
            if (mCachedComics != null) {
                comics = getCachedComics();
                return comics;
            }
        }

        // Grab remote data if cache is dirty or local data not available.
        comics = mComicsRemoteDataSource.getComics();

        processLoadedComics(comics);
        return getCachedComics();
    }

    public boolean cachedComicsAvailable() {
        return mCachedComics != null && !mCacheIsDirty;
    }

    public List<Comic> getCachedComics() {
        return mCachedComics == null ? null : new ArrayList<>(mCachedComics.values());
    }

    public Comic getCachedComic(String comicId) {
        return mCachedComics.get(comicId);
    }

    private void processLoadedComics(List<Comic> comics) {
        if (comics == null) {
            mCachedComics = null;
            mCacheIsDirty = false;
            return;
        }
        if (mCachedComics == null) {
            mCachedComics = new LinkedHashMap<>();
        }
        mCachedComics.clear();
        for (Comic comic : comics) {
            mCachedComics.put(comic.getId(), comic);
        }
        mCacheIsDirty = false;
    }

    @Override
    public Comic getComic(@NonNull final String comicId) {
        checkNotNull(comicId);

        Comic cachedComic = getComicWithId(comicId);

        // Respond immediately with cache if we have one
        if (cachedComic != null) {
            return cachedComic;
        }

        // Query the network.
        return mComicsRemoteDataSource.getComic(comicId);
    }

    @Nullable
    private Comic getComicWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedComics == null || mCachedComics.isEmpty()) {
            return null;
        } else {
            return mCachedComics.get(id);
        }
    }

    @Override
    public void refreshComics() {
        mCacheIsDirty = true;
        notifyContentObserver();
    }

    public interface ComicsRepositoryObserver {

        void onComicsChanged();

    }
}
