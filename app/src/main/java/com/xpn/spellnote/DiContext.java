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
import com.xpn.spellnote.services.word.AddWordSuggestionService;
import com.xpn.spellnote.services.word.SavedWordsService;
import com.xpn.spellnote.services.word.SpellCheckerService;
import com.xpn.spellnote.services.word.SuggestionService;
import com.xpn.spellnote.services.word.local.SavedWordsServiceImpl;
import com.xpn.spellnote.services.word.local.SpellCheckerServiceImpl;
import com.xpn.spellnote.services.word.local.SuggestionServiceImpl;
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
    private final SpellCheckerService spellCheckerService;
    private final SuggestionService suggestionService;
    private final SavedWordsService savedWordsService;
    private final AddWordSuggestionService addWordSuggestionService;
    private final AvailableDictionariesService availableDictionariesService;


    DiContext(Context context) {

        // Mappers
        BeanMapper <DictionaryModel, DictionarySchema> dictionaryMapper = new DictionaryMapper();
        BeanMapper <DocumentModel, DocumentSchema> documentMapper = new DocumentMapper();
        BeanMapper <WordModel, WordSchema> wordMapper = new WordMapper();

        // Local DB services
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        documentService = new LocalDocumentServiceImpl(realmConfiguration, documentMapper);
        savedDictionaryService = new SavedDictionaryServiceImpl(realmConfiguration, dictionaryMapper);
        spellCheckerService = new SpellCheckerServiceImpl(wordMapper);
        suggestionService = new SuggestionServiceImpl(spellCheckerService, wordMapper);
        savedWordsService = new SavedWordsServiceImpl(wordMapper);

        // REST services
        addWordSuggestionService = retrofit.create(AddWordSuggestionService.class);
        availableDictionariesService = retrofit.create(AvailableDictionariesService.class);
    }


    public DocumentService getDocumentService() {
        return documentService;
    }
    public SavedDictionaryService getSavedDictionaryService() {
        return savedDictionaryService;
    }
    public SpellCheckerService getSpellCheckerService() {
        return spellCheckerService;
    }
    public SuggestionService getSuggestionService() {
        return suggestionService;
    }
    public SavedWordsService getSavedWordsService() {
        return savedWordsService;
    }
    public AddWordSuggestionService getAddWordSuggestionService() {
        return addWordSuggestionService;
    }
    public AvailableDictionariesService getAvailableDictionariesService() {
        return availableDictionariesService;
    }
}
