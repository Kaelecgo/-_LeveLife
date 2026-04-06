package com.irenaprokhyra.levelife.util;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.irenaprokhyra.levelife.R;

public class FurnitureDrawableResolver {

    private FurnitureDrawableResolver() {}

    @DrawableRes
    public static int resolveDrawableResId(@NonNull Context context, String imageRef) {
        if (imageRef == null || imageRef.isEmpty()) {
            return R.drawable.logo_with_no_bg512;
        }

        // Limpieza básica: quitar extensiones si existen por error y asegurar minúsculas
        String cleanedRef = imageRef.trim();
        if (cleanedRef.contains(".")) {
            cleanedRef = cleanedRef.substring(0, cleanedRef.lastIndexOf('.'));
        }

        int drawableResId = context.getResources().getIdentifier(
                cleanedRef.toLowerCase(),
                "drawable",
                context.getPackageName()
        );

        return drawableResId != 0 ? drawableResId : R.drawable.logo_with_no_bg512;
    }

}
