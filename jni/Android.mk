LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := com.threeDBJ.puzzleDroid.Mat4
LOCAL_CFLAGS    := -Werror
LOCAL_SRC_FILES := com.threeDBJ.puzzleDroid.Mat4.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)