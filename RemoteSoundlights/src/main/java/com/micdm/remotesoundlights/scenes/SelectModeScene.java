package com.micdm.remotesoundlights.scenes;

public class SelectModeScene extends BaseScene {

    public static interface OnSelectModeListener {
        public void onSelectMode(ModeType type);
    }

    public static enum ModeType {
        GUEST,
        BOSS
    }

    private OnSelectModeListener listener;

    public SelectModeScene(OnSelectModeListener listener) {
        this.listener = listener;
        listener.onSelectMode(ModeType.BOSS);
    }
}
