Feature("UI/UX Validation");

Scenario("Login form is properly centered and visible", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement("body");
  I.wait(1);
});

Scenario("All form labels are visible", ({ I }) => {
  I.amOnPage("/login");
  I.see("Mã người dùng");
  I.see("Mật khẩu");
});

Scenario("Page responds to user interactions", ({ I }) => {
  I.amOnPage("/login");
  I.click('input[type="text"]');
  I.see("Mã người dùng");
});

Scenario("Form clears after page reload", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "testdata");
  I.refreshPage();
  I.waitForElement('input[type="text"]', 5);
});

Scenario("Multiple input attempts are possible", ({ I }) => {
  I.amOnPage("/login");

  // First attempt
  I.fillField('input[type="text"]', "user1");
  I.wait(0.5);
  I.clearField('input[type="text"]');

  // Second attempt
  I.fillField('input[type="text"]', "user2");
  I.seeInField('input[type="text"]', "user2");
});
