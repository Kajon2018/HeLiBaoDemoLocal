package com.aeye.helibao;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/5/5.
 */

public class GpioControl {
    public  static  Thread   thread;
    public static  String   sys_path="/sys/devices/platform/maidi_sysfs";
    public static  String   kai_men="kai_men_shu_chu";
    public static int put( String path, String  jiedian,  char c) {

        File file = new File(path, jiedian);
        try {
            //鍚戞枃浠跺啓鍏ユ敞鍐屼俊鎭�
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(c);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return -1;
        }
        return -1;
    }
    public static int get(String path, String  jiedian) {

        File file = new File(path, jiedian);
        try {
            byte[] readBytes = new byte[1];
            //鍚戞枃浠跺啓鍏ユ敞鍐屼俊鎭�
            FileInputStream fos = new FileInputStream(file);

            fos.read(readBytes);
           return readBytes[0];
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return -1;
        }

    }
    public static int openDoor(){
        if (thread != null && thread.isAlive())
            return  0;
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                put(sys_path,kai_men,'1');
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                put(sys_path,kai_men,'0');
            }
        });
        thread.run();

        return  0;
    }
//    public static int closeDoor(){
//        put(sys_path,kai_men,'0');
//        return  0;
//    }
}
