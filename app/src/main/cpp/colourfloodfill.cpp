#include <jni.h>
#include <cstddef>

//
// Created by Lakmal Weerasekara on 3/2/17.
//

extern "C" {
JNIEXPORT void JNICALL Java_com_lakmalz_floodfill_helpers_ColourFill_floodFill(JNIEnv *env,
                                                                                              jobject obj,
                                                                                              jobject bitmap,
                                                                                              uint32_t x,
                                                                                              uint32_t y,
                                                                                              uint32_t fillColor,
                                                                                              uint32_t targetColor,
                                                                                              uint32_t tolerance);

}


JNIEXPORT void JNICALL Java_com_lakmalz_floodfill_helpers_ColourFill_floodFill(JNIEnv *env,
                                                                                              jobject obj,
                                                                                              jobject bitmap,
                                                                                              uint32_t x,
                                                                                              uint32_t y,
                                                                                              uint32_t fillColor,
                                                                                              uint32_t targetColor,
                                                                                              uint32_t tolerance) {
    AndroidBitmapInfo bitmapInfo;

    if ((AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0) {
        return;
    }

    if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return;
    }
    void *bitmapPixels;
    if ((AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        return;
    }

    floodFill(env, x, y, fillColor, bitmapPixels, &bitmapInfo, tolerance, false);
    AndroidBitmap_unlockPixels(env, bitmap);
}
