import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import org.jboss.seam.annotations.*;
import org.hibernate.validator.*;


@Entity
@Name("person")
@Table(name = "extperson")
public class Person implements Serializable {

    private long id;

    private String name;

    private int age;

    private String email;

    private String comment;

    public Person() {
        name = "";
        age = 25;
        email = "";
        comment = "";
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    @Pattern(regex = "^[a-zA-Z.-]+ [a-zA-Z.-]+", message = "Need a firstname and a lastname")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // @Min(value=3) @Max(value=100)
    @NotNull
    @Range(min = 3, max = 100, message = "Age must be between 3 and 100")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // @Pattern(regex="^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,4}$")
    @NotNull
    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(max = 250)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    // add by dennis, for selection compare
    public boolean equals(Object obj){
        if(obj instanceof Person){
            return ((Person)obj).id == id;
        }
        return false;
    }

}
