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

package com.chefless.ela.marvel100comics.comics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.data.source.ComicsDataSource;
import com.chefless.ela.marvel100comics.data.source.ComicsLoader;
import com.chefless.ela.marvel100comics.data.source.ComicsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chefless.ela.marvel100comics.comics.ComicsFilterType.COMICS_IN_BUDGET;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI, retrieves the data and updates the
 * UI as required. It is implemented as a non UI to make use of the
 * {@link LoaderManager} mechanism for managing loading and updating data asynchronously.
 */
public class ComicsPresenter implements ComicsContract.Presenter,
        LoaderManager.LoaderCallbacks<List<Comic>> {

    private final static int COMICS_QUERY = 1;

    private final ComicsRepository mComicsRepository;

    private final ComicsContract.View mComicsView;

    private final ComicsLoader mLoader;

    private final LoaderManager mLoaderManager;

    private List<Comic> mCurrentComics;

    private ComicsFilterType mCurrentFiltering = ComicsFilterType.ALL_COMICS;

    private boolean mFirstLoad;

    private int mTotalPages;

    private float mBudget;

    public ComicsPresenter(@NonNull ComicsLoader loader, @NonNull LoaderManager loaderManager,
                           @NonNull ComicsRepository comicsRepository, @NonNull ComicsContract.View comicsView) {
        mLoader = checkNotNull(loader, "loader cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mComicsRepository = checkNotNull(comicsRepository, "ComicsRepository cannot be null");
        mComicsView = checkNotNull(comicsView, "comicsView cannot be null!");

        mComicsView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(COMICS_QUERY, null, this);
    }

    @Override
    public Loader<List<Comic>> onCreateLoader(int id, Bundle args) {
        mComicsView.setLoadingIndicator(true);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Comic>> loader, List<Comic> data) {
        mComicsView.setLoadingIndicator(false);

        mCurrentComics = data;
        if (mCurrentComics == null) {
            mComicsView.showLoadingComicsError();
        } else {
            showFilteredComics();
        }
    }

    private void showFilteredComics() {
        List<Comic> comicsToDisplay = new ArrayList<>();
        mTotalPages = 0;
        float amountLeft = mBudget;

        ArrayList<Comic> items = new ArrayList<>(mCurrentComics);
        //we sort the comics asc by price, to list more of them in the budget
        if(mCurrentFiltering==COMICS_IN_BUDGET)
            Collections.sort(items);

        if (mCurrentComics != null) {
            for (Comic comic : items) {
                switch (mCurrentFiltering) {
                    case ALL_COMICS:
                        comicsToDisplay.add(comic);
                        mTotalPages += comic.getPageCount();
                        break;
                    case COMICS_IN_BUDGET:
                        float printPrice = comic.getPrintPrice();
                        if (amountLeft >= printPrice)
                        {
                            comicsToDisplay.add(comic);
                            mTotalPages += comic.getPageCount();
                            amountLeft -= printPrice;
                        }
                        break;
                    default:
                        comicsToDisplay.add(comic);
                        mTotalPages += comic.getPageCount();
                        break;
                }
            }
        }

        processComics(comicsToDisplay);
    }

    @Override
    public void onLoaderReset(Loader<List<Comic>> loader) {
        // no-op
    }

    /**
     * @param forceUpdate Pass in true to refresh the data in the {@link ComicsDataSource}
     */
    public void loadComics(boolean forceUpdate) {
        if (forceUpdate || mFirstLoad) {
            mFirstLoad = false;
            mComicsRepository.refreshComics();
        } else {
            showFilteredComics();
        }
    }

    public void processComics(List<Comic> comics) {
        if (comics.isEmpty()) {
            // Show a message indicating there are no comics for that filter type.
            processEmptyComics();
        } else {
            // Show the list of comics
            mComicsView.showComics(comics);
            mComicsView.showTotalPagesNo();
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    public void showFilterLabel() {
        switch (mCurrentFiltering) {
            case COMICS_IN_BUDGET:
                mComicsView.showBudgetFilterLabel();
                mComicsView.showHideBudget(true);
                break;
            default:
                mComicsView.showAllFilterLabel();
                mComicsView.showHideBudget(false);
                break;
        }
    }

    private void processEmptyComics() {
        switch (mCurrentFiltering) {
            case COMICS_IN_BUDGET:
                mComicsView.showNoComicsInBudget();
                break;
            default:
                mComicsView.showNoComics();
                break;
        }
    }

    @Override
    public void openComicDetails(@NonNull Comic requestedComic) {
        checkNotNull(requestedComic, "requestedComic cannot be null!");
        mComicsView.showComicDetailsUi(requestedComic.getId());
    }

    /**
     * Sets the current comic filtering type.
     *
     * @param requestType Can be {@link ComicsFilterType#ALL_COMICS},
     *                    {@link ComicsFilterType#COMICS_IN_BUDGET}
     */
    @Override
    public void setFiltering(ComicsFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public ComicsFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void setBudget(float budget) {
        this.mBudget = budget;
    }

    @Override
    public float getBudget() {
        return mBudget;
    }

    @Override
    public int getPagesNo() {
        return mTotalPages;
    }
}
