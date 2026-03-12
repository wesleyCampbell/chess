package dataaccess;

import org.junit.jupiter.api.*;

import dataaccess.sqldao.SQLUserDAO;

import model.*;

public class UserDAoTests extends DatabaseDAoTests {
	private static UserDAO userDAO;
	//
	// ==================== TEST SETUP ===============
	//
	
	@BeforeEach
	public void init() {
		userDAO = Assertions.assertDoesNotThrow(() -> new SQLUserDAO());
	}
	
	//
	// ==================== TEST CASES ================
	//
	
	@Test
	@Order(1)
	public void userDataPersistance() {
		UserData data = Assertions.assertDoesNotThrow(() -> userDAO.getUser(username1));

		Assertions.assertEquals(username1, data.username());
	}

	@Test
	@Order(3)
	public void userDataGetCorrect() {
		UserData data = Assertions.assertDoesNotThrow(() -> userDAO.getUser(username2));

		Assertions.assertEquals(username2, data.username());
		Assertions.assertEquals(password2, data.password());
	}

	@Test
	@Order(4)
	public void userDataGetIncorrect() {
		String badUsername = "malitious user";
		Assertions.assertThrows(AuthenticationException.class, () -> userDAO.getUser(badUsername));
	}
	
	@Test
	@Order(5)
	public void userDataInsert() {
		String newUsername = "billy bob";
		String newPassword = "super secure";
		String newEmail = "doesn't even matter";

		Assertions.assertThrows(AuthenticationException.class, () -> userDAO.getUser(newUsername));

		UserData newUserData = new UserData(newUsername, newPassword, newEmail);

		Assertions.assertDoesNotThrow(() -> userDAO.createUser(newUserData));

		UserData verifyData = Assertions.assertDoesNotThrow(() -> userDAO.getUser(newUsername));

		Assertions.assertEquals(newUserData.username(), verifyData.username());
		Assertions.assertEquals(newUserData.password(), verifyData.password());
		Assertions.assertEquals(newUserData.email(), verifyData.email());
	}

	@Test
	@Order(6)
	public void userDataClear() {
		Assertions.assertDoesNotThrow(() -> userDAO.getUser(username1));

		Assertions.assertDoesNotThrow(() -> userDAO.clearAllUserData());

		Assertions.assertThrows(AuthenticationException.class, () -> userDAO.getUser(username1));
		Assertions.assertThrows(AuthenticationException.class, () -> userDAO.getUser(username2));
	}
}
