package com.android.aquacomputer.farbwerk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.lang.InterruptedException;

/**
 * Created by Sebastian Grams on 04.11.2014.
 */
public class BluetoothConnector extends Thread
{
    private Context app_context = null;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothDevice myDevice = null;
    private BluetoothSocket socket = null;
    private InputStream receiveStream = null;
    private OutputStream sendStream = null;
    private AsyncWriter writer = null;
    private int[] send_colors = new int[4];
    private int[] fade_times = new int[4];
    private boolean[] save_data_flag = new boolean[4];
    private boolean[] send_trigger = new boolean[4];
    private boolean stop_request = false;

    public BluetoothConnector(Context ctx)
    {
        //app context is needed for LocalBroadcastManager notifications
        app_context = ctx;

        //my bluetooth adapter
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //reset colors to default
        for(int i=0; i<4;i++)
        {
            send_colors[i] = 0;
            fade_times[i] = 100;
            send_trigger[i] = false;
            save_data_flag[i] = false;
        }
    }

    @Override
    public void run()
    {
        int timeout_count = 0;              //timeout counter
        boolean connection_state = false;   //current connection state
        stop_request = false;
        connect();                          //start connect to device
        while(!stop_request)
        {
            try {
                Thread.sleep(50);   //sleep to prevent high cpu loads
            } catch (InterruptedException e) {}

            if(isConnected() != connection_state)
            {
                //notify UI for changed connection
                connection_state = isConnected();
                if(app_context != null) {
                    //send a message to the device UI class that the connection has changed
                    Intent intent = new Intent("com.android.aquacomputer.farbwerk.connectionchanged");
                    intent.putExtra("connectionstate", connection_state);
                    LocalBroadcastManager.getInstance(app_context).sendBroadcast(intent);
                }

                if(connection_state)
                {
                    //successful reconnection, send all channels again to the device
                    send_trigger[0] = true;
                    send_trigger[1] = true;
                    send_trigger[2] = true;
                    send_trigger[3] = true;
                }
            }

            if(connection_state == false)
                connect();  //try to connect to BT device
            else
                timeout_count++;

            if(connection_state)
            {
                //check incoming data
                int bytes = 0;
                try{
                    if(receiveStream != null)
                    bytes = receiveStream.available();
                }catch(Exception e){}

                if(bytes > 0)
                {
                    timeout_count = 0;
                    try{
                        if(receiveStream != null) {
                            byte[] buffer = new byte[bytes];
                            receiveStream.read(buffer); //readout available bytes
                        }
                    }catch(Exception e){}
                }

                //when timeout is mor than 60cycles with 50ms, device is not
                //connected anymore
                if(timeout_count > 60) {
                    timeout_count = 0;
                    connection_state = false;
                    disconnect();
                }
            }

            if(connection_state) {
                //we are connected, send colors to device when a colors needs an update
                if (send_trigger[0])
                    _sendData(send_colors[0], 0, fade_times[0], save_data_flag[0]);
                if (send_trigger[1])
                    _sendData(send_colors[1], 1, fade_times[1], save_data_flag[1]);
                if (send_trigger[2])
                    _sendData(send_colors[2], 2, fade_times[2], save_data_flag[2]);
                if (send_trigger[3])
                    _sendData(send_colors[3], 3, fade_times[3], save_data_flag[3]);

                save_data_flag[0] = false;
                save_data_flag[1] = false;
                save_data_flag[2] = false;
                save_data_flag[3] = false;
            }
        }
        disconnect();
    }

    public void start_worker()
    {
        if(this.isAlive() == false)
            this.start();
    }

    public void stop_worker()
    {
        stop_request = true;
        try {
            this.join(1000);
        } catch (InterruptedException e) {}
    }

    public void setDevice(String name)
    {
        if(myBluetoothAdapter == null)
            return;
        Set<BluetoothDevice> items = getPaired();
        for(BluetoothDevice d : items)
        {
            if(d.getName().contentEquals(name))
                myDevice = d;
        }
    }

