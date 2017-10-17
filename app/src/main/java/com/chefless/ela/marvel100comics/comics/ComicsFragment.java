package com.chefless.ela.marvel100comics.comics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chefless.ela.marvel100comics.R;
import com.chefless.ela.marvel100comics.comicdetail.ComicDetailActivity;
import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.databinding.FragmentComicsBinding;

import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicsFragment extends Fragment implements ComicsContract.View, ComicsAdapter.ComicsAdapterOnClickHandler {

    private ComicsContract.Presenter mPresenter;

    private ComicsAdapter mAdapter;

    private View mNoComicsView;

    private TextView mNoComicsMainView;

    private RelativeLayout mComicsView;

    private TextView mFilteringLabelView;

    private RecyclerView mRVComics;

    private ProgressBar mLoadingIndicator;

    private TextView mTotalPagesNoView;

    private EditText mBudgetView;

    public ComicsFragment() {
        // Required empty public constructor
    }

    public static ComicsFragment newInstance() {
        return new ComicsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ComicsAdapter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentComicsBinding binding = FragmentComicsBinding.inflate(inflater, container, false);
        initUI(binding);

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    private void initUI(FragmentComicsBinding binding) {

        mRVComics = binding.rvComics;
        mComicsView = binding.comicsLL;

        mNoComicsView = binding.noComics;
        mNoComicsMainView = binding.noComicsMain;
        mLoadingIndicator = binding.pbLoadingData;
        mFilteringLabelView = binding.filteringLabel;
        mTotalPagesNoView = binding.totalPagesNo;
        mBudgetView = binding.budget;

        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false);
        mRVComics.setLayoutManager(layoutManager);
        mRVComics.setHasFixedSize(true);
        mRVComics.setAdapter(mAdapter);

        mBudgetView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                onBudgetChanged(mBudgetView.getText().toString());
            }
        });
    }

    private void onBudgetChanged(String input) {
        if(TextUtils.isEmpty(input))
            return;
        try {
            float budget = Float.parseFloat(input);
            mPresenter.setBudget(budget);
            mPresenter.loadComics(false);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                setLoadingIndicator(true);
                mAdapter.setData(null);
                mPresenter.loadComics(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.comics_fragment_menu, menu);
    }

    @Override
    public void setPresenter(ComicsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        mLoadingIndicator.setVisibility(active?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void showComics(List<Comic> comics) {
        mAdapter.setData(comics);

        mComicsView.setVisibility(View.VISIBLE);
        mNoComicsView.setVisibility(View.GONE);
    }

    @Override
    public void showComicDetailsUi(String comicId) {
        Intent intent = new Intent(getContext(), ComicDetailActivity.class);
        intent.putExtra(ComicDetailActivity.EXTRA_COMIC_ID, comicId);
        startActivity(intent);
    }

    @Override
    public void showLoadingComicsError() {
        showMessage(getString(R.string.loading_comics_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNoComics() {
        showNoComicsViews(getResources().getString(R.string.no_comics_all));
    }

    private void showNoComicsViews(String mainText) {
        mComicsView.setVisibility(View.GONE);
        mNoComicsView.setVisibility(View.VISIBLE);
        mNoComicsMainView.setText(mainText);
    }

    @Override
    public void showNoComicsInBudget() {
        showNoComicsViews(getResources().getString(R.string.no_comics_in_budget));
    }

    @Override
    public void showBudgetFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_in_budget));
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_comics, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.in_budget:
                        mPresenter.setFiltering(ComicsFilterType.COMICS_IN_BUDGET);
                        break;
                    default:
                        mPresenter.setFiltering(ComicsFilterType.ALL_COMICS);
                        break;
                }
                mPresenter.loadComics(false);
                return true;
            }
        });

        popup.show();
    }

    @Override
    public void showTotalPagesNo() {
        String text = String.format(Locale.getDefault(), "Total pages no: %d", mPresenter.getPagesNo());
        mTotalPagesNoView.setText(text);
    }

    @Override
    public void showHideBudget(Boolean show) {
        mBudgetView.setVisibility(show?View.VISIBLE:View.INVISIBLE);
        if(show)
            mBudgetView.requestFocus();
    }

    //region ComicsAdapter.ComicsAdapterOnClickHandler implementation
    @Override
    public void onClick(Comic comic) {
        showComicDetailsUi(comic.getId());
    }
    //endregion
}
