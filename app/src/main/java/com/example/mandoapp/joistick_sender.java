package com.example.mandoapp;

import android.os.AsyncTask;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class joistick_sender extends AsyncTask<Float, Void, Void> {

    private Socket socket;
    private DataOutputStream outputStream;

    public joistick_sender(Socket socket) {
         this.socket=socket;
    }

    @Override
    protected Void doInBackground(Float... params) {
        float deltaX = params[0];
        float deltaY = params[1];
        try {
            outputStream = new DataOutputStream(Control_Vol.socket.getOutputStream());
        }catch (Exception e){

        }
        try {
            // Enviar los valores de deltaX y deltaY al servidor
            outputStream.writeFloat(deltaX);
            outputStream.writeFloat(deltaY);
            outputStream.flush();
        } catch (IOException e) {
            Log.e("JoystickSenderTask", "Error al enviar los datos al servidor: " + e.getMessage());
        }

        return null;
    }
}
