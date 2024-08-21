package fr.robinjesson.azertyapi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "text")
@Data
public class TextEntity extends ElementEntity{
}
