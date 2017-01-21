// Copyright (c) 2016 Per Malmberg
// Licensed under MIT, see LICENSE file.

import cmdparser4j.Changeable;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangeableTest {

	@Test
	public void testToString() {
		Changeable<Integer> c = new Changeable<Integer>(123);
		assertEquals("123", c.toString() );

	}

	@Test
	public void testEquals() {
		Changeable<Integer> c = new Changeable<Integer>(123);
		Changeable<Integer> c2 = new Changeable<Integer>(123);
		assertTrue(c.equals(c2));

		assertFalse( c.equals( 456 ));
		assertTrue( c.equals( 123 ));
	}

	@Test
	public void testHashCode() {
		assertEquals(new Integer(456).hashCode(), new Changeable<Integer>(456).hashCode());
	}

	@Test
	public void testGet() {
		Changeable<Integer> c = new Changeable<Integer>(123);
		assertEquals( new Integer(123), c.get() );
	}

	@Test
	public void testSet() {
		Changeable<Integer> c = new Changeable<Integer>(0);
		c.set( 888);
		assertEquals( new Integer(888), c.get() );
	}
}