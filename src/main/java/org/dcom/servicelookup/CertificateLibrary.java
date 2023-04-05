/*
Copyright (C) 2022 Cardiff University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

*/
package org.dcom.servicelookup;

import java.util.HashMap;
import org.dcom.core.security.DCOMBearerToken;
import org.dcom.core.security.ServiceCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* This class implements the representation of the library of DCOM service certificates that the service lookup is currently hosting.
*/
@Component("CertificateLibrary")
public class CertificateLibrary {

  private HashMap<String,ServiceCertificate> certificates;
  private static final Logger LOGGER = LoggerFactory.getLogger( CertificateLibrary.class );
  
  public CertificateLibrary() {
      certificates=new HashMap<String,ServiceCertificate>();
      checkAllLoaded();
  }
  
  public boolean checkTokenAgainstAll(DCOMBearerToken token) {
      checkAllLoaded();
      for(ServiceCertificate cert: certificates.values()) {
        if (cert.checkTokenValidity(token)) return true;
      }
      return false;
  }
  
  private void checkAllLoaded() {
        try {
          File f = new File(System.getenv("DCOMCertificates"));
          File[] pathnames = f.listFiles();
          for (File pathname : pathnames) {
              if (pathname.isDirectory()) {
                    File[] pathnames2 = pathname.listFiles();
                    for (File pathname2 : pathnames2) {
                      if (!certificates.containsKey(pathname2.getPath())) {
                          certificates.put(pathname2.getPath(),new ServiceCertificate(pathname2));
                          LOGGER.info("Loaded Certificate:"+pathname2.getPath());
                      }
                    }
              } else {
                if (!certificates.containsKey(pathname.getPath())) {
                    certificates.put(pathname.getPath(),new ServiceCertificate(pathname));
                    LOGGER.info("Loaded Certificate:"+pathname.getPath());
                }
              }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
  }
  
}