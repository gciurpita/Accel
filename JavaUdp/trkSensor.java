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

// -----------------------------------------------------------------------------
// steam locomotive backend

public class trkSensor extends JPanel
        implements MouseListener, KeyListener
{
    public static void main (String [] args)
            throws IOException, IllegalArgumentException
    {
        trkSensor ts = new trkSensor ();
        ts.startup ();
    }

    // ----------------------------------------------------
    //
    JFrame frame = new JFrame ();

    final int Wid = 1200;
    final int Ht  =  800;

    int       keyVal;
    int       tics;

    int       vecA []      = new int [Wid];
    int       vecB []      = new int [Wid];
    int       vecC []      = new int [Wid];
    int       vecD []      = new int [Wid];
    int       vecE []      = new int [] {
  27,  27,  27,  27,  27,  27,  27,  27,  26,  25,  25,  26,  26,  26,  25,
  24,  24,  25,  25,  25,  24,  23,  23,  23,  23,  23,  22,  20,  18,  16,
  15,  12,  12,  13,  16,  16,  16,  16,  16,  16,  16,  17,  20,  20,  20,
  20,  21,  22,  24,  25,  26,  25,  25,  25,  27,  28,  26,  25,  24,  26,
  27,  28,  28,  28,  28,  28,  29,  28,  27,  26,  26,  26,  29,  32,  32,
  31,  28,  16,   0, -19, -42, -68, -86, -85, -65, -38, -16,   0,  11,  14,
  19,  22,  22,  22,  23,  24,  26,  28,  28,  30,  31,  33,  34,  34,  32,
  32,  32,  33,  35,  34,  33,  33,  33,  33,  34,  35,  34,  33,  34,  34,
  34,  33,  33,  33,  33,  33,  34,  33,  33,  32,  31,  32,  34,  33,  32,
  31,  32,  32,  33,  32,  31,  30,  31,  32,  33,  33,  30,  30,  30,  30,
  29,  28,  28,  28,  27,  28,  28,  26,  25,  24,  24,  24,  24,  22,  21,
  21,  21,  20,  19,  17,  16,  16,  15,  14,  14,  13,  12,  11,  10,  10,
  10,  10,   8,   7,   8,   9,   9,   9,   8,   8,  10,  12,  13,  13,  13,
  14,  16,  18,  18,  19,  20,  20,  21,  22,  22,  23,  23,  24,  25,  26,
  27,  26,  26,  25,  24,  26,  28,  28,  29,  27,  27,  26,  28,  28,  27,
  27,  27,  28,  28,  28,  27,  26,  27,  28,  28,  28,  27,  26,  25,  26,
  25,  26,  25,  25,  26,  27,  28,  26,  25,  24,  24,  24,  25,  26,  26,
  24,  23,  24,  25,  26,  26,  24,  24,  24,  26,  24,  24,  25,  24,  25,
  26,  25,  25,  25,  25,  27,  27,  26,  24,  24,  25,  25,  26,  26,  26,
  25,  25,  26,  27,  27,  26,  25,  26,  26,  25,  26,  26,  25,  25,  25,
  26,  26,  26,  25,  26,  27,  28,  26,  26,  26,  24,  27,  28,  26,  25,
  24,  24,  25,  25,  28,  25,  25,  25,  25,  25,  25,  24,  24,  25,  25,
  24,  23,  22,  22,  23,  23,  23,  22,  22,  19,  17,  17,  17,  14,  11,
  11,  12,  14,  16,  17,  17,  16,  16,  17,  18,  20,  20,  20,  19,  20,
  21,  22,  24,  24,  26,  25,  26,  25,  24,  24,  25,  25,  28,  28,  28,
  26,  25,  26,  27,  28,  26,  24,  24,  26,  28,  28,  28,  28,  28,  29,
  21,   6, -10, -30, -55, -75, -81, -71, -48, -27,  -9,   4,  11,  14,  18,
  19,  20,  21,  24,  25,  27,  29,  30,  32,  31,  30,  30,  29,  30,  31,
  33,  33,  32,  31,  32,  32,  32,  31,  31,  31,  31,  33,  34,  33,  31,
  31,  32,  32,  32,  31,  30,  30,  30,  32,  32,  32,  32,  31,  30,  32,
  33,  32,  31,  30,  29,  30,  32,  30,  29,  28,  29,  29,  30,  30,  28,
  27,  27,  26,  27,  26,  25,  25,  26,  25,  26,  25,  24,  22,  20,  19,
  20,  19,  18,  17,  16,  16,  17,  16,  14,  13,  12,  11,  11,  11,   9,
   9,  10,   9,  10,   9,   9,  10,   9,  10,  10,  10,  10,  11,  12,  13,
  15,  15,  15,  14,  15,  18,  18,  19,  18,  18,  19,  21,  22,  22,  22,
  22,  22,  24,  24,  24,  24,  23,  23,  24,  25,  24,  24,  23,  25,  25,
  25,  24,  24,  23,  24,  24,  24,  24,  24,  24,  23,  23,  23,  23,  22,
  22,  22,  22,  22,  23,  22,  22,  21,  22,  23,  23,  22,  21,  21,  21,
  21,  21,  21,  20,  20,  21,  22,  22,  22,  20,  21,  22,  22,  22,  21,
  21,  21,  21,  21,  21,  21,  20,  21,  22,  23,  22,  21,  21,  21,  21,
  23,  22,  22,  21,  21,  22,  22,  22,  22,  22,  22,  22,  22,  22,  21,
  21,  21,  22,  22,  22,  21,  21,  22,  21,  22,  22,  22,  20,  21,  22,
  22,  22,  22,  20,  21,  22,  22,  22,  21,  20,  18,  18,  19,  19,  17,
  16,  17,  17,  18,  18,  18,  17,  15,  14,  14,  12,   9,   8,   8,  10,
  12,  12,  12,  12,  14,  14,  16,  15,  15,  14,  15,  16,  18,  17,  18,
  19,  19,  20,  23,  22,  22,  21,  22,  23,  24,  23,  23,  22,  22,  23,
  24,  25,  24,  23,  24,  24,  24,  23,  22,  23,  24,  27,  29,  29,  23,
  12,  -5, -23, -43, -67, -86, -93, -83, -61, -38, -17,  -1,   6,   9,  14,
  18,  19,  18,  19,  20,  24,  27,  28,  29,  28,  29,  30,  31,  30,  29,
  29,  30,  30,  31,  31,  32,  31,  31,  32,  32,  31,  30,  30,  30,  31,
  32,  32,  31,  30,  31,  31,  32,  32,  31,  30,  30,  31,  32,  32,  31,
  30,  30,  30,  31,  31,  31,  30,  30,  31,  31,  30,  30,  29,  29,  29,
  29,  29,  28,  27,  27,  27,  28,  28,  25,  24,  23,  24,  23,  23,  22,
  20,  18,  19,  19,  18,  18,  16,  14,  14,  15,  13,  12,  11,  11,  12,
  11,  11,  10,   8,  10,  10,  10,  10,   9,   9,   9,  11,  13,  13,  14,
  14,  15,  17,  18,  18,  20,  19,  21,  21,  23,  25,  24,  23,  24,  25,
  26,  26,  26,  26,  26,  27,  28,  28,  28,  26,  28,  28,  29,  29,  27,
  28,  28,  28,  28,  28,  27,  26,  27,  29,  30,  29,  28,  28,  27,  28,
  28,  28,  27,  26,  26,  26,  28,  28,  28,  27,  27,  28,  28,  27,  26,
  26,  27,  27,  28,  27,  26,  26,  25,  27,  28,  28,  28,  27,  26,  26,
  27,  27,  28,  26,  26,  27,  27,  28,  27,  27,  27,  27,  28,  27,  27,
  26,  28,  28,  29,  29,  28,  28,  28,  28,  28,  28,  27,  27,  28,  29,
  30,  30,  30,  29,  28,  28,  28,  29,  29,  27,  27,  28,  30,  30,  28,
  27,  27,  29,  30,  30,  28,  28,  28,  28,  29,  29,  28,  28,  28,  29,
  29,  28,  26,  26,  26,  26,  26,  25,  24,  24,  25,  26,  25,  24,  22,
  21,  19,  19,  18,  18,  18,  17,  18,  20,  21,  22,  20,  20,  20,  21,
  24,  25,  25,  25,  26,  26,  28,  29,  29,  30,  31,  32,  31,  30,  30,
  30,  33,  33,  33,  34,  33,  32,  32,  34,  34,  32,  31,  32,  33,  34,
  34,  36,  36,  37,  36,  32,  21,   2, -16, -36, -57, -78, -87, -80, -60,
 -36, -15,   4,  14,  18,  20,  22,  26,  28,  28,  28,  29,  30,  34,  35,
  35,  36,  36,  36,  38,  39,  38,  38,  37,  37,  38,  40,  40,  39,  38,
  39,  40,  39,  39,  38,  37,  38,  38,  38,  39,  38,  38,  37,  38,  39,
  38,  37,  38,  38,  38,  38,  38,  37,  37,  38,  38,  38,  37,  38,  36,
  36,  37,  38,  38,  37,  36,  37,  36,  36,  36,  36,  34,  34,  34,  34,
  34,  33,  32,  32,  31,  32,  32,  30,  28,  28,  30,  28,  27,  25,  24,
  23,  23,  22,  22,  20,  18,  18,  18,  19,  19,  16,  15,  14,  15,  15,
  15,  14,  14,  13,  14,  14,  14,  15,  15,  15,  16,  18,  20,  20,  20,
  21,  23,  25,  26,  27,  26,  26,  28,  29,  29,  30,  29,  30,  30,  31,
  32,  32,  32,  31,  32,  33,  33,  32,  31,  31,  32,  33,  33,  33,  32,
  33,  33,  34,  34,  34,  32,  32,  33,  34,  33,  33,  33,  34,  34,  34,
};




    int       SegLen []    = { Wid/3, Wid/3, Wid/8 };
    int       x            = Wid / 2;
    int       y            = Ht / 2;
    int       servoIdx     = 0;


    // ----------------------------------------------------
    public trkSensor ()
            throws IOException, IllegalArgumentException
    {
        addKeyListener (this);
        addMouseListener (this);

        System.out.println ("trkSensor:");
    }

    // ----------------------------------------------------
    private void startup ()
    {
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

        int A = -50;
        for (int n = 0; n < Wid; n++)  {
            double ang   = 2 * Math.PI * n / Wid; 
            vecA [n] = (int) (A * Math.cos ( 4 * ang));
            vecB [n] = (int) (A * Math.cos ( 8 * ang));
            vecC [n] = (int) (A * Math.cos (12 * ang));
            vecD [n] = vecA [n] + vecB [n] + vecC [n];
        }
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
        int N = tics;
        if (Wid <= tics)
            N = Wid;
        int n = tics % N;   // endpt

        if (false)
            System.out.format (
            "paintWaves: %6d %6d, %6d %6d\n", tics, N, n, tics - n);

        paintVec (g2d, 100, vecA, n, N, Color.red);
        paintVec (g2d, 300, vecB, n, N, Color.blue);
        paintVec (g2d, 500, vecE, n, N, Color.orange);
        paintVec (g2d, 700, vecD, n, N, Color.green);
    }
}
