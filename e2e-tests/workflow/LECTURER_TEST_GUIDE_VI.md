# ✅ LECTURER WORKFLOW TEST - HƯỚNG DẪN SỬ DỤNG

## 📋 MỤC ĐÍCH

Test này kiểm tra toàn bộ quy trình của **Giảng viên**:

1. ✅ Đăng nhập vào hệ thống
2. ✅ Nhấn nút "Tạo giáo trình mới"
3. ✅ Điền thông tin cơ bản (Mã môn, Mô tả)
4. ✅ Tiến qua 5 bước tạo giáo trình:
   - Bước 1: Thông tin cơ bản
   - Bước 2: CLO / PLO
   - Bước 3: Đánh giá
   - Bước 4: Kế hoạch
   - Bước 5: Tài liệu
5. ✅ Hoàn nhập (Submit) giáo trình
6. ✅ Xác minh giáo trình được tạo thành công

---

## 🚀 CHẠY TEST

### Chạy test này:

```bash
npm run test:e2e -- workflow/lecturer_final_test.js
```

### Chạy tất cả test:

```bash
npm run test:e2e
```

### Chạy với chi tiết từng bước:

```bash
npx codeceptjs run workflow/lecturer_final_test.js --steps
```

---

## 📊 KẾT QUẢ

```
✅ Lecturer tạo giáo trình từ đầu đến cuối ...................... PASS
✅ Lecturer xem dashboard và nút tạo ............................ PASS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ 2 passed in 32 seconds
```

---

## 🔧 KỸ THUẬT CÓ NHI

### Vấn đề gặp phải và giải pháp:

| Vấn đề                       | Nguyên nhân            | Giải pháp                                        |
| ---------------------------- | ---------------------- | ------------------------------------------------ |
| Ký tự Tiếng Việt bị encoding | CSS/XPath selector lỗi | Dùng `executeScript` + `.textContent.includes()` |
| Input field không tìm được   | Selector sai           | Dùng `document.querySelector()` trong JS         |
| Button không click được      | Selector không khớp    | Dùng `.dispatchEvent()` để trigger events        |
| Course code không hiện       | Trang chưa reload      | Dùng `window.location.reload()`                  |

### Phương pháp chính:

```javascript
// Click button bằng text content (tránh encoding issue)
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

// Fill input field
I.executeScript((value) => {
  const inputs = document.querySelectorAll('input[type="text"]');
  if (inputs.length > 0) {
    inputs[0].value = value;
    inputs[0].dispatchEvent(new Event("input", { bubbles: true }));
  }
}, courseCode);
```

---

## 📝 TEST FILES

### File chính:

- **`lecturer_final_test.js`** - ✅ Test hoàn chỉnh (RECOMMENDED)
  - 2 scenarios
  - Chi tiết từng bước
  - Có log output

### Các file liên quan:

- `lecturer_create_full_test.js` - Test nền tảng
- `lecturer_simple_login_test.js` - Test login đơn giản
- `debug_find_fields_test.js` - Test debug (tìm form fields)

---

## 👤 THÔNG TIN ĐĂNG NHẬP

```
Username: lecturer_it1
Password: Password123
Role: Lecturer (Giảng viên)
```

---

## 📈 TIẾP THEO

### Mở rộng thêm:

- [ ] Test HOD review giáo trình
- [ ] Test Academic Affairs approval
- [ ] Test student viewing published syllabus
- [ ] Test course relationships
- [ ] Test CLO/PLO mapping

### Tối ưu hiệu năng:

- [ ] Xóa test redundant (hiện có 145 tests, có thể giảm còn 80 core tests)
- [ ] Giảm wait time nếu có thể
- [ ] Parallel test execution

---

## ⚙️ CONFIGURATION

File config: `codecept.conf.js`

```javascript
// Test discovery pattern
include: ['./**/*_test.js']

// Playwright settings
helpers: {
  Playwright: {
    url: 'http://localhost:3000',
    browser: 'chromium',
    show: false
  }
}
```

---

## 🐛 TROUBLESHOOTING

### Test timeout?

```bash
# Tăng timeout trong config
timeout: 300000  // 5 minutes
```

### Selector không tìm được?

```bash
# Chạy với verbose mode để debug
npx codeceptjs run workflow/lecturer_final_test.js --verbose
```

### Xem screenshot từ test fail?

```
output/Lecturer_tao_giao_trinh_tu_dau_den_cuoi.failed.png
output/Lecturer_xem_dashboard_va_nut_tao.failed.png
```

---

## 📞 HỖ TRỢ

Nếu gặp lỗi, kiểm tra:

1. Frontend server chạy `http://localhost:3000`?
2. Có browser Chromium được cài đặt?
3. Credentials đúng không?
4. Network connection ổn định?

---

**Last Updated**: April 2026  
**Status**: ✅ All Tests Passing (2/2)  
**Pass Rate**: 100%
