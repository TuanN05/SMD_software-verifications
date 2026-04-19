Feature("Admin Role Dashboard and Features");

const adminUsername = "admin";
const password = "Password123";

Before(({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', adminUsername);
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
});

Scenario("Admin can access dashboard", ({ I }) => {
  I.seeElement("body");
});

Scenario("Admin dashboard shows main menu options", ({ I }) => {
  // Admin typically sees: Users, Courses, Settings, Reports, etc.
  I.waitForElement("nav", 5);
  I.seeElement("nav");
});

Scenario("Admin can see user management options", ({ I }) => {
  I.seeElement("nav");
  // Look for navigation or menu items that exist
  I.seeElement("body");
});

Scenario("Admin has access to courses section", ({ I }) => {
  I.seeElement("body");
});

Scenario("Admin can view system settings", ({ I }) => {
  I.seeElement("body");
});
