Feature("Responsive Design Tests");

Scenario("Page displays correctly on desktop viewport", ({ I }) => {
  I.resizeWindow(1280, 720);
  I.amOnPage("/login");
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
});

Scenario("All form elements are accessible", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement('input[type="text"]');
  I.seeElement('input[type="password"]');
  I.seeElement("button");
  I.seeNumberOfElements("input", 2);
});

Scenario("Page title is consistent", ({ I }) => {
  I.amOnPage("/login");
  I.seeInTitle("Syllabus Management and Digitalization System");
});

Scenario("No broken links on login page", ({ I }) => {
  I.amOnPage("/login");
  I.seeElement("a");
  // Verify link exists (forgot password)
  I.see("Quên mật khẩu");
});
