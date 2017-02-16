#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <queue>
#include <android/bitmap.h>

#include <assert.h>

#define  LOG_TAG    "jnibitmap"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define DEBUG 0

extern "C" {
JNIEXPORT void JNICALL Java_com_noah_floodfill_activity_MainActivity_floodFill(JNIEnv *env,
                                                                               jobject obj,
                                                                               jobject handle,
                                                                               uint32_t x,
                                                                               uint32_t y,
                                                                               uint32_t fillColor,
                                                                               uint32_t targetColor,
                                                                               uint32_t tolerance);

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

/*JNIEXPORT jintArray JNICALL Java_com_noah_floodfill_activity_MainActivity_getImageMat(JNIEnv *env,
                                                                                      jobject obj,
                                                                                      jobject bitmap);*/

//-------------------------------------------------------------------------------------------------------------------------------
/*void boundaryFill(JNIEnv *env, uint32_t x,
                  uint32_t y,
                  jobject bitmap,
                  uint32_t color,
                  uint32_t boundary);
uint32_t getPixel(JNIEnv *env, jobject bm, int x, int y);
void setPixel(JNIEnv *env, jobject bm, int x, int y, uint32_t val);*/
//-------------------------------------------------------------------------------------------------------------------------------
}

//---------
/*void boundaryFill(JNIEnv *env, uint32_t x, uint32_t y, jobject bitmap, uint32_t color, uint32_t boundary) {

    int current;
    current = getPixel(env, bitmap, x, y);

    if ((current != boundary) && (current != color)) {
        setPixel(env, bitmap, x, y, color);
        boundaryFill(env, x + 1, y, bitmap, color, boundary);
        *//*boundaryFill(env,x - 1, y, bitmap,color, boundary);
        boundaryFill(env,x , y+1, bitmap,color, boundary);
        boundaryFill(env,x , y-1, bitmap,color, boundary);*//*
    }
}*/
//---------

/*JNIEXPORT jintArray JNICALL Java_co_zaven_digitalimageprocessing_activities_DipActivity_getImageMat(
        JNIEnv *env, jobject obj, jobject bitmap) {
    AndroidBitmapInfo bitmapInfo;
    uint32_t *storedBitmapPixels = NULL;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return NULL;
    }

    void *bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return NULL;

    }

    jintArray outArray = (*env).NewIntArray(bitmapInfo.width * bitmapInfo.height);
    jint carray[bitmapInfo.width * bitmapInfo.height];
    uint32_t *pixels = (uint32_t *) bitmapPixels;
    for (int i = 0; i < bitmapInfo.width * bitmapInfo.height; i++) {
        carray[i] = pixels[i];
        if (i > 555 && i < 565) LOGD("print %d %d %d", i, carray[i], pixels[i]);
    }
    (*env).SetIntArrayRegion(outArray, 0, bitmapInfo.width * bitmapInfo.height, carray);
//    for(int i=0;i<bitmapInfo.height;i++){
//        for(int j=0;j<bitmapInfo.width;j++){
//
//        }
//    }
    AndroidBitmap_unlockPixels(env, bitmap);
    return outArray;
}*/

