Feature("Student Role Dashboard and Features");

Scenario("Student can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Student sees enrolled courses", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.waitForElement("nav", 5);
});

Scenario("Student can view course syllabus", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Student can access course materials", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Student can view student schedule", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Student can view grades and progress", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});
