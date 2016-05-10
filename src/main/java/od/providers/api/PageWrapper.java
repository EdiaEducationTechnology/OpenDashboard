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
package od.providers.api;

import java.util.List;

/**
 * @author ggilbert
 *
 */
public class PageWrapper<T> extends PageInfo {
  
  @Override
  public String toString() {
    return "PageWrapper [content=" + content + ", page=" + page + "]";
  }

  private List<T> content;
  private PageInfo page;
  
  public List<T> getContent() {
    return content;
  }
  public void setContent(List<T> content) {
    this.content = content;
  }
  public PageInfo getPage() {
    if (page == null) {
      return this;
    }
    return page;
  }
  public void setPage(PageInfo page) {
    this.page = page;
  }
 }