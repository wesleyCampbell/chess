package dataaccess;

import org.junit.jupiter.api.*;

import dataaccess.sqldao.SQLAuthDAO;
import model.AuthData;

public class AuthDAoTests extends DatabaseDAoTests {
	private static AuthDAO authDAO;
	//
	// ==================== TEST SETUP ===============
	//
	
	@BeforeEach
	public void init() {
		authDAO = Assertions.assertDoesNotThrow(() -> new SQLAuthDAO());
	}

	//
	// ==================== TEST CASES ================
	//
	
	@Test
	@Order(1)
	public void authDataPersistance() {
		AuthData data = Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authToken1));

		Assertions.assertEquals(username1, data.username());
	}

	@Test
	@Order(2)
	public void authDataRemove() {
		AuthData data = Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authToken2));

		Assertions.assertDoesNotThrow(() -> authDAO.removeAuth(data));

		Assertions.assertThrows(AuthenticationException.class, () -> authDAO.getAuth(authToken2));
	}

	@Test
	@Order(3)
	public void authDataGetCorrect() {
		AuthData data = Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authToken1));

		Assertions.assertEquals(data.authToken(), authToken1);
		Assertions.assertEquals(username1, data.username());
	}

	@Test
	@Order(4)
	public void authDataGetIncorrect() {
		String badAuthToken = "heheheha";
		Assertions.assertThrows(AuthenticationException.class, () -> authDAO.getAuth(badAuthToken));
	}

	@Test
	@Order(5)
	public void authDataInsert() {
		String newAuthToken = "THIS IS A NEW AUTH TOKEN";
		String newUsername = "newUsername";

		Assertions.assertThrows(AuthenticationException.class, () -> authDAO.getAuth(newAuthToken));

		AuthData newAuthData = new AuthData(newAuthToken, newUsername);
		Assertions.assertDoesNotThrow(() -> authDAO.createAuth(newAuthData));

		AuthData verifyData = Assertions.assertDoesNotThrow(() -> authDAO.getAuth(newAuthToken));

		Assertions.assertEquals(newAuthData.authToken(), verifyData.authToken());
		Assertions.assertEquals(newAuthData.username(), verifyData.username());
	}

	@Test
	@Order(6)
	public void authDataClear() {
		Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authToken1));

		Assertions.assertDoesNotThrow(() -> authDAO.clearAllAuthData());

		Assertions.assertThrows(AuthenticationException.class, () -> authDAO.getAuth(authToken1));
	}

}
