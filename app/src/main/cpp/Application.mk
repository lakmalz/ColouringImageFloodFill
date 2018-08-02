# The ARMv7 is significantly faster due to the use of the hardware FPU
APP_ABI:=armeabi-v7a, arm64-v8a, x86, x86_64
APP_STL:=stlport_shared
APP_CPPFLAGS += -fexceptions