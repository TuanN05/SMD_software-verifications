Feature("User Authentication");

Scenario("User can access login page", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
  I.seeElement("button");
});

Scenario("Login form displays username and password fields", ({ I }) => {
  I.amOnPage("/login");
  I.see("Mã người dùng");
  I.see("Mật khẩu");
});

Scenario("Verify page title on login page", ({ I }) => {
  I.amOnPage("/login");
  I.seeInTitle("Syllabus Management and Digitalization System");
});
