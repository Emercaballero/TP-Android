package com.teamdc.stephendiniz.autoaway;

import android.hardware.Camera;

/**
 * Created by sscotti on 5/10/15.
 */
public class Camara {

    private static Camara camara = new Camara();

    private static Camera camera;

   static {
        try{
            camera = Camera.open();
        } catch (Exception e){
            camera = null;
        }
    }

    public static Camara getInstance(){
        return camara;
    }

    public void encenderFlash(){
        if(!sePuedeUsarLaCamara()) return;

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    public void apagarFlash() {
        if(!sePuedeUsarLaCamara()) return;
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);

        camera.stopPreview();
    }

    public void hacerGuinio() {
        this.apagarFlash();
        this.encenderFlash();
        this.apagarFlash();
    }

    public void hacerGuinio(int cantidadDeVeces) {
        for(int i=0 ; i< cantidadDeVeces; i++){
            this.hacerGuinio();
        }
    }

    private boolean sePuedeUsarLaCamara(){
       return camera != null;
    }


}
