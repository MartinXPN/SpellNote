package com.xpn.spellnote.ui.document.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.snackbar.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.xpn.spellnote.BR;
import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ItemDocumentListBinding;
import com.xpn.spellnote.databinding.NativeAdBinding;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.ads.NativeAdHelper;
import com.xpn.spellnote.ui.ads.RemoveAdsBilling;
import com.xpn.spellnote.ui.document.edit.ActivityEditDocument;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.ui.util.BindingHolder;
import com.xpn.spellnote.ui.util.tutorial.Tutorial;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseFragmentDocumentList extends BaseSortableFragment
        implements DocumentListItemVM.ViewContract, DocumentListVM.ViewContract, NativeAdHelper.OnAdDisplayListener {

    protected static final int EDIT_DOCUMENT_CODE = 123;

    protected DocumentListVM viewModel;
    protected ArrayList<DocumentModel> documentList = new ArrayList<>();
    protected DocumentListAdapter adapter = new DocumentListAdapter();
    protected DocumentContract contract;
    protected NativeAdHelper adHelper;

    public abstract String getTitle();
    public abstract DocumentListItemVM getListItemVM(DocumentModel model, DocumentListItemVM.ViewContract viewContract);
    public abstract void onShowEmptyLogo();
    public abstract void onHideEmptyLogo();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu( true );

        DiContext diContext = ((SpellNoteApp) getActivity().getApplication()).getDiContext();
        viewModel = new DocumentListVM(this, diContext.getDocumentService());
        contract = (DocumentContract) getActivity();

        // setup ads
        RemoveAdsBilling billing = new RemoveAdsBilling(null, getActivity(), getString(R.string.license_key), getString(R.string.remove_ads_id));
        adHelper = new NativeAdHelper(getActivity(), billing, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        contract.setTitle(getTitle());
        viewModel.onStart();
        updateDocumentList();
        adHelper.loadAd();
    }

    @Override
    public void onDestroy() {
        viewModel.onDestroy();
        adHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == EDIT_DOCUMENT_CODE ) {
            updateDocumentList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem( R.id.action_sort_ascending ).setChecked( getAscending() );
        String sortingOrder = getSortingOrder();
        if( sortingOrder.equals( TagsUtil.ORDER_DATE_MODIFIED ) )   menu.findItem( R.id.action_sort_by_date_modified ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_TITLE ) )           menu.findItem( R.id.action_sort_by_title ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_LANGUAGE) )         menu.findItem( R.id.action_sort_by_language ).setChecked( true );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.action_sort_by_date_modified )   { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_DATE_MODIFIED);    return true; }
        if( id == R.id.action_sort_by_title )           { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_TITLE);            return true; }
        if( id == R.id.action_sort_by_language )        { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_LANGUAGE);          return true; }

        if( id == R.id.action_sort_ascending )          { item.setChecked( !item.isChecked() );  setAscending( !getAscending() );     return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateDocumentList() {
        viewModel.loadDocuments(getCategory(), getSortingOrder(), getAscending());
    }


    @Override
    public void onRemoveDocumentFromShownList(DocumentModel document ) {
        int documentIndex = documentList.indexOf( document );
        documentList.remove( document );
        adapter.notifyItemRemoved( documentIndex );
    }

    @Override
    public void onShowUndoOption(DocumentModel previousDocument, @StringRes int messageResourceId) {
        /// Show UNDO snack-bar
        if( getView() == null )
            return;
        Snackbar.make( getView(), messageResourceId, Snackbar.LENGTH_LONG )
                .setAction( R.string.undo, view -> {
                    viewModel.restoreDocument(previousDocument);
                    updateDocumentList();
                }).show();
    }

    @Override
    public void onEditDocument(Long documentId) {
        ActivityEditDocument.launchForResult(this, documentId, EDIT_DOCUMENT_CODE);
    }

    @Override
    public void onShowExplanation(@StringRes int messageResourceId) {
        Toast.makeText(getActivity(), getString(messageResourceId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendDocument(String title, String content) {
        Util.sendDocument( getActivity(), title, content );
    }

    @Override
    public void onDocumentsAvailable(List<DocumentModel> documents) {
        documentList = new ArrayList<>(documents);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAdReady(UnifiedNativeAd ad) {
        adapter.showAd();
    }

    @Override
    public void onHideAd(UnifiedNativeAd ad) {
        adapter.hideAd();
    }

    private class DocumentListAdapter extends RecyclerSwipeAdapter<BindingHolder> {
        private static final int MIN_ITEM_COUNT_FOR_TUTORIAL = 1;
        private static final int ADVERTISEMENT_LIST_ITEM_ID = 3;

        private static final int LIST_ITEM_TYPE_DOCUMENT = 0;
        private static final int LIST_ITEM_TYPE_ADVERTISEMENT = 1;

        private boolean isAdShown = false;

        private boolean shouldAdBeShown(int position) {
            return adHelper.getAd() != null
                    && position == ADVERTISEMENT_LIST_ITEM_ID
                    && documentList.size() > ADVERTISEMENT_LIST_ITEM_ID
                    && ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
        }
        void showAd() {
            if( !shouldAdBeShown(ADVERTISEMENT_LIST_ITEM_ID) )
                return;

            if( !isAdShown)
                notifyItemInserted(DocumentListAdapter.ADVERTISEMENT_LIST_ITEM_ID);
            isAdShown = true;
        }
        void hideAd() {
            if(isAdShown)
                notifyItemRemoved(DocumentListAdapter.ADVERTISEMENT_LIST_ITEM_ID);
            isAdShown = false;
        }

        @NonNull
        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if( viewType == LIST_ITEM_TYPE_ADVERTISEMENT ) {
                View v = LayoutInflater.from( parent.getContext() ).inflate(R.layout.native_ad, parent, false);
                return new BindingHolder(v);
            }
            View v = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_document_list, parent, false);
            return new BindingHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final BindingHolder holder, int position) {
            if( shouldAdBeShown(position) ) {
                adHelper.populate((NativeAdBinding) holder.getBinding());
                return;
            }

            int documentItemPosition = isAdShown && position >= ADVERTISEMENT_LIST_ITEM_ID ? position - 1 : position;
            holder.getBinding().setVariable(BR.viewModel, getListItemVM( documentList.get( documentItemPosition ), BaseFragmentDocumentList.this));
            holder.getBinding().executePendingBindings();


            mItemManger.bindView(holder.itemView, position);
            ((ItemDocumentListBinding)holder.getBinding()).swipe.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override public void onStartOpen(SwipeLayout layout) {
                    closeAllExcept( layout );
                }
                @Override public void onOpen(SwipeLayout layout) {
                    if(swipeTutorial != null && swipeTutorial.isShowing())
                        swipeTutorial.setDisplayed();
                }
                @Override public void onStartClose(SwipeLayout layout) {}
                @Override public void onClose(SwipeLayout layout) {}
                @Override public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {}
                @Override public void onHandRelease(SwipeLayout layout, float xVel, float yVel) {}
            });


            /// show swipe tutorial if position = MIN_ITEM_COUNT_FOR_TUTORIAL
            if( position == MIN_ITEM_COUNT_FOR_TUTORIAL ) {
                showSwipeTutorial(holder.itemView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if( shouldAdBeShown(position) )
                return LIST_ITEM_TYPE_ADVERTISEMENT;
            return LIST_ITEM_TYPE_DOCUMENT;
        }

        @Override
        public int getItemCount() {
            if( documentList.isEmpty() )    onShowEmptyLogo();
            else                            onHideEmptyLogo();

            int add = isAdShown ? 1 : 0;
            return documentList.size() + add;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }
    }

    Tutorial swipeTutorial = null;
    public void showSwipeTutorial(View target) {
        if(swipeTutorial == null)
            swipeTutorial = new Tutorial(getActivity(), "swipe_tutorial", R.string.tutorial_swipe, Gravity.BOTTOM)
                .setTarget(target);
        swipeTutorial.showTutorial();
    }


    public interface DocumentContract {
        void setTitle(String title);
    }
}
