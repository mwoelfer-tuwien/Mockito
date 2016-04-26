package Woelfer;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.hamcrest.MockitoHamcrest.*;

public class MockitoTest {
	LinkedList mockedList;
	@Before
	public void setUp() throws Exception {
		mockedList = mock(LinkedList.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBehaviour() {
		List mockedList = mock(List.class);
		//using mock object
		mockedList.add("one");
		mockedList.clear();
		 
		//verification
		verify(mockedList).add("one");
		verify(mockedList).clear();
	}
	
	@Test(expected=RuntimeException.class)
	public void testStubbing(){
		//You can mock concrete classes, not just interfaces
		 LinkedList mockedList = mock(LinkedList.class);

		 //stubbing
		 when(mockedList.get(0)).thenReturn("first");
		 when(mockedList.get(1)).thenThrow(new RuntimeException());

		 //following prints "first"
		 System.out.println(mockedList.get(0));

		 //following throws runtime exception
		 System.out.println(mockedList.get(1));

		 //following prints "null" because get(999) was not stubbed
		 System.out.println(mockedList.get(999));

		 //Although it is possible to verify a stubbed invocation, usually it's just redundant
		 //If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
		 //If your code doesn't care what get(0) returns, then it should not be stubbed. Not convinced? See here.
		 verify(mockedList).get(0);
	}
	
	@Test
	public void testArgumentMatcher(){
		 //stubbing using built-in anyInt() argument matcher
		 when(mockedList.get(anyInt())).thenReturn("element");

		 //stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
		 //when(mockedList.contains(argThat(isValid()))).thenReturn("element");

		 //following prints "element"
		 System.out.println(mockedList.get(999));

		 //you can also verify using an argument matcher
		 verify(mockedList).get(anyInt());
	}
	
	@Test
	public void testNumberOfInovotaions(){
		 //using mock
		 mockedList.add("once");

		 mockedList.add("twice");
		 mockedList.add("twice");

		 mockedList.add("three times");
		 mockedList.add("three times");
		 mockedList.add("three times");

		 //following two verifications work exactly the same - times(1) is used by default
		 verify(mockedList).add("once");
		 verify(mockedList, times(1)).add("once");

		 //exact number of invocations verification
		 verify(mockedList, times(2)).add("twice");
		 verify(mockedList, times(3)).add("three times");

		 //verification using never(). never() is an alias to times(0)
		 verify(mockedList, never()).add("never happened");

		 //verification using atLeast()/atMost()
		 verify(mockedList, atLeastOnce()).add("three times");
		 verify(mockedList, atLeast(2)).add("twice");
		 verify(mockedList, atMost(5)).add("three times");
	}
	
	//mit der methode doThrow() kann man sagen welche Exception geworfen werden soll wenn (=> .when()) etwas bestimmtes gemacht wird
	@Test
	public void testMethodWithExceptions(){
		doThrow(new RuntimeException()).when(mockedList).clear();

	   //following throws RuntimeException:
	   mockedList.clear();
	}
	
	/*
	 * Zuerst wird ein mock der Klasse List erstellt, in welchem dann 2 Strings geadded werden
	 * Danach wird ein inOrder objekt erzeugt, welches als Parameter im Konstruktor jene objekte nimmt die es �berpr�fen soll
	 * In n�chsten Block wird dann tats�chlich �berpr�ft ob die Strings in der richtigen Reihenfolge geadded wurden
	 * 
	 * Als n�chstes werden 2 mocks der Klasse List erstellt, in welche jeweils 1 String geadded wird
	 * Danach wird wieder ein inOrder objekt erzeugt, welches wieder als Parameter die Objekte nimmt die es �berpr�ft
	 * Nun kann man wieder �berpr�fen ob tats�chlich zuerst der eine String und dann der andere geadded wurde
	 */
	@Test
	public void testOrder(){
		 // A. Single mock whose methods must be invoked in a particular order
		 List singleMock = mock(List.class);

		 //using a single mock
		 singleMock.add("was added first");
		 singleMock.add("was added second");

		 //create an inOrder verifier for a single mock
		 InOrder inOrder = inOrder(singleMock);

		 //following will make sure that add is first called with "was added first, then with "was added second"
		 inOrder.verify(singleMock).add("was added first");
		 inOrder.verify(singleMock).add("was added second");

		 // B. Multiple mocks that must be used in a particular order
		 List firstMock = mock(List.class);
		 List secondMock = mock(List.class);

		 //using mocks
		 firstMock.add("was called first");
		 secondMock.add("was called second");

		 //create inOrder object passing any mocks that need to be verified in order
		 InOrder inOrder2 = inOrder(firstMock, secondMock);

		 //following will make sure that firstMock was called before secondMock
		 inOrder2.verify(firstMock).add("was called first");
		 inOrder2.verify(secondMock).add("was called second");

	}

	
	@Test
	public void testInteractions(){
		List mockOne = mock(List.class);
		List mockTwo = mock(List.class);
		List mockThree = mock(List.class);
		//using mocks - only mockOne is interacted
		 mockOne.add("one");

		 //ordinary verification
		 verify(mockOne).add("one");

		 //verify that method was never called on a mock
		 verify(mockOne, never()).add("two");

		 //verify that other mocks were not interacted
		 verifyZeroInteractions(mockTwo, mockThree);
	}
}
