package ru.kamotora.graal.api;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Test {
    @Id
    private String email;

}
