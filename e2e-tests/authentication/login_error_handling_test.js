Feature("Login Error Handling and Invalid Credentials");

Scenario("Login fails with invalid username", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "invalid_user");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(2);
  // Should remain on login page or show error
  I.seeElement('input[type="text"]');
});

Scenario("Login fails with invalid password", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "admin");
  I.fillField('input[type="password"]', "WrongPassword");
  I.click("button");
  I.wait(2);
  // Should remain on login page or show error
  I.seeElement('input[type="password"]');
});

Scenario("Login fails with empty credentials", ({ I }) => {
  I.amOnPage("/login");
  I.click("button");
  I.wait(2);
  // Should remain on login page
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
});

Scenario("Login fails with only username", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "admin");
  I.click("button");
  I.wait(2);
  // Should remain on login page
  I.seeElement('input[type="password"]');
});

Scenario("Login fails with only password", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(2);
  // Should remain on login page
  I.seeElement('input[type="text"]');
});

Scenario("Multiple failed login attempts", ({ I }) => {
  for (let i = 0; i < 3; i++) {
    I.amOnPage("/login");
    I.fillField('input[type="text"]', "admin");
    I.fillField('input[type="password"]', "WrongPassword");
    I.click("button");
    I.wait(1);
  }
  // System should still allow login page to load
  I.seeElement('input[type="text"]');
});
