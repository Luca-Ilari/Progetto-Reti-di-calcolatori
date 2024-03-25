//
//  WebServer.h
//  Server
//
//  Created by luca on 24/03/24.
//

#ifndef webServer_h
#define webServer_h

#ifdef WIN32
DWORD WINAPI webServer();
#else
void *webServer();
#endif

#endif /* webServer_h */
