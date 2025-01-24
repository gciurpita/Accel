#ifndef WIFI_H
# define WIFI_H

extern int  dbgWifi;

enum { ST_NUL, ST_INIT, ST_CHK, ST_CFG_UDP, ST_UP, ST_ERROR };

const unsigned STR_SIZE = 40;

extern char host [STR_SIZE];
extern char ssid [STR_SIZE];
extern char pass [STR_SIZE];

// ----------------------------------------
void  wifiConnect (void);
void  nodeConnect (void);
void  wifiIpAdd   (char *ip);
void  wifiIpClr   (void);
char *wifiIpGet   (int idx);
void  wifiIpList  (void);
int   wifiMonitor (void);
void  wifiReceive (void);
void  wifiReset   (void);
void  wifiSend    (const char* s);
#endif
