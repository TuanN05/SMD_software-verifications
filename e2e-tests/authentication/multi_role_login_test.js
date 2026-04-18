Feature("Multi-Role Login and Dashboard Access");

const users = [
  { username: "admin", role: "Admin" },
  { username: "academic_staff", role: "Academic Staff" },
  { username: "principal", role: "Principal" },
  { username: "head_it", role: "IT Head" },
  { username: "head_biz", role: "Business Head" },
  { username: "lecturer_it1", role: "Lecturer IT 1" },
  { username: "lecturer_it2", role: "Lecturer IT 2" },
  { username: "lecturer_biz", role: "Lecturer Business" },
  { username: "lecturer_design", role: "Lecturer Design" },
  { username: "student", role: "Student" },
];

const password = "Password123";

// Admin Login and Dashboard
Scenario("Admin can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "admin");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Academic Staff Login and Dashboard
Scenario("Academic Staff can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "academic_staff");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Principal Login and Dashboard
Scenario("Principal can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "principal");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// IT Head Login and Dashboard
Scenario("IT Head can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_it");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Business Head Login and Dashboard
Scenario("Business Head can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_biz");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Lecturer IT 1 Login and Dashboard
Scenario("Lecturer IT1 can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Lecturer IT 2 Login and Dashboard
Scenario("Lecturer IT2 can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it2");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Lecturer Business Login and Dashboard
Scenario("Lecturer Business can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_biz");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Lecturer Design Login and Dashboard
Scenario("Lecturer Design can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_design");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});

// Student Login and Dashboard
Scenario("Student can login and see dashboard", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "student");
  I.fillField('input[type="password"]', password);
  I.click("button");
  I.wait(3);
  I.seeElement("body");
});
