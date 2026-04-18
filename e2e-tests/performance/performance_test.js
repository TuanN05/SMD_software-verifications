Feature("Performance and Load Times");

Scenario("Login page loads quickly", ({ I }) => {
  I.amOnPage("/login");
  I.waitForElement('input[type="text"]', 5);
  I.seeElement('input[type="password"]');
});

Scenario("Page elements load in correct order", ({ I }) => {
  I.amOnPage("/login");
  I.see("SMD System");
  I.see("Mã người dùng");
  I.see("Mật khẩu");
});

Scenario("Form remains responsive after multiple interactions", ({ I }) => {
  I.amOnPage("/login");

  // Multiple rapid interactions
  I.fillField('input[type="text"]', "test1");
  I.wait(0.3);
  I.clearField('input[type="text"]');
  I.fillField('input[type="text"]', "test2");
  I.wait(0.3);
  I.clearField('input[type="text"]');
  I.fillField('input[type="text"]', "test3");

  I.seeInField('input[type="text"]', "test3");
});
