package hello;

import com.netflix.hystrix.HystrixRequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@EnableCircuitBreaker
@RestController
@EnableDiscoveryClient
@EnableHystrixDashboard
@SpringBootApplication
public class ReadingApplication {

  @Autowired
  private DiscoveryClient discoveryClient;

  @Autowired
  private BookService bookService;

  @Bean
  @LoadBalanced
  public RestTemplate rest(RestTemplateBuilder builder) {
    return builder.build();
  }

  @RequestMapping("/read/{id}")
  public Book toRead(@PathVariable String id) {
    return bookService.getBook(id);
  }

  @RequestMapping("/read-ex/{id}")
  public Book toReadEx(@PathVariable String id) {
    return bookService.getBookException(id);
  }

  @RequestMapping("/read-timeout/{id}")
  public Book toReadTimeOut(@PathVariable String id) {
    return bookService.getBookTimeOut(id);
  }


  @RequestMapping("/read-sem/{id}")
  public Book toReadSemaphore(@PathVariable String id) {
    return bookService.getBookSemaphore(id);
  }


  @RequestMapping("/read-shaky/{id}")
  public Book toReadShaky(@PathVariable String id) {
    return bookService.getBookShaky(id);
  }

  @RequestMapping("/read-collapser/{id}")
  public Book toReadwithCollapser(@PathVariable String id) throws ExecutionException, InterruptedException {
    Future<Book> f1 =bookService.getBookWithCollapser(id);
    Future<Book> f2 =bookService.getBookWithCollapser(id+"1");
    Book book=f1.get();
    //   System.out.println(HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());
    return book;
  }

  @RequestMapping("/read-collapser-global/{id}")
  public Book toReadwithCollapserGlobal(@PathVariable String id) throws ExecutionException, InterruptedException {
    long startTime=System.currentTimeMillis();
    Book response= bookService.getBookWithCollapserGlobal(id);
    System.out.println("Time: "+(System.currentTimeMillis() - startTime));
    return response;

  }

  @RequestMapping("/read-cache/{id}")
  public Book toReadcache(@PathVariable String id) {
    bookService.getBookCache(id);
    return bookService.getBookCache(id);
  }

  @RequestMapping("/service-instances/{applicationName}")
  public List<ServiceInstance> serviceInstancesByApplicationName(
          @PathVariable String applicationName) {
    return this.discoveryClient.getInstances(applicationName);
  }

  public static void main(String[] args) {
    SpringApplication.run(ReadingApplication.class, args);
  }

}
