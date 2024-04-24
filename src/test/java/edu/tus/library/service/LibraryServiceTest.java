package edu.tus.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LibraryServiceTest {
	
	@Test
	public void testBuildIdLogic()
	{
		LibraryService lib =new LibraryService();
		String id = lib.buildId("ZMAN", 24);
		assertEquals(id,"OLDZMAN24");
		String id1 = lib.buildId("MAN", 24);
		assertEquals(id1,"MAN24");
		
	}

}
