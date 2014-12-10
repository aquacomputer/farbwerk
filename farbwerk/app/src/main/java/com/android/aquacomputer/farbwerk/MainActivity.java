package com.android.aquacomputer.farbwerk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.net.Uri;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;
import android.widget.ImageView;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends Activity
{
    private BluetoothConnector bt = null;
    private Button btn_refresh;
    private Button btn_bt_on;
    private Button btn_bt_off;
    private ListView bt_devicesListView;
    private ArrayAdapter<String> devices_items;
    private boolean lock_page = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BluetoothConnector(this);

        btn_bt_on = (Button)findViewById(R.id.btn_bt_on);
        btn_bt_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_on(v);
            }
        });

        btn_bt_off = (Button)findViewById(R.id.btn_bt_off);
        btn_bt_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_off(v);
            }
        });

        btn_refresh = (Button)findViewById(R.id.btn_devices_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_view();
            }
        });

        bt_devicesListView = (ListView)findViewById(R.id.devices_listView);
        bt_devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg)
            {
                device_item_click(position);
            }
        });
        devices_items = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        bt_devicesListView.setAdapter(devices_items);

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

        refresh_view();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refresh_view();
    }

    public void bt_on(View view)
    {
        Intent  it = bt.adapterTurnOn();
        if(it != null)
        {
            startActivityForResult(it, bt.getEnableRequest());
        }
    }

    public void bt_off(View view)
    {
        bt.adapterTurnOff();
        refresh_view();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        if(requestCode == bt.getEnableRequest()){
            refresh_view();
        }
    }

    private void refresh_view()
    {
        if(bt.isAvailable())
        {
            btn_bt_on.setEnabled(false);
            btn_bt_on.setVisibility(View.GONE);
            btn_bt_off.setEnabled(true);
            btn_bt_off.setVisibility(View.VISIBLE);
            btn_refresh.setEnabled(true);
            btn_refresh.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_bt_on.setEnabled(true);
            btn_bt_on.setVisibility(View.VISIBLE);
            btn_bt_off.setEnabled(false);
            btn_bt_off.setVisibility(View.GONE);
            btn_refresh.setEnabled(false);
            btn_refresh.setVisibility(View.GONE);
        }
        devices_items.clear();
        if(bt.isAvailable())
        {
            Set<BluetoothDevice> items = bt.getPaired();
            if(items != null)
            {
                for (BluetoothDevice device : items)
                {
                    if(device.getName().contains("Farbwerk"))
                        devices_items.add(device.getName()+ "\n" + device.getAddress());
                }
            }
        }
    }

    private void device_item_click(int idx)
    {
        if(lock_page)
            return;

        BluetoothDevice[] items = null;
        items = bt.getPaired().toArray(new BluetoothDevice[0]);
        if(items == null || items.length < 1 || idx >= items.length)
            return;

        lock_page = true;
        List<BluetoothDevice> items_list = new ArrayList<BluetoothDevice>();
        for(BluetoothDevice d: items)
        {
            if(d.getName().contains("Farbwerk"))
                items_list.add(d);
        }
        BluetoothDevice device = items_list.get(idx);
        if(device != null && device.getName().contains("Farbwerk") == false)
        {
            Toast.makeText(getApplicationContext(),"This is no Farbwerk device", Toast.LENGTH_LONG).show();
        }
        else if(device != null)
        {
            Intent i = new Intent(getApplicationContext(), FarbwerkDevice.class);
            i.putExtra("bt_name", device.getName());
            startActivity(i);
        }
        lock_page = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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


    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
