package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.FragmentCameraImageTextRecognitionBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.util.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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
    public static final int PICK_IMAGE = 1007;

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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onStop() {
        if( camera != null )
            camera.stop();
        viewModel.onDestroy();
        super.onStop();
    }


    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {
            viewModel.onTextRecognized("");
            Toast.makeText(getActivity(), R.string.error_no_text_found, Toast.LENGTH_SHORT).show();
            return;
        }

        // clear the graphic overlay
        binding.graphicOverlay.clear();
        int[] coordinates = ImageUtil.getBitmapPositionInsideImageView(binding.image);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(coordinates[0], coordinates[1], coordinates[0], coordinates[1]);
        binding.graphicOverlay.setLayoutParams(params);
        Timber.d("left: %d, top: %d, right: %d, bottom: %d", coordinates[0], coordinates[1], coordinates[2], coordinates[3]);


        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(binding.graphicOverlay, words.get(l));
                    binding.graphicOverlay.add(cloudDocumentTextGraphic);
                }
            }
        }
        viewModel.onTextRecognized(text.getText());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert getActivity() != null;

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                Toast.makeText(getActivity(), "No image selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
            }
            catch (FileNotFoundException e) {
                Toast.makeText(getActivity(), R.string.error_something_wrong, Toast.LENGTH_SHORT).show();
                Timber.e(e, "Image corrupted");
            }
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            viewModel.onCaptured(image);
        }
    }

    @Override
    public void onChooseFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.hint_choose_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onCaptureImage() {
        if( camera == null ) {
            Toast.makeText(getActivity(), R.string.error_no_permission_granted, Toast.LENGTH_SHORT).show();
            contract.onCloseTextRecognizer();
            return;
        }
        camera.takePicture().toBitmap().whenAvailable(bitmapPhoto -> {
            if(bitmapPhoto == null ) {
                Toast.makeText(getActivity(), R.string.error_something_wrong, Toast.LENGTH_SHORT).show();
                return null;
            }
            Matrix matrix = new Matrix();
            matrix.setRotate(-bitmapPhoto.rotationDegrees);

            /// compress image for faster performance
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            double originalImageSizeMB = bitmapPhoto.bitmap.getByteCount() / 1e6;
            double targetImageSizeMB = 0.3;
            int quality = 100 - (int) (100. * originalImageSizeMB / targetImageSizeMB);
            quality = Math.min(quality, 95);
            quality = Math.max(quality, 20);

            bitmapPhoto.bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Timber.d("Length changed from %d to %d with quality %d", bitmapPhoto.bitmap.getByteCount(), byteArray.length, quality);
            Timber.d("Original image size: %fMB", originalImageSizeMB);
            Timber.d("Compressed image size: %fMB", byteArray.length / 1e6);

            Bitmap rotatedBitmap = Bitmap.createBitmap(compressedBitmap, 0, 0, compressedBitmap.getWidth(), compressedBitmap.getHeight(), matrix, true);
            viewModel.onCaptured(rotatedBitmap);
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

        detector.processImage(image)
                .addOnSuccessListener(this::processCloudTextRecognitionResult)
                .addOnFailureListener(Timber::e);
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
