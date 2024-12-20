package com.toyproject.musinsa.entity.product;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name="product_id")
    private Product product;


    //Eager :상품 상세를 들어가면 바로 사진을 가져와야 하기 때문에.
    @OneToMany(mappedBy = "productDescription",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<ProductDescriptionImg> descriptionImgs = new ArrayList<>();

}