    /**
     * return true if bluetooth is available
     * @return
     */
    public boolean isAvailable()
    {
        if(myBluetoothAdapter == null)
            return false;
        if(!myBluetoothAdapter.isEnabled())
            return false;
        return true;
    }

    public int getEnableRequest()
    {
        return REQUEST_ENABLE_BT;
    }

    public Intent adapterTurnOn()
    {
        Intent turnOnIntent = null;
        if(myBluetoothAdapter != null && !myBluetoothAdapter.isEnabled())
        {
            turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        }
        return turnOnIntent;
    }

    public void  adapterTurnOff()
    {
        if(myBluetoothAdapter != null && myBluetoothAdapter.isEnabled())
            myBluetoothAdapter.disable();
    }

    private void connect()
    {
        if(isConnected() || myDevice == null)
            return;
        try
        {
            socket = myDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            receiveStream = socket.getInputStream();
            sendStream = socket.getOutputStream();
            socket.connect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void disconnect()
    {
        if(isConnected() == false && myDevice != null)
            return;
        try
        {
            if(receiveStream != null)
                receiveStream.close();
            if(sendStream != null)
                sendStream.close();
            if(socket != null)
                socket.close();

            receiveStream = null;
            receiveStream = null;
            sendStream = null;
            socket = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isConnected()
    {
        if(socket == null || !socket.isConnected())
            return false;
        return true;
    }

    public Set<BluetoothDevice> getPaired()
    {
        if(myBluetoothAdapter == null)
            return null;
        Set<BluetoothDevice> items = myBluetoothAdapter.getBondedDevices();
        return items;
    }

    class AsyncWriter extends AsyncTask<FarbwerkColor, FarbwerkColor, FarbwerkColor>
    {
        public BluetoothSocket socket = null;
        public InputStream receiveStream = null;
        public OutputStream sendStream = null;

        @Override
        protected FarbwerkColor doInBackground(FarbwerkColor... color)
        {
            send(color[0]);
            return color[0];
        }

        private void send(FarbwerkColor color)
        {
            if(color == null)
                return;
            byte[] buffer = color.toBuffer();
            try
            {
                if(buffer != null && buffer.length > 0)
                {
                    sendStream.write(buffer);
                    sendStream.flush();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean canSend(){
        if(isConnected() == false)
            return false;
        if(writer == null || writer.getStatus() == AsyncTask.Status.FINISHED)
            return true;
        return false;
    }

    public void sendData(int color, int output_id, int fade_time)
    {
        sendData(color, output_id, fade_time, false);
    }

    public void sendData(int color, int output_id, int fade_time, boolean save_in_device)
    {
        boolean force_update = false;
        if(output_id <0 || output_id > 3)
            return;
        if(send_colors[output_id] != color)
            force_update = true;
        if(fade_times[output_id] != fade_time)
            force_update = true;

        if(force_update) {
            send_colors[output_id] = color;         //new color
            fade_times[output_id] = fade_time;      //new fade time
            send_trigger[output_id] = true;         //tiger to start an color update
            if(save_in_device)
                save_data_flag[output_id] = true;
        }
    }

    /**
     * @param color = color in argb format
     * @param output_id = 0..3 (rgb output 0..3
     * @param fade_time = 0..10000 ms
     */
    private void _sendData(int color, int output_id, int fade_time, boolean save_in_device)
    {
        if(output_id <0 || output_id > 3)
            return;

        if(canSend() == false)
            return;

        if(writer == null || writer.getStatus() == AsyncTask.Status.FINISHED)
        {
            //farbwerk color output object
            FarbwerkColor send_data = new FarbwerkColor();
            send_data.setColor(send_colors[output_id]);
            send_data.save = save_in_device;
            send_data.fade_time = fade_time;
            send_data.output_id = output_id;

            writer = new AsyncWriter();
            writer.socket = socket;
            writer.receiveStream = receiveStream;
            writer.sendStream = sendStream;
            writer.execute(send_data);
            send_trigger[output_id] = false;
        }
    }


}
