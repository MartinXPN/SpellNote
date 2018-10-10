package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.databinding.Bindable;
import android.graphics.Bitmap;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.ui.BaseViewModel;

import timber.log.Timber;


public class CameraVM extends BaseViewModel {

    private final ViewContract viewContract;
    private State state = State.CAPTURING;
    private Bitmap currentImage;

    CameraVM(ViewContract viewContract) {
        this.viewContract = viewContract;
    }

    @Bindable
    public State getState() {
        return this.state;
    }
    private void setState(State state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
    }

    @Bindable
    public Bitmap getCurrentImage() {
        return currentImage;
    }
    private void setCurrentImage(Bitmap image) {
        this.currentImage = image;
        notifyPropertyChanged(BR.currentImage);
    }

    public void captureImage() {
        viewContract.onCaptureImage();
        setState(State.PROCESSING_TEXT);
    }

     void onCaptured(Bitmap image) {
        setCurrentImage(image);
        viewContract.onRecognizeText(image);
     }


    public enum State {
        CAPTURING,          /// waiting for the user to capture an image
        PROCESSING_TEXT,    /// waiting for the server to extract text from the image
        DONE                /// retrieved the text from the server
    }

    public interface ViewContract {
        void onCaptureImage();
        void onRecognizeText(Bitmap picture);
    }
}
