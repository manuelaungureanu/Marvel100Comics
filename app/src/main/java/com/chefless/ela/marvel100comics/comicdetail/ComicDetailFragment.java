package com.chefless.ela.marvel100comics.comicdetail;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chefless.ela.marvel100comics.BasePresenter;
import com.chefless.ela.marvel100comics.R;
import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.data.models.ComicCreator;
import com.chefless.ela.marvel100comics.databinding.FragmentComicDetailBinding;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A placeholder fragment containing a simple view.
 */
public class ComicDetailFragment extends Fragment implements ComicDetailContract.View {

    @NonNull
    private static final String ARG_PARAM_COMIC_ID = "comicId";
    private String mComicId;
    private BasePresenter mPresenter;
    private FragmentComicDetailBinding mBinding;

    private TextView mDetailTitle;
    private TextView mDetailDescription;
    private ProgressBar mLoadingIndicator;
    private ListView mCreators;

    public static ComicDetailFragment newInstance(String comicId) {
        ComicDetailFragment fragment = new ComicDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_COMIC_ID, comicId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mComicId = getArguments().getString(ARG_PARAM_COMIC_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    public ComicDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentComicDetailBinding.inflate(inflater, container, false);
        initUI(mBinding);
        return mBinding.getRoot();
    }

    private void initUI(FragmentComicDetailBinding mBinding) {
        mLoadingIndicator = mBinding.pbLoadingData;
    }


    @Override
    public void setPresenter(BasePresenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        mLoadingIndicator.setVisibility(active?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void showMissingComic() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public void setModel(@NonNull Object model) {
        Object obj = checkNotNull(model);
        Comic comic = (Comic) obj;
        mBinding.setComic(comic);

        mDetailTitle = mBinding.tvName;
        mDetailDescription = mBinding.tvDescription;

        String mainImageUrl = comic.getMainImageUrl();
       if(!TextUtils.isEmpty(mainImageUrl))

        Glide.with(getActivity())
                .load(mainImageUrl)
                .thumbnail(0.1f)
                .into(mBinding.ivMainImage);
    }

    @BindingAdapter("bind:items")
    public  static void bindList(ListView view, ComicCreator.ComicCreatorItem[] arr) {
        ComicCreatorsAdapter adapter = new ComicCreatorsAdapter(arr);
        setListViewHeight(view, adapter);
        view.setAdapter(adapter);

    }

    //listView is inside a scrollview, we need to compute and set its height
    public static void setListViewHeight(ListView listView, ComicCreatorsAdapter adapter) {
        if (adapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
