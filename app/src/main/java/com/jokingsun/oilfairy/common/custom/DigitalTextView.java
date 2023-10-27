package com.jokingsun.oilfairy.common.custom;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.io.File;

public class DigitalTextView extends AppCompatTextView {
    public DigitalTextView(@NonNull Context context) {
        super(context);
    }

    public DigitalTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DigitalTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context){
        String file = "Digital7-rg1mL.ttf";

        AssetManager assetManager = context.getAssets();
        Typeface font = Typeface.createFromAsset(assetManager,file);
        setTypeface(font);

    }
}
