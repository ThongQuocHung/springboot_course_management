package group_2.cursus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class CursusApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void testBeanExistence() {
		assertThat(applicationContext.containsBean("cursusApplication")).isTrue();
	}

	@Test
	void testUserRepositoryBean() {
		assertThat(applicationContext.containsBean("userRepository")).isTrue();
	}

	@Test
	void testCourseRepositoryBean() {
		assertThat(applicationContext.containsBean("courseRepository")).isTrue();
	}
}
