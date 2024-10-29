package com.toyproject.musinsa.entity.product;

import com.toyproject.musinsa.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Product extends BaseEntity {

    // 현재 사이트에서 상품을 구분하는 코드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 각 브랜드에서 해당 상품을 구분하는 코드
    private String productCode;

    private String productName;

    // 기본 Eager -> 기본으로 대표사진만 나오면 되므로.
    // cascadeType.All : Product
    @OneToOne(mappedBy = "product",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private ProductDescription productDescription;

    private String categoryId;

    private String repImg;

    private int price;

    private int eventPrice;

    private String productStatus;

    private LocalDateTime createDatetime;

    private String registerManager;


    private float starRating;

    @ColumnDefault("244")
    private int viewCount;

    private int reviewCount;
}
