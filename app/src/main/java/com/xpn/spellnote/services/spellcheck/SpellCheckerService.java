package com.xpn.spellnote.services.spellcheck;

import java.util.List;
import java.util.Map;


public interface SpellCheckerService {

    boolean isWordCorrect(String word, String locale);
    Map <String, Boolean> areWordsCorrect(List <String> words, String locale);
}
