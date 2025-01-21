//
// basis for java graphic animation
//

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

import java.time.*;

import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

// -----------------------------------------------------------------------------
// steam locomotive backend

public class trkSensor extends JPanel
        implements MouseListener, KeyListener, Runnable
{
    // ----------------------------------------------------
    JFrame frame = new JFrame ();

    final int Wid = 1200;
    final int Ht  =  800;

    int       keyVal;
    int       tics;
    int       idx;

    int       vecA []      = new int [Wid];
    int       vecB []      = new int [Wid];
    int       vecC []      = new int [Wid];

    int       vecD []      = new int [Wid];
    int       vecE []      = new int [Wid];
    int       vecF []      = new int [Wid];

    int       offA;
    int       offB;
    int       offC;

    int       offD;
    int       offE;
    int       offF;

    int       SegLen []    = { Wid/3, Wid/3, Wid/8 };
    int       x            = Wid / 2;
    int       y            = Ht / 2;
    int       servoIdx     = 0;

    // ----------------------------------------------------
    public static void main (String [] args)
            throws IOException, IllegalArgumentException
    {
        trkSensor ts = new trkSensor ();
        System.out.println (" main: trkSensor return");

        ts.udpRec ();
    }

    // ----------------------------------------------------
    public void udpRec () throws IOException
    {
        // Create a socket to listen on port 4445
        DatagramSocket ds      = new DatagramSocket (4445);
        byte []        receive = new byte[65535];

        System.out.println (" main: loop");

        DatagramPacket DpReceive = null;
        while (true)
        {
            // Step 2 : create a DatgramPacket to receive the data.
            DpReceive = new DatagramPacket (receive, receive.length);

            // Step 3 : revieve the data in byte buffer.
            ds.receive (DpReceive);

            // convert bytes to String
            String s = "";
            for (int i = 0; 0 != receive [i]; i++)
                s = s + (char) receive [i];

            // split and translate fields
            String []  fld = s.split("  *");

            if (true)  {
                System.out.println ("udpRec: " + s);
            }

         // int  accX = Integer.parseInt (fld [1]);

            for (int n = 1; n < fld.length; n++)  {
                if (false)
                    System.out.format ("   %d: %6d\n",
                        n, Integer.parseInt (fld [n]));

                int i = idx % Wid;

                if (0 == idx)  {
                    switch (n)  {
                    case 1:
                        offA = Integer.parseInt (fld [n]);
                        break;
    
                    case 2:
                        offB = Integer.parseInt (fld [n]);
                        break;
    
                    case 3:
                        offC = Integer.parseInt (fld [n]);
                        break;
    
                    case 4:
                        offD = Integer.parseInt (fld [n]);
                        break;
    
                    case 5:
                        offE = Integer.parseInt (fld [n]);
                        break;
    
                    case 6:
                        offF = Integer.parseInt (fld [n]);
                        break;
                    }
                }

                int K = 20;

                switch (n)  {
                case 1:
                    vecA [i] = (Integer.parseInt (fld [n]) - offA) / K;
                    break;

                case 2:
                    vecB [i] = (Integer.parseInt (fld [n]) - offB) / K;
                    break;

                case 3:
                    vecC [i] = (Integer.parseInt (fld [n]) - offC) / K;
                    break;

                case 4:
                    vecD [i] = (Integer.parseInt (fld [n]) - offD) / K;
                    break;

                case 5:
                    vecE [i] = (Integer.parseInt (fld [n]) - offE) / K;
                    break;

                case 6:
                    vecF [i] = (Integer.parseInt (fld [n]) - offF) / K;
                    break;
                }
            }

            if (0 == idx)
                    System.out.format ("offset: %6d %6d %6d %6d %6d %6d\n",
                            offA, offB, offC, offD, offE, offF);

            idx++;
        }
    }

    // ----------------------------------------------------
    int cnt;

    public void run () {
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println (cnt++);
            }
        };

        Timer timer = new Timer("runTimer");
        timer.scheduleAtFixedRate (task, 0, 1000);

    }

    // ----------------------------------------------------
    //
    public trkSensor ()
            throws IOException, IllegalArgumentException
    {
        addKeyListener (this);
        addMouseListener (this);

        System.out.println ("trkSensor:");
        frame.setContentPane (this);

        this.setPreferredSize (new Dimension (Wid, Ht));

        frame.pack ();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setVisible (true);

        // position app near top center of screen
        Rectangle r = frame.getBounds();        // window size
        frame.setBounds (000, 0, r.width, r.height);
        frame.setTitle ("Jarm");

        // start timer
        TimerTask task = new TimerTask() {
            public void run() {
                timer   ();
                repaint ();
            }
        };

        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate (task, 0, 2);

        if (false)  {
            int A = -50;
            for (int n = 0; n < Wid; n++)  {
                double ang   = 2 * Math.PI * n / Wid;
                vecA [n] = (int) (A * Math.cos ( 4 * ang));
                vecB [n] = (int) (A * Math.cos ( 8 * ang));
                vecC [n] = (int) (A * Math.cos (12 * ang));
                vecD [n] = vecA [n] + vecB [n] + vecC [n];
            }
        }

        System.out.println (" trkSensor: done");
    }

    // ----------------------------------------------------
    private void timer ()
    {
        tics++;
     // System.out.format ("timer: %6d\n", tics);
    }

    // ----------------------------------------------------
    public void mousePressed (MouseEvent ev)
    {
        x = (int) ev.getX();
        y = (int) ev.getY();
        System.out.format ("mousePressed: (%4d, %4d)\n", x, y);

        requestFocusInWindow ();
        repaint ();
    }

    public void mouseClicked  (MouseEvent e) { }
    public void mouseEntered  (MouseEvent e) { }
    public void mouseExited   (MouseEvent e) { }
    public void mouseReleased (MouseEvent e) { }

    // ----------------------------------------------------
    public void keyPressed  (KeyEvent e) { }
    public void keyReleased (KeyEvent e) { }

    public void keyTyped    (KeyEvent e)
    {
        byte[]  buf     = new byte [10];

        char c = e.getKeyChar();

        switch (c)  {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            keyVal = 10*keyVal + c - '0';
            break;

        case '-':
            keyVal = -keyVal;
            break;

        case 'A':
            break;

        case 'a':
            break;

        case 'd':
            break;

        case 'e':
            break;

        case 'l':
            break;

        case 'm':
            break;

        case 's':
            break;

        case 'T':
            break;

        case 'v':
            break;

        case 'w':
            break;

        case 'x':
            keyVal = 0;
            break;

        case 'y':
            keyVal = 0;
            break;

        case 'z':
            break;

        case '\n':
            break;

        default:
            System.out.format ("keyTyped: %c\n", c);
            break;
        }
        repaint ();
    }

    // ------------------------------------------------------------------------
 // @Override
    public  void paintComponent (Graphics g)
    {
     // System.out.println ("paintComponent:");

        Graphics2D  g2d = (Graphics2D) g;

        // clear background
        g2d.setColor (new Color(0, 32, 0));
        g2d.fillRect (0, 0, Wid, Ht);

        paintWaves (g2d, 0);
    }

    // ----------------------------------------------------
    private  void paintVec (
        Graphics2D g2d,
        int        y0,
        int        vec [],
        int        end,
        int        size,
        Color      col )
    {
        g2d.setColor (col);

        if (end == size)  {
            for (int n = 1; n < size; n++)
                g2d.drawLine (n-1, y0 + vec [n-1], n, y0 + vec [n]);
        }
        else  {
            for (int n = 1; n < size-end; n++)
                g2d.drawLine (n, y0 + vec [end+n-1], n, y0 + vec [end+n]);

            if (0 == end) {
                g2d.drawLine (0, y0 + vec [size-1], 1, y0 + vec [0]);
            }
            else  {
                for (int n = 1+size-end; n < size; n++)
                    g2d.drawLine (n-1, y0 + vec [n-(size-end)-1],
                                  n,   y0 + vec [n-(size-end)]);
            }
        }
    }

    // ----------------------------------------------------
    private  void paintWaves (
        Graphics2D g2d,
        int        ang0 )
    {
        if (0 == idx)
            return;

        int N = idx;
        if (Wid <= idx)
            N = Wid;
        int n = idx % N;   // endpt

        if (false)
            System.out.format (
            "paintWaves: %6d %6d, %6d %6d\n", tics, N, n, tics - n);

        paintVec (g2d, 100, vecA, n, N, Color.red);
        paintVec (g2d, 200, vecD, n, N, Color.green);

        paintVec (g2d, 300, vecB, n, N, Color.blue);
        paintVec (g2d, 400, vecE, n, N, Color.yellow);

        paintVec (g2d, 500, vecC, n, N, Color.orange);
        paintVec (g2d, 600, vecF, n, N, Color.cyan);
    }
}
