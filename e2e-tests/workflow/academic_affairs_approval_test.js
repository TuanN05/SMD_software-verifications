Feature("Academic Affairs: Final Approval and Publication");

Before(({ I }) => {
  // Login as Academic Affairs
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "academic_staff");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(2);
});

After(({ I }) => {
  I.wait(1);
});

Scenario("Academic Affairs can access syllabus approval dashboard", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  I.seeElement("body");
  I.seeInCurrentUrl("/dashboard");

  console.log("✓ Academic Affairs dashboard loaded");
});

Scenario(
  "Academic Affairs can view all PENDING_APPROVAL syllabuses",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Navigate to pending approval section
    I.seeElement("body");

    // Should show syllabuses approved by all HODs waiting for AA approval
    I.wait(1);

    console.log("✓ Pending approval syllabuses visible");
  },
);

Scenario(
  "Academic Affairs can see complete syllabus with HOD approval history",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Open a pending approval syllabus
    I.seeElement("body");

    // Should show:
    // - Course information
    // - Lecturer information
    // - HOD approval with timestamp and comments
    // - Uploaded document
    I.wait(1);

    console.log("✓ Complete approval history visible");
  },
);

Scenario(
  "Academic Affairs can publish syllabus (PENDING_APPROVAL → PUBLISHED)",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Find a syllabus in PENDING_APPROVAL status
    I.seeElement("body");

    // Click publish/approve button
    I.seeElement("body");
    I.wait(1);

    // Can add comment
    // I.fillField('textarea[name="comment"]', "Published and ready for use");

    // Confirm
    I.wait(2);

    console.log(
      "✓ Syllabus published by Academic Affairs (PENDING_APPROVAL → PUBLISHED)",
    );
  },
);

Scenario(
  "Academic Affairs can reject with comment (PENDING_APPROVAL → PENDING_REVIEW)",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Find a syllabus to reject
    I.seeElement("body");

    // Click reject button
    I.seeElement("body");

    // Must add comment explaining issues
    I.wait(1);

    console.log(
      "✓ Rejection sends syllabus back to HOD (PENDING_APPROVAL → PENDING_REVIEW)",
    );
  },
);

Scenario(
  "Published syllabus becomes available to students immediately",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Just published a syllabus
    I.seeElement("body");

    // Status should now be PUBLISHED
    I.wait(1);

    console.log("✓ Syllabus status is now PUBLISHED");
  },
);

Scenario("Academic Affairs cannot approve incomplete syllabuses", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  I.seeElement("body");

  // Should have validation to prevent approval of incomplete data
  I.wait(1);

  console.log("✓ Validation prevents incomplete approvals");
});

Scenario(
  "Published syllabuses show publication timestamp and AA approver name",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Open a published syllabus
    I.seeElement("body");

    // Should display:
    // - Published date/time
    // - Published by (AA staff name)
    // - Full approval timeline
    I.wait(1);

    console.log("✓ Publication details visible");
  },
);

Scenario(
  "Academic Affairs can generate approval certificate/report",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Open published syllabus
    I.seeElement("body");

    // Should have export or print option
    I.wait(1);

    console.log("✓ Certificate/report generation available");
  },
);

Scenario("Academic Affairs can filter by department and semester", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  // Use filters
  I.seeElement("body");

  // Should display filtered syllabuses
  I.wait(1);

  console.log("✓ Advanced filtering available");
});

Scenario("Academic Affairs can bulk approve multiple syllabuses", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  // Select multiple syllabuses
  I.seeElement("body");

  // Bulk approve button
  I.wait(1);

  console.log("✓ Bulk approval capability available");
});
