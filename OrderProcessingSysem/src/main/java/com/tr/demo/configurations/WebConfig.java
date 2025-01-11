package com.tr.demo.configurations;

import com.tr.demo.resolver.CustomerPrincipalArgumentResolver;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomerPrincipalArgumentResolver customerPrincipalArgumentResolver;

    public WebConfig(CustomerPrincipalArgumentResolver customerPrincipalArgumentResolver) {
        this.customerPrincipalArgumentResolver = customerPrincipalArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customerPrincipalArgumentResolver);
    }
}
