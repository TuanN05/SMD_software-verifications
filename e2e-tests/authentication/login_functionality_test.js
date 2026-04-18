Feature("User Login and Navigation");

Scenario("User can view the login page elements", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
  I.see("Đăng nhập");
});

Scenario("User can enter credentials in login form", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "testuser");
  I.fillField('input[type="password"]', "testpass123");
  I.seeInField('input[type="text"]', "testuser");
  I.seeInField('input[type="password"]', "testpass123");
});

Scenario("User sees forgot password link", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement("a");
  I.see("Quên mật khẩu");
});

Scenario("Verify login button is clickable", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement("button");
  I.see("Đăng nhập");
});
