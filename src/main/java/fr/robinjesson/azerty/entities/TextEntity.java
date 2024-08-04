package fr.robinjesson.azerty.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "text")
@Getter
@Setter
public class TextEntity extends ElementEntity{
}
