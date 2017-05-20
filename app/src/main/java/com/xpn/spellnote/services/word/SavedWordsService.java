package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import java.util.ArrayList;

import io.reactivex.Completable;


public interface SavedWordsService {

    Completable saveAllWords(ArrayList <WordModel> words);
}
