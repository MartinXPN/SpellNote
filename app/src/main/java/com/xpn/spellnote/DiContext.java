package com.xpn.spellnote;

import android.content.Context;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.all.DictionariesService;
import com.xpn.spellnote.services.dictionary.saved.SavedDictionaryService;
import com.xpn.spellnote.services.dictionary.saved.local.DictionaryMapper;
import com.xpn.spellnote.services.dictionary.saved.local.DictionarySchema;
import com.xpn.spellnote.services.dictionary.saved.local.LocalSavedDictionaryServiceImpl;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.services.document.local.DocumentMapper;
import com.xpn.spellnote.services.document.local.DocumentSchema;
import com.xpn.spellnote.services.document.local.LocalDocumentServiceImpl;
import com.xpn.spellnote.services.word.all.WordsService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Probably will be replaced with Dagger2
 */
public class DiContext {

    private static final String BASE_URL = "https://spellnote.herokuapp.com";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();

    private final DocumentService documentService;
    private final SavedDictionaryService savedDictionaryService;
    private final DictionariesService dictionariesService;
    private final WordsService wordsService;


    public DiContext(Context context) {

        // Mappers
        BeanMapper <DictionaryModel, DictionarySchema> dictionaryMapper = new DictionaryMapper();
        BeanMapper <DocumentModel, DocumentSchema> documentMapper = new DocumentMapper();

        // Local DB services
        documentService = new LocalDocumentServiceImpl(documentMapper);
        savedDictionaryService = new LocalSavedDictionaryServiceImpl(dictionaryMapper);

        // REST services
        dictionariesService = retrofit.create(DictionariesService.class);
        wordsService = retrofit.create(WordsService.class);
    }


    public DocumentService getDocumentService() {
        return documentService;
    }
    public SavedDictionaryService getSavedDictionaryService() {
        return savedDictionaryService;
    }
    public DictionariesService getDictionariesService() {
        return dictionariesService;
    }
    public WordsService getWordsService() {
        return wordsService;
    }
}
