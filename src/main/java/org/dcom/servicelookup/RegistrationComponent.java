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

import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;
import com.owlike.genson.Genson;
import java.util.ArrayList;
import org.dcom.core.services.ServiceLookup;
import org.dcom.core.DCOM;
import org.dcom.core.security.ServiceCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*TThe file iterates over the contents of services.json and registers them as services. This is used for legacy services that cannot be modified to register themselves with the DCOM ecosystem
*/

public class RegistrationComponent extends Thread {
  
  private static final Logger LOGGER = LoggerFactory.getLogger( RegistrationComponent.class );
    
  private ArrayList<Object> data;
  private ServiceCertificate myCert;

  public RegistrationComponent(String fileName) throws Exception {
        data=new ArrayList<Object>();
        data=(new Genson()).deserialize(new FileInputStream(new File(fileName)),ArrayList.class);
        if ( !DCOM.checkDCOMCertificatePassword() || !DCOM.checkDCOMCertificatePath()) {
          LOGGER.error("Certificate Variables Not Defined");
          System.exit(0);
        }
        myCert=new ServiceCertificate(new File(DCOM.getDCOMCertificatePath()),DCOM.getDCOMCertificatePassword());
  }
  
  public void run()  {
    try {
      
      Thread.sleep(20000);
      LOGGER.info("Registering Services");
      ServiceLookup serviceLookup=DCOM.getServiceLookup("http://127.0.0.1:8080");
      for (int i=0; i < data.size();i++) {
          HashMap<String,Object> serviceData=(HashMap<String,Object>)data.get(i);
          String appType=(String)serviceData.get("type");
          String name=(String)serviceData.get("name");
          String hostName=(String)serviceData.get("host");
          String port=(String)serviceData.get("port");
          LOGGER.info("Registering:"+appType+":"+name+":"+hostName+":"+port);
          serviceLookup.registerMyself(appType,name,hostName,Integer.parseInt(port),myCert.generateBearerToken());
      }
      LOGGER.info("Registration Complete");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

}


