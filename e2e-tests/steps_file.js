module.exports = function () {
  return actor({
    /**
     * Login as a user
     */
    loginAsUser(email, password) {
      this.amOnPage("/login");
      this.fillField("Email", email);
      this.fillField("Password", password);
      this.click("Sign In");
      this.waitForNavigation();
      this.waitForText("Dashboard", 10);
    },

    /**
     * Logout
     */
    logout() {
      this.click("Profile");
      this.click("Logout");
      this.waitForText("Login", 10);
    },

    /**
     * Navigate to courses
     */
    navigateToCourses() {
      this.amOnPage("/courses");
      this.waitForText("Course List", 10);
    },

    // ========== Workflow Helper Functions ==========

    /**
     * Create a new syllabus (DRAFT status)
     */
    createNewSyllabus(courseCode, courseName, credits, description) {
      this.click("Create Syllabus");
      this.fillField('input[name="courseCode"]', courseCode);
      this.fillField('input[name="courseName"]', courseName);
      this.fillField('input[name="credits"]', credits);
      this.fillField('textarea[name="description"]', description);
      this.click('button:contains("Save as Draft")');
      this.wait(2);
    },

    /**
     * Submit syllabus for review (DRAFT → PENDING_REVIEW)
     */
    submitSyllabusForReview(syllabusCode, comment = "") {
      this.click(`[data-code="${syllabusCode}"]`);
      this.click('button:contains("Submit for Review")');
      if (comment) {
        this.fillField('textarea[name="comment"]', comment);
      }
      this.click('button:contains("Confirm Submit")');
      this.wait(2);
    },

    /**
     * Approve syllabus as HOD (PENDING_REVIEW → PENDING_APPROVAL)
     */
    approveSyllabusAsHOD(syllabusCode, comment = "") {
      this.click(`[data-code="${syllabusCode}"]`);
      this.click('button:contains("Approve")');
      if (comment) {
        this.fillField('textarea[name="comment"]', comment);
      }
      this.click('button:contains("Confirm Approval")');
      this.wait(2);
    },

    /**
     * Reject syllabus as HOD (PENDING_REVIEW → DRAFT)
     */
    rejectSyllabusAsHOD(syllabusCode, comment) {
      this.click(`[data-code="${syllabusCode}"]`);
      this.click('button:contains("Reject")');
      this.fillField('textarea[name="comment"]', comment); // Required
      this.click('button:contains("Confirm Rejection")');
      this.wait(2);
    },

    /**
     * Publish syllabus as Academic Affairs (PENDING_APPROVAL → PUBLISHED)
     */
    publishSyllabusAsAcademicAffairs(syllabusCode, comment = "") {
      this.click(`[data-code="${syllabusCode}"]`);
      this.click('button:contains("Publish")');
      if (comment) {
        this.fillField('textarea[name="comment"]', comment);
      }
      this.click('button:contains("Confirm Publication")');
      this.wait(2);
    },

    /**
     * Delete syllabus as Admin
     */
    deleteSyllabusAsAdmin(syllabusCode) {
      this.click(`[data-code="${syllabusCode}"]`);
      this.click('button:contains("Delete")');
      this.click('button:contains("Confirm Delete")');
      this.wait(2);
    },

    /**
     * Verify syllabus status
     */
    verifySyllabusStatus(syllabusCode, expectedStatus) {
      this.see(`${syllabusCode}`);
      this.see(expectedStatus);
    },

    /**
     * Verify syllabus visible in list
     */
    verifySyllabusInList(syllabusCode) {
      this.see(syllabusCode);
    },

    /**
     * Verify syllabus deleted/not visible
     */
    verifySyllabusNotVisible(syllabusCode) {
      this.dontSee(syllabusCode);
    },

    /**
     * Upload syllabus document
     */
    uploadSyllabusDocument(syllabusCode, filePath) {
      this.click(`[data-code="${syllabusCode}"]`);
      this.attachFile('input[type="file"]', filePath);
      this.click('button:contains("Upload")');
      this.wait(2);
    },

    /**
     * Download syllabus PDF
     */
    downloadSyllabusPDF(syllabusCode) {
      this.click(`[data-code="${syllabusCode}"]`);
      this.click('button:contains("Download PDF")');
      this.wait(1);
    },

    /**
     * View syllabus details/preview
     */
    viewSyllabusDetails(syllabusCode) {
      this.click(`[data-code="${syllabusCode}"]`);
      this.wait(1);
    },
  });
};
