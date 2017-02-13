package com.example.controllibrary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Created by guang on 1/22/2017.
 */

public class EV3Commander {
    static final String mac_addr = "001653484c68";

    static final byte  opNop                        = (byte)  0x01;
    static final byte  DIRECT_COMMAND_REPLY         = (byte)  0x00;

    static InputStream in;
    static OutputStream out;



    public static ByteBuffer sendDirectCmd (ByteBuffer operations,
                                            int local_mem, int global_mem)
            throws IOException {
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
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static void printHex(String desc, ByteBuffer buffer) {
        System.out.print(desc + " 0x|");
        for (int i= 0; i < buffer.position() - 1; i++) {
            System.out.printf("%02X:", buffer.get(i));
        }
        System.out.printf("%02X|", buffer.get(buffer.position() - 1));
        System.out.println();
    }



   /* public static void main (String args[] ) {
        try {
            byte[] array = hexStringToByteArray("C008820100842E2E2F70726A732F42726B50726F675F534156452F44656D6F2E7270660060640301606400");
            ByteBuffer operations = ByteBuffer.allocateDirect(1);
            operations.put(array);

            ByteBuffer reply = sendDirectCmd(operations, 0, 0);
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }*/

}
