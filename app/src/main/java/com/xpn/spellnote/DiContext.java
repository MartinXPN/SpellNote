package com.xpn.spellnote;

import android.content.Context;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.AvailableDictionariesService;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;
import com.xpn.spellnote.services.dictionary.local.DictionaryMapper;
import com.xpn.spellnote.services.dictionary.local.DictionarySchema;
import com.xpn.spellnote.services.dictionary.local.SavedDictionaryServiceImpl;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.services.document.local.DocumentMapper;
import com.xpn.spellnote.services.document.local.DocumentSchema;
import com.xpn.spellnote.services.document.local.LocalDocumentServiceImpl;
import com.xpn.spellnote.services.word.SavedWordsService;
import com.xpn.spellnote.services.word.WordsService;
import com.xpn.spellnote.services.word.local.SavedWordsServiceImpl;
import com.xpn.spellnote.services.word.local.WordMapper;
import com.xpn.spellnote.services.word.local.WordSchema;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Probably will be replaced with Dagger2
 */
public class DiContext {

    private static final String BASE_URL = "https://spellnote-83543.firebaseio.com";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();

    private final DocumentService documentService;
    private final SavedDictionaryService savedDictionaryService;
    private final AvailableDictionariesService availableDictionariesService;
    private final WordsService wordsService;
    private final SavedWordsService savedWordsService;


    DiContext(Context context) {

        // Mappers
        BeanMapper <DictionaryModel, DictionarySchema> dictionaryMapper = new DictionaryMapper();
        BeanMapper <DocumentModel, DocumentSchema> documentMapper = new DocumentMapper();
        BeanMapper <WordModel, WordSchema> wordMapper = new WordMapper();

        // Local DB services
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        documentService = new LocalDocumentServiceImpl(realmConfiguration, documentMapper);
        savedDictionaryService = new SavedDictionaryServiceImpl(realmConfiguration, dictionaryMapper, wordMapper);
        savedWordsService = new SavedWordsServiceImpl(realmConfiguration, wordMapper);

        // REST services
        availableDictionariesService = retrofit.create(AvailableDictionariesService.class);
        wordsService = retrofit.create(WordsService.class);
    }


    public DocumentService getDocumentService() {
        return documentService;
    }
    public SavedDictionaryService getSavedDictionaryService() {
        return savedDictionaryService;
    }
    public AvailableDictionariesService getAvailableDictionariesService() {
        return availableDictionariesService;
    }
    public WordsService getWordsService() {
        return wordsService;
    }
    public SavedWordsService getSavedWordsService() {
        return savedWordsService;
    }
}
