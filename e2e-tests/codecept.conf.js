const { setHeadlessWhen, setCommonPlugins } = require("@codeceptjs/configure");

setHeadlessWhen(process.env.HEADLESS);
setCommonPlugins();

exports.config = {
  tests: "./**/*_test.js",
  output: "./output",
  helpers: {
    Playwright: {
      url: "http://localhost:3000",
      show: true,
      browser: "chromium",
      smartWait: 5000,
      waitForNavigation: "networkidle",
    },
  },
  include: {
    I: "./steps_file.js",
  },
  plugins: {
    screenshotOnFail: {
      enabled: true,
    },
    htmlReporter: {
      enabled: true,
    },
  },
  name: "SMD E2E Tests",
};
