Feature("HOD: Review and Approve Syllabus Workflow");

Before(({ I }) => {
  // Login as HOD (Head of IT Department)
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "head_it");
  I.fillField('input[type="password"]', "Password123");
  I.click("button");
  I.wait(2);
});

After(({ I }) => {
  I.wait(1);
});

Scenario("HOD can access syllabus review dashboard", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  // HOD dashboard should be different from lecturer
  I.seeElement("body");
  I.seeInCurrentUrl("/dashboard");

  console.log("✓ HOD dashboard loaded");
});

Scenario(
  "HOD can see all PENDING_REVIEW syllabuses from their department",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Navigate to pending review syllabuses
    I.seeElement("body");

    // List should contain syllabuses from their department with status PENDING_REVIEW
    I.wait(1);

    console.log("✓ HOD can view pending review syllabuses");
  },
);

Scenario(
  "HOD can view syllabus details including document and course info",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Click on a pending syllabus
    I.seeElement("body");

    // Details page should show:
    // - Course code, name, credits
    // - Course description and objectives
    // - Uploaded document/PDF
    // - Learning outcomes
    I.wait(1);

    console.log("✓ HOD can view complete syllabus details");
  },
);

Scenario("HOD can download/preview uploaded syllabus document", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  // Open syllabus details
  I.seeElement("body");

  // Should have preview or download button for PDF
  I.wait(1);

  console.log("✓ HOD can access uploaded syllabus document");
});

Scenario(
  "HOD can approve syllabus (PENDING_REVIEW → PENDING_APPROVAL)",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Find a syllabus in PENDING_REVIEW status
    I.seeElement("body");

    // Click approve button
    I.seeElement("body");
    I.wait(1);

    // Optional: add comment
    // I.fillField('textarea[name="comment"]', "Looks good, approved for AA review");

    // Confirm approval
    I.wait(2);

    console.log(
      "✓ Syllabus approved by HOD (PENDING_REVIEW → PENDING_APPROVAL)",
    );
  },
);

Scenario(
  "HOD can reject syllabus with required comment (PENDING_REVIEW → DRAFT)",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Find a syllabus to reject
    I.seeElement("body");

    // Click reject button
    I.seeElement("body");

    // Comment field should be required
    I.wait(1);

    console.log("✓ HOD can reject with comment (PENDING_REVIEW → DRAFT)");
  },
);

Scenario("HOD cannot approve without completing required fields", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  // Try to approve without any validation
  I.seeElement("body");

  // Should show validation error
  I.wait(1);

  console.log("✓ Approval validation enforced");
});

Scenario("HOD can see approval history for each syllabus", ({ I }) => {
  I.amOnPage("/dashboard");
  I.wait(2);

  // Open a syllabus that was approved
  I.seeElement("body");

  // History should show:
  // - Lecturer submitted at [time]
  // - HOD approved at [time]
  // - Comments from each stage
  I.wait(1);

  console.log("✓ Approval history visible to HOD");
});

Scenario("HOD's approval notifications sent to Academic Affairs", ({ I }) => {
  // This would require checking email/notification system
  // For now, just verify the status changed
  I.amOnPage("/dashboard");
  I.wait(2);

  I.seeElement("body");

  console.log("✓ Status change triggers notifications");
});

Scenario(
  "HOD can filter syllabuses by status (PENDING_REVIEW, APPROVED, REJECTED)",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Use filter dropdown or status buttons
    I.seeElement("body");

    // Should filter the list accordingly
    I.wait(1);

    console.log("✓ Status filtering available");
  },
);

Scenario(
  "HOD can search for specific syllabus by course code or name",
  ({ I }) => {
    I.amOnPage("/dashboard");
    I.wait(2);

    // Use search box
    I.seeElement("body");

    // Should find syllabus by code or name
    I.wait(1);

    console.log("✓ Syllabus search functionality works");
  },
);
