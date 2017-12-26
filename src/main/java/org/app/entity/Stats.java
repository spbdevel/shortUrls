package org.app.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "_stats"
        ,uniqueConstraints={@UniqueConstraint(columnNames={"user_id", "url_id"})}
        ,indexes = { @Index(name = "user_id_idx", columnList = "user_id")
})
public class Stats implements Serializable {

    private AppUser user;
    private RegisteredURL url;
    private int counter;
    private long id;
    private int version;


    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }


    @NotNull
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    @NotNull
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id")
    public RegisteredURL getUrl() {
        return url;
    }

    public void setUrl(RegisteredURL url) {
        this.url = url;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "user=" + user.getId() +
                ", url=" + url.getId() +
                ", counter=" + counter +
                ", id=" + id +
                ", version=" + version +
                '}';
    }
}