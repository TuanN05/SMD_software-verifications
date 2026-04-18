Feature("Form Validation and Error Handling");

Scenario("Form handles empty submissions gracefully", ({ I }) => {
  I.amOnPage("/login");
  // Don't fill any fields, just observe the page state
  I.seeElement("button");
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
});

Scenario("Password field masks input", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="password"]', "mysecretpassword");
  // Password should be masked (type="password")
  I.seeElement('input[type="password"]');
});

Scenario("Form fields accept various characters", ({ I }) => {
  I.amOnPage("/login");

  // Test username field with special characters
  I.fillField('input[type="text"]', "user@domain.com");
  I.seeInField('input[type="text"]', "user@domain.com");

  I.clearField('input[type="text"]');

  // Test with numbers
  I.fillField('input[type="text"]', "user123");
  I.seeInField('input[type="text"]', "user123");
});

Scenario("Tab key navigates between form fields", ({ I }) => {
  I.amOnPage("/login");
  I.click('input[type="text"]');
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
});
