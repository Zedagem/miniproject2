package edu.miu.product.controller;

import edu.miu.product.entity.Product;
import edu.miu.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public List<Product> findAll(){
      return   productService.findAll();
    }
    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id){
               return productService.findById(id);
    }
    @PostMapping
    public void save(@RequestBody Product p){
        productService.save(p);
    }
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Product product){
        productService.update(id,product);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        productService.deleteById(id);
    }
}
