package org.tames.ecommercecrud.modules.product.exception;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String message) {
    super(message);
  }

  public ProductNotFoundException(Long id) {
    super("Product with ID: " + id + " not found");
  }
}
