package com.android.aquacomputer.farbwerk;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.AdapterView;
import android.net.Uri;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.support.v4.content.LocalBroadcastManager;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;

public class FarbwerkDevice extends Activity implements OnColorChangedListener
{
    public static final String PREFERENCES_NAME = "farbwerk_preferences";
    private BluetoothConnector bt = null;
    private Spinner output_selector;
    private int[] output_color_last = new int[4];
    private String deviceName = "";
    private BroadcastReceiver resultReceiver;
    private ImageView img_connection;
    private ColorPicker picker;
    private ValueBar valueBar;
    private SaturationBar saturationBar;
    private ImageButton save_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farbwerk_device);
        bt = new BluetoothConnector(this);
        resultReceiver = createBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(resultReceiver, new IntentFilter("com.android.aquacomputer.farbwerk.connectionchanged"));

        img_connection = (ImageView)findViewById(R.id.image_bt);
        img_connection.setImageResource(R.drawable.bluetooth_not_ok);

        Bundle extras = getIntent().getExtras();
        String device_name = extras.getString("bt_name");
        bt.setDevice(device_name);
        bt.start_worker();

        deviceName = device_name;
        picker = (ColorPicker) findViewById(R.id.picker);
        valueBar = (ValueBar) findViewById(R.id.valuebar);
        saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        picker.addValueBar(valueBar);
        picker.addSaturationBar(saturationBar);
        picker.setShowOldCenterColor(false);

        save_button = (ImageButton) findViewById(R.id.image_btn_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_save_click(v);
            }
        });

        int default_color = 0xff000000 | 0xff << 16; //red
        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
        output_color_last[0] = settings.getInt(device_name + "_1", default_color);
        output_color_last[1] = settings.getInt(device_name + "_2", default_color);
        output_color_last[2] = settings.getInt(device_name + "_3", default_color);
        output_color_last[3] = settings.getInt(device_name + "_4", default_color);

        output_selector = (Spinner)findViewById(R.id.output_selection);
        output_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Your code here
                int output_id = output_selector.getSelectedItemPosition();
                if(output_id >= 0 && output_id<=3)
                    picker.setColor(output_color_last[output_id]);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        picker.setOnColorChangedListener(this);

        ImageView img = (ImageView)findViewById(R.id.image_link);
        img.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://aquacomputer.de"));
                startActivity(intent);
            }
        });

        send_color(output_color_last[0], 0);
        send_color(output_color_last[1], 1);
        send_color(output_color_last[2], 2);
        send_color(output_color_last[3], 3);
    }

    private BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connection = intent.getBooleanExtra("connectionstate", false);
                if(connection) {
                    if(img_connection != null)
                        img_connection.setImageResource(R.drawable.bluetooth_ok);
                    Toast toast = Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    if(img_connection != null)
                        img_connection.setImageResource(R.drawable.bluetooth_not_ok);
                    Toast toast = Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };
    }

    @Override
    public void onColorChanged(int color)
    {
        int output_id = output_selector.getSelectedItemPosition();
        output_color_last[output_id] = color;
        send_color(color, output_id);
    }

    private void send_color(int color, int id)
    {
        int fade_time = 75; //75ms fade time
        bt.sendData(color, id, fade_time);
    }

    private void on_save_click(View v)
    {
        int color = picker.getColor();
        int output_id = output_selector.getSelectedItemPosition();
        output_color_last[output_id] = color;
        bt.sendData(color, output_id, 0, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_farbwerk_device, menu);
        return true;
    }

    /**save settings*/
    public void savePreferences(){
        try {
            SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(deviceName + "_1", output_color_last[0]);
            editor.putInt(deviceName + "_2", output_color_last[1]);
            editor.putInt(deviceName + "_3", output_color_last[2]);
            editor.putInt(deviceName + "_4", output_color_last[3]);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bt.start_worker();
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        bt.stop_worker();

        if (resultReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(resultReceiver);
        }
        savePreferences();
        super.onDestroy();
    }
}
