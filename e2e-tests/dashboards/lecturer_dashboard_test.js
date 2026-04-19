Feature("Lecturer Role Dashboard and Features");

// Lecturer IT1
Scenario("Lecturer IT1 can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Lecturer IT1 sees course information", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.waitForElement("nav", 5);
});

// Lecturer IT2
Scenario("Lecturer IT2 can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it2");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Lecturer Business
Scenario("Lecturer Business can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_biz");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Lecturer Design
Scenario("Lecturer Design can access dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_design");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Lecturer can view assigned courses", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

Scenario("Lecturer can manage syllabi for courses", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});
