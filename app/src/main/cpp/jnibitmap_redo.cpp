#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <queue>
#include <android/bitmap.h>

#define  LOG_TAG    "jnibitmap"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define DEBUG 0

std::deque<uint32_t> pixelsAllX;
std::deque<uint32_t> pixelsAllY;
std::deque<uint32_t> queueFillColor;


extern "C" {
JNIEXPORT void JNICALL Java_lakmalz_git_colouringimagefloodfill_ColorActivity_constructor(JNIEnv *env);

JNIEXPORT void JNICALL Java_lakmalz_git_colouringimagefloodfill_ColorActivity_redo(JNIEnv *env,
                                                                                   jobject obj,
                                                                                   jobject bitmap,
                                                                                   uint32_t fillColor,
                                                                                   uint32_t targetColor,
                                                                                   uint32_t tolerance);

JNIEXPORT void JNICALL Java_lakmalz_git_colouringimagefloodfill_ColorActivity_floodFill(JNIEnv *env,
                                                                                        jobject obj,
                                                                                        jobject bitmap,
                                                                                        uint32_t x,
                                                                                        uint32_t y,
                                                                                        uint32_t fillColor,
                                                                                        uint32_t targetColor,
                                                                                        uint32_t tolerance);
void clear(std::deque<uint32_t> &q);

bool isPixelValid(int currentColor, int oldColor, int *startColor, int tolerance);

void floodFill(JNIEnv *env,
               uint32_t x,
               uint32_t y,
               uint32_t fillColor,
               uint32_t targetColor,
               jobject bitmap,
               void *bitmapPixels,
               AndroidBitmapInfo *bitmapInfo,
               uint32_t tolerance);

}

//-----------------separator with method declaration----------------------------

JNIEXPORT void JNICALL Java_lakmalz_git_colouringimagefloodfill_ColorActivity_constructor(JNIEnv *env) {
    clear(pixelsAllX);
    clear(pixelsAllY);
    clear(queueFillColor);
}

