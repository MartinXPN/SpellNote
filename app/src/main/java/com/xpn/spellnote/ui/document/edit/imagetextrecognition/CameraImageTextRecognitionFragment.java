package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.xpn.spellnote.GlideApp;
import com.xpn.spellnote.GlideRequest;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.FragmentCameraImageTextRecognitionBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.util.image.CompressTransformation;
import com.xpn.spellnote.ui.util.image.RotateTransformation;
import com.xpn.spellnote.util.Util;

import java.util.Arrays;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.log.LoggersKt;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.selector.FlashSelectorsKt;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import timber.log.Timber;


public class CameraImageTextRecognitionFragment extends Fragment implements CameraVM.ViewContract {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    public static final int PICK_IMAGE_REQUEST_CODE = 1007;
    private int currentTextRecognitionTaskId = 1;

    private TextRecognitionContract contract;
    private FragmentCameraImageTextRecognitionBinding binding;
    private CameraVM viewModel;
    private Fotoapparat camera;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Timber.d("Permission not granted");
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_image_text_recognition, container, false);
        contract = (TextRecognitionContract) getActivity();
        viewModel = new CameraVM(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        assert getActivity() != null;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        if( camera == null )
            camera = Fotoapparat.with(getActivity())
                .into(binding.camera)
                .previewScaleType(ScaleType.CenterInside)
                .photoResolution(ResolutionSelectorsKt.highestResolution())
                .lensPosition(LensPositionSelectorsKt.back())
                // Use the first focus mode which is supported by device
                .focusMode(SelectorsKt.firstAvailable(
                        FocusModeSelectorsKt.continuousFocusPicture(),
                        FocusModeSelectorsKt.autoFocus(),
                        FocusModeSelectorsKt.fixed()
                ))
                // Similar to how it is done for focus mode, this time for flash
                .flash(SelectorsKt.firstAvailable(
                        FlashSelectorsKt.off(),
                        FlashSelectorsKt.autoRedEye(),
                        FlashSelectorsKt.autoFlash(),
                        FlashSelectorsKt.torch()
                ))
                .logger(LoggersKt.logcat())
                .build();

        camera.start();
    }

    @Override
    public void onStop() {
        if( camera != null )
            camera.stop();
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

    private void setImage(Bitmap image, int rotation) {
        setImage(
                GlideApp.with(this)
                        .asBitmap()
                        .load(image)
                        .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(
                                new RotateTransformation(rotation),
                                new CompressTransformation(0.3)
                        )))
        );
    }

    private void setImage(Uri uri) {
        Timber.d("Load image: %s", uri.toString() );
        setImage(
                GlideApp.with(this)
                        .asBitmap()
                        .load(uri)
                        .transform(new CompressTransformation(0.3))
        );
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
        if( camera == null ) {
            Toast.makeText(getActivity(), R.string.error_no_permission_granted, Toast.LENGTH_SHORT).show();
            contract.onCloseTextRecognizer();
            return;
        }
        camera.takePicture().toBitmap().whenAvailable(bitmapPhoto -> {
            if(bitmapPhoto != null )
                setImage(bitmapPhoto.bitmap, -bitmapPhoto.rotationDegrees);
            else
                viewModel.onFailure();
            return null;
        });
    }

    @Override
    public void onRecognizeText(Bitmap picture) {
        FirebaseVisionCloudDocumentRecognizerOptions options = new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList(contract.getCurrentDictionary().getLocale()))
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

    public void onCancelPreviousRecognitionTasks() {
        binding.graphicOverlay.clear();
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
