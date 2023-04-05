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

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dcom.core.security.DCOMBearerToken;

/**
*This class intercepts requests for registration to Eureka. For each request it checks if there is a valid certificate for the service that is requests registration and if so, registers it. If the certificate is not valid or not held the registration request is rejected.
*/

@Component
public class AuthenticationFilter extends OncePerRequestFilter {


  @Autowired
  private CertificateLibrary certLibrary;
  
  private static final Logger LOGGER = LoggerFactory.getLogger( AuthenticationFilter.class );
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    
    String method=request.getMethod();
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me");

    if (method.equalsIgnoreCase("GET")) {
        LOGGER.info("Accepting:"+method+":"+request.getServletPath());
        chain.doFilter(request, response);
    } else if (method.equalsIgnoreCase("OPTIONS")) {
        chain.doFilter(request, response);
    } else {
      DCOMBearerToken token=new DCOMBearerToken(request.getHeader("Authorization"));
      if (certLibrary.checkTokenAgainstAll(token)) {
        LOGGER.info("Accepting:"+method+":"+request.getServletPath());
        chain.doFilter(request, response);
      } else {
        LOGGER.info("Rejecting  :"+method+":"+request.getServletPath());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      }
    }
  }
}