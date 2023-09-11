package com.addon.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public boolean active = true;
    private SensorManager sensorManager; //Объект для работы с датчиком
    private int count = 0; //Количество шагов
    private TextView text; //Ссылка на TextView
    private long lastUpdate; //Время последнего изменения состояния датчика

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView2); //Находим текст
        text.setText(String.valueOf(count)); //Создаем объект, для работы с датчиком
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE); //Регистрируем класс, где будет реализован метод, вызываемый при изменении

        //Запускаем датчик
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = System.currentTimeMillis(); //Устанавливаем последнее время обновления


        Button button = findViewById(R.id.button);
        button.setText("Пауза");
    }

    @Override
    protected void onResume(){ //Продолжить
        super.onResume(); //Подписываемся на действие
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL); //Запускаем датчик
    }

    @Override
    protected void onPause(){ //Пауза
        super.onPause(); //Подписываемся на действие
        sensorManager.unregisterListener((SensorEventListener) this); //Останавливаем датчик
    }

    //Функция паузы
    public void OnStoped(View view) {
        active = !active; //Активно/неактивно
        if(!active) { //Если неактивно
            Button button = findViewById(R.id.button); //Находим кнопку
            button.setText("Возобновить"); //Присваиваем текст
        } else { //Если активно
            Button button = findViewById(R.id.button); //Находим кнопку
            button.setText("Пауза"); //Присваиваем текст
        }
    }

    public void onSensorChanged(SensorEvent event){ //Функция изменения датчика
        Button button = findViewById(R.id.button);
        if(button.getText() == "Пауза")
        {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //Если акселерометра
                float[] values = event.values; //Проекции ускорения на оси системы координат
                float x = values[0]; //Координата x
                float y = values[1]; //Координата y
                float z = values[2]; //Координата z

                //Квадрат модуля ускорения телефона, деленный на квадрат ускорения свободного падения
                float accelationSquareRoot = (x* x+y * y+z *z) / (SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);

                //Текущее время
                long actualTime=System.currentTimeMillis();

                if(accelationSquareRoot>=2){ //Если тряска сильная
                    if(actualTime - lastUpdate<200){ //Если с момента начала тряски прошло меньше 200 миллисекунд - выходим из обработчика
                        return;
                    }
                    lastUpdate = actualTime; //Актуализируем данные

                    count++; //Увеличиваем шаг
                    text.setText(String.valueOf(count)); //Обновляем текст
                }
            }
        }

    }

    //Метод сообщения о движении датчика с разной скоростью
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}