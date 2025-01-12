const char *version = "Broadcast UDP - 250110a";

#include "eeprom.h"
#include "gyro.h"
#include "wifi.h"

unsigned debug   = 1;
int   error   = 0;
int   ledMode = 0;
char  s [100];

unsigned long msec;

// -----------------------------------------------------------------------------
const byte PinLedGrn = 17;      // ezsbc, 16/17/18 red/grn/blu

struct LedPeriod {
    unsigned long   period [2];
};
LedPeriod ledPeriod [] = {
    {  100, 1900 },
    {  200,   50 },
    {  150,  100 },
};

// -------------------------------------
void
ledStatus ()
{
    static unsigned long msecLst;
    int state = digitalRead (PinLedGrn);
    if (msec - msecLst >= ledPeriod [ledMode].period [state])  {
        msecLst = msec;
        digitalWrite (PinLedGrn, ! state);
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
        else if ('U' == buf [0])
            eepromUpdate ();
        else
           printf ("cmds: invalid - %s\n",  buf);
    }
}

// -----------------------------------------------------------------------------
void
loop ()
{
    msec = millis ();
    ledStatus ();

    gyro (s);
    Serial.println (s);

    wifiMonitor ();

    cmds ();
}

// -----------------------------------------------------------------------------
void setup ()
{
    Serial.begin (115200);
    delay (500);
    Serial.println (version);

    pinMode (PinLedGrn, OUTPUT);
    digitalWrite (PinLedGrn, LOW);

    eepromInit ();
    gyroInit   ();
}
