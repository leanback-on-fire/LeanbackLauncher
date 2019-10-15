package com.amazon.tv.leanbacklauncher.widget;

public class EditModeManager {
    private static EditModeManager sEditModeManager = new EditModeManager();
    private String mSelectedComponentName;

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
