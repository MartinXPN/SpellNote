package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.xpn.spellnote.ui.util.image.ImageUtil;

import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;


public class FirebaseCloudTextGraphicOverlay extends GraphicOverlay {
    public FirebaseCloudTextGraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void process(FirebaseVisionDocumentText text, ImageView image) {

        // clearImage the graphic overlay
        clear();
        if( text == null )
            return;

        Rect coordinates = ImageUtil.getBitmapPositionInsideImageView(image);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(coordinates.left, coordinates.top, coordinates.left, coordinates.top);
        setLayoutParams(params);
        Timber.d("Bitmap: left: %d, top: %d, right: %d, bottom: %d", coordinates.left, coordinates.top, coordinates.right, coordinates.bottom);
        Timber.d("Margins: left: %d, top: %d, right: %d, bottom: %d", coordinates.left, coordinates.top, coordinates.left, coordinates.top);


        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(this, words.get(l));
                    add(cloudDocumentTextGraphic);
                }
            }
        }
    }
}
