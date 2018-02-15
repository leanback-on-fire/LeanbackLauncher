package com.rockon999.android.leanbacklauncher.widget;

public class EditModeManager {
    private static EditModeManager sEditModeManager;
    private String mSelectedComponentName;

    static {
        sEditModeManager = new EditModeManager();
    }

    public static EditModeManager getInstance() {
        return sEditModeManager;
    }

    public void setSelectedComponentName(String name) {
        this.mSelectedComponentName = name;
    }

    public String getSelectedComponentName() {
        return this.mSelectedComponentName;
    }
}
