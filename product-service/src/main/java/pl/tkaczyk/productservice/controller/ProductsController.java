package pl.tkaczyk.productservice.controller;


import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.tkaczyk.core.dto.Product;
import pl.tkaczyk.productservice.dto.ProductCreationRequest;
import pl.tkaczyk.productservice.dto.ProductCreationResponse;
import pl.tkaczyk.productservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsController {
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> findAll() {
        return productService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCreationResponse save(@RequestBody @Valid ProductCreationRequest request) {
        var product = new Product();
        BeanUtils.copyProperties(request, product);
        Product result = productService.save(product);

        var productCreationResponse = new ProductCreationResponse();
        BeanUtils.copyProperties(result, productCreationResponse);
        return productCreationResponse;
    }
}
