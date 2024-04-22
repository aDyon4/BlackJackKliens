package com.example.bjkliens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.*;
import java.util.Stack;

public class HelloController {


    @FXML private ImageView ivLap;
    @FXML private Label lbPont;
    @FXML private ListView<String> lvList;
    @FXML private Button exit;
    @FXML private Button bet;
    @FXML private Button join;
    @FXML private TextField tfId;
    @FXML private TextField tfText;
    @FXML private TextField tfBet;

    DatagramSocket socket = null;
    Image[] lapok = new Image[8];

    int randSzam = (int)(Math.random()*9)+2;
    String[] randBetu = { "C", "D", "H", "S" };
    int randBetuSzam = (int)(Math.random()*3);

    Image lap = new Image(getClass().getResourceAsStream("lapok/"+randSzam+""+randBetu[randBetuSzam]+".png"));
    Image lapHata = new Image(getClass().getResourceAsStream("lapok/gray_back.png"));
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
        tfBet.setDisable(true);
    }

    int osszesPent = 0;
    @FXML private void onClickPressed(){
        osszesPent = 20000;
        String zseton = "join:"+osszesPent;
        String id = tfId.getText();
        //System.out.printf("id: %s", id);
        kuld(zseton, "192.168.1.212", 688);
        exit.setDisable(false);
        bet.setDisable(false);
        tfBet.setDisable(false);
        join.setDisable(true);
    }

    @FXML private void onBetClick(){

        int tet = Integer.parseInt(tfBet.getText());
        if(tet<=osszesPent){
            ivLap.setImage(lap);
            lbPont.setText(randSzam+"");
            kuld("bet:"+tet, "192.168.1.212", 688);
        }
        else System.out.println("nincs ennyi pénzed");
    }

    @FXML private void onPlusPressed(){
            kuld("plus:"+100, "192.168.1.212", 688);
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
        kuld("exit", "192.168.1.212", 688);
        ivLap.setImage(lapHata);
        exit.setDisable(true);
        bet.setDisable(true);
        tfBet.setDisable(true);
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