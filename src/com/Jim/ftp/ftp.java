package com.Jim.ftp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ftp extends Thread{

    Socket ClientSocket;
    DataInputStream din;
    DataOutputStream dout;
    ftp(Socket soc)
    {
        try
        {
            ClientSocket=soc;
            din=new DataInputStream(ClientSocket.getInputStream());
            dout=new DataOutputStream(ClientSocket.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();

        }
        catch(Exception ex)
        {
        }
    }
    void SendFile() throws Exception
    {
        String filename=din.readUTF();
        File file = new File(filename);
        if(!file.exists())
        {
            dout.writeUTF("File Not Found");
            return;
        }
        else
        {
            dout.writeUTF("READY");
            FileInputStream fin = new FileInputStream(file);
            int number;
            do
            {
                number=fin.read();
                dout.writeUTF(String.valueOf(number));
            }
            while(number!=-1);
            fin.close();
            dout.writeUTF("File Receive Successfully");
        }
    }

    void ReceiveFile() throws Exception
    {
        String filename= din.readUTF();
        if(filename.compareTo("File not found")==0)
        {
            return;
        }
        File f=new File(filename);
        String option;

        if(f.exists())
        {
            dout.writeUTF("File Already Exists");
            option=din.readUTF();
        }
        else
        {
            dout.writeUTF("SendFile");
            option="Y";
        }

        if(option.compareTo("Y")==0)
        {
            FileOutputStream fout=new FileOutputStream(f);
            int ch;
            String temp;
            do
            {
                temp=din.readUTF();
                ch=Integer.parseInt(temp);
                if(ch!=-1)
                {
                    fout.write(ch);
                }
            }while(ch!=-1);
            fout.close();
            dout.writeUTF("File Send Successfully");
        }
        else
        {
            return;
        }

    }

    void BrowseDir() throws Exception
    {
        String filename=din.readUTF();
        if(filename.equalsIgnoreCase("back")){
            dout.writeUTF("Initial Point");
            return;
        }
        File folder = new File(filename);
        ArrayList<String> files = new ArrayList<String>();
        ArrayList<String> directories = new ArrayList<String>();
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                directories.add(listOfFiles[i].getName());
            }
        }

        String dir="Directories:\n";
        for(int i=0;i<directories.size();i++){
            dir=dir+""+(i+1)+":"+directories.get(i)+"\n";
        }
        dir=dir+"\n\nFiles\n";
        for(int i=0;i<files.size();i++){
            dir= dir+""+(i+1)+":"+files.get(i)+"\n";
        }
        dout.writeUTF(dir);
        return;
    }

    void verify(){
        while(true){
            try {
                String usr = din.readUTF();
                String pass =din.readUTF();
                File fl = new File("C:\\users.txt");
                FileInputStream fi= new FileInputStream(fl);
                DataInputStream di=new DataInputStream(fi);
                String str=null;
                boolean validation =false;
                str=di.readLine();
                while(true){
                    if(str==null) break;
                    String pas = di.readLine();
                    if(str.equals(usr) && pas.equals(pass)){
                        validation=true;
                        break;
                    }
                    str=di.readLine();
                }
                if(validation){
                    dout.writeUTF("login successful");
                    break;
                }
                else{
                    dout.writeUTF("login failed");
                }
                System.out.println("reached");
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }
    public void run()
    {

        verify();

        while(true)
        {
            try
            {
                String Command=din.readUTF();
                if(Command.compareTo("RECEIVE")==0)
                {
                    SendFile();
                    continue;
                }
                else if(Command.compareTo("SEND")==0)
                {
                    ReceiveFile();
                    continue;
                }
                else if(Command.compareTo("LIST")==0)
                {
                    BrowseDir();
                    continue;
                }
                else if(Command.compareTo("DISCONNECT")==0)
                {
                    System.exit(1);
                }
            }
            catch(Exception ex)
            {
            }
        }
    }
}
