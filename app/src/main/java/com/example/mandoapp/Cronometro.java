package com.example.mandoapp;

import android.os.Handler;
import android.widget.Button;

public class Cronometro {
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private Handler handler;
    private Runnable updateRunnable;

    public Cronometro() {

        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    handler.postDelayed(this, 100); // Actualizar cada segundo
                }
            }
        };
    }

    public void empezar() {
        if (!isRunning) {
            isRunning = true;
            startTime = System.currentTimeMillis() - elapsedTime;
            handler.post(updateRunnable);
        }
    }

    public void parar() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(updateRunnable);
            elapsedTime=0;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getTiempo() {
        return elapsedTime;
    }
}



