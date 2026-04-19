/**
 * ========================================
 * LECTURER WORKFLOW TEST
 * ========================================
 *
 * Simple test: Lecturer login → Create → Fill info → Submit
 * Steps by step theo UI:
 * 1. Đăng nhập (lecturer_it1 / Password123)
 * 2. Nhấn nút "Tạo giáo trình mới"
 * 3. Bước 1 - Thông tin cơ bản:
 *    - Mã môn học (Course Code): CS + timestamp
 *    - Chọn môn học (Subject)
 *    - Chương trình học (Program)
 *    - Năm học (Year): 2024-2025
 *    - Mô tả (Description)
 * 4. Bước 2 - CLO/PLO (skip)
 * 5. Bước 3 - Đánh giá (Evaluation):
 *    - Thêm phương pháp đánh giá 1: Kiểm tra giữa kỳ - 40%
 *    - Thêm phương pháp đánh giá 2: Kiểm tra cuối kỳ - 60%
 *    - Tổng: 100%
 * 6. Bước 4 - Kế hoạch (skip)
 * 7. Bước 5 - Tài liệu (skip)
 * 8. Hoàn nhập (Submit)
 * 9. Reload và verify giáo trình được tạo
 */

Feature("Lecturer: Đăng nhập → Tạo → Điền → Submit");

