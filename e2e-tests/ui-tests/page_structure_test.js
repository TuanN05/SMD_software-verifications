Feature("Navigation and Page Structure");

Scenario("Navigation elements are visible on login page", ({ I }) => {
  I.amOnPage("/login");
  // Check if page has header/title
  I.see("SMD System");
});

Scenario("Application renders without errors", ({ I }) => {
  I.amOnPage("/login");
  I.waitForElement("body", 5);
  I.seeInSource("<html");
});

Scenario("Input fields have correct types", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
  I.seeNumberOfElements("input", 2);
});

Scenario("Submit button exists and is visible", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement('button[type="submit"]', 1);
});
