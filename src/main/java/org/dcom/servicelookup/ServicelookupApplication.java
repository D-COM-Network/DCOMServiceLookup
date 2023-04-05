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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;


/**
*The startup file for the service lookup. This starts spring boot, loads the services.json and then starts Eureka.
*/
@SpringBootApplication
@EnableEurekaServer
public class ServicelookupApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		try {
				RegistrationComponent registration=new RegistrationComponent("./services.json");
				registration.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpringApplication.run(ServicelookupApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		try {
				String jsonPath = System.getenv("DCOMServiceConfig");
				RegistrationComponent registration=new RegistrationComponent(jsonPath);
				registration.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return application.sources(ServicelookupApplication.class);
	}

}
