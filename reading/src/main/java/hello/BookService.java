package hello;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class BookService {

    final String bookServiceEndpoint = "http://BOOKSTORE-SERVICE";

    @Autowired
    private final RestTemplate restTemplate;


    public BookService(RestTemplate rest) {
        this.restTemplate = rest;
    }

    @HystrixCommand(fallbackMethod = "getBookFallBacK",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "5")
            })
    public Book getBook(String id) {
        System.out.println("Isolation Thread:" + Thread.currentThread().getId());
        URI uri = URI.create(bookServiceEndpoint + "/book/" + id);

        return this.restTemplate.getForObject(uri, Book.class);
    }

    public Book getBookFallBacK(String id) {
        System.out.println("Enter fallback:" + Thread.currentThread().getId());
        return new Book(-1, "i'm fallback");
    }

    @HystrixCommand(fallbackMethod = "getBookFallBackNull")
    public Book getBookException(String id) {
        System.out.println("Isolation Thread:" + Thread.currentThread().getId());
        throw new RuntimeException("Oops Sometging wrong");
    }

    public Book getBookFallBackNull(String id) {
        System.out.println("Enter fallback:" + Thread.currentThread().getId());
        return null;
    }

    @HystrixCommand(commandKey = "shortTimeOut",fallbackMethod = "getBookFallBackStub")
    public Book getBookTimeOut(String id) {
        System.out.println("Isolation Thread:" + Thread.currentThread().getId());
        URI uri = URI.create(bookServiceEndpoint + "/slowbook/" + id);
        Book response= this.restTemplate.getForObject(uri, Book.class);
        return response;
    }

    public Book getBookFallBackStub(String id, Throwable e) {
        System.out.println("Enter fallback:" + Thread.currentThread().getId());
        return new Book(Long.parseLong(id), "UNKNOW TITLE");
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public Book getBookSemaphore(String id) {
        System.out.println("Execution Thread:" + Thread.currentThread().getId());
        URI uri = URI.create(bookServiceEndpoint + "/book/" + id);
        Book response = this.restTemplate.getForObject(uri, Book.class);
        return response;
    }

    @HystrixCommand(fallbackMethod = "getBookFallBacK")
    public Book getBookShaky(String id){
        System.out.println("Execution Thread:" + Thread.currentThread().getId());
        if(Math.random() > 0.5){
            throw new RuntimeException("Oh");
        }
        URI uri = URI.create(bookServiceEndpoint + "/book/" + id);
        Book response = this.restTemplate.getForObject(uri, Book.class);
        return response;
    }
  /*
  * Request Collapser
  * */

    @HystrixCollapser(scope = com.netflix.hystrix.HystrixCollapser.Scope.REQUEST, batchMethod = "getBookWithCollapserA")
    public Future<Book> getBookWithCollapser(String id) {
        return null;
    }

    @HystrixCommand
    public List<Book> getBookWithCollapserA(List<String> ids) throws URISyntaxException {
        String idStr = ids.toString().replace("[", "").replace("]", "").replace(" ", "");
        URI uri = URI.create(bookServiceEndpoint + "/books?id=" + idStr);
        System.out.println(uri);

        ResponseEntity<List<Book>> response = this.restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
        });
        return response.getBody();
    }

    @HystrixCollapser(scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL, batchMethod = "getBookWithCollapserB",
            collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds", value = "3000")})
    public Book getBookWithCollapserGlobal(String id) {
        return null;
    }

    @HystrixCommand
    public List<Book> getBookWithCollapserB(List<String> ids) throws URISyntaxException {
        //List<String> uniqueIds = ids.stream().distinct().collect(Collectors.toList());
        String idStr = ids.toString().replace("[", "").replace("]", "").replace(" ", "");
        URI uri = URI.create(bookServiceEndpoint + "/books?id=" + idStr);
        System.out.println(uri);

        ResponseEntity<List<Book>> response = this.restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
        });
        //List<Book> responseBook = ids.stream().map(it -> response.getBody().stream().filter(u -> String.valueOf(u.getId()).equals(it)).findFirst().get()).collect(Collectors.toList());
        return response.getBody();
    }


    @CacheResult
    @HystrixCommand
    public Book getBookCache(@CacheKey String id) {
        System.out.println("Execute get book api: " + id);
        URI uri = URI.create(bookServiceEndpoint + "/book/" + id);
        return this.restTemplate.getForObject(uri, Book.class);
    }

}
