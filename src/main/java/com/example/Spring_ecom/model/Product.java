package com.example.Spring_ecom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_id", columnList = "id"),
                @Index(name = "idx_name", columnList = "name"),
                @Index(name = "idx_brand", columnList = "brand"),
                @Index(name = "idx_description", columnList = "description"),
                @Index(name = "idx_price", columnList = "price"),

        }



)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date releaseDate;
    private boolean productAvailable;
    private int availableQuantity;
    private String imagename;
    private String imagetype;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagepath;


}
