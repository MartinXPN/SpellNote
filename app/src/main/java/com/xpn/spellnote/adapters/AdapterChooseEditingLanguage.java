package com.xpn.spellnote.adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpn.spellnote.R;


public class AdapterChooseEditingLanguage extends RecyclerView.Adapter<AdapterChooseEditingLanguage.ViewHolder> {


    Fragment parentFragment;
    ViewHolder holder;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClicked( int position );
    }

    public AdapterChooseEditingLanguage(Fragment parentFragment ) {
        this.parentFragment = parentFragment;
        this.listener = (OnItemClickListener) parentFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parentFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate( R.layout.language_grid_item, parent, false );

        holder = new ViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.wholeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked( holder.getAdapterPosition() );
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView languageFlag;
        TextView languageName;
        View wholeItem;

        ViewHolder( View v ) {
            super( v );
            languageFlag = (ImageView) v.findViewById( R.id.language_flag);
            languageName = (TextView) v.findViewById( R.id.language_name);
            wholeItem = v.findViewById( R.id.language_item );
        }
    }
}
