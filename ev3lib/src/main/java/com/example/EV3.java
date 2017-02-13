package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EV3 {
    public static final byte  opNop                        = (byte)  0x01;
    public static final byte  DIRECT_COMMAND_REPLY         = (byte)  0x00;
    public static final byte SET_BRICKNAME                 = (byte)  0x08;
    public static final byte opCom_Set                     = (byte)  0xD4;
    public static final byte LCS_HEADER                    = (byte)  0x84;
    public static final byte LCS_FOOTER                    = (byte)  0x00;
    public static final byte opUI_Write                    = (byte)  0x82;
    public static final byte LED                           = (byte)  0x1B;
    public static final byte GREEN                         = (byte)  0x01;
    public static final byte RED                           = (byte)  0x02;
    public static final byte opUI_Button                   = (byte)  0x83;
    public static final byte PRESS                         = (byte)  0x05;
    public static final byte WAIT_FOR_PRESS                = (byte)  0x03;
    public static final byte BACK_BUTTON                   = (byte)  0x06;
    public static final byte RIGHT_BUTTON                  = (byte)  0x04;
    public static final byte ENTER_BUTTON                  = (byte)  0x02;
    public static final byte DOWN_BUTTON                   = (byte)  0x05;
    public static final byte opSound                       = (byte)  0x94;
    public static final byte TONE                          = (byte)  0x01;
    public static final byte PLAY                          = (byte)  0x02;

    private InputStream in;
    private OutputStream out;

    public EV3(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public ByteBuffer sendDirectCmd (ByteBuffer operations, int local_mem, int global_mem) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(operations.position() + 7);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) (operations.position() + 5));   // length
        buffer.putShort((short) 42);                            // counter
        buffer.put(DIRECT_COMMAND_REPLY);                       // type
        buffer.putShort((short) (local_mem*1024 + global_mem)); // header
        for (int i=0; i < operations.position(); i++) {         // operations
            buffer.put(operations.get(i));
        }

        byte[] cmd = new byte [buffer.position()];
        for (int i=0; i<buffer.position(); i++) cmd[i] = buffer.get(i);
        out.write(cmd);
        printHex("Sent", buffer);

        byte[] reply = new byte[global_mem + 5];
        in.read(reply);
        buffer = ByteBuffer.wrap(reply);
        buffer.position(reply.length);
        printHex("Recv", buffer);

        return buffer;
    }
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public void printHex(String desc, ByteBuffer buffer) {
        System.out.print(desc + " 0x|");
        for (int i= 0; i < buffer.position() - 1; i++) {
            System.out.printf("%02X:", buffer.get(i));
        }
        System.out.printf("%02X|", buffer.get(buffer.position() - 1));
        System.out.println();
    }

    public ByteBuffer doNothing() throws IOException {
        ByteBuffer operations = ByteBuffer.allocateDirect(1);
        operations.put(opNop);
        ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        return reply;
    }
    public ByteBuffer setBrickName(String name) throws IOException {
        byte[] lcsReturn = LCS(name);
        ByteBuffer operations = ByteBuffer.allocateDirect(2+lcsReturn.length);
        operations.put(opCom_Set);
        operations.put(SET_BRICKNAME);
        operations.put(lcsReturn);
        ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        return reply;
    }
    public ByteBuffer setColor(byte color) throws IOException {
        ByteBuffer operations = ByteBuffer.allocateDirect(3);
        operations.put(opUI_Write);
        operations.put(LED);
        operations.put(color);
        ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        return reply;
    }
    public ByteBuffer shutdown() throws IOException {
        ByteBuffer operations = ByteBuffer.allocateDirect(13);
        operations.put(opUI_Button);
        operations.put(PRESS);
        operations.put(BACK_BUTTON);
        operations.put(opUI_Button);
        operations.put(WAIT_FOR_PRESS);
        operations.put(opUI_Button);
        operations.put(PRESS);
        operations.put(RIGHT_BUTTON);
        operations.put(opUI_Button);
        operations.put(WAIT_FOR_PRESS);
        operations.put(opUI_Button);
        operations.put(PRESS);
        operations.put(ENTER_BUTTON);
        ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        return reply;
    }

    public ByteBuffer playTone(Integer vol, Integer freq, Integer dur) throws IOException {
        byte[] volume=LCX(vol);
        byte[] frequency = LCX(freq);
        byte[] duration = LCX(dur);
        ByteBuffer operations = ByteBuffer.allocateDirect(2+volume.length+frequency.length+duration.length);
        operations.put(opSound);
        operations.put(TONE);
        operations.put(volume);
        operations.put(frequency);
        operations.put(duration);
        ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        return reply;
    }

    public ByteBuffer playSoundFile(String name, int volume) throws IOException{
        byte[] fileName = LCS(name);
        byte[] soundVol = LCX(volume);
        ByteBuffer operations = ByteBuffer.allocateDirect(2+fileName.length+soundVol.length);
        operations.put(opSound);
        operations.put(PLAY);
        operations.put(soundVol);
        operations.put(fileName);
        ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        return reply;
    }
    private byte[] LCS(String name) {
        byte[] ev3name = name.getBytes();
        byte[] destination = new byte[ev3name.length + 2];
        destination[0] = LCS_HEADER;
        for (int i = 1; i < destination.length -1; i++)
        {
            destination[i] = ev3name[i-1];
        }
        destination[ev3name.length+1] = LCS_FOOTER;
        return destination;
    }

    private byte[] LCX(int num) {
        if (num >= 0 && num < 32) {
            byte b =(byte)num;
            byte[] result = new byte[1];
            result[0] = b;
            return result;
        }
        else if (num>=-127 && num<=127){
            byte b = (byte)num;
            byte[]result = new byte[2];
            result[0] = (byte) 0x81;
            result[1] = b;
            return result;
        }

        return null;
    }

    public static void main (String args[] ) {
        EV3 ev3 = new EV3(null, null);
        byte[] result = ev3.LCX(31);
        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }
    }
    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

}
