package com.wolfman.rabbitmqspringboot;

import com.wolfman.rabbitmqspringboot.producer.MyProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSpringbootApplicationTests {

	@Autowired
	MyProvider provider;

	@Test
	public void send() {
		provider.send();
	}

}

