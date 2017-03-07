package com.xpn.spellnote.ui.document.list;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.xpn.spellnote.BR;
import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.BindingHolder;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;

import java.util.ArrayList;


public abstract class BaseFragmentDocumentList extends BaseSortableFragment
        implements DocumentListItemVM.ViewContract {

    protected ArrayList<DocumentModel> documentList = new ArrayList<>();
    protected DocumentListAdapter adapter = new DocumentListAdapter();

    /// empty public constructor ( documentation-required )
    public BaseFragmentDocumentList() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu( true );

        documentList = new ArrayList<>(); //(ArrayList<DocumentModel>) CreatedDocuments.getAllDocuments( getCategory(), getSortingOrder(), getAscending() );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Codes.EDIT_DOCUMENT_CODE ) {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_view_documents, menu );

        menu.findItem( R.id.action_sort_ascending ).setChecked( getAscending() );
        String sortingOrder = getSortingOrder();
        if( sortingOrder.equals( TagsUtil.ORDER_DATE_MODIFIED ) )   menu.findItem( R.id.action_sort_by_date_modified ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_TITLE ) )           menu.findItem( R.id.action_sort_by_title ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_LANUAGE ) )         menu.findItem( R.id.action_sort_by_language ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_COLOR ) )           menu.findItem( R.id.action_sort_by_color ).setChecked( true );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.action_sort_by_date_modified )   { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_DATE_MODIFIED);    return true; }
        if( id == R.id.action_sort_by_title )           { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_TITLE);            return true; }
        if( id == R.id.action_sort_by_language )        { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_LANUAGE);          return true; }
        if( id == R.id.action_sort_by_color )           { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_COLOR);            return true; }

        if( id == R.id.action_sort_ascending )          { item.setChecked( !item.isChecked() );  setAscending( !getAscending() );     return true; }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateDocumentList() {
        documentList = new ArrayList<>(); //(ArrayList<DocumentModel>) CreatedDocuments.getAllDocuments( getCategory(), getSortingOrder(), getAscending() );
        adapter.notifyDataSetChanged();
    }


    public void onRemoveDocumentFromShownList(DocumentModel document ) {
        int documentIndex = documentList.indexOf( document );
        documentList.remove( document );
        adapter.notifyItemRemoved( documentIndex );
    }

    @Override
    public void onPrepareDocumentToMove(DocumentModel document) {
        onRemoveDocumentFromShownList( document );
    }


    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }


    protected class DocumentListAdapter extends RecyclerSwipeAdapter<BindingHolder> {

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_document_list, parent, false);
            return new BindingHolder(v);
        }

        @Override
        public void onBindViewHolder(final BindingHolder holder, int position) {
            final DocumentListItemVM documentViewModel = new DocumentListItemVM( documentList.get( position ), BaseFragmentDocumentList.this.getActivity(), BaseFragmentDocumentList.this);
            holder.getBinding().setVariable(BR.document, documentViewModel);
            holder.getBinding().executePendingBindings();


            mItemManger.bindView(holder.itemView, position);
            ((SwipeLayout)holder.getBinding().getRoot().findViewById(R.id.swipe)).addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override public void onStartOpen(SwipeLayout layout) {
                    closeAllExcept( layout );
                }
                @Override public void onOpen(SwipeLayout layout) {}
                @Override public void onStartClose(SwipeLayout layout) {}
                @Override public void onClose(SwipeLayout layout) {}
                @Override public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {}
                @Override public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {}
            });
        }

        @Override
        public int getItemCount() {
            return documentList.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }
    }
}
