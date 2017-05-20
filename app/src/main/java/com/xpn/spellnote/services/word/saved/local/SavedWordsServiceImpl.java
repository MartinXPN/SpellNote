package com.xpn.spellnote.services.word.saved.local;

import com.activeandroid.ActiveAndroid;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.saved.SavedWordsService;

import java.util.ArrayList;

import io.reactivex.Completable;
import timber.log.Timber;


public class SavedWordsServiceImpl implements SavedWordsService {

    private final BeanMapper<WordModel, WordSchema> mapper;

    public SavedWordsServiceImpl(BeanMapper<WordModel, WordSchema> mapper) {
        this.mapper = mapper;
    }


    @Override
    public Completable saveAllWords(ArrayList<WordModel> words) {
        return Completable.fromAction(() -> {
            ActiveAndroid.beginTransaction();
            try {
                int i = 0;
                for (WordModel word : words ) {
                    WordSchema schema = mapper.mapTo(word);
                    schema.save();
                    if( ++i % 1000 == 0 ) {
                        Timber.d("Saved 1000 words");
                    }
                }
                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();
            }
        });
    }
}
