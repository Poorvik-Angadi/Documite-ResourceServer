package com.angadi.springresourceserver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class SpringResourceServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringResourceServerApplication.class, args);
	}

}
