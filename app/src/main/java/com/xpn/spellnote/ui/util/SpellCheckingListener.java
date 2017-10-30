package com.xpn.spellnote.ui.util;


import java.util.List;

public interface SpellCheckingListener {

    void markIncorrect(int left, int right, List<String> incorrectWords);
    void markCorrect(int left, int right, List<String> correctWords);
}