JNIEXPORT void JNICALL Java_lakmalz_git_colouringimagefloodfill_ColorActivity_redo(JNIEnv *env,
                                                                                   jobject obj,
                                                                                   jobject bitmap,
                                                                                   uint32_t fillColor,
                                                                                   uint32_t targetColor,
                                                                                   uint32_t tolerance) {

    AndroidBitmapInfo oldBitmapInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &oldBitmapInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (oldBitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }
    if (!pixelsAllX.empty()) {
        int cx = pixelsAllX.back();
        int cy = pixelsAllY.back();
        uint32_t cFillColor = fillColor;//queueLoopFillColor.back();
        floodFill(env, cx, cy, cFillColor, targetColor, bitmap, oldBitmapPixels, &oldBitmapInfo, tolerance);

        pixelsAllX.pop_back();
        pixelsAllY.pop_back();
        queueFillColor.pop_back();
    }

    std::deque<uint32_t> pixelsLoopX = pixelsAllX;
    std::deque<uint32_t> pixelsLoopY = pixelsAllY;
    std::deque<uint32_t> queueLoopFillColor = queueFillColor;
    while (!pixelsLoopX.empty()) {
        int cx = pixelsLoopX.back();
        int cy = pixelsLoopY.back();
        uint32_t cFillColor = queueLoopFillColor.back();
        pixelsLoopX.pop_back();
        pixelsLoopY.pop_back();
        queueLoopFillColor.pop_back();
        floodFill(env, cx, cy, cFillColor, targetColor, bitmap, oldBitmapPixels, &oldBitmapInfo, tolerance);
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}

void clear(std::deque<uint32_t> &q) {
    std::deque<uint32_t> empty;
    std::swap(q, empty);
}

JNIEXPORT void JNICALL Java_lakmalz_git_colouringimagefloodfill_ColorActivity_floodFill(JNIEnv *env,
                                                                                        jobject obj,
                                                                                        jobject bitmap,
                                                                                        uint32_t x,
                                                                                        uint32_t y,
                                                                                        uint32_t fillColor,
                                                                                        uint32_t targetColor,
                                                                                        uint32_t tolerance) {
    AndroidBitmapInfo bitmapInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }
    pixelsAllX.push_back(x);
    pixelsAllY.push_back(y);
    queueFillColor.push_back(fillColor);

    std::deque<uint32_t> pixelsLoopX = pixelsAllX;
    std::deque<uint32_t> pixelsLoopY = pixelsAllY;
    std::deque<uint32_t> queueLoopFillColor = queueFillColor;
    while (!pixelsLoopX.empty()) {
        int cx = pixelsLoopX.back();
        int cy = pixelsLoopY.back();
        uint32_t cFillColor = queueLoopFillColor.back();
        pixelsLoopX.pop_back();
        pixelsLoopY.pop_back();
        queueLoopFillColor.pop_back();
        floodFill(env, cx, cy, cFillColor, targetColor, bitmap, bitmapPixels, &bitmapInfo, tolerance);
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}

bool isPixelValid(int currentColor, int oldColor, int *startColor, int tolerance) {

    if (tolerance != 0) {
        int alpha = ((currentColor & 0xFF000000) >> 24);
        int red = ((currentColor & 0xFF0000) >> 16) * alpha / 255; // red
        int green = ((currentColor & 0x00FF00) >> 8) * alpha / 255; // Green
        int blue = (currentColor & 0x0000FF) * alpha / 255; // Blue

        return (red >= (startColor[0] - tolerance)
                && red <= (startColor[0] + tolerance)
                && green >= (startColor[1] - tolerance)
                && green <= (startColor[1] + tolerance)
                && blue >= (startColor[2] - tolerance)
                && blue <= (startColor[2] + tolerance));
    } else {
        if (currentColor == oldColor) {
            return true;
        } else {
            return false;
        }
    }
}


void floodFill(JNIEnv *env,
               uint32_t x,
               uint32_t y,
               uint32_t color,
               uint32_t targetColor,
               jobject bitmap,
               void *bitmapPixels,
               AndroidBitmapInfo *bitmapInfo,
               uint32_t tolerance) {


    // Used to hold the the start( touched ) color that we like to change/fill
    int values[3] = {};

    if (x > bitmapInfo->width - 1)
        return;
    if (y > bitmapInfo->height - 1)
        return;
    if (x < 0)
        return;
    if (y < 0)
        return;

    uint32_t *pixels = (uint32_t *) bitmapPixels;

    uint32_t oldColor;

    int red = 0;
    int blue = 0;
    int green = 0;
    int alpha = 0;
    oldColor = pixels[y * bitmapInfo->width + x];

    // Get red,green and blue values of the old color we like to change
    alpha = (int) ((color & 0xFF000000) >> 24);

    values[0] = (int) ((oldColor & 0xFF0000) >> 16) * alpha / 255; // red
    values[1] = (int) ((oldColor & 0x00FF00) >> 8) * alpha / 255; // Green
    values[2] = (int) (oldColor & 0x0000FF) * alpha / 255; // Blue


    alpha = (int) ((color & 0xFF000000) >> 24);
    blue = (int) ((color & 0xFF0000) >> 16);
    green = (int) ((color & 0x00FF00) >> 8);
    red = (int) (color & 0x0000FF);
    blue = blue * alpha / 255;
    green = green * alpha / 255;
    red = red * alpha / 255;

    int tmp = 0;
    tmp = red;
    red = blue;
    blue = tmp;

    color = ((alpha << 24) & 0xFF000000) | ((blue << 16) & 0xFF0000) |
            ((green << 8) & 0x00FF00) |
            (red & 0x0000FF);

    LOGD("edit1");
    std::queue<uint32_t> pixelsX;
    std::queue<uint32_t> pixelsY;

    int nx = 0;
    int ny = 0;
    int wx = 0;
    int wy = 0;
    int ex = 0;
    int ey = 0;

    pixelsX.push(x);
    pixelsY.push(y);

    while (!pixelsX.empty()) {

        nx = pixelsX.front();
        ny = pixelsY.front();
        pixelsX.pop();
        pixelsY.pop();

        if (pixels[ny * bitmapInfo->width + nx] == color)
            continue;

        wx = nx;
        wy = ny;
        ex = wx + 1;
        ey = wy;

        while (wx > 0 && isPixelValid(pixels[wy * bitmapInfo->width + wx], oldColor, values, tolerance)) {
            pixels[wy * bitmapInfo->width + wx] = color;

            if (wy > 0 && isPixelValid(pixels[(wy - 1) * bitmapInfo->width + wx], oldColor, values, tolerance)) {
                pixelsX.push(wx);
                pixelsY.push(wy - 1);
            }
            if (wy < bitmapInfo->height - 1 &&
                isPixelValid(pixels[(wy + 1) * bitmapInfo->width + wx], oldColor, values, tolerance)) {
                pixelsX.push(wx);
                pixelsY.push(wy + 1);
            }
            wx--;
        }

        while (ex < bitmapInfo->width - 1 && isPixelValid(pixels[ey * bitmapInfo->width + ex], oldColor, values, tolerance)) {
            pixels[ey * bitmapInfo->width + ex] = color;
            if (ey > 0 && isPixelValid(pixels[(ey - 1) * bitmapInfo->width + ex], oldColor, values, tolerance)) {
                pixelsX.push(ex);
                pixelsY.push(ey - 1);

            }
            if (ey < bitmapInfo->height - 1 &&
                isPixelValid(pixels[(ey + 1) * bitmapInfo->width + ex], oldColor, values, tolerance)) {
                pixelsX.push(ex);
                pixelsY.push(ey + 1);
            }
            ex++;
        }

    }
}




