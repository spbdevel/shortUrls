package org.app.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "_registered_url"
      ,uniqueConstraints={@UniqueConstraint(columnNames={"short_url", "account_id"})}
      ,indexes = { @Index(name = "long_url_idx", columnList = "long_url")
      ,@Index(name = "short_url_idx", columnList = "short_url", unique = true)
      }
)
public class RegisteredURL implements Serializable {

    private Long id;
    private AppUser account;
    private String longUrl;
    private String shortUrl;
    private Integer redirectType;


    public RegisteredURL(){}


    @PrePersist
    public void prePersist() {
        if(redirectType == null)
            redirectType = 302;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public AppUser getAccount() {
        return account;
    }

    public void setAccount(AppUser account) {
        this.account = account;
    }

    @NotNull
    @Column(name="long_url")
    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    @NotNull
    @Column(name="short_url", length = 15)
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @NotNull
    @Column(name="redirect_type")
    public Integer getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(Integer redirectType) {
        this.redirectType = redirectType;
    }

    @Override
    public String toString() {
        return "RegisteredURL{" +
                "id=" + id +
                ", account=" + account.getId() +
                ", longUrl='" + longUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", redirectType='" + redirectType + '\'' +
                '}';
    }
}