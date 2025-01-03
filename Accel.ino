/*
Arduino and MPU6050 Accelerometer and Gyroscope Sensor Tutorial
by Dejan, https://howtomechatronics.com
*/
#include <Wire.h>
# include "SH1106Wire.h"

SH1106Wire  display(0x3c, 21, 22);

enum { Y0 = 0, Y1 = 14, Y2 = 28, Y3 = 42 };

const int MPU = 0x68; // MPU6050 I2C address

float AccX, AccY, AccZ;
float GyroX, GyroY, GyroZ;
float accAngleX, accAngleY, gyroAngleX, gyroAngleY, gyroAngleZ;
float roll, pitch, yaw;
float AccErrorX, AccErrorY, GyroErrorX, GyroErrorY, GyroErrorZ;
float elapsedTime, currentTime, previousTime;
int c = 0;

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
    display.drawString(0, Y0, "acc");
    display.display();

    delay (1000);

    // -------------------------------------
    Wire.begin ();                      // Initialize comunication
    Wire.beginTransmission (MPU);       // Start communication with MPU6050 // MPU=0x68
    Wire.write (0x6B);                  // Talk to the register 6B
    Wire.write (0x00);                  // Make reset - place a 0 into the 6B register
    Wire.endTransmission (true);        //end the transmission

    delay (20);
}

// -----------------------------------------------------------------------------
const byte AddrAcc  = 59;
const byte AddrTemp = 65;
const byte AddrGyro = 67;
const byte Nreg     = 14;

byte reg [Nreg];

void loop () {
    // === Read acceleromter data === //
    Wire.beginTransmission (MPU);
    Wire.write (AddrAcc);
    Wire.endTransmission (false);

    Wire.requestFrom (MPU, Nreg);

    for (int n = 0; n < Nreg; n++)
        reg [n] = Wire.read ();

    char s [50];

    display.clear();
    display.drawString(0, Y0, " acc   gyro");

#if 0
    sprintf (s, " %02X %02X", reg [0], reg [1]);
    display.drawString(0,  Y1, s);
    sprintf (s, " %02X %02X", reg [8], reg [9]);
    display.drawString(64, Y1, s);


    sprintf (s, " %02X %02X", reg [2],  reg [3]);
    display.drawString(0,  Y2, s);
    sprintf (s, " %02X %02X", reg [10], reg [11]);
    display.drawString(64, Y2, s);

    sprintf (s, " %02X %02X", reg [4],  reg [5]);
    display.drawString(0,  Y3, s);
    sprintf (s, " %02X %02X", reg [12], reg [13]);
    display.drawString(64, Y3, s);

#else
    sprintf (s, " %02X", reg [0]);
    display.drawString(0,  Y1, s);
    sprintf (s, " %02X", reg [8]);
    display.drawString(64, Y1, s);

    sprintf (s, " %02X", reg [2]);
    display.drawString(0,  Y2, s);
    sprintf (s, " %02X", reg [10]);
    display.drawString(64, Y2, s);

    sprintf (s, " %02X", reg [4]);
    display.drawString(0,  Y3, s);
    sprintf (s, " %02X", reg [12]);
    display.drawString(64, Y3, s);

#endif

    display.display();

    delay (100);
}
