package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.perf.metrics.AddTrace;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.xpn.spellnote.GlideApp;
import com.xpn.spellnote.GlideRequest;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.FragmentCameraImageTextRecognitionBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.util.CameraInfoUtil;
import com.xpn.spellnote.ui.util.image.CompressTransformation;
import com.xpn.spellnote.ui.util.image.RotateTransformation;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class CameraImageTextRecognitionFragment extends Fragment implements CameraVM.ViewContract {

    public static final int PICK_IMAGE_REQUEST_CODE = 1007;
    private int currentTextRecognitionTaskId = 1;

    private TextRecognitionContract contract;
    private FragmentCameraImageTextRecognitionBinding binding;
    private CameraVM viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_image_text_recognition, container, false);
        contract = (TextRecognitionContract) getActivity();
        viewModel = new CameraVM(this);
        binding.setViewModel(viewModel);

        binding.camera.setLifecycleOwner(getViewLifecycleOwner());
        binding.camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        binding.camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);
        binding.camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                setImage(picture, CameraInfoUtil.getRotation(picture));
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Override
    public void onStop() {
        viewModel.onDestroy();
        super.onStop();
    }

    private void setImage(GlideRequest <Bitmap> request) {
        request.placeholder(R.drawable.rectangle_transparent)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Timber.e(e);
                        viewModel.onFailure();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        viewModel.onImageReady(resource);
                        return false;
                    }
                })
                .into(binding.image);
    }

    private void setImage(byte[] data, int rotation) {
        setImage(GlideApp.with(this)
                        .asBitmap()
                        .load(data)
                        .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(
                                new RotateTransformation(rotation),
                                new CompressTransformation(0.3)
                        ))));
    }

    private void setImage(Uri uri) {
        Timber.d("Load image: %s", uri.toString() );
        setImage(GlideApp.with(this)
                        .asBitmap()
                        .load(uri)
                        .transform(new CompressTransformation(0.3)));
    }


    @AddTrace(name = "processCloudTextRecognitionResult")
    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        binding.graphicOverlay.process(text, binding.image);

        // Task completed successfully
        if (text == null) {
            viewModel.onTextRecognized("");
            Toast.makeText(getActivity(), R.string.error_no_text_found, Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.onTextRecognized(text.getText());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert getActivity() != null;

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                viewModel.onFailure();
                Toast.makeText(getActivity(), "No image selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            setImage(data.getData());
        }
    }

    @Override
    public void onChooseFromGallery() {
        Util.chooseImageFromGallery(this, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    @AddTrace(name = "onCaptureImage")
    public void onCaptureImage() {
        binding.camera.captureSnapshot();
    }

    @Override
    public void onRecognizeText(Bitmap picture) {
        List<String> languageHints = new ArrayList<>();
        languageHints.add(contract.getCurrentDictionary().getLocale());

        FirebaseVisionCloudDocumentRecognizerOptions options = new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                        .setLanguageHints(languageHints)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(picture);
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance().getCloudDocumentTextRecognizer(options);

        /// fix current id
        onCancelPreviousRecognitionTasks();
        final int currentTaskId = currentTextRecognitionTaskId;
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionDocumentText -> {
                    if( currentTaskId == currentTextRecognitionTaskId ) {
                        processCloudTextRecognitionResult(firebaseVisionDocumentText);
                    }
                })
                .addOnFailureListener(e -> {
                    Timber.e(e);
                    viewModel.onFailure();
                    Toast.makeText(getContext(), R.string.error_something_wrong, Toast.LENGTH_SHORT).show();
                });
    }

    public void clearImage() {
        binding.graphicOverlay.clear();
        GlideApp.with(this)
                .load(R.drawable.rectangle_transparent)
                .into(binding.image);
    }

    public void onCancelPreviousRecognitionTasks() {
        ++currentTextRecognitionTaskId;
    }

    @Override
    public void onDelegateRecognizedText(String text) {
        contract.onTextRecognized(text);
    }

    @Override
    public void onClose() {
        contract.onCloseTextRecognizer();
    }

    public interface TextRecognitionContract {
        void onTextRecognized(String text);
        void onCloseTextRecognizer();
        DictionaryModel getCurrentDictionary();
    }
}
