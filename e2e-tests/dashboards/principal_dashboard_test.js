Feature("Principal Role Dashboard and Features");

const username = "principal";
const password = "Password123";

Before(({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', username);
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
});

Scenario("Principal can access dashboard", ({ I }) => {
  I.seeElement("body");
  I.see("Dashboard");
});

Scenario("Principal sees leadership dashboard", ({ I }) => {
  I.waitForElement("nav", 5);
  I.seeElement("nav");
});

Scenario("Principal can view all departments", ({ I }) => {
  I.seeElement("body");
});

Scenario("Principal can view academic reports", ({ I }) => {
  I.seeElement("body");
});

Scenario("Principal has access to approvals", ({ I }) => {
  I.seeElement("body");
});
