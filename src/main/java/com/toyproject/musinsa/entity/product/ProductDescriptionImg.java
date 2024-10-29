package com.toyproject.musinsa.entity.product;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String imgPath;

    private String name;

    private int orderNum;

    // fk를 가지는 쪽이 @JoinColumn을 사용.
    // ManyToOne 은 @JoinColumn 써주기.
    @ManyToOne
    @JoinColumn(name = "PRODUCTDESCRIPTION_ID")
    private ProductDescription productDescription;


    private String isUsed;

    private String kindOf;

    private int size;

}
