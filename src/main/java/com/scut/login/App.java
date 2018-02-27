package com.scut.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

/**
 * 入口类 博客出处：http://www.cnblogs.com/GoodHelper/
 *
 */

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
public class App {
	@Configuration
	@ConditionalOnClass({ Servlet.class, StandardServletMultipartResolver.class,
			MultipartConfigElement.class })
	@ConditionalOnProperty(prefix = "spring.http.multipart", name = "enabled", matchIfMissing = true)
	@EnableConfigurationProperties(MultipartProperties.class)
	public class MultipartAutoConfiguration {
		@Autowired
		private MultipartProperties multipartProperties;

//    public MultipartAutoConfiguration(MultipartProperties multipartProperties) {
//        this.multipartProperties = multipartProperties;
//    }

		@Bean
		@ConditionalOnMissingBean
		public MultipartConfigElement multipartConfigElement() {
			this.multipartProperties.setMaxFileSize("-1");
			return this.multipartProperties.createMultipartConfig();
		}

		@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
		@ConditionalOnMissingBean(MultipartResolver.class)
		public StandardServletMultipartResolver multipartResolver() {
			StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
			multipartResolver.setResolveLazily(false);
			return multipartResolver;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
