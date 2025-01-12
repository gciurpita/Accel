// Dejan, https://howtomechatronics.com

#include <Wire.h>
#include "gyro.h"

// -----------------------------------------------------------------------------
const int  MPU      = 0x68; // MPU6050 I2C address
const int  AddrAccX = 59;       // first 6
const int  AddrAccY = 61;
const int  AddrAccZ = 63;

const int  AddrTemp = 65;       // next 2
const int  AddrGyroX = 67;       // last 6
const int  AddrGyroY = 69;       // last 6
const int  AddrGyroZ = 71;       // last 6

const int  Nreg      = AddrGyroZ - AddrAccX + 1;

// -------------------------------------
int8_t reg    [Nreg];

// -------------------------------------
void gyroRead ()
{
    Wire.beginTransmission (MPU);
    Wire.write (AddrAccX);
    Wire.endTransmission (false);

    Wire.requestFrom (MPU, Nreg);

    for (int n = 0; n < Nreg; n++)  {
        reg    [n] = Wire.read ();
    }
}

// -----------------------------------------------------------------------------
int16_t _bias   [Nreg];

// -------------------------------------
void gyro (
    char *s )
{
    gyroRead ();

    s [0] = '\0';
    char t [20];
    for (int n = 0; n < Nreg-1; n += 2)  {
        int16_t val = (reg  [n] << 8) + reg  [n+1];
        sprintf (t, " %6d", val - _bias [n]);
        strcat (s, t);
    }
}


// -----------------------------------------------------------------------------
void gyroInit ()
{
    Wire.begin ();

    Wire.beginTransmission (MPU);
    Wire.write (0x6B);                  // perform reset w/ reg 0x6b
    Wire.write (0x00);
    Wire.endTransmission (true);

    // -------------------------------------
    gyroRead ();
    for (int n = 0; n < Nreg-1; n += 2)
        _bias [n] = (reg  [n] << 8) + reg  [n+1];

    delay (20);
}
