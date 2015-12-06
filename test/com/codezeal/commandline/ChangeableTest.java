package com.codezeal.commandline;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Per Malmberg on 2015-12-05.
 */
public class ChangeableTest {

	@Test
	public void testToString() throws Exception {
		Changeable<Integer> c = new Changeable<Integer>(123);
		assertEquals("123", c.toString() );

	}

	@Test
	public void testEquals() throws Exception {
		Changeable<Integer> c = new Changeable<Integer>(123);
		Changeable<Integer> c2 = new Changeable<Integer>(123);
		assertTrue(c.equals(c2));

		assertFalse( c.equals( 456 ));
		assertTrue( c.equals( 123 ));
	}

	@Test
	public void testHashCode() throws Exception {
		assertEquals(new Integer(456).hashCode(), new Changeable<Integer>(456).hashCode());
	}

	@Test
	public void testGet() throws Exception {
		Changeable<Integer> c = new Changeable<Integer>(123);
		assertEquals( new Integer(123), c.get() );
	}

	@Test
	public void testSet() throws Exception {
		Changeable<Integer> c = new Changeable<Integer>(0);
		c.set( 888);
		assertEquals( new Integer(888), c.get() );
	}
}