package com.android.aquacomputer.farbwerk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sebastian Grams on 09.12.2014.
 */
public class FarbwerkColor {

    public FarbwerkColor(){
        scale_r = 1.0;
        scale_g = 0.3;
        scale_b = 0.5;

        r = 0;
        g = 0;
        b = 0;
        output_id = 0;
        fade_time = 0;

        save = false;
    }

    boolean save;

    //color parts
    public double r;
    public double g;
    public double b;

    //scaled to output to adjust the LED
    public double scale_r;
    public double scale_g;
    public double scale_b;

    public int output_id;   //0..3, output 1..4
    public int fade_time;   //fade time milliseconds

    public void setColor(int color)
    {
        int r_int = (color>>16) & 0xff;
        int g_int = (color>>8) & 0xff;
        int b_int = (color>>0) & 0xff;

        r = (1.0 / 255.0) * (double)r_int;
        g = (1.0 / 255.0) * (double)g_int;
        b = (1.0 / 255.0) * (double)b_int;
    }

    private short getScaledDeviceColor(double color, double scale)
    {
        color = 8191.0 * color * scale;   //rescale color
        if(color > 8191) color = 8191;  //check range
        else if(color < 0) color = 0;
        return (short)color;
    }

    public byte[] toBuffer()
    {
        ByteBuffer myBuffer = ByteBuffer.allocate(12);
        myBuffer.order(ByteOrder.BIG_ENDIAN);

        myBuffer.put((byte)1); //start flag

        int output_type = 2;
        output_type |= (output_id << 4);
        myBuffer.put((byte)output_type); //type output

        if(fade_time < 0)
            fade_time = 0;
        else if(fade_time > 32000)
            fade_time = 32000;

        if(save)
            fade_time |= 0x8000;
        myBuffer.putShort((short)fade_time);  //fade time in ms

        short color_device;

        //RED Output
        color_device = getScaledDeviceColor(r, scale_r);
        myBuffer.putShort(color_device); //add color to buffer

        //GREEN Output
        color_device = getScaledDeviceColor(g, scale_g);
        myBuffer.putShort(color_device); //add color to buffer

        //BLUE Output
        color_device = getScaledDeviceColor(b, scale_b);
        myBuffer.putShort(color_device); //add color to buffer

        //add CRC to data
        int crc = CRC16.calculate(myBuffer.array(), 1, 9);
        myBuffer.putShort((short)crc);

        return  myBuffer.array();
    }

}
