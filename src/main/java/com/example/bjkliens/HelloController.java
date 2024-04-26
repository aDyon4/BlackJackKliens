package com.example.bjkliens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.*;
import java.util.Stack;

public class HelloController {

    @FXML private Pane pnLapok;
    @FXML private ImageView ivLap;
    @FXML private Label lbPont;
    @FXML private Label lbTet;
    @FXML private Label lbOt;
    @FXML private Label lbHuszonot;
    @FXML private Label lbOtven;
    @FXML private Label lbSzaz;
    @FXML private Label lbHit;
    @FXML private Label lbStand;

    @FXML private ListView<String> lvList;
    @FXML private Button exit;
    @FXML private Button bet;
    @FXML private Button join;

    @FXML private TextField tfId;
    @FXML private TextField tfText;

    DatagramSocket socket = null;
    Image[] lapok = new Image[8];

    int randSzam = 0;
    String[] randBetu = {"C", "D", "H", "S"};
    int randBetuSzam = 0;
    //Image lap = new Image(getClass().getResourceAsStream("lapok/" + randSzam + "" + randBetu[randBetuSzam] + ".png"));
    Image lapHata = new Image(getClass().getResourceAsStream("lapok/gray_back.png"));
    int osszesPenz = 20000;
    int plusTet = 0;
    int tetOtven = 0, tetSzaz = 0, tetKettoOtven = 0, tetOtszaz = 0;


    public void initialize() {

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
        lbOt.setDisable(true);
        lbHuszonot.setDisable(true);
        lbOtven.setDisable(true);
        lbSzaz.setDisable(true);
        lbHit.setDisable(true);
        lbStand.setDisable(true);
    }


    @FXML private void onClickPressed() {
        String zseton = "join:" + osszesPenz;
        String id = tfId.getText();
        //System.out.printf("id: %s", id);
        kuld(zseton, "192.168.1.212", 688);
        join.setDisable(true);
        exit.setDisable(false);
        bet.setDisable(false);
        lbOt.setDisable(false);
        lbHuszonot.setDisable(false);
        lbOtven.setDisable(false);
        lbSzaz.setDisable(false);
        lbHit.setDisable(true);
        lbStand.setDisable(true);

    }
    /*private void setUjLap(String szin, int szam){
        ivLap.setImage(new Image(getClass().getResourceAsStream("lapok/" + szam + "" + szin + ".png")));
        lbPont.setText(szam + "");
    }*/
    @FXML private void onBetClick() {
        int tet = Integer.parseInt(lbTet.getText());
        if (tet <= osszesPenz) {
            //setUjLap(randBetu[randBetuSzam], randSzam);
            /*ivLap.setImage(lap);*/
            randSzam+=Integer.parseInt(lbPont.getText());
            /*lbPont.setText(randSzam + "");*/
            kuld("bet:" + tet, "192.168.1.212", 688);
        } else System.out.println("nincs ennyi pénzed");
        lapokAdasa();lapokAdasa();
        lbOt.setDisable(true);
        lbHuszonot.setDisable(true);
        lbOtven.setDisable(true);
        lbSzaz.setDisable(true);
        lbHit.setDisable(false);
        lbStand.setDisable(false);
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

    @FXML
    private void onExitClick() {
        kuld("exit", "192.168.1.212", 688);
        pnLapok.getChildren().clear();
        lbTet.setText("0");
        ivLap.setImage(lapHata);
        plusTet = 0;
        join.setDisable(false);
        exit.setDisable(true);
        bet.setDisable(true);
        lbOt.setDisable(true);
        lbHuszonot.setDisable(true);
        lbOtven.setDisable(true);
        lbSzaz.setDisable(true);
        lbHit.setDisable(true);
        lbStand.setDisable(true);
    }

    private void fogad() {
        byte[] data = new byte[256];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true) {
            try {
                socket.receive(packet);
                String uzenet = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                String ip = packet.getAddress().getHostAddress();
                int port = packet.getPort();
                Platform.runLater(() -> onFogad(uzenet, ip, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onFogad(String uzenet, String ip, int port) {
        System.out.printf("%s", uzenet);
        String[] s = uzenet.split(":");
        if (s[0].equals("joined")) {
            String tet = s[1];
            System.out.printf("ÖSSZEG: %s", tet);
            kuld(tet, ip, port);
        }
        if(s[0].equals("paid")){
            osszesPenz = Integer.parseInt(s[1]);
            System.out.printf("\n%d pénzed maradt\n", osszesPenz);
        }
    }

    int db = 0;
    int lapokSzam = 0;
    @FXML private void onHitPressed() {
        kuld("hit:" + plusTet, "192.168.1.212", 688);
        lapokAdasa();
    }
    @FXML private void onOtPressed() {
        if(osszesPenz > 5) { tetKettoOtven++; osszesPenz-=5; plusTet+=5; lbTet.setText(plusTet+""); }
        else { lbTet.setText("Nincs elegendő pénzed"); }
    }
    @FXML private void onHuszonotPressed() {
        if(osszesPenz > 25) { tetSzaz++; osszesPenz-=25; plusTet+=25; lbTet.setText(plusTet+"");}
        else { lbTet.setText("Nincs elegendő pénzed"); }
    }
    @FXML private void onOtvenPressed() {
        if(osszesPenz > 50) { tetOtven++; osszesPenz-=50; plusTet+=50; lbTet.setText(plusTet+"");}
        else { lbTet.setText("Nincs elegendő pénzed"); }
    }
    @FXML private void onSzazPressed() {
        if(osszesPenz > 100) { tetOtszaz++; osszesPenz-=100; plusTet+=100; lbTet.setText(plusTet+"");}
        else { lbTet.setText("Nincs elegendő pénzed"); }
    }


    private void lapokAdasa(){
        int randSzam = (int) (Math.random() * 9) + 2;
        randSzam = (int) (Math.random() * 9) + 2;
        int randBetuSzam = (int) (Math.random() * 3);
        ImageView kartya = new ImageView( new Image(getClass().getResourceAsStream("lapok/" + randSzam + "" + randBetu[randBetuSzam] + ".png")));
        lapokSzam = randSzam+Integer.parseInt(lbPont.getText());
        db++;
        kartya.setFitWidth(120);
        kartya.setFitHeight(183);
        kartya.setX(10+60*db);
        lbPont.setText(lapokSzam + "");
        pnLapok.getChildren().add(kartya);
    }
}

