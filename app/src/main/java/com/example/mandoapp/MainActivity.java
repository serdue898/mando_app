package com.example.mandoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ingenieriajhr.joystickjhr.JoystickJhr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private ImageView joystick;
    private ImageView ball;
    private float initialX, initialY;
    private Control_Vol control ;
    EditText ip;
    float deltaX;
    float deltaY;

    Runnable movimiento=null;
    Runnable presionar=null;
    public static View conectado;
    private boolean isJoystickMoving = false;
    private Handler handler = new Handler();
    private Runnable continuousUpdateRunnable;
    private Cronometro cronometro = new Cronometro();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control= new Control_Vol(this);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.Tx_ip);
        // Inicializar otras vistas y objetos de tu actividad aquí

        // Restaurar el estado si hay un estado guardado
        if (savedInstanceState != null) {
            String savedIp = savedInstanceState.getString("savedIp");
            ip.setText(savedIp);

            isJoystickMoving = savedInstanceState.getBoolean("isJoystickMoving", false);
            if (isJoystickMoving) {
                // Restaurar el estado del joystick y reanudar la actualización continua si es necesario
                handleJoystickMovement();
            }
        }
        conectado = findViewById(R.id.img_conectado);
        Button increaseButton = findViewById(R.id.btt_vol_up);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.increaseVolume();
            }
        });

        Button decreaseButton = findViewById(R.id.btt_vol_down);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.decreaseVolume();
            }
        });
        Button btt_conecta = findViewById(R.id.btt_conectar);
        btt_conecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ip.getWindowToken(), 0);
                control.connect(ip.getText().toString());
            }
        });
        final JoystickJhr joystick2 = findViewById(R.id.joystick2);
        int sensibilidad=50;
        final boolean[] pulsado = {false};
        joystick2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                joystick2.move(motionEvent);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cronometro.empezar();
                        control.sendCommand("raton");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (cronometro.getTiempo() > 150) {
                            cronometro.parar();
                            handleJoystickMovement();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isJoystickMoving) {

                            cronometro.parar();
                            pulsar();
                        }
                        pulsado[0] = false;
                        isJoystickMoving = false;
                        control.sendCommand("parar");
                        if (continuousUpdateRunnable != null) {
                            handler.removeCallbacks(continuousUpdateRunnable);
                        }

                        break;
                }

                return true;
            } private void handleJoystickMovement() {
                if (!isJoystickMoving) {
                    isJoystickMoving = true;
                    continuousUpdateRunnable = new  Runnable() {
                        @Override
                        public void run() {
                            control.sendCommand(((int) joystick2.joyX()) + "," +((int) joystick2.joyY()) );
                            if (isJoystickMoving) {
                                handler.postDelayed(this, 100);
                            }
                        }
                    };
                    handler.post(continuousUpdateRunnable);
                }
            }

            private void pulsar() {
                Runnable pulsarRunnable = new Runnable() {
                    @Override
                    public void run() {
                        control.sendCommand("click");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                pulsarRunnable.run();
            }});



        Button pausar = findViewById(R.id.btt_pause);
        pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.sendCommand("pausar");
            }
        });
        Button adelantar = findViewById(R.id.btt_adelantar);
        adelantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.sendCommand("adelantar");
            }
        });
        Button atrasar = findViewById(R.id.btt_atrasar);
        atrasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.sendCommand("atrasar");
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar el estado de las vistas y variables aquí
        outState.putString("savedIp", ip.getText().toString());
        outState.putBoolean("isJoystickMoving", isJoystickMoving);
    }

    // Otros métodos de tu actividad

    private void handleJoystickMovement() {
        // Lógica para manejar el movimiento del joystick, similar a tu implementación actual
        // ...
        if (isJoystickMoving) {
            handler.postDelayed(continuousUpdateRunnable, 100);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        control.disconnect();
    }
}