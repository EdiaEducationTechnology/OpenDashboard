package od.providers.api;

/**
 * Created by roland on 10-05-16.
 */
public class PageInfo {
  @Override
  public String toString() {
	return "PageInfo [totalPages=" + totalPages + ", totalElements=" + totalElements + ", size=" + size + ", number=" + number + "]";
  }

  private Integer totalPages;
  private Integer totalElements;
  private Integer size;
  private Integer number;

  public Integer getTotalPages() {
	return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
	this.totalPages = totalPages;
  }

  public Integer getTotalElements() {
	return totalElements;
  }

  public void setTotalElements(Integer totalElements) {
	this.totalElements = totalElements;
  }

  public Integer getSize() {
	return size;
  }

  public void setSize(Integer size) {
	this.size = size;
  }

  public Integer getNumber() {
	return number;
  }

  public void setNumber(Integer number) {
	this.number = number;
  }

}
