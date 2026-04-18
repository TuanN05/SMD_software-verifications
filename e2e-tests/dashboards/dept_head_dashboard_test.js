Feature("Department Head Dashboard and Features");

// IT Head Login and Dashboard
Scenario("IT Head can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_it");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("IT Head sees department dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_it");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.waitForElement("nav", 5);
});

Scenario("IT Head can manage IT department courses", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_it");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Business Head Login and Dashboard
Scenario("Business Head can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_biz");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Business Head sees department dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_biz");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.waitForElement("nav", 5);
});

Scenario("Business Head can manage Business department courses", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_biz");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});
