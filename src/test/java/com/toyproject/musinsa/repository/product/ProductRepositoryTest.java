package com.toyproject.musinsa.repository.product;

import com.toyproject.musinsa.entity.product.Product;
import com.toyproject.musinsa.entity.product.ProductDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct(){

        return Product.builder()
                .productCode("aaaaaaaa")
                .productName("나이키 반팔")
                .categoryId("C01")
                .repImg("ahsdgpomkn,bwniohaggj;hswrgkb.mmeio;h")
                .productDescription(new ProductDescription())
                .productStatus("Sale")
                .price(59000)
                .eventPrice(39000)
                .registerManager("정룡우")
                .build();
    }

    @Test
    @DisplayName("product 생성 test")
    public void test1(){
        Product product =createProduct();

        productRepository.save(product);


        Optional<Product> product1 = productRepository.findByProductName(product.getProductName());
        assertTrue(product1.isPresent());

        System.out.println(product1.get());
    }

    @Test
    @DisplayName("product 삭제 test")
    public void test2(){
        Product product =createProduct();
        productRepository.save(product);

        Optional<Product> product1 = productRepository.findByProductName(product.getProductName());
        assertTrue(product1.isPresent());

        productRepository.deleteByProductName(product.getProductName());
        Optional<Product> product2 = productRepository.findByProductName(product.getProductName());
        assertTrue(product2.isEmpty());
    }

    @Test
    @DisplayName("product 수정 test")
    public void test3(){
        Product product =createProduct();
        productRepository.save(product);
        Optional<Product> product1 = productRepository.findByProductName(product.getProductName());
        assertTrue(product1.isPresent());

        // 이름 변경
        product.setProductName("아디다스 반팔");
        productRepository.save(product);

        //변경된 이름으로 DB검색
        Optional<Product> product2 = productRepository.findByProductName(product.getProductName());
        assertTrue(product2.isPresent());
        assertTrue(product1.orElse(null).getId() == product2.orElse(null).getId());
    }

    @Test
    @DisplayName("product 읽기 test - 정렬(OrderBy)")
    public void test4(){
        for(int i =0; i<=10;i++){
            Product product =createProduct();
            productRepository.save(product);
        }

        // Id 를 내림차순으로 DB검색
        Optional<List<Product>> productList = productRepository.findTop9ByOrderByIdDesc();
        assertTrue(productList.isPresent());

        List<Product> products = productList.get();

        long productIdUpper = products.get(0).getId();

        // 내림 차순 검증 로직.
        for(int i =1; i<=products.size()-1;i++){
            System.out.println(products.get(i).getId());
            assertTrue(products.get(i).getId() < productIdUpper);
            productIdUpper = products.get(i).getId();
        }
    }

}