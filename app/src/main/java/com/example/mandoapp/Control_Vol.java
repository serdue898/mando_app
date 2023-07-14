package com.example.mandoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Control_Vol {

    private ThreadPoolExecutor executor;
        private static final int SERVER_PORT = 9600;

        private Socket socket;
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
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ventana);
                            builder.setTitle("Error de conexión");
                            builder.setMessage("No se pudo conectar al servidor.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        }).start();
    }

        public void increaseVolume() {
            sendCommand("increase_volume");
        }

        public void decreaseVolume() {
            sendCommand("decrease_volume");
        }

        private void sendCommand(final String command) {
            if (socket != null && socket.isConnected() && writer != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            writer.write(command);
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        public void disconnect() {
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
    }