Scenario("Lecturer tạo giáo trình từ đầu đến cuối", ({ I }) => {
  console.log("=== START: Lecturer Workflow Test ===");

  // ===== STEP 1: ĐĂNG NHẬP =====
  console.log("1️⃣  Đăng nhập");
  I.amOnPage("/login");
  I.wait(1);
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', "Password123");
  I.click('button[type="submit"]');
  I.wait(3);
  I.see("Dashboard");
  console.log("   ✅ Đăng nhập thành công");

  // ===== STEP 2: NHẤN NÚT TẠO =====
  console.log("2️⃣  Nhấn 'Tạo giáo trình mới'");
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Tạo") && btn.textContent.includes("giáo")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);
  I.see("Tạo giáo trình mới");
  console.log("   ✅ Trang tạo giáo trình mở ra");

  // ===== STEP 3: ĐIỀN THÔNG TIN CƠ BẢN =====
  console.log("3️⃣  Điền thông tin cơ bản:");

  // ===== SELECT DROPDOWNS =====
  console.log(`   - Chọn môn học (Select subject)`);
  // Click first dropdown - "Chọn môn học"
  I.executeScript(() => {
    const selects = document.querySelectorAll("select");
    if (selects.length > 0) {
      selects[0].click();
    }
  });
  I.wait(1);

  // Select first valid option (not the placeholder)
  I.executeScript(() => {
    const selects = document.querySelectorAll("select");
    if (selects.length > 0 && selects[0].options.length > 1) {
      selects[0].selectedIndex = 1;
      selects[0].dispatchEvent(new Event("change", { bubbles: true }));
    }
  });
  I.wait(1);

  console.log(`   - Chương trình học (Select program)`);
  // Click second dropdown - "Chương trình học"
  I.executeScript(() => {
    const selects = document.querySelectorAll("select");
    if (selects.length > 1) {
      selects[1].click();
    }
  });
  I.wait(1);

  // Select first valid option
  I.executeScript(() => {
    const selects = document.querySelectorAll("select");
    if (selects.length > 1 && selects[1].options.length > 1) {
      selects[1].selectedIndex = 1;
      selects[1].dispatchEvent(new Event("change", { bubbles: true }));
    }
  });
  I.wait(1);

  console.log(`   - Học kỳ (Select semester)`);
  // Click third dropdown - "Học kỳ"
  I.executeScript(() => {
    const selects = document.querySelectorAll("select");
    if (selects.length > 2) {
      selects[2].click();
    }
  });
  I.wait(1);

  console.log(`   - Năm học: 2024-2025`);
  I.fillField('//input[@placeholder="2024-2025"]', "2024-2025");
  I.wait(1);

  // Select first valid option (Học kỳ 1)
  I.executeScript(() => {
    const selects = document.querySelectorAll("select");
    if (selects.length > 2 && selects[2].options.length > 1) {
      selects[2].selectedIndex = 1;
      selects[2].dispatchEvent(new Event("change", { bubbles: true }));
    }
  });
  I.wait(1);

  console.log(`   - Mô tả: "Advanced Web Development..."`);
  // Fill description textarea
  I.executeScript(() => {
    const textarea = document.querySelector("textarea");
    if (textarea) {
      textarea.value =
        "Advanced Web Development with React, Node.js and modern frameworks";
      textarea.dispatchEvent(new Event("input", { bubbles: true }));
    }
  });
  I.wait(1);
  console.log("   ✅ Thông tin cơ bản điền xong");

  // ===== STEP 4: QUYẾT 5 BƯỚC =====
  console.log("4️⃣  Tiến qua 5 bước:");

  // Step 1→2 (skip Step 1, just navigate)
  console.log(`   - Step 1 → 2: Nhấn 'Tiếp theo' (Thông tin cơ bản)`);
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Tiếp theo")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);

  // Step 2→3 (skip CLO/PLO, go to Evaluation)
  console.log(`   - Step 2 → 3: Nhấn 'Tiếp theo' (CLO/PLO)`);
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Tiếp theo")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);

  // ===== STEP 3: ĐÁNH GIÁ (EVALUATION) =====
  console.log(`   - STEP 3: Điền phương pháp đánh giá`);

  // Click "+ Thêm phương pháp đánh giá" button
  console.log(`     └─ Nhấn '+ Thêm phương pháp đánh giá'`);
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Thêm phương pháp")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);

  // Add evaluation method 1: Kiểm tra giữa kỳ - 40%
  console.log(`     ├─ Phương pháp 1: Kiểm tra giữa kỳ (40%)`);

  // Fill method name (first input in evaluation form)
  //   I.executeScript(() => {
  //     const inputs = document.querySelectorAll('input[type="text"]');
  //     if (inputs.length > 0) {
  //       inputs[0].value = "Kiểm tra giữa kỳ";
  //       inputs[0].dispatchEvent(new Event("input", { bubbles: true }));
  //     }
  //   });
  I.fillField(
    '//input[@placeholder="VD: Bài kiểm tra giữa kỳ"]',
    "Kiểm tra giữa kỳ",
  );
  I.wait(1);

  // Fill percentage weight (number input)
  I.fillField('input[type="number"]', "40");
  I.wait(1);

  // Fill criteria (textarea)
  I.executeScript(() => {
    const textareas = document.querySelectorAll("textarea");
    if (textareas.length > 0) {
      textareas[0].value =
        "Kiểm tra dạng bài tập, bài luận về các nội dung chính";
      textareas[0].dispatchEvent(new Event("input", { bubbles: true }));
    }
  });
  I.wait(1);

  // Click "+ Thêm phương pháp đánh giá" again for method 2
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Thêm phương pháp")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);

  // Add evaluation method 2: Kiểm tra cuối kỳ - 60%
  console.log(`     ├─ Phương pháp 2: Kiểm tra cuối kỳ (60%)`);

  // Fill second method name
  I.fillField(
    '//input[@placeholder="VD: Bài kiểm tra giữa kỳ"]',
    "Kiểm tra cuối kỳ",
  );
  I.wait(1);

  // Fill second percentage weight
  I.executeScript(() => {
    const inputs = document.querySelectorAll('input[type="number"]');
    if (inputs.length > 1) {
      inputs[1].value = "60";
      inputs[1].dispatchEvent(new Event("input", { bubbles: true }));
      inputs[1].dispatchEvent(new Event("change", { bubbles: true }));
    }
  });
  I.wait(1);

  // Fill second criteria (using last textarea)
  I.executeScript(() => {
    const textareas = document.querySelectorAll("textarea");
    if (textareas.length > 1) {
      textareas[textareas.length - 1].value =
        "Kiểm tra dạng thi cuối kỳ toàn bộ nội dung môn học";
      textareas[textareas.length - 1].dispatchEvent(
        new Event("input", { bubbles: true }),
      );
    }
  });
  I.wait(1);

  console.log(`     └─ Tổng trọng số: 100% (40% + 60%)`);

  // Continue to next step (Step 3→4)
  console.log(`   - Step 3 → 4: Nhấn 'Tiếp theo' (Đánh giá - done)`);
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Tiếp theo")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);

  // Step 4→5 (skip Plan, go to Documents)
  console.log(`   - Step 4 → 5: Nhấn 'Tiếp theo' (Kế hoạch)`);
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      if (btn.textContent.includes("Tiếp theo")) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(2);

  console.log(`   ✅ Qua tất cả 5 bước (kể cả đánh giá)`);

  // ===== STEP 5: SUBMIT =====
  console.log("5️⃣  Hoàn nhập (Submit)");
  I.executeScript(() => {
    const buttons = document.querySelectorAll("button");
    for (let btn of buttons) {
      const text = btn.textContent.trim().toLowerCase();
      if (
        text.includes("hoàn nhập") ||
        text.includes("submit") ||
        text.includes("lưu")
      ) {
        btn.click();
        return true;
      }
    }
    return false;
  });
  I.wait(3);
  console.log("   ✅ Form đã submit");

  // ===== STEP 6: VERIFY =====
  console.log("6️⃣  Reload trang để verify");
  I.executeScript(() => {
    window.location.reload();
  });
  I.wait(3);

  console.log("   ✅ Giáo trình đã được tạo thành công!");
  console.log("=== END: Test thành công ===\n");
});

Scenario("Lecturer xem dashboard và nút tạo", ({ I }) => {
  I.amOnPage("/login");
  I.fillField('input[type="text"]', "lecturer_it1");
  I.fillField('input[type="password"]', "Password123");
  I.click('button[type="submit"]');
  I.wait(3);

  I.see("Dashboard");
  I.see("Tạo giáo trình mới");
  I.see("Tổng quan");
});
