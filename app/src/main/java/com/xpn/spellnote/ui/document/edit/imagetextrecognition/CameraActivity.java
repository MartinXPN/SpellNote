package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.ActivityCameraBinding;

import java.util.List;


public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION_ID = 1001;

    private ActivityCameraBinding binding;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_ID:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Can\'t use camera without the permission", Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        binding.camera.setLifecycleOwner(this);
        binding.capture.setOnClickListener(v -> binding.camera.capturePicture());

        binding.camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                // Create a bitmap or a file...
                // CameraUtils will read EXIF orientation for you, in a worker thread.
                CameraUtils.decodeBitmap(picture, bitmap -> runCloudTextRecognition(bitmap));
            }
        });
    }

    private void runCloudTextRecognition(Bitmap mSelectedImage) {
        binding.capture.setEnabled(false);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        texts -> {
                            binding.capture.setEnabled(true);
                            processCloudTextRecognitionResult(texts);
                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                            binding.capture.setEnabled(true);
                            e.printStackTrace();
                        });
    }

    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {
            Toast.makeText(getApplicationContext(), "No text found", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.graphicOverlay.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(binding.graphicOverlay,
                            words.get(l));
                    binding.graphicOverlay.add(cloudDocumentTextGraphic);
                }
            }
        }
    }
}
