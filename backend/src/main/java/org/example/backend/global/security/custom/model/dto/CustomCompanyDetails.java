package org.example.backend.global.security.custom.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.company.model.entity.Company;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CustomCompanyDetails implements UserDetails {
    private final Company company;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                return company.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return company.getPassword();
    }

    @Override
    public String getUsername() {
        return company.getEmail();
    }
    public Long getIdx() {
        return company.getIdx();
    }

}
