// Dejan, https://howtomechatronics.com

const char *Version = "Accel - 250103c";

#include <Wire.h>
#include "SH1106Wire.h"

SH1106Wire  display(0x3c, 21, 22);
enum { Y0 = 0, Y1 = 14, Y2 = 28, Y3 = 42 };

const int  MPU      = 0x68; // MPU6050 I2C address
const int  AddrAccX = 59;       // first 6
const int  AddrAccY = 61;
const int  AddrAccZ = 63;

const int  AddrTemp = 65;       // next 2
const int  AddrGyroX = 67;       // last 6
const int  AddrGyroY = 69;       // last 6
const int  AddrGyroZ = 71;       // last 6

const int  Nreg      = AddrGyroZ - AddrAccX + 1;

// 16 red   active LOW left
// 17 green active LOW
// 18 blue  active LOW
// 19 blue  active LOW right

const byte PinLeds [] = { 16, 17, 18, 19 };
const byte PinLedA = 15;
const byte PinLedB = 25;

int8_t reg    [Nreg];
int8_t regLst [Nreg];

char s [50];

// -----------------------------------------------------------------------------
inline int abs (int x) { 
    if (0 > x)
        return -x;
    return x;
}

// -----------------------------------------------------------------------------
void
dispSplit (
    int idx,
    int y,
    int x0,
    int x1 )
{
    int delta = reg [idx] - regLst [idx];
    int mag   = abs(delta);

    digitalWrite (PinLedA, LOW);
    digitalWrite (PinLedB, LOW);

    sprintf (s, " %02X", mag);
    if (0 > delta)  {
        if (2 < mag)  {
            display.drawString(x0, y, s);
 //         digitalWrite (PinLedB, HIGH);
        }
    }
    else  {
        if (2 < mag)  {
            display.drawString(x1, y, s);
 //         digitalWrite (PinLedA, HIGH);
        }
    }

#if 0
 // sprintf (s, "disp: %2d, %2d, %2d, %2d", idx, y, x0, x1);
    Serial.println (s);
#endif
}

// -----------------------------------------------------------------------------
void direct (
    int adr,
    int y,
    int x0,
    int x1 )
{
    Wire.beginTransmission (MPU);
    Wire.write (adr);
    Wire.endTransmission (false);

    Wire.requestFrom (MPU, 2);

    for (int n = 0; n < Nreg; n++)  {
        regLst [n] = reg [n];
        reg    [n] = Wire.read ();
    }

    dispSplit (0, y, x0, x1);

#if 0
    sprintf (s, "dir: %2d, %2d, %2d, %2d", adr, y, x0, x1);
    Serial.println (s);
#endif
}

// -----------------------------------------------------------------------------
void all ()
{
    Wire.beginTransmission (MPU);
    Wire.write (AddrAccX);
    Wire.endTransmission (false);

    Wire.requestFrom (MPU, Nreg);

    for (int n = 0; n < Nreg; n++)  {
        regLst [n] = reg [n];
        reg    [n] = Wire.read ();
    }

    display.clear();
    sprintf (s, "Accel - %s", Version); 
    display.drawString(0, Y0, s);

    // -------------------------------------
    // x/y/z accel
    dispSplit ( 0, Y1,  0, 32);
    dispSplit ( 2, Y2,  0, 32);
    dispSplit ( 4, Y3,  0, 32);

    // -------------------------------------
    // x/y/z gyro
#if 1
    dispSplit ( 8, Y1, 64, 96);
    dispSplit (10, Y2, 64, 96);
    dispSplit (12, Y3, 64, 96);
#endif

    // -------------------------------------
    display.display();

    delay (100);
}

// -----------------------------------------------------------------------------
int cnt;
void separate ()
{
    display.clear();

 // switch ((++cnt/30) % 6)  {
    switch (2)  {
    case 0:
        direct (59, Y1,  0, 32);
        break;

    case 1:
        direct (61, Y2,  0, 32);
        break;

    case 2:
        direct (63, Y3,  0, 32);
        break;

    case 3:
        direct (67, Y1, 64, 96);
        break;

    case 4:
        direct (69, Y2, 64, 96);
        break;

    case 5:
        direct (71, Y3, 64, 96);
        break;
    }
    display.display();

    delay (100);
}

// -----------------------------------------------------------------------------
void loop ()
{
#if 1
    all ();
#elif 1
    separate ();
#else
    display.clear();
    direct (AddrAccZ, Y2, 64, 96);
    display.display();
#endif

    // ---------------------------
    static int val;

    if (Serial.available ())  {
        char c = Serial.read ();

        switch (c) {
        case '0'...'9':
            val = 10*val + c - '0';
            return;

        case 'c':
            digitalWrite (val, LOW);
            break;

        case 'o':
            pinMode (val, OUTPUT);
            printf ("loop: pinMode (%d, OUTPUT)\n", val);
            break;

        case 's':
            digitalWrite (val, HIGH);
            break;

        }
        val = 0;
    }
}

// -----------------------------------------------------------------------------
void setup () {
    Serial.begin (19200);

    // -------------------------------------
    // init OLED display
    display.init();
 // display.flipScreenVertically();
    display.setFont(ArialMT_Plain_16);
    display.clear();
    display.setTextAlignment(TEXT_ALIGN_LEFT);
    display.setColor(WHITE);

    display.clear();
    display.drawString(0, Y0, Version);
    display.display();

    delay (1000);

    // -------------------------------------
    pinMode (PinLedA, OUTPUT);
    pinMode (PinLedB, OUTPUT);

#if 0
    for (int n =0; n < 4; n++)  {
        display.clear();
        sprintf (s, " led %d", n);
        display.drawString (0, Y0, s);
        display.display();

        pinMode (PinLeds [n], OUTPUT);
        digitalWrite  (PinLeds [n], HIGH);
        delay (1000);
        digitalWrite  (PinLeds [n], LOW);
    }
#endif

    // -------------------------------------
    Wire.begin ();

    Wire.beginTransmission (MPU);
    Wire.write (0x6B);                  // perform reset w/ reg 0x6b
    Wire.write (0x00);
    Wire.endTransmission (true);

    delay (20);
}
