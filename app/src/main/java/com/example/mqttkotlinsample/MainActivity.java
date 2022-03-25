package com.example.mqttkotlinsample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    TextView subText;
    private static final String TAG = "MainActivity ";
    private static String verizon_mqtt_server_url = "tcp://Mqtt.vzmode.br2dev.dltdemo.io:1883";
    private static String clientId = "175";
    private static String userName = "user3";
    private static String pwd = "dfFg7sEX52BQ";
    //    VZCV2X/<Version>/<Direction>/<Entity_Type>/<Entity_subtype>/<Vendor_ID>/<Entity_I
//D>/<Msg_Format>/<Msg_Type>

    private static String pub_topic = "VZCV2X/+/IN/SW/NA/VZ/175/UPER";
//    private static String sub_topic = "VZCV2X/+/OUT/SW/VZ/175/UPER";

//    REGIONAL/SAMP/<Version>/<GEOHASHID>/<EntityType>/<EntitySubtype>/<VendorID>/
//</MsgFormat>/<MsgType>


    private static String sub_topic = "REGIONAL/DYN/3/gbsuv/SW/NA/VZ/UPER/PSM";
    private static int qos = 1;

    Myproto myproto = new Myproto();

    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        subText = findViewById(R.id.subText);

        //        String clientId = MqttClient.generateClientId();
//        Log.d(TAG, "onCreate: clientId " + clientId);

        client = new MqttAndroidClient(this.getApplicationContext(), verizon_mqtt_server_url, clientId);
        try {
            MqttConnectOptions connOpts = setUpConnectionOptions(userName, pwd);
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
/*
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setSubscription();
                    Log.d(TAG, "onSuccess: ");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure: ");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: printStackTrace");
        }
*/

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost: ");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                subText.setText(new String(message.getPayload()));
                Toast.makeText(MainActivity.this, "message arrived.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "mqtt messageArrived: " + Arrays.toString(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "mqtt deliveryComplete: ");
                Toast.makeText(MainActivity.this, "deliveryComplete.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void published(View v) {
        String message = "the payload";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(myproto);
            oos.flush();
            byte[] data = bos.toByteArray();

            client.publish(pub_topic, data, qos, false);
            Log.d(TAG, "mqtt published called");
            Log.d(TAG, "pub_topic-> "+pub_topic);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "published: printStackTrace ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription() {
        try {
            client.subscribe(sub_topic, qos);
            Log.d(TAG, "mqtt subscribe  ");
            Log.d(TAG, "sub_topic-> " + sub_topic);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "setSubscription: printStackTrace");
        }
    }

    public void connectMqtt(View v) {
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "mqtt connection successful ");
                    setSubscription();
                    Toast.makeText(MainActivity.this, "connection successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "mqtt connection onFailure: ");
                    Toast.makeText(MainActivity.this, "connection fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "conn: printStackTrace");
            Toast.makeText(MainActivity.this, "MqttException", Toast.LENGTH_SHORT).show();
        }
    }

    /*public void disconnectMqtt(View v) {
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: ");
                    Toast.makeText(MainActivity.this, "disconnectMqtt onSuccess", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "disconnectMqtt onFailure", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: ");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "disconnectMqtt MqttException", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "disconn: printStackTrace ");
        }
    }*/
}