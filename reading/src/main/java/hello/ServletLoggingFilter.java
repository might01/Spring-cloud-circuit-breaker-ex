package hello;


import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class ServletLoggingFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(((HttpServletRequest)request).getRequestURI().equalsIgnoreCase("/favicon.ico")){
            return ;
        }
        String url = ((HttpServletRequest)request).getRequestURL().toString();

        System.out.println("Request => "+url);
        System.out.println("Thread Id:"+Thread.currentThread().getId());
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            chain.doFilter(request, response);
        }finally {
            context.shutdown();
        }

    }

    @Override
    public void destroy() {

    }
}