JNIEXPORT void JNICALL Java_com_noah_floodfill_activity_MainActivity_floodFill(JNIEnv *env,
                                                                               jobject obj,
                                                                               jobject bitmap,
                                                                               uint32_t x,
                                                                               uint32_t y,
                                                                               uint32_t fillColor,
                                                                               uint32_t targetColor,
                                                                               uint32_t tolerance) {
    AndroidBitmapInfo bitmapInfo;
    uint32_t *storedBitmapPixels = NULL;

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

    floodFill(env, x, y, fillColor, targetColor, bitmap, bitmapPixels, &bitmapInfo, tolerance);
    //boundaryFill(env, x, y, bitmap, color, 4278190080);

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
        //return true;
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



    /*std::queue<uint32_t> pixelsqX;
    std::queue<uint32_t> pixelsqY;
    uint32_t *pixels = (uint32_t *) bitmapPixels;

    pixelsqX.push(x);
    pixelsqY.push(y);

    int py = 0;
    int px = 0;

    while (pixelsqX.size() > 0) {
        px = pixelsqX.front();
        py = pixelsqY.front();
        pixelsqX.pop();
        pixelsqY.pop();


        *//*if (pixels[py * info->width + px] != targetColor)
            continue;*//*

        int wx = px;
        int wy = py;
        int ex = px;
        int ey = py;//Point w = n, e = new Point(n.x + 1, n.y);
        while ((wx > 0) && *//*(bmp.getPixel(wx, wy)*//* (pixels[wy * info->width + wx] == fillColor)) {
            pixels[ey * info->width + ex] = targetColor;
            if ((wy > 0) && (pixels[(wy - 1) * info->width + wx] == fillColor)) {
                pixelsqX.push(wx);
                pixelsqY.push(wy - 1);
            }
            if ((wy < info->height - 1) && (pixels[(wy + 1) * info->width + wx] == fillColor)) {
                pixelsqX.push(wx);
                pixelsqY.push(wy + 1);
                wx--;
            }

            while ((ex < info->width - 1) && (pixels[ey * info->width + ex] == fillColor)) {
                pixels[ey * info->width + ex] = targetColor;

                if ((ey > 0) && (pixels[(ey - 1) * info->width + ex] == fillColor)) {
                    pixelsqX.push(ex);
                    pixelsqY.push(ey - 1);
                }if ((ey < info->height - 1) && (pixels[(ey + 1) * info->width + ex] == fillColor)){
                    pixelsqX.push(ex);
                    pixelsqY.push(ey + 1);
                }

                ex++;
            }
        }*/
//-----------------
    /*char spanLeft = 0, spanRight = 0;

    std::queue<uint32_t> pixelsX;
    std::queue<uint32_t> pixelsY;


    pixelsX.push(x);
    pixelsY.push(y);

    int px = 0;
    int py = 0;
    int wx = 0;
    int wy = 0;
    int ex = 0;
    int ey = 0;

    uint32_t *pixels = (uint32_t *) bitmapPixels;

     while (!pixelsX.empty()) {
         px = pixelsX.front();
         py = pixelsY.front();
         pixelsX.pop();
         pixelsY.pop();

         //while(py >= 0 && getPixel(pixels,info,px,py) == targetColor) py--;

         while (py >= 0 && pixels[py * info->width + px] == targetColor) py--;


         py++;
         spanLeft = spanRight = 0;

         while (py < info->height && pixels[py * info->width + px] != targetColor) { // change getpixel
             setPixel(env, bitmap, px, py, fillColor);

             if (!spanLeft && px > 0 && pixels[py * info->width + (px - 1)] == targetColor) {
                 pixelsX.push(px - 1);
                 pixelsY.push(py);
                 spanLeft = 1;
             } else if (spanLeft && px > 0 && pixels[py * info->width + (px - 1)] != targetColor) {
                 spanLeft = 0;
             }
             if (!spanRight && px < info->width - 1 && pixels[py * info->width + (px + 1)] == targetColor) {
                 //Create new point add it to queue
                 pixelsX.push(px + 1);
                 pixelsY.push(py);
                 spanRight = 1;
             }
             else if (spanRight && px < info->width - 1 && pixels[py * info->width + (px + 1)] != targetColor) {
                 spanRight = 0;
             }
             py++;
         }
     }*/

    //----------------

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

    // Get red,green and blue values of the old color we like to chnage
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

        while (wx > 0 && /*(pixels[wy * bitmapInfo->width + wx] ==
                          color)*/isPixelValid(pixels[wy * bitmapInfo->width /*+ wx*/], oldColor, values, tolerance)) {
            pixels[wy * bitmapInfo->width + wx] = color;

            if (wy > 0 && /*(pixels[(wy - 1) * bitmapInfo->width + wx] == color)*/isPixelValid(pixels[(wy - 1) * bitmapInfo->width + wx], oldColor, values, tolerance)) {
                pixelsX.push(wx);
                pixelsY.push(wy - 1);
            }
            if (wy < bitmapInfo->height - 1 && /*(pixels[(wy + 1) * bitmapInfo->width + wx] == color)*/ isPixelValid(pixels[(wy +1) *bitmapInfo->width + wx], oldColor, values, tolerance)) {
                pixelsX.push(wx);
                pixelsY.push(wy + 1);
            }
            wx--;
        }

        while (ex < bitmapInfo->width - 1 && /*(pixels[ey * bitmapInfo->width + ex] == color)*/isPixelValid(pixels[ey * bitmapInfo->width + ex], oldColor, values, tolerance)) {
            //pixels[ey * bitmapInfo->width + ex] = color;
            if (ey > 0 &&/* (pixels[(ey - 1) * bitmapInfo->width + ex] == color) */isPixelValid(pixels[(ey - 1) * bitmapInfo->width + ex], oldColor, values, tolerance)) {
                pixelsX.push(ex);
                pixelsY.push(ey - 1);

            }
            if (ey < bitmapInfo->height - 1 && /*(pixels[(ey + 1) * bitmapInfo->width + ex] == color)*/
                isPixelValid(pixels[(ey + 1) * bitmapInfo->width + ex], oldColor, values,
                             tolerance)) {
                pixelsX.push(ex);
                pixelsY.push(ey + 1);
            }
            ex++;
        }

    }
}


/*void floodFill(uint32_t x, uint32_t y, uint32_t color, void *bitmapPixels,
               AndroidBitmapInfo *bitmapInfo, uint32_t tolerance) {

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

    // Get red,green and blue values of the old color we like to chnage
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

        while (wx > 0 &&
               isPixelValid(pixels[wy * bitmapInfo->width + wx], oldColor, values, tolerance)) {
            pixels[wy * bitmapInfo->width + wx] = color;

            if (wy > 0 && isPixelValid(pixels[(wy - 1) * bitmapInfo->width + wx], oldColor, values,
                                       tolerance)) {
                pixelsX.push(wx);
                pixelsY.push(wy - 1);

            }
            if (wy < bitmapInfo->height - 1 &&
                isPixelValid(pixels[(wy + 1) * bitmapInfo->width + wx], oldColor, values,
                             tolerance)) {
                pixelsX.push(wx);
                pixelsY.push(wy + 1);

            }
            wx--;
        }


        while (ex < bitmapInfo->width - 1 &&
               isPixelValid(pixels[ey * bitmapInfo->width + ex], oldColor, values, tolerance)) {
            pixels[ey * bitmapInfo->width + ex] = color;
            if (ey > 0 && isPixelValid(pixels[(ey - 1) * bitmapInfo->width + ex], oldColor, values,
                                       tolerance)) {
                pixelsX.push(ex);
                pixelsY.push(ey - 1);

            }
            if (ey < bitmapInfo->height - 1 &&
                isPixelValid(pixels[(ey + 1) * bitmapInfo->width + ex], oldColor, values,
                             tolerance)) {
                pixelsX.push(ex);
                pixelsY.push(ey + 1);

            }
            ex++;
        }

    }
}*/



