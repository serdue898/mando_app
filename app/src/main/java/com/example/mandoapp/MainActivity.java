package com.example.mandoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView joystick;
    private ImageView ball;
    private float initialX, initialY;
    private Control_Vol control = new Control_Vol(this);
    EditText ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.Tx_ip);

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
                control.connect(ip.toString());
            }
        });
        joystick = findViewById(R.id.joystick);
        ball = findViewById(R.id.ball);
        joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();
                        moveBall(initialX, initialY);
                        // Aquí puedes realizar acciones iniciales si es necesario
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getX();
                        float y = event.getY();
                        moveBall(x, y);
                        // Calcula los cambios en la posición del joystick
                        float deltaX = x - initialX;
                        float deltaY = y - initialY;
                        // Aquí puedes utilizar los valores deltaX y deltaY para controlar el movimiento
                        // del ratón o realizar cualquier otra acción necesaria.
                        break;
                    case MotionEvent.ACTION_UP:
                        ball.setVisibility(View.GONE);
                        // Aquí puedes realizar acciones finales si es necesario
                        break;
                }
                return true;
            }
        });
    }

    private void moveBall(float x, float y) {
        ball.setVisibility(View.VISIBLE);
        ball.setX(x - ball.getWidth() / 2);
        ball.setY(y - ball.getHeight() / 2);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        control.disconnect();
    }
}