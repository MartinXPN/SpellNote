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
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.FragmentCameraImageTextRecognitionBinding;
import com.xpn.spellnote.models.DictionaryModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public class CameraImageTextRecognitionFragment extends Fragment implements CameraVM.ViewContract {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    public static final int PICK_IMAGE = 1007;

    private TextRecognitionContract contract;
    private FragmentCameraImageTextRecognitionBinding binding;
    private CameraVM viewModel;


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

        binding.camera.addCallback(new CameraView.Callback() {

            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                ExifInterface exifInterface;
                try {
                    exifInterface = new ExifInterface(new ByteArrayInputStream(data));
                } catch (IOException e) {
                    Toast.makeText(getActivity(), R.string.error_something_wrong, Toast.LENGTH_SHORT).show();
                    Timber.e(e, "Couldn\'t display the image");
                    return;
                }
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_NORMAL:          break;
                    case ExifInterface.ORIENTATION_ROTATE_180:      matrix.setRotate(180);          break;
                    case ExifInterface.ORIENTATION_ROTATE_90:       matrix.setRotate(90);           break;
                    case ExifInterface.ORIENTATION_ROTATE_270:      matrix.setRotate(-90);          break;
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: matrix.setScale(-1, 1); break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:   matrix.setRotate(180);  matrix.postScale(-1, 1);    break;
                    case ExifInterface.ORIENTATION_TRANSPOSE:       matrix.setRotate(90);   matrix.postScale(-1, 1);    break;
                    case ExifInterface.ORIENTATION_TRANSVERSE:      matrix.setRotate(-90);  matrix.postScale(-1, 1);    break;
                    default:    break;
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                /// compress image for faster performance
                double originalImageSizeMB = data.length / 1e6;
                double targetImageSizeMB = 0.2;
                int quality = 100 - (int) (100. * originalImageSizeMB / targetImageSizeMB);
                quality = Math.min(quality, 90);
                quality = Math.max(quality, 20);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                byte[] byteArray = stream.toByteArray();
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Timber.d("Length changed from %d to %d with quality %d", data.length, byteArray.length, quality);
                Timber.d("Original image size: %fMB", originalImageSizeMB);
                Timber.d("Compressed image size: %fMB", byteArray.length / 1e6);

                Bitmap rotatedBitmap = Bitmap.createBitmap(compressedBitmap, 0, 0, compressedBitmap.getWidth(), compressedBitmap.getHeight(), matrix, true);
                viewModel.onCaptured(rotatedBitmap);
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
    public void onResume() {
        super.onResume();
        assert getActivity() != null;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.camera.start();
            for(AspectRatio ratio : binding.camera.getSupportedAspectRatios()) {
                Timber.d(ratio.toString());
            }
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onStop() {
        binding.camera.stop();
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
        binding.graphicOverlay.clear();
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
        binding.camera.takePicture();
    }

    @Override
    public void onRecognizeText(Bitmap picture) {
        FirebaseVisionCloudDocumentRecognizerOptions options = new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                        .setLanguageHints(Collections.singletonList(contract.getCurrentDictionary().getLocale()))
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
