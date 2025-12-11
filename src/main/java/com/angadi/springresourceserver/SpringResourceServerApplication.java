package com.angadi.springresourceserver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class SpringResourceServerApplication {

	public static void main(String[] args) {

		System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\poorv\\IdeaProjects\\spring-resource-server\\src\\main\\resources\\client-truststore.jks");

		// Set the password for your truststore
		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
		SpringApplication.run(SpringResourceServerApplication.class, args);
	}

}
