#include <jni.h>
#include <cstddef>

//
// Created by Lakmal Weerasekara on 3/2/17.
//

extern "C" {
/*JNIEXPORT jstring JNICALL Java_com_noah_floodfill_activity_MainActivity_getTestString(JNIEnv *env);*/

JNIEXPORT void JNICALL Java_com_noah_floodfill_activity_MainActivity_floodFill(JNIEnv *env, jobject obj, jintArray pixels, jint w,
                                                                               jint h, jobject pt, jint targetColor,
                                                                               jint fillColor);

}


JNIEXPORT jobject JNICALL Java_com_golysoft_view_DrawThread_floodFillScanLineQueue(JNIEnv *env, jobject obj, jintArray pixels,
                                                                                   jint w,
                                                                                   jint h, jobject pt, jint targetColor,
                                                                                   jint fillColor) {


    if(targetColor == fillColor) return NULL;

    jint *pixelPtr;

    jboolean spanLeft = JNI_FALSE, spanRight = JNI_FALSE;
    jboolean fullScreen = JNI_TRUE;
}


/*JNIEXPORT jstring JNICALL Java_com_noah_floodfill_activity_MainActivity_getTestString(JNIEnv *env) {
    return (*env).NewStringUTF("Clicked.");
}*/
