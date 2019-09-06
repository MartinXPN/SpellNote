package com.xpn.spellnote.ui;

import androidx.databinding.BaseObservable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseViewModel extends BaseObservable implements ViewModelLifecycle {

    protected CompositeDisposable subscriptions = new CompositeDisposable();

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
    }

    protected void addSubscription(Disposable subscription) {
        subscriptions.add(subscription);
    }
}
