package com.chefless.ela.marvel100comics;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.chefless.ela.marvel100comics.comics.ComicsContract;
import com.chefless.ela.marvel100comics.comics.ComicsFilterType;
import com.chefless.ela.marvel100comics.comics.ComicsPresenter;
import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.data.models.ComicPrice;
import com.chefless.ela.marvel100comics.data.source.ComicsLoader;
import com.chefless.ela.marvel100comics.data.source.ComicsRepository;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link ComicsPresenter}
 */
public class ComicsPresenterTest {

    private static List<Comic> COMICS = Lists.newArrayList(
            new Comic("123", "Spiderman", 11, new ComicPrice[]{new ComicPrice("printPrice", 35.5f)}),
            new Comic("345", "Superman", 20, new ComicPrice[]{new ComicPrice("printPrice", 11.5f)}),
            new Comic("678", "Avengers", 35, new ComicPrice[]{new ComicPrice("printPrice", 10f)})
            );

    private static List<Comic> EMPTY_COMICS = new ArrayList<>(0);

    @Mock
    private ComicsRepository mRepository;

    @Mock
    private ComicsContract.View mView;

    @Captor
    private ArgumentCaptor<List> mShowComicsArgumentCaptor;

    @Mock
    private ComicsLoader mLoader;

    @Mock
    private LoaderManager mLoaderManager;

    private ComicsPresenter mPresenter;
    private List<ComicsRepository.ComicsRepositoryObserver> mObservers = new ArrayList<>();

    @Before
    public void setupPresenter() {
        MockitoAnnotations.initMocks(this);
        // Get a reference to the class under test
        mPresenter = new ComicsPresenter(mLoader, mLoaderManager, mRepository, mView);
    }

    @Test
    public void loadAllComicsFromRepositoryAndLoadIntoView() {
        // When the loader finishes with tasks and filter is set to all
        mPresenter.setFiltering(ComicsFilterType.ALL_COMICS);
        mPresenter.onLoadFinished(mock(Loader.class), COMICS);

        // Then progress indicator is hidden and all comics are shown in UI
        verify(mView).setLoadingIndicator(false);
        verify(mView).showComics(mShowComicsArgumentCaptor.capture());
        assertThat(mShowComicsArgumentCaptor.getValue().size(), is(3));
    }

    @Test
    public void clickOnComic_ShowsDetailUi() {
        // Given a stubbed comic
        Comic requestedComic = new Comic("1", "Avengers", 12, new ComicPrice[]{new ComicPrice("printPrice", 10f)});

        // When opening details is requested
        mPresenter.openComicDetails(requestedComic);

        // Then comic detail UI is shown
        verify(mView).showComicDetailsUi(any(String.class));
    }

    @Test
    public void unavailableComics_ShowsError() {
        // When the loader finishes with error
        mPresenter.setFiltering(ComicsFilterType.ALL_COMICS);
        mPresenter.onLoadFinished(mock(Loader.class), null);

        // Then an error message is shown
        verify(mView).showLoadingComicsError();
    }

    @Test
    public void checkTotalPagesNo(){
        mPresenter.setFiltering(ComicsFilterType.ALL_COMICS);
        mPresenter.onLoadFinished(mock(Loader.class), COMICS);

        assertTrue(mPresenter.getPagesNo()==66);
    }

    @Test
    public void checkMaxComicsInBudget(){
        mPresenter.setFiltering(ComicsFilterType.COMICS_IN_BUDGET);
        mPresenter.setBudget(45);
        mPresenter.onLoadFinished(mock(Loader.class), COMICS);

        verify(mView).showComics(mShowComicsArgumentCaptor.capture());
        assertThat(mShowComicsArgumentCaptor.getValue().size(), is(2));
    }
}
