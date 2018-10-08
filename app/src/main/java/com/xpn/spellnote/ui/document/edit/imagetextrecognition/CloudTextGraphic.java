package com.xpn.spellnote.ui.document.edit.imagetextrecognition;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.xpn.spellnote.ui.document.edit.imagetextrecognition.GraphicOverlay.Graphic;

import java.util.List;


public class CloudTextGraphic extends Graphic {
    private static final int TEXT_COLOR = Color.BLACK;
    private static final float TEXT_SIZE = 60.0f;
    private static final float STROKE_WIDTH = 5.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final FirebaseVisionDocumentText.Word word;

    CloudTextGraphic(GraphicOverlay overlay, FirebaseVisionDocumentText.Word word) {
        super(overlay);

        this.word = word;
        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        if (word == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }

        StringBuilder wordStr = new StringBuilder();
        Rect wordRect = word.getBoundingBox();
        if( wordRect == null )
            return;

        canvas.drawRect(wordRect, rectPaint);
        List<FirebaseVisionDocumentText.Symbol> symbols = word.getSymbols();
        for (int m = 0; m < symbols.size(); m++) {
            wordStr.append(symbols.get(m).getText());
        }
        canvas.drawText(wordStr.toString(), wordRect.left, wordRect.bottom, textPaint);
    }
}
