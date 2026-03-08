package service;

import service.ClearDataService.ClearDataRequest;

import org.junit.jupiter.api.*;

public class ClearDataServiceTests extends WithAllDataTests {
	//
	// ======================= TEST CASES ======================= 
	//
	
	@Test
	@DisplayName("ClearData Service Unit Tests") 
	public void clearData() {
		ClearDataService service = new ClearDataService(authDAO, gameDAO, userDAO);


		ClearDataRequest request = new ClearDataRequest();

		Assertions.assertDoesNotThrow(() -> service.clearData(request));
	}

}
