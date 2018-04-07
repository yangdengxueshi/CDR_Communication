package com.dexin.javalib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * TCP多线程服务器
 */
public final class MyServer {
    private static List<Socket> sSocketList = new ArrayList<>();//定义保存所有Socket的List

    private MyServer() {
    }

    public static void main(String[] args) throws IOException {
        try (ServerSocket lServerSocket = new ServerSocket(30000)) {
            while (true) {
                Socket lSocket = lServerSocket.accept();//此代码会阻塞,将一直等待客户端的链接
                sSocketList.add(lSocket);
                new Thread(new ServerRunnable(lSocket)).start();//每当客户端连接后启动一条ServerThread线程为该客户端服务
            }
        }
    }

    /**
     * 负责处理每个线程通信的线程类
     */
    static class ServerRunnable implements Runnable {
        private Socket mSocket;//定义当前线程所处理的Socket
        private BufferedReader mBufferedReader;//该线程所处理的Socket所对应的输入流

        ServerRunnable(Socket socket) throws IOException {
            mSocket = socket;
            mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));//初始化该socket对应的输入流 ①
        }

        @Override
        public void run() {
            String content;
            while ((content = readFromClient()) != null) {//采用循环不断从Socket中
                System.out.println(MessageFormat.format("某个客户端发送过来的消息:{0}", content));
                for (Iterator<Socket> lIterator = sSocketList.iterator(); lIterator.hasNext(); ) {//遍历socketList中的每个Socket,将读到的内容向每个Socket发送一次
                    Socket lSocket = lIterator.next();
                    try {
                        OutputStream lOutputStream = lSocket.getOutputStream();
                        lOutputStream.write((MessageFormat.format("{0}\n", content)).getBytes("UTF-8"));
                        System.out.println(MessageFormat.format("群发某个客户端发送过来的消息{0}", new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA).format(new Date())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        lIterator.remove();//删除该Socket
                        System.out.println(sSocketList);
                    }
                }
            }
        }

        /**
         * 定义从客户端读取数据的方法
         *
         * @return 从客户端读取过来的数据
         */
        private String readFromClient() {
            try {
                return mBufferedReader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                sSocketList.remove(mSocket);//如果捕获到异常,表明该Socket对应的客户端已经关闭,则删除该Socket ②
            }
            return null;
        }
    }
}
