package com.tr.demo.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.model.enums.CustomerStatusEnums;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
@Setter
@Builder
public class CustomerPrincipal  implements UserDetails , Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private CustomerStatusEnums customerStatus;
    private Integer failLoginCount;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private String email;
    private Integer orderCount;
    private Double discountRate;
    private String customerName;

    public static CustomerPrincipal constructCustomerPrincipal(final CustomerEntity customerEntity) {
        return CustomerPrincipal.builder()
                .id(customerEntity.getId())
                .username(customerEntity.getUsername())
                .password(customerEntity.getPassword())
                .customerStatus(CustomerStatusEnums.fromValue(customerEntity.getStatus()))
                .failLoginCount(customerEntity.getFailLoginCount())
                .authorities(Collections.emptyList())
                .enabled(customerEntity.getEnabled())
                .email(customerEntity.getEmail())
                .orderCount(customerEntity.getOrderCount())
                .discountRate(customerEntity.getTiersEntity().getDiscountRate())
                .customerName(customerEntity.getName())
                .build();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerPrincipal that = (CustomerPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
