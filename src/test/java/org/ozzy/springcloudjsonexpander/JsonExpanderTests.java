package org.ozzy.springcloudjsonexpander;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {org.ozzy.springcloudjsonexpander.JsonPropertyExpandingPropertyLocator.class})
@TestPropertySource(properties = { "myproperty = { \"first\": 50, \"second\": \"fish\" , \"obj\" : { \"bar\": \"foo\" } } "})
class JsonExpanderTests {

	@Test
	void contextLoads() {
	}

	@Test
	void verifyExpansion(@Autowired Environment env, @Autowired JsonPropertyExpandingPropertyLocator jpepl){
		PropertySource<?> ps = jpepl.locate(env);
		assertNotNull(ps);
		assertTrue(ps.containsProperty("myproperty.first"));
		assertTrue(ps.containsProperty("myproperty.second"));
		assertTrue(ps.containsProperty("myproperty.obj.bar"));
		assertEquals(ps.getProperty("myproperty.first").toString(),"50");
		assertEquals(ps.getProperty("myproperty.second").toString(),"fish");
		assertEquals(ps.getProperty("myproperty.obj.bar").toString(),"foo");
	}

}
