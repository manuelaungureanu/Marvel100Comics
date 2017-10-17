package com.chefless.ela.marvel100comics.comicdetail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.chefless.ela.marvel100comics.data.models.ComicCreator;
import com.chefless.ela.marvel100comics.databinding.ComicsListItemBinding;
import com.chefless.ela.marvel100comics.databinding.CreatorListItemBinding;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ela on 17/10/2017.
 */

public class ComicCreatorsAdapter<ComicCreatorItem> extends BaseAdapter {

    private ComicCreator.ComicCreatorItem[] mSource;
    public ComicCreatorsAdapter(ComicCreator.ComicCreatorItem[] creatorItems) {
        this.mSource = creatorItems;
    }

    @Override
    public int getCount() {
        return mSource==null ? 0 : mSource.length;
    }

    @Override
    public Object getItem(int position) {
        return mSource[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CreatorListItemBinding binding = CreatorListItemBinding.inflate(inflater, parent, false);
        binding.setItem(mSource[position]);
        return binding.getRoot();
    }
}
