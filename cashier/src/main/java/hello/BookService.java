package hello;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class BookService {

  final String bookServiceEndpoint="http://BOOKSTORE-SERVICE";

  @Autowired
  private final RestTemplate restTemplate;


  public BookService(RestTemplate rest) {
    this.restTemplate = rest;
  }

  @HystrixCommand(fallbackMethod = "getBookFallBacK")
  public Book getBook(String id) {
    System.out.println("Thread parent:"+Thread.currentThread().getId());
    URI uri = URI.create(bookServiceEndpoint+"/book/"+id);

    return this.restTemplate.getForObject(uri, Book.class);
  }
  public Book getBookFallBacK(String id) {
    return new Book(-1,"i'm fallback");
  }

}
