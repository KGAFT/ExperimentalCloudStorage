package com.KGAFT.ClientCloudStorage;

import java.io.*;
import java.net.Socket;

public class Main {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static final String PATH = "C:/Program Files (x86)/ExperimentalCloudClient";
    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost",5000)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            if(args[0].contains("get")){
                saveFiles();
                dataInputStream.close();
                dataOutputStream.close();
            }
            else if(args[0].contains("upload")){
                dataOutputStream.writeUTF("upload");
                File file = new File("D:/user/documents/books");
                File[] toSend = file.listFiles();
                dataOutputStream.writeInt(toSend.length);
                for(File element  : toSend){
                    sendFile(element);
                }
                dataInputStream.close();
                dataOutputStream.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void sendFile(File file) throws Exception{
        int bytes = 0;
        FileInputStream fileInputStream = new FileInputStream(file);
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
    private static void saveFiles(){
        try{
            int lastBytes = 0;
            String name = dataInputStream.readUTF();
            FileOutputStream fileWriter = new FileOutputStream(PATH+name);
            long size = dataInputStream.readLong();     // read file size
            byte[] buffer = new byte[4*1024];
            while (size > 0 && (lastBytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileWriter.write(buffer,0,lastBytes);
                size -= lastBytes;      // read upto file size
            }
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
