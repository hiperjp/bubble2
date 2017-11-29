#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <cmath>

#define TAG "bitmap-styler"
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)

typedef struct 
{
    uint8_t alpha;
    uint8_t red;
    uint8_t green;
    uint8_t blue;
} argb;

extern "C" JNIEXPORT void JNICALL Java_com_nkanaev_comics_fragment_HeaderFragment_stylizeBitmap(JNIEnv* env, jobject obj, jobject bitmap, int primary)
{
    AndroidBitmapInfo info;
    void* pixels;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        return;
    }
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        return;
    }

    int A = (primary >> 24) & 0xff;
    int R = (primary >> 16) & 0xff;
    int G = (primary >>  8) & 0xff;
    int B = (primary      ) & 0xff;
    LOGW("%d -> %d, %d, %d, %d", primary, A, R, G, B);

    double s = acos(-1.0)/6;
    for (int y = 0; y < info.height; y++) {
        argb* line = (argb*) pixels;
        for (int x = 0; x < info.width; x++) {
            argb& color = line[x];
            int l = (int)(0.299 * color.red + 0.587 * color.green + 0.114 * color.blue);
            int t = (int)((cos(s*(x+0.5))*cos(s*(y+0.5))+1)*127);
            if (l > t)
            {
                color.alpha = (uint8_t) R;
                color.red = (uint8_t) G;
                color.green = (uint8_t) B;
                color.blue = (uint8_t) A;
            }
            else
            {
                color.red = 0;
                color.green = 0;
                color.blue = 0;
            }
        }
        pixels = (char*)pixels + info.stride;
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}