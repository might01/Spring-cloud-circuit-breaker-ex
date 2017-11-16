package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class BookstoreApplication {

  @RequestMapping(value = "/book/{id}")
  public Book readingList(@PathVariable(value="id")Long id){
    System.out.println("getBook :"+ id.toString());
    return new Book(id,"This is Book "+id);
  }

  @RequestMapping(value = "/slowbook/{id}")
  public Book readingSlowList(@PathVariable(value="id")Long id){
    System.out.println("getSlowBook :"+ id.toString());
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      System.out.println("Oops!");
    }
    return new Book(id,"This is Book "+id);
  }










  @RequestMapping(value = "/books")
  public List<Book> readingList(@RequestParam(value="id")List<Long> ids){
    System.out.println("List :"+ ids.toString());
    List<Book> response=new ArrayList<>();
    for(Long id:ids){
      Book book=new Book(id,"This is Book "+id);
        response.add(book);
    }
    return response;
  }

  public static void main(String[] args) {
    SpringApplication.run(BookstoreApplication.class, args);
  }
}
