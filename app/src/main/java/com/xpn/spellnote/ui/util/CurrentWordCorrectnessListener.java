package com.xpn.spellnote.ui.util;


public interface CurrentWordCorrectnessListener {

    void onCurrentWordIsCorrect(String word);
    void onCurrentWordIsWrong(String word);
    void onMultipleWordsSelected();
}
