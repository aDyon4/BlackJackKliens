package com.example.bjkliens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.*;

public class HelloController {

    @FXML private TextField tfText;
    @FXML private ListView<String> lvList;
    @FXML private TextField tfId;
    @FXML private Button exit;
    @FXML private Button bet;
    @FXML private Button join;
    @FXML private TextField tfBet;

    DatagramSocket socket = null;

    public void initialize(){
        try {
            socket = new DatagramSocket(678);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                fogad();
            }
        });
        t.setDaemon(true);
        t.start();
        join.setDisable(false);
        exit.setDisable(true);
        bet.setDisable(true);
    }

    int osszesPent = 0;
    @FXML private void onClickPressed(){
        osszesPent = 20000;
        String zseton = "join:"+osszesPent;
        String id = tfId.getText();
        //System.out.printf("id: %s", id);
        kuld(zseton, "10.201.2.5", 678);
        exit.setDisable(false);
        bet.setDisable(false);
        join.setDisable(true);
    }

    @FXML private void onBetClick(){
        int tet = Integer.parseInt(tfBet.getText());
        if(tet<=osszesPent){ kuld("bet:"+tet, "10.201.2.5", 678); }
        else System.out.println("nincs ennyi pénzed");

    }

    private void kuld(String uzenet, String ip, int port) {
        try {
            byte[] adat = uzenet.getBytes("utf-8");
            InetAddress ipv4 = Inet4Address.getByName(ip);
            DatagramPacket packet = new DatagramPacket(adat, adat.length, ipv4, port);
            socket.send(packet);
            System.out.printf("%s:%d -> %s\n", ip, port, uzenet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onExitClick(){
        kuld("exit", "10.201.2.5", 678);
        exit.setDisable(true);
        bet.setDisable(true);
        join.setDisable(false);
    }

    private void fogad() {
        byte[] data = new byte[256];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true){
            try {
                socket.receive(packet);

                String uzenet = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                String ip  = packet.getAddress().getHostAddress();
                int port = packet.getPort();
                Platform.runLater(() -> onFogad(uzenet, ip, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onFogad(String uzenet, String ip, int port){
        System.out.printf("%s", uzenet);
        String[] s = uzenet.split(":");

        if(s[0].equals("joined")){
            String tet = s[1];
            System.out.printf("ÖSSZEG: %s", tet);
            kuld(tet, ip , port);
        }
    }



}