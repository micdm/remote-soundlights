package com.micdm.remotesoundlights.flashlight;

import android.hardware.Camera;

public class FlashlightController {

    private Camera camera;

    public void init() {
        camera = getCamera();
        if (camera != null) {
            camera.startPreview();
        }
    }

    private Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            return null;
        }
    }

    public void deinit() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public boolean isReady() {
        return camera != null;
    }

    public boolean isActive() {
        Camera.Parameters params = camera.getParameters();
        String mode = params.getFlashMode();
        return mode != null && mode.equals(Camera.Parameters.FLASH_MODE_TORCH);
    }

    public void activate() {
        if (!isReady()) {
            return;
        }
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
    }

    public void deactivate() {
        if (!isReady()) {
            return;
        }
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
    }
}
