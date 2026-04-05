# Hướng Dẫn Sử Dụng Postman Collection - Role Management Tests

## 📥 Bước 1: Import Collection vào Postman

1. Mở Postman
2. Chọn **File** → **Import** (hoặc Ctrl+O)
3. Chọn file: `SMD_Role_Management_Tests.postman_collection.json`
4. Postman sẽ tự động import collection với các folders và requests

## ⚙️ Bước 2: Cấu Hình Environment

### Tạo Environment mới:

1. Chọn **Environments** (góc trái Postman)
2. Chọn **Create New Environment**
3. Đặt tên: `SMD Development`
4. Thêm biến:

| Variable      | Initial Value         | Current Value           |
| ------------- | --------------------- | ----------------------- |
| baseUrl       | http://localhost:8080 | http://localhost:8080   |
| token         | (để trống)            | (tự động sau khi login) |
| lecturerToken | (để trống)            | (tự động sau khi login) |
| adminId       | (để trống)            | (tự động sau khi login) |
| lecturerId    | (để trống)            | (tự động sau khi login) |

5. Chọn **Save**

### Chọn Environment:

- Ở phía trên cùng Postman, khoảng **No Environment** → chọn **SMD Development**

## 🔐 Bước 3: Đăng Nhập (Authentication)

**Chạy 2 request này trước:**

1. **0. Authentication & Setup → Login as ADMIN**
   - Sẽ lấy token ADMIN và lưu vào `{{token}}`
   - Sẽ lấy ID admin và lưu vào `{{adminId}}`

2. **0. Authentication & Setup → Login as LECTURER**
   - Sẽ lấy token LECTURER và lưu vào `{{lecturerToken}}`
   - Sẽ lấy ID lecturer và lưu vào `{{lecturerId}}`

Sau khi chạy xong 2 request này, token sẽ được lưu tự động vào environment.

## 🧪 Bước 4: Chạy Test Cases

### Cách chạy từng test:

1. Click vào request cần test
2. Chọn **Send**
3. Xem kết quả ở phía dưới - sẽ hiển thị:
   - Status code (200, 400, 403, 404, 409...)
   - Response body
   - Tests passed/failed

### Cách chạy toàn bộ collection:

1. Click chuột phải vào folder (ví dụ: **Function 12: Create a new role**)
2. Chọn **Run collection**
3. Chọn environment: **SMD Development**
4. Chọn **Run {số request} requests**

## 📋 Chi Tiết Các Test Case

### **Function 12: Create a new role**

| Test     | Expected Result               | Status |
| -------- | ----------------------------- | ------ |
| ITC_12.1 | 200 + role created            | ✅     |
| ITC_12.2 | 409 Conflict (duplicate)      | ✅     |
| ITC_12.3 | 403 Forbidden (no permission) | ✅     |
| BVA_12.1 | 400 (null roleName)           | ✅     |
| BVA_12.2 | 200 (1 character)             | ✅     |
| BVA_12.3 | 400 (empty string)            | ✅     |
| BVA_12.4 | 200 (255 characters)          | ✅     |
| BVA_12.5 | 400 (256 characters)          | ✅     |
| BVA_12.6 | 200/400 (special chars)       | ✅     |
| BVA_12.7 | 200 (whitespace trim)         | ✅     |

### **Function 13: Initialize default roles**

| Test     | Expected Result         | Status |
| -------- | ----------------------- | ------ |
| ITC_13.1 | 200 + success message   | ✅     |
| ITC_13.2 | Get all 6 default roles | ✅     |
| ITC_13.3 | 200 (idempotency)       | ✅     |

### **Function 14: Assign role to user**

| Test     | Expected Result               | Status |
| -------- | ----------------------------- | ------ |
| ITC_14.1 | 200 + role assigned           | ✅     |
| ITC_14.2 | 200 + multiple roles          | ✅     |
| ITC_14.3 | 403 Forbidden (no permission) | ✅     |
| BVA_14.1 | 200 (user has 0 roles)        | ✅     |
| BVA_14.3 | 409 (duplicate role)          | ✅     |
| BVA_14.5 | 400/404 (invalid userId)      | ✅     |
| BVA_14.7 | 404 (role not exists)         | ✅     |

