package org.app.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "_user", uniqueConstraints={@UniqueConstraint(columnNames={"account_name"})})
public class AppUser implements Serializable {

    private Long id;
    private String accountName;
    private String description;
    private String password;
    private List<Role> roles;

    public AppUser(){}

    public AppUser(String accountName, String description, String password, List<Role> roles) {
        this.accountName = accountName;
        this.description = description;
        this.password = password;
        this.roles = roles;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Size(min=2, max=30)
    @NotNull
    @Column(name = "account_name", unique = true)
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @NotNull
    @Column(name = "user_password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role_lnk", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", description='" + description + '\'' +
                ", password=..."+
                ", roles=" + roles +
                '}';
    }
}