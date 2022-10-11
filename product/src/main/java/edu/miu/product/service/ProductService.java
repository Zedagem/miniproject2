package edu.miu.product.service;

import edu.miu.product.entity.Product;

import java.util.List;

public interface ProductService {
     List<Product> findAll();
     Product findById(Long id);
     void save(Product p);
     void update(Long id, Product product);
     void deleteById(Long id);


}
