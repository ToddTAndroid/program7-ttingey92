// Todd Tingey
package edu.uwyo.toddt.battlebots;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


/**
 *
 *
 */
public class MainFragment extends Fragment implements Button.OnClickListener {
    // Fragment variables
    TextView output, output2;
    ViewSwitcher mySwitcher;
    Button mkconn, moveNW, moveN, moveNE, moveW, moveE, moveSW, moveS, moveSE, btnFire, idle, btnScan, btnFireP;
    EditText hostname, port, botname, bulletVal, armourVal, scanVal;
    Thread myNet;
    String name, armour, bullet, scan;
    String botMessage, strCommand;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_main, container, false);
        output = (TextView) myView.findViewById(R.id.view_output);
        output2 = (TextView) myView.findViewById(R.id.view_output2);
        mySwitcher = (ViewSwitcher) myView.findViewById(R.id.vSwitcher);
        hostname = (EditText) myView.findViewById(R.id.edit_hostname);
        hostname.setText("10.121.130.246"); // Default IP for testing purposes
        port = (EditText) myView.findViewById(R.id.edit_port);
        port.setText("3012"); // Default port
        botname = (EditText) myView.findViewById(R.id.edit_botname);
        bulletVal = (EditText) myView.findViewById(R.id.edit_bulletVal);
        armourVal = (EditText) myView.findViewById(R.id.edit_armourVal);
        scanVal = (EditText) myView.findViewById(R.id.edit_scanVal);

        // Buttons
        mkconn = (Button) myView.findViewById(R.id.btn_mkconn);
        mkconn.setOnClickListener(this);


        moveNW = (Button) myView.findViewById(R.id.btn_mv_NW);
        moveNW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move -1 -1";
            }
        });

        moveN = (Button) myView.findViewById(R.id.btn_mv_N);
        moveN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move 0 -1";
            }
        });

        moveNE = (Button) myView.findViewById(R.id.btn_mv_NE);
        moveNE.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move 1 -1";
            }
        });

        moveW = (Button) myView.findViewById(R.id.btn_mv_W);
        moveW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move -1 0";
            }
        });

        moveE = (Button) myView.findViewById(R.id.btn_mv_E);
        moveE.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move 1 0";
            }
        });

        moveSE = (Button) myView.findViewById(R.id.btn_mv_SE);
        moveSE.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move 1 1";
            }
        });

        moveS = (Button) myView.findViewById(R.id.btn_mv_S);
        moveS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move 0 1";
            }
        });

        moveSW = (Button) myView.findViewById(R.id.btn_mv_SW);
        moveSW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "move -1 1";
            }
        });

        btnFire = (Button) myView.findViewById(R.id.btn_fire);
        btnFire.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "fire";
            }
        });

        // This button is not visible in the app itself, but for some reason when I
        // exlude this button from my
        btnFireP = (Button) myView.findViewById(R.id.btn_fire_p);
        btnFireP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "fire p";
            }
        });

        idle = (Button) myView.findViewById(R.id.btn_idle);
        idle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "noop";
            }
        });

        btnScan = (Button) myView.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                strCommand = "scan";
            }
        });


        return myView;
    }


    @Override
    public void onClick(View v){
        name = botname.getText().toString();
        bullet = bulletVal.getText().toString();
        scan = scanVal.getText().toString();
        armour = armourVal.getText().toString();
        mySwitcher.showNext();

        doNetwork stuff = new doNetwork();
        myNet = new Thread(stuff);
        myNet.start();
    }

    private Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            output.append(msg.getData().getString("msg"));
            output2.append(msg.getData().getString("msg"));
            return true;
        }
    });

    public void mkmsg(String str){
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    class doNetwork implements Runnable {
        public PrintWriter out;
        public BufferedReader in;
        String botSpecs = name + " " + armour + " " + bullet + " " + scan;

        public void run(){

            int p = Integer.parseInt(port.getText().toString());
            String h = hostname.getText().toString();
            mkmsg("Host is " + h + "\n");
            mkmsg("Port is " + p + "\n");
            try {
                InetAddress serverAddr = InetAddress.getByName(h);
                mkmsg("Attempt Connecting..." + h + "\n");
                Socket socket = new Socket(serverAddr, p);

                // Connected, setup read and write
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                // Complete initial message set up, and begin loop
                try {
                    mkmsg("Attempting to receive a message... \n");
                    String str = in.readLine();
                    mkmsg("received a message: \n" + str + "\n");

                    mkmsg("Sending bot details... \n");
                    out.println(botSpecs);
                    mkmsg("Sent specs: " + botSpecs + "\n");

                    mkmsg("Attempting to receive Bot info... \n");
                    str = in.readLine();
                    mkmsg("received final specs: \n" + str + "\n");

                    strCommand = "noop";

                    // Variable for my loop
                    Boolean dead = false;

                    // player, powerup, and enemy positions
                    int foeX=100, foeY=100; // Just set to some value until a scan finds an enemy
                    int pX=100, pY=100;
                    int myX=0, myY=0;

                    do {
                        // set the message as the current command
                        botMessage = strCommand;
                        str = in.readLine();

                        if(str.equals("Info Dead") || str.equals("Info GameOver")) {
                            dead = true;

                        } else {

                            String[] servMsg = str.split("\\s+");
                            String[] clientMsg = botMessage.split("\\s+");

                            if(servMsg[0].equals("Status")){
                                if(!servMsg[3].equals("0") && clientMsg[0].equals("move")){
                                    botMessage = "noop";
                                }

                                if(!servMsg[4].equals("0") && clientMsg[0].equals("fire")){
                                    botMessage = "noop";

                                }

                                myX = Integer.parseInt(servMsg[1]);
                                myY = Integer.parseInt(servMsg[2]);
                            }
                            else if(servMsg[0].equals("Info")){
                                botMessage = "noop";
                            }
                            else if(servMsg[0].equals("scan")) {
                                String scanLine = str;
                                String[] scanArray = servMsg;
                                mkmsg("Receiving scan data... \n");

                                // Loop through each scan message until 'done' is read
                                 while (!scanArray[1].equals("done")){
                                     // check for enemies
                                     if(scanArray[1].equals("bot")){
                                         foeX = Integer.parseInt(servMsg[3]);
                                         foeY = Integer.parseInt(servMsg[4]);
                                     }
                                     if(scanArray[1].equals("powerup")){
                                         pX = Integer.parseInt(servMsg[3]);
                                         pY = Integer.parseInt(servMsg[4]);
                                     }

                                     mkmsg(scanLine + "\n");
                                     scanLine = in.readLine();
                                     scanArray = scanLine.split("\\s+");

                                }

                                mkmsg("End of scan data... \n");

                                // Ensure no additional scan is requested
                                scanLine = in.readLine();
                                botMessage = "noop";
                                strCommand = "noop";
                            }

                            // adds the appropriate angle to the fire command
                            if(botMessage.equals("fire")){
                                int angle = Angle(myX, myY, foeX, foeY) + 90;
                                botMessage = "fire " + Integer.toString(angle);

                            }
                            if (botMessage.equals("fire p")){
                                int angle = Angle(myX, myY, pX, pY) + 90;
                                botMessage = "fire " + Integer.toString(angle);
                            }


                            out.println(botMessage);
                        }

                    }
                    while(!dead);

                    mkmsg("We are done, closing connection\n");
                } catch (Exception e) {
                    mkmsg("Error happened sending/receiving\n");

                } finally {
                    in.close();
                    out.close();
                    socket.close();
                }

            } catch (Exception e){
                mkmsg("Unable to connect... \n");
            }
        }
    }

    public int Angle(int x1, int y1, int x2, int y2) {
        float dx = (float) (x2-x1);
        float dy = (float) (y2-y1);
        double angle=0.0d;

        // Calculate angle
        if (dx == 0.0) {
            if (dy == 0.0)
                angle = 0.0;
            else if (dy > 0.0)
                angle = Math.PI / 2.0;
            else
                angle = Math.PI * 3.0 / 2.0;
        } else if (dy == 0.0) {
            if  (dx > 0.0)
                angle = 0.0;
            else
                angle = Math.PI;
        } else {
            if  (dx < 0.0)
                angle = Math.atan(dy/dx) + Math.PI;
            else if (dy < 0.0)
                angle = Math.atan(dy/dx) + (2*Math.PI);
            else
                angle = Math.atan(dy/dx);
        }

        // Convert to degrees
        angle = angle * 180 / Math.PI;

        // Return
        return (int) angle;
    }
}
