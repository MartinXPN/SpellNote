package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import java.util.List;

import io.reactivex.Single;


public interface SavedWordsService {

    Single<List<WordModel>> getUserDefinedWords(String locale);
}
