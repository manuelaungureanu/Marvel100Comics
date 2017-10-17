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

import android.support.annotation.NonNull;

import com.chefless.ela.marvel100comics.BasePresenter;
import com.chefless.ela.marvel100comics.BaseView;
import com.chefless.ela.marvel100comics.data.models.Comic;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface ComicsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showComics(List<Comic> comics);

        void showComicDetailsUi(String comicId);

        void showLoadingComicsError();

        void showNoComics();

        void showNoComicsInBudget();

        void showBudgetFilterLabel();

        void showAllFilterLabel();

        void showFilteringPopUpMenu();

        void showTotalPagesNo();

        void showHideBudget(Boolean show);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadComics(boolean forceUpdate);

        void openComicDetails(@NonNull Comic requestedComic);

        void setFiltering(ComicsFilterType requestType);

        ComicsFilterType getFiltering();

        void setBudget(float budget);

        float getBudget();

        int getPagesNo();
    }
}
