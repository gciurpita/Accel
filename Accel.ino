const char *version = "Accel/Broadcast UDP - 250124a";

#include "eeprom.h"
#include "gyro.h"
#include "wifi.h"

unsigned debug   = 1;
bool     run     = true;
int      ledMode = 0;
char     s [90];

unsigned long msec;

// -----------------------------------------------------------------------------
const byte PinLed1Red = 16;
const byte PinLed1Grn = 17;
const byte PinLed1Blu = 18;
const byte PinLed2Blu = 19;

enum { LedOff = HIGH, LedOn= LOW };

struct LedState {
    unsigned long   period [2];
    byte            pinLed;
}
ledStates [] = {
    {{  400,  100 }, PinLed1Blu },
    {{  100, 1900 }, PinLed1Grn },
    {{  150,  100 }, PinLed1Red },
};
const int NledStates = sizeof(ledStates)/sizeof(LedState);

// -------------------------------------
void
ledStatus ()
{
    static unsigned long msecLst;

    int state = digitalRead (ledStates [ledMode].pinLed);
    if (msec - msecLst >= ledStates [ledMode].period [state])  {
        msecLst = msec;
        digitalWrite (ledStates [ledMode].pinLed, ! state);
#if 0
        Serial.print   ("ledStatus: ");
        Serial.println (ledMode);
#endif
    }
}

// -----------------------------------------------------------------------------
#define MAX_TOKS   10
static int    _nToks;
static char * _toks [MAX_TOKS];
static int    _vals [MAX_TOKS];

int
_tokenize (
    char *s)
{
    int n = 0;
    for (_toks [n] = strtok (s, " "); _toks [n]; ) {
        _vals [n] = atoi (_toks [n]);
     // printf ("   %2d: %6d %s\n", n, _vals [n], _toks [n]);
        _toks [++n] = strtok (NULL, " ");
    }

    return _nToks = n;
}

// ---------------------------------------------------------
void cmds ()
{
    if (Serial.available ()) {
        char buf [90];
        int  n = Serial.readBytesUntil (';', buf, sizeof(buf)-1);
        buf [n] = '\0';

        if ('_' == buf [0])  {
            int nTok = _tokenize (&buf [1]);

            if (! strcmp (_toks [0], "host") && 2 == nTok)
                strcpy (host, _toks [1]);

            else if (! strcmp (_toks [0], "pass") && 2 == nTok)
                strcpy (pass, _toks [1]);

            else if (! strcmp (_toks [0], "ssid") && 2 == nTok)
                strcpy (ssid, _toks [1]);

            else {
                printf ("%s: invalid input\n", __func__);
                for (int n = 0; n < nTok; n++)
                    printf (" %s: %2d  %s\n", __func__, n, _toks [n]);
            }
        }
        else if ('r' == buf [0])
            run = ! run;
        else if ('U' == buf [0])
            eepromUpdate ();
        else
           printf ("cmds: invalid - %s\n",  buf);
    }
}

// -----------------------------------------------------------------------------
const unsigned long MsecGyroPeriod = 10;
      unsigned long msecGyro;

void
loop ()
{
    msec = millis ();
    ledStatus ();

    if (ST_UP == wifiMonitor ())
        ledMode = 1;

    cmds ();

    if (run && msec - msecGyro >= MsecGyroPeriod) {
        msecGyro += MsecGyroPeriod;
        gyro (s);
        wifiSend       (s);
    }
}

// -----------------------------------------------------------------------------
void setup ()
{
    Serial.begin (115200);
    delay (500);
    Serial.println (version);

    for (int n = 0; n < NledStates; n++)  {
        pinMode      (ledStates [n].pinLed, OUTPUT);
        digitalWrite (ledStates [n].pinLed, LedOff);
    }

    eepromInit ();
    gyroInit   ();
}
