package com.KGAFT.CloudStorage;

import com.KGAFT.CloudStorage.Server.RequestHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        RequestHandler handler = new RequestHandler();
        handler.run();
    }


}