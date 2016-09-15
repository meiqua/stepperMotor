package com.meiqua.steppermotor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    BluetoothSPP bt;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Button midButton;
    private Button upButton;
    private Button backButton;
    private Button leftButton;
    private Button rightButton;
    private Button climbButton;
    private Button dang1Button;
    private Button dang2Button;
    private Button stopClimbButton;
    private Button climbBackButton;

    private Switch aSwitch;

    boolean midFlag=true;
    boolean upFlag=false;
    boolean backFlag=false;
    boolean rightFlag=false;
    boolean leftFlag=false;
    boolean climbFlag=false;
    boolean climbBackFlag=false;
    boolean stopClimbFlag=true;
    boolean dangFlag=false;
    boolean changDang=false;

    final float pi=3.1415926f;
    final float angleFlag=0.25f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        bt = new BluetoothSPP(this);
        //bt.setupService();

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
        //    finish();
        }

        midButton=(Button)findViewById(R.id.mid);
        leftButton=(Button)findViewById(R.id.left);
        rightButton=(Button)findViewById(R.id.right);
        upButton=(Button)findViewById(R.id.up);
        backButton=(Button)findViewById(R.id.back);
        aSwitch=(Switch)findViewById(R.id.switch1);
        climbButton=(Button)findViewById(R.id.climb);
        dang1Button=(Button)findViewById(R.id.dang1);
        dang2Button=(Button)findViewById(R.id.dang2);
        stopClimbButton=(Button)findViewById(R.id.stopClimb);
        climbBackButton=(Button)findViewById(R.id.climbBack);
        updateButtonState();

        climbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    climbFlag = true;
                     climbBackFlag=false;
                    stopClimbFlag=false;
                updateButtonState();
            }
        });

        climbBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                climbFlag = false;
                climbBackFlag=true;
                stopClimbFlag=false;
                updateButtonState();
            }
        });
        stopClimbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                climbFlag = false;
                climbBackFlag=false;
                stopClimbFlag=true;
                updateButtonState();
            }
        });
        dang1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangFlag=false;
                updateButtonState();
            }
        });
        dang2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangFlag=true;
                updateButtonState();
            }
        });
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    midFlag=true;
                    leftFlag=false;
                    rightFlag=false;
                    upFlag=false;
                    backFlag=false;
                updateButtonState();
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftFlag=true;
                rightFlag=false;
                midFlag=false;
                updateButtonState();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightFlag=true;
                leftFlag=false;
                midFlag=false;
                updateButtonState();
            }
        });
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFlag=true;
                backFlag=false;
                midFlag=false;
                updateButtonState();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFlag=true;
                upFlag=false;
                midFlag=false;
                updateButtonState();
            }
        });


    }
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            //    setup();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.connect) {
            if (item.getTitle().equals("disconnect")) {
                bt.disconnect();
                bt.send("stop", false);
                item.setTitle("connect");
            } else {
                bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                item.setTitle("disconnect");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                //        setup();

            } else {
                // Do something if user doesn't choose any device (Pressed back)
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                //   finish();
            }
        }
    }



    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        ,SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (aSwitch.isChecked()){
          if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
             updateButton(event.values[0],event.values[1],event.values[2]);
          }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void updateButton(float x,float y,float z) {
        float Gyz =(float)Math.sqrt( y * y + z * z);
        float Gzx = (float)Math.sqrt(x * x + z * z);
        float tanX = 0;
        float tanY = 0;
        if (Gzx == 0) {
            if (y > 0)
                tanY = pi / 2;
            else
                tanY = -pi / 2;
        } else
            tanX = x / Gzx;

        if (Gyz == 0) {
            if (x > 0)
                tanX = pi / 2;
            else
                tanX = -pi / 2;
        } else
            tanY = y / Gyz;

        Log.i("-->", "updateButton tanx: "+tanX);
        Log.i("-->", "updateButton tany: "+tanY);
        Log.i("-Gyz->", "updateButton Gyz: "+Gyz);

        if (tanY >= angleFlag) {
            upFlag = false;
            backFlag = true;
        } else if (tanY < -angleFlag) {
            backFlag = false;
            upFlag = true;
        } else {
            upFlag = false;
            backFlag = false;
        }

        if (tanX >= angleFlag) {
            leftFlag = true;
            rightFlag = false;
        } else if (tanX < -angleFlag) {
            rightFlag = true;
            leftFlag = false;
        } else {
            leftFlag = false;
            rightFlag = false;
        }

        if (leftFlag == false && rightFlag == false &&
                upFlag == false && backFlag == false) {
            midFlag = true;
        } else{
            midFlag = false;
        }
        updateButtonState();
    }

    private void updateButtonState(){

        if (climbFlag){
            midFlag=false;
            if (!dangFlag){
                dangFlag=true;
                changDang=true;
            }
            leftFlag=rightFlag=false;
//            upFlag=true;
//            backFlag=false;
        }else{
            if (changDang)
                dangFlag=false;
                changDang=false;
        }

        if(!dangFlag){
            dang1Button.setBackgroundColor(Color.GREEN);
            dang2Button.setBackgroundColor(Color.GRAY);
            if (bt.isServiceAvailable())
                bt.send("dang1\n",false);
        }else {
            dang2Button.setBackgroundColor(Color.GREEN);
            dang1Button.setBackgroundColor(Color.GRAY);
            if (bt.isServiceAvailable())
                bt.send("dang2\n",false);
        }

            if (upFlag){
                upButton.setBackgroundColor(Color.GREEN);
                if (bt.isServiceAvailable())
                bt.send("up\n",false);
            }else {
                upButton.setBackgroundColor(Color.GRAY);
            }

        if (backFlag){
            backButton.setBackgroundColor(Color.GREEN);
            if (bt.isServiceAvailable())
            bt.send("back\n", false);
        }else {
            backButton.setBackgroundColor(Color.GRAY);
        }

        if(backFlag==false&&upFlag==false){
            if (bt.isServiceAvailable())
            bt.send("stopMove\n",false);
        }

        if (leftFlag){
            leftButton.setBackgroundColor(Color.GREEN);
            if (bt.isServiceAvailable())
            bt.send("left\n", false);
        }else {
            leftButton.setBackgroundColor(Color.GRAY);
        }

        if (rightFlag){
            rightButton.setBackgroundColor(Color.GREEN);
            if (bt.isServiceAvailable())
            bt.send("right\n", false);
        }else {
            rightButton.setBackgroundColor(Color.GRAY);
        }

        if (leftFlag==false&&rightFlag==false){
            if (bt.isServiceAvailable())
            bt.send("stopTurn\n", false);
        }

        if (midFlag){
            midButton.setBackgroundColor(Color.GREEN);
//            if (bt.isServiceAvailable())
//            bt.send("mid\n", false);
        }else {
            midButton.setBackgroundColor(Color.GRAY);
        }
        if (climbFlag){
            climbButton.setBackgroundColor(Color.GREEN);
            if (bt.isServiceAvailable())
            bt.send("climb\n",false);
        }else {
            climbButton.setBackgroundColor(Color.GRAY);
        }
        if (climbBackFlag){
            climbBackButton.setBackgroundColor(Color.GREEN);
            if (bt.isServiceAvailable())
                bt.send("climbBack\n",false);
        }else {
            climbBackButton.setBackgroundColor(Color.GRAY);
        }
        if (stopClimbFlag){
            stopClimbButton.setBackgroundColor(Color.GREEN);
            if (bt.isServiceAvailable())
                bt.send("stopClimb\n",false);
        }else {
            stopClimbButton.setBackgroundColor(Color.GRAY);
        }
    }
}
