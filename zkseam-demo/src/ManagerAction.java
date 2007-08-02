import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.util.List;
import javax.ejb.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.*;
import org.jboss.seam.ejb.*;
import org.hibernate.validator.Valid;

import javax.persistence.*;

import static javax.persistence.PersistenceContextType.EXTENDED;

@Stateful
@Name("manager")
public class ManagerAction implements Manager {

  @In (required=false) @Out (required=false)
  private Person person;

  @PersistenceContext (type=EXTENDED)
  private EntityManager em;

  // @RequestParameter
  Long pid;
 
  public String sayHello () {
    em.persist (person);
    // find ();
    return "fans";
  }

  // public String startOver () {
  //   System.out.println("startover called");
  //   return "hello";
  // }

  @DataModel
  private List <Person> fans;

  @DataModelSelection
  private Person selectedFan;

  @Factory("fans")
  public void findFans () {
    System.out.println("Find called");
    fans = em.createQuery("select p from Person p")
                                  .getResultList();
  }

  public void setPid (Long pid) {
    this.pid = pid;
    
    if (pid != null) {
      person = (Person) em.find(Person.class, pid);
    } else {
      person = new Person ();
    }
  }
  
  public Long getPid () {
    return pid;
  }

  public String delete () {
    // merge? or page scope data model?
    Person toDelete = em.merge (selectedFan);
    em.remove( toDelete );
    findFans ();
    return null;
  }

  public String update () {
    // The "person" component is detached since this is not
    // a long running conversation
    // Person p = em.merge (person);
    // find ();
      
    //modify by dennis chen, We must force update when zul action.
    em.merge(person);
      
    return "fans";
  }

  @Remove @Destroy
  public void destroy() {}

}
