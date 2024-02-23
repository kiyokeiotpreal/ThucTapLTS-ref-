package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rankCustomer")
@Builder
public class RankCustomer extends BaseEntity {
    private int point;

    private String description;

    private String name;

    private boolean isActive = true;

    @OneToMany(mappedBy = "rankcustomer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Promotion> promotions;

    @OneToMany(mappedBy = "rankcustomer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<User> users;
}
