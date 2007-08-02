
import javax.ejb.*;
import java.util.List;

@Local
public interface Manager {
  public String sayHello ();
  // public String startOver ();
  public void findFans ();
  public void setPid (Long pid);
  public Long getPid ();
  public String delete ();
  public String update ();
  public void destroy ();
}
