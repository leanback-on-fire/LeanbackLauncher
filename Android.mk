LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := LeanbackOnFire
LOCAL_CERTIFICATE := platform
LOCAL_DEX_PREOPT := false
LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main/java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res \
       $(TOP)/frameworks/support/v7/preference/res \
       frameworks/support/v17/leanback/res \
       frameworks/support/v7/recyclerview/res \
       frameworks/support/v17/preference-leanback/res \
       $(TOP)/frameworks/support/v14/preference/res
LOCAL_AAPT_FLAGS := --auto-add-overlay \
      --extra-packages android.support.v7.preference:android.support.v17.leanback:android.support.v7.recyclerview:android.support.v14.preference
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v17-leanback
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-preference
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-recyclerview
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-palette
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v14-preference
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v17-preference-leanback
#LOCAL_STATIC_JAVA_LIBRARIES += org.junit
LOCAL_STATIC_JAVA_LIBRARIES += libxutils
LOCAL_STATIC_JAVA_LIBRARIES += libandroidutils
#LOCAL_STATIC_JAVA_LIBRARIES += libjunit
#LOCAL_STATIC_JAVA_LIBRARIES += libfm
LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)

#include $(CLEAR_VARS)
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += libjunit:libs/junit.jar
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += libfm:libs/fm.jar
#include $(BUILD_MULTI_PREBUILT)

