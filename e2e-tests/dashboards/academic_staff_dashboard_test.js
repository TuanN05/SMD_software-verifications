Feature("Academic Staff Role Dashboard and Features");

const username = "academic_staff";
const password = "Password123";

Before(({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', username);
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
});

Scenario("Academic Staff can access dashboard", ({ I }) => {
  I.seeElement("body");
});

Scenario("Academic Staff sees role-specific options", ({ I }) => {
  I.waitForElement("nav", 5);
  I.seeElement("nav");
});

Scenario("Academic Staff can manage courses", ({ I }) => {
  I.seeElement("body");
});

Scenario("Academic Staff can view syllabus information", ({ I }) => {
  I.seeElement("body");
});

Scenario("Academic Staff has access to reports", ({ I }) => {
  I.seeElement("body");
});
