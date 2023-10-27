package com.example.mandoapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Control_Vol extends Service {
        AlertDialog.Builder aviso = null;
        private ThreadPoolExecutor executor;
        private static final int SERVER_PORT = 9600;

        public static Socket socket;
        private BufferedWriter writer;
        private Context ventana;
        public Control_Vol(Context context) {
            this.ventana = context;
            int corePoolSize = 5; // Número mínimo de hilos en el grupo
            int maximumPoolSize = 10; // Número máximo de hilos en el grupo
            long keepAliveTime = 60; // Tiempo máximo en segundos que los hilos inactivos se mantendrán vivos
            TimeUnit timeUnit = TimeUnit.SECONDS; // Unidad de tiempo para el tiempo de espera
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(); // Cola de trabajo para las tareas

            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue);
        }
    public void connect(String ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, SERVER_PORT);
                    OutputStream outputStream = socket.getOutputStream();
                    writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                } catch (IOException e) {
                    crear_aviso();
                }
            }
        }).start();
    }

        public void increaseVolume() {
            sendCommand("aumentar_volumen");
        }

        public void decreaseVolume() {
            sendCommand("bajar_volumen");
        }

    private boolean isSendingMessage = false;

    public void sendCommand(final String command) {
        // Verificar si ya se está enviando un mensaje
        if (isSendingMessage) {
            return; // Ignorar llamadas adicionales mientras se envía el mensaje actual
        }

        isSendingMessage = true; // Marcar que se está enviando un mensaje

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writer.write(command);
                    writer.flush();
                } catch (IOException e) {
                    crear_aviso();
                }
                catch (Exception e2){
                    crear_aviso();
                }
                finally {
                    isSendingMessage = false;
                }
            }
        }).start();
    }
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }


    public void disconnect() {
            sendCommand("fin");
            try {
                if (writer != null) {
                    writer.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void crear_aviso(){
            if (aviso== null) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        aviso = new AlertDialog.Builder(ventana);
                        aviso.setTitle("Error de conexión");
                        aviso.setMessage("No se pudo conectar al servidor.");
                        aviso.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                aviso=null;
                            }
                        });
                        AlertDialog dialog = aviso.create();
                        dialog.show();
                    }
                });
            }

        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

