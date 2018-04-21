package com.Jim.ftp;

import java.net.ServerSocket;

public class FTPServer {
    public static void main(String args[]) throws Exception
    {
        ServerSocket serverSocket =new ServerSocket(21);
        System.out.println("FTP Server Started on Port Number 21");
        while(true)
        {
            System.out.println("Waiting for Connection ...");
            ftp t =new ftp(serverSocket.accept());
        }
    }
}

