package com.KGAFT.CloudStorage.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestHandler extends Thread{
    private static final String PATH = "C:/Program Files (x86)/ExperimentalCloud/";
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    @Override
    public void run() {
        super.run();
        while(true){
            try(ServerSocket serverSocket = new ServerSocket(5000)){
                System.out.println("listening to port:5000");
                Socket clientSocket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            dataInputStream = new DataInputStream(clientSocket.getInputStream());
                            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                            String request = dataInputStream.readUTF();
                            if(request.contains("upload")){
                                System.out.println(clientSocket+" connected.");
                                startDownloadingInfo(dataInputStream);

                            }
                            else if(request.contains("get")){
                                upLoadAllInfo(dataOutputStream);
                            }
                            dataInputStream.close();
                            dataOutputStream.close();
                            clientSocket.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void startDownloadingInfo(DataInputStream userStream) throws Exception {
        //This method is reading such amount of files
        int amount = dataInputStream.readInt();
        for(int counter = 0; counter<amount; counter++){
            downloadInfo(dataInputStream);
        }
    }
    private void downloadInfo(DataInputStream userStream) throws Exception {
        //This method getting files and saves it to storage
        int lastBytes = 0;
        String name = userStream.readUTF();
        FileOutputStream fileWriter = new FileOutputStream(PATH+name);
        long size = userStream.readLong();     // read file size
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (lastBytes = userStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileWriter.write(buffer,0,lastBytes);
            size -= lastBytes;      // read upto file size
        }
        fileWriter.close();
    }
    private void upLoadAllInfo(DataOutputStream userOutput) throws IOException {
        //This method sending all files via method to send one file
        File file = new File(PATH);
        File[] toSend = file.listFiles();
        dataOutputStream.writeInt(toSend.length);
        for(File element  : toSend){
            sendFile(element, userOutput);
        }
    }
    private void sendFile(File file, DataOutputStream dataOutputStream) throws IOException {
        //Sending one file
        int bytes = 0;
        FileInputStream fileInputStream = new FileInputStream(file);
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();

    }
}
