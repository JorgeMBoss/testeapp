package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A UserApp.
 */
@Table("user_app")
public class UserApp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String userId;

    @Transient
    private User user;

    @JsonIgnoreProperties(value = { "pessoas", "criacaos", "criacaoAnamnese", "criacaoConsumos", "userApps" }, allowSetters = true)
    @Transient
    private Set<EmpresaApp> empresaApps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserApp id(Long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public UserApp user(User user) {
        this.setUser(user);
        this.userId = user != null ? user.getId() : null;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String user) {
        this.userId = user;
    }

    public Set<EmpresaApp> getEmpresaApps() {
        return this.empresaApps;
    }

    public UserApp empresaApps(Set<EmpresaApp> empresaApps) {
        this.setEmpresaApps(empresaApps);
        return this;
    }

    public UserApp addEmpresaApp(EmpresaApp empresaApp) {
        this.empresaApps.add(empresaApp);
        empresaApp.getUserApps().add(this);
        return this;
    }

    public UserApp removeEmpresaApp(EmpresaApp empresaApp) {
        this.empresaApps.remove(empresaApp);
        empresaApp.getUserApps().remove(this);
        return this;
    }

    public void setEmpresaApps(Set<EmpresaApp> empresaApps) {
        if (this.empresaApps != null) {
            this.empresaApps.forEach(i -> i.removeUserApp(this));
        }
        if (empresaApps != null) {
            empresaApps.forEach(i -> i.addUserApp(this));
        }
        this.empresaApps = empresaApps;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserApp)) {
            return false;
        }
        return id != null && id.equals(((UserApp) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserApp{" +
            "id=" + getId() +
            "}";
    }
}
