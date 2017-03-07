package com.xpn.spellnote.services;


public interface BeanMapper <A, B> {

    A mapFrom(B source);
    B mapTo(A source);
}
