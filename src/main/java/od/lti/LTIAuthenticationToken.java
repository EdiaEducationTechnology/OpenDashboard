/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.lti;

import java.util.Collection;

import lti.LaunchRequest;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author ggilbert
 *
 */
public class LTIAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private Logger log = Logger.getLogger(LTIAuthenticationToken.class);
  private static final long serialVersionUID = 1L;
  
  private LaunchRequest launchRequest;
  private String tenantId;

  public LTIAuthenticationToken(LaunchRequest launchRequest, String tenantId, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.launchRequest = launchRequest;
    this.tenantId = tenantId;
  }

  public LaunchRequest getLaunchRequest() {
    return launchRequest;
  }

  public String getTenantId() {
    return tenantId;
  }

}
