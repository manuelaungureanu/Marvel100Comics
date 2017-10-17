package com.chefless.ela.marvel100comics.comics;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;


import com.bumptech.glide.Glide;
import com.chefless.ela.marvel100comics.data.models.Comic;
import com.chefless.ela.marvel100comics.databinding.ComicsListItemBinding;

import java.util.List;

/**
 * Created by ela on 26/09/2017.
 */

public class ComicsAdapter extends RecyclerView.Adapter<ComicsAdapter.ComicAdapterViewHolder>
{
    private List<Comic> mData;

    /** An on click handler added for the activity for interact with the recycler viw*/
    private final ComicsAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ComicsAdapterOnClickHandler {
        void onClick(Comic comic);
    }

    public ComicsAdapter(ComicsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ComicAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ComicsListItemBinding binding = ComicsListItemBinding.inflate(inflater, parent, false);
        return new ComicAdapterViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ComicAdapterViewHolder holder, int position) {
        Comic item = mData.get(position);
        holder.binding.setItem(item);
        holder.binding.setPosition(position+1);

        if(item==null)
            return;

        String url = String.format("%s.%s", item.getThumbnail().getPath(),item.getThumbnail().getExtension());
        if(!URLUtil.isValidUrl(url))
            return;

        Glide.with(holder.itemView.getContext())
                .load(url)
                .thumbnail(0.1f)
                .into(holder.binding.ivThumb);
    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    public void setData(List<Comic> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public class ComicAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ComicsListItemBinding binding;

        public ComicAdapterViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Comic comic = mData.get(position);
            mClickHandler.onClick(comic);
        }
    }
}
