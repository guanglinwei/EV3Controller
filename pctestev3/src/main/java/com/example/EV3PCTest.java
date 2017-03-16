package com.example;

import java.io.*;
import javax.microedition.io.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.EV3.*;


public class EV3PCTest {
//    static final String mac_addr = "001653484c68";
    static final String mac_addr = "001653493acd";
    private InputStream in;
    private OutputStream out;

    public void connectBluetooth ()
            throws IOException {
        String s = "btspp://" + mac_addr + ":1";
        StreamConnection c = (StreamConnection) Connector.open(s);
        in = c.openInputStream();
        out = c.openOutputStream();
    }

    public static void main (String args[] ) {
        try {

            EV3PCTest pctest = new EV3PCTest();
            pctest.connectBluetooth();
//            byte[] array = hexStringToByteArray("C008820100842E2E2F70726A732F42726B50726F675F534156452F44656D6F2E7270660060640301606400");
//            ByteBuffer operations = ByteBuffer.allocateDirect(1);
//            operations.put(array);

            EV3 ev3 = new EV3(pctest.in, pctest.out);
//            ByteBuffer reply = ev3.doNothing();
//            ByteBuffer reply = ev3.setBrickName("abc");
//            ByteBuffer reply = ev3.setColor(EV3.GREEN);
//            ByteBuffer reply = ev3.shutdown();
//            ByteBuffer reply = ev3.runFirstProgram();
//            ByteBuffer reply = ev3.playTone(100, 440, 1000);
//            ByteBuffer reply = ev3.playSoundFile("./ui/DownloadSucces", 100);
//            ByteBuffer reply = ev3.playChordC(100, 1000);
//            ByteBuffer reply = ev3.openDrawing("../apps/Motor Control/MotorCtlAD.rgf", 0, 0);
//            ByteBuffer reply = ev3.fillPart(1, 0, 0);
//            ByteBuffer reply = ev3.startProgram("../prjs/BrkProg_SAVE/Demo.rpf");
//            ByteBuffer reply = ev3.startMotor(50);
//            ev3.printHex("got reply:", reply);
//            Thread.sleep(10000);
//            ByteBuffer replyb = ev3.stopMotor();
//            System.out.println(EV3.MOTOR_B + EV3.MOTOR_C);
            ByteBuffer reply = ev3.moveMotor(-50, (byte) (EV3.MOTOR_B + EV3.MOTOR_C), 5);
            ev3.printHex("got reply: ", reply);

        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }


}
