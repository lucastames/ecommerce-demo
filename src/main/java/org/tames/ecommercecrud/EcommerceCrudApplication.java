package org.tames.ecommercecrud;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.tames.ecommercecrud.modules.category.entity.Category;
import org.tames.ecommercecrud.modules.category.repository.CategoryRepository;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;
import org.tames.ecommercecrud.modules.review.repository.ReviewRepository;

@SpringBootApplication
public class EcommerceCrudApplication {
  @Autowired CategoryRepository categoryRepository;
  @Autowired ProductRepository productRepository;
  @Autowired ReviewRepository reviewRepository;

  public static void main(String[] args) {
    SpringApplication.run(EcommerceCrudApplication.class, args);
  }

  @Bean
  CommandLineRunner commandLineRunner() {
    return (args) -> {
      Category c1 = new Category("Cat1");
      Category c2 = new Category("Cat2");
      Category c3 = new Category("Cat3");
      categoryRepository.saveAll(List.of(c1, c2, c3));

      Product p1 = new Product("Test1", BigDecimal.TEN, "Desc1", 10);
      p1.addCategory(c1);
      productRepository.save(p1);
      // Produto p2 = new Produto("Test2", BigDecimal.TEN, "Desc2", 20);
      // p1.setCategories(Set.of(c2));
      // Produto p3 = new Produto("Test3", BigDecimal.TEN, "Desc3", 30);
      // p1.setCategories(Set.of(c1, c3));
      // produtoRepository.saveAll(List.of(p1, p2, p3));

      // Avaliacao a1 = new Avaliacao("Desc", LocalDate.now(), Nota.BOM, p1);
      // Avaliacao a2 = new Avaliacao("Desc", LocalDate.now(), Nota.BOM, p2);
      // Avaliacao a3 = new Avaliacao("Desc", LocalDate.now(), Nota.BOM, p3);
      // Avaliacao a4 = new Avaliacao("Desc", LocalDate.now(), Nota.BOM, p1);
      // avaliacaoRepository.saveAll(List.of(a1, a2, a3, a4));
    };
  }
}
