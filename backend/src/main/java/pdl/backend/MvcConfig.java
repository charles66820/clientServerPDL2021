package pdl.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      // REDIRECTION FOR FRONTEND (when user reload the page)
      registry.addViewController("/about").setViewName("forward:/index.html");
      registry.addViewController("/image/*").setViewName("forward:/index.html");
    }
}