package kr.co.gochang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GochangApplication {

	public static void main(String[] args) {
		SpringApplication.run(GochangApplication.class, args);
	}

}
