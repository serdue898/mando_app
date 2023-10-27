package com.example.mandoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Conexion extends Service {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializa y conecta el socket aquí
        try {
            int port = 9600;
            socket = new Socket("server_address", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Realiza las operaciones de comunicación del socket aquí
        // Puedes usar un hilo o AsyncTask para manejar la comunicación en segundo plano

        return START_STICKY; // Este valor hace que el servicio se reinicie si se detiene
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cierra la conexión del socket cuando se detiene el servicio
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
