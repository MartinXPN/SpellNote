package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.databinding.Bindable;
import android.graphics.Bitmap;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.ui.BaseViewModel;


public class CameraVM extends BaseViewModel {

    private final ViewContract view;
    private State state = State.CAPTURING;
    private String recognizedText;

    CameraVM(ViewContract viewContract) {
        this.view = viewContract;
    }

    @Bindable
    public State getState() {
        return this.state;
    }
    private void setState(State state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
    }


    public void onFailure() {
        setState(State.CAPTURING);
    }

    public void chooseFromGallery() {
        view.onCancelPreviousRecognitionTasks();
        view.onChooseFromGallery();
    }

    public void captureImage() {
        view.onCaptureImage();
        setState(State.PROCESSING_TEXT);
    }

     void onImageReady(Bitmap image) {
        setState(State.PROCESSING_TEXT);
        view.onRecognizeText(image);
     }

     void onTextRecognized(String text) {
        recognizedText = text;
        setState(State.DONE);
     }

     public void onRetakeImage() {
        view.onCancelPreviousRecognitionTasks();
        setState(State.CAPTURING);
     }

     public void onDone() {
        view.onDelegateRecognizedText(this.recognizedText);
        view.onClose();
     }

     public void onClose() {
         view.onCancelPreviousRecognitionTasks();
         view.onClose();
     }


    public enum State {
        CAPTURING,          /// waiting for the user to capture an image
        PROCESSING_TEXT,    /// waiting for the server to extract text from the image
        DONE                /// retrieved the text from the server
    }

    public interface ViewContract {
        void onChooseFromGallery();
        void onCaptureImage();
        void onRecognizeText(Bitmap picture);
        void onDelegateRecognizedText(String text);
        void onClose();
        void onCancelPreviousRecognitionTasks();
    }
}
