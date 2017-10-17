package com.chefless.ela.marvel100comics;

import android.content.Context;

import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.data.models.ComicPrice;
import com.chefless.ela.marvel100comics.data.source.ComicsDataSource;
import com.chefless.ela.marvel100comics.data.source.ComicsRepository;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class ComicsRepositoryTest {
    
    private static List<Comic> COMICS = Lists.newArrayList(
            new Comic("123", "Spiderman", 11, new ComicPrice[]{new ComicPrice("printPrice", 35.5f)}),
            new Comic("345", "Superman", 20, new ComicPrice[]{new ComicPrice("printPrice", 11.5f)}),
            new Comic("678", "Avengers", 35, new ComicPrice[]{new ComicPrice("printPrice", 10f)})
    );

    private ComicsRepository mRepository;

    @Mock
    private ComicsDataSource mRemoteDataSource;

    @Mock
    private Context mContext;

    @Mock
    private ComicsDataSource.GetComicCallback mGetCallback;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<ComicsDataSource.GetComicCallback> mCallbackCaptor;

    @Before
    public void setupComicsRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mRepository = ComicsRepository.getInstance(mRemoteDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        ComicsRepository.destroyInstance();
    }

    @Test
    public void getComics_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the comics repository
        twoComicsLoadCallsToRepository();

        // Then comics were only requested once from Service API
        verify(mRemoteDataSource).getComics();
    }



    @Test
    public void getComicsWithDataSourceUnavailable_returnsNull() {
        /// Given unavailable comics in both data sources
        setComicsNotAvailable(mRemoteDataSource);

        // When calling getComics in the repository
        List<Comic> returnedComics = mRepository.getComics();

        // Then the returned comics are null
        assertNull(returnedComics);
    }

    @Test
    public void getComicWithDataSourceUnavailable_firesOnDataUnavailable() {
        // Given a comic id
        final String comicId = "123";

        // And the remote data source has no data available
        setComicNotAvailable(mRemoteDataSource, comicId);

        // When calling getComic in the repository
        Comic comic = mRepository.getComic(comicId);

        // Verify no data is returned
        assertThat(comic, is(nullValue()));
    }

    @Test
    public void getComics_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        mRepository.refreshComics();

        // Make the remote data source return data
        setComicsAvailable(mRemoteDataSource, COMICS);

        // When calling getComics in the repository
        mRepository.getComics();
    }

    /**
    * Convenience method that issues two calls to the comics repository
    */
    private void twoComicsLoadCallsToRepository() {

        // and a remote data source with no data
        when(mRemoteDataSource.getComics()).thenReturn(COMICS);

        // When comics are requested from repository
        mRepository.getComics(); // First call to API

        // Then the remote data source is called
        verify(mRemoteDataSource).getComics();

        mRepository.getComics(); // Second call to API
    }

    private void setComicsNotAvailable(ComicsDataSource dataSource) {
        when(dataSource.getComics()).thenReturn(null);
    }

    private void setComicsAvailable(ComicsDataSource dataSource, List<Comic> comics) {
        when(dataSource.getComics()).thenReturn(comics);
    }

    private void setComicNotAvailable(ComicsDataSource dataSource, String comicId) {
        when(dataSource.getComic(comicId)).thenReturn(null);
    }

    private void setComicAvailable(ComicsDataSource dataSource, Comic comic) {
        when(dataSource.getComic(comic.getId())).thenReturn(comic);
    }
}
