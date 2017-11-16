package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@EnableCircuitBreaker
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class CashierApplication {

  @Autowired
  private DiscoveryClient discoveryClient;

  @Autowired
  private BookService bookService;

  @Bean
  @LoadBalanced
  public RestTemplate rest(RestTemplateBuilder builder) {
    return builder.build();
  }

  @RequestMapping("/cashier/{id}")
  public Book toRead(@PathVariable String id) {
    System.out.println("Thread controller:"+Thread.currentThread().getId());
    return bookService.getBook(id);
  }

  @RequestMapping("/service-instances/{applicationName}")
  public List<ServiceInstance> serviceInstancesByApplicationName(
          @PathVariable String applicationName) {
    return this.discoveryClient.getInstances(applicationName);
  }

  public static void main(String[] args) {
    SpringApplication.run(CashierApplication.class, args);
  }

}