### **Function 15: Get user's roles**

| Test     | Expected Result      | Status |
| -------- | -------------------- | ------ |
| ITC_15.1 | 200 + single role    | ✅     |
| ITC_15.2 | 200 + multiple roles | ✅     |
| ITC_15.3 | 200 + user details   | ✅     |
| BVA_15.1 | 200 + empty roles    | ✅     |
| BVA_15.2 | 200 + 1 role         | ✅     |
| BVA_15.3 | 200 + 3 roles        | ✅     |

## 🔍 Cách Xem Chi Tiết Test Results

### Bước 1: Chạy request

- Chọn request
- Chọn **Send**

### Bước 2: Xem Test Results

- Kích vào tab **Tests** (bên cạnh Response)
- Sẽ thấy danh sách tests:
  - ✅ Test passed (xanh)
  - ❌ Test failed (đỏ)

### Bước 3: Xem Response Details

- Tab **Body** - xem JSON response
- Tab **Headers** - xem HTTP headers
- Tab **Status Code** - xem 200, 400, 403...

## 💡 Mẹo & Lưu Ý

### Lưu ý quan trọng:

1. **Phải login trước** - Chạy 2 request đăng nhập ở Function 0 trước
2. **Token hết hạn** - Nếu test bị 401 Unauthorized, login lại
3. **Database state** - Một số test (duplicate) phụ thuộc vào state
4. **User IDs** - Theo cấu hình DatabaseSeeder:
   - admin: ID = 1
   - lecturer_it1: ID = 5
   - student: ID = 7

### Tạo Postman Workspace riêng:

1. Chọn **Workspaces** (góc trên)
2. Chọn **Create New** → **Workspace**
3. Đặt tên: "SMD Testing"
4. Import collection vào workspace

### Export test results:

1. Chạy collection đầy đủ
2. Sau khi xong, chọn **⋯** → **Export Results**
3. Chọn format CSV hoặc JSON
4. Lưu với tên: `test_results_[ngay].json`

## 🚀 Chạy Tất Cả Tests Cùng Lúc

### Cách tốt nhất để chạy toàn bộ:

1. Click vào **SMD Role Management Tests** (collection root)
2. Chọn **Run collection** (icon ▶️)
3. Postman sẽ mở **Collection Runner**
4. Cấu hình:
   - **Environment**: SMD Development
   - **Iterations**: 1 (chạy 1 lần)
   - **Delay**: 100ms (giữa các request)
5. Chọn **Run SMD Role Management Tests**

### Kết quả:

- Postman sẽ hiển thị summary:
  - Tổng requests
  - ✅ Passed
  - ❌ Failed
  - ⏭️ Skipped

## 📊 Kiểm Tra Chi Tiết Từng Test

**Ví dụ: Kiểm tra ITC_12.1 - Valid role name**

```
✅ Status code is 200
✅ Response contains role
✅ Role has roleId
```

**Nếu test failed:**

1. Xem **Response** body để tìm lỗi
2. Kiểm tra **status code** (200, 400, 403...)
3. Xem error message trong response
4. Kiểm tra test script trong tab **Tests**

## 🐛 Troubleshooting

| Vấn Đề                    | Giải Pháp                                |
| ------------------------- | ---------------------------------------- |
| 401 Unauthorized          | Chạy lại login request                   |
| 404 Not Found             | Kiểm tra baseUrl + endpoint              |
| 500 Internal Server Error | Backend lỗi, kiểm tra server logs        |
| Test assertion failed     | Kiểm tra response data + expected result |
| Variables không được set  | Kiểm tra lại login requests              |

## 📝 Ghi Chú

- Collection này có **40+ test cases** cho Role Management
- Tất cả tests đã tích hợp assertions để tự động kiểm tra
- Tokens sẽ tự động được lưu sau login
- Collection dùng được cho automation via CLI (Newman)

---

**Tác giả**: SMD Team  
**Ngày cập nhật**: April 5, 2026  
**Version**: 1.0
