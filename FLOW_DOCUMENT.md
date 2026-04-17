# Tài Liệu Luồng Chạy HRM Application

## Tổng Quan

HRM (Human Resource Management) là ứng dụng quản lý nhân sự Android với các tính năng:
- Đăng nhập / Đăng xuất
- Quản lý phòng ban & nhân viên
- Quản lý chấm công
- Quản lý lương
- Quản lý khen thưởng & kỷ luật
- Quản lý đào tạo
- Cài đặt hệ thống

---

## Luồng Chính

```
Splash Screen
    ↓
LoginActivity (SharedPreferences: isLogin, isBiometric)
    ├─ Đăng nhập thường
    ├─ Đăng nhập vân tay (Biometric)
    └─ Nhớ mật khẩu
    ↓
HomeActivity (Dashboard chính)
    ├─ Dashboard Stats
    └─ Navigation Menu
        ├─ Phòng Ban
        ├─ Nhân Viên
        ├─ Chấm Công
        ├─ Lương
        ├─ Khen Thưởng
        ├─ Kỷ Luật
        ├─ Đào Tạo
        └─ Cài Đặt
```

---

## 1. LoginActivity (Đăng Nhập)

Vị trí: com.example.hrm.activities.LoginActivity

### Luồng chính:
```
onCreate()
    ↓
Kiểm tra SharedPreferences "SESSION"
    ├─ isLogin = true → chuyển HomeActivity
    └─ isLogin = false → hiện form login
    ↓
Người dùng bấm "Đăng Nhập"
    ↓
handleLogin()
    ├─ Kiểm tra username/password rỗng
    ├─ Query AccountDAO
    └─ Match tài khoản?
        ├─ ✓ Yes → Lưu prefs, chuyển Home
        └─ ✗ No → Toast lỗi
```

### Chi tiết các hàm:

**onCreate()**
- Được gọi khi activity khởi tạo
- Lấy SharedPreferences với key "SESSION"
- Kiểm tra boolean isLogin
- Kiểm tra isBiometricEnabled để tự động login bằng vân tay
- Nếu chưa login, hiển thị layout login
- Gán click listener cho button login và icon fingerprint
- Nếu đã login, chuyển trực tiếp sang HomeActivity

**initViews()**
- Liên kết các UI element từ layout
- edtUsername: EditText nhập tên đăng nhập
- edtPassword: EditText nhập mật khẩu
- btnLogin: Button bấm để đăng nhập
- chkRemember: CheckBox ghi nhớ tài khoản
- imgFingerprint: ImageView icon vân tay
- accountDAO: DAO để query database tài khoản

**handleLogin()**
- Lấy username và password từ EditText
- Kiểm tra xem có rỗng không (dùng TextUtils.isEmpty)
- Gọi accountDAO.checkLogin(username, password)
- Nếu kết quả không null (tài khoản hợp lệ):
  - Lấy adminname từ kết quả
  - Save vào SharedPreferences: username, adminname, isLogin=true
  - Nếu checkbox "Remember" được check, lưu username
  - Hiển thị Toast chào mừng
  - Chuyển sang HomeActivity bằng startActivity()
  - Gọi finish() để không quay lại LoginActivity
- Nếu null (tài khoản sai): hiển thị Toast "Sai tài khoản hoặc mật khẩu"

**checkBiometric()**
- Kiểm tra xem thiết bị có hỗ trợ biometric không
- Kiểm tra xem user đã bật biometric trong settings không
- Nếu cả hai điều kiện đúng, gọi showBiometricPrompt()

**showBiometricPrompt()**
- Tạo BiometricPrompt với callback để xử lý kết quả
- onAuthenticationSucceeded(): Nếu xác thực vân tay thành công
  - Chuyển sang HomeActivity
  - Gọi finish()
- onAuthenticationError(): Nếu có lỗi
  - Hiển thị Toast với thông báo lỗi
- onAuthenticationFailed(): Nếu xác thực thất bại
  - Hiển thị Toast "Xác thực thất bại"
- Tạo PromptInfo với title, subtitle, negative button text
- Gọi authenticate() để hiển thị dialog xác thực

### Điểm cần lưu ý:

- SharedPreferences Key: "SESSION" (phải match với các Activity khác)
- KHÔNG bao giờ lưu password vào prefs, chỉ lưu username
- Biometric check phải có cách fallback (nhập mật khẩu thủ công)
- Dùng TextUtils.isEmpty() thay vì .equals("") để check rỗng
- finish() phải gọi để không quay lại login khi bấm back

### Lỗi thường gặp:

SAI: Lưu password vào prefs
prefs.edit().putString("password", password).apply();

ĐÚNG: Chỉ lưu username
prefs.edit().putString("username", username).apply();

SAI: Check empty sai cách
if (username.equals("")) { }

ĐÚNG: Dùng TextUtils
if (TextUtils.isEmpty(username)) { }

SAI: Quên gọi finish()
startActivity(new Intent(this, HomeActivity.class));
// Khi bấm back sẽ quay lại LoginActivity

ĐÚNG: Gọi finish() sau startActivity
startActivity(new Intent(this, HomeActivity.class));
finish();

### Element Structure:

LoginActivity
  ├─ edtUsername (EditText) - Nhập username
  ├─ edtPassword (EditText) - Nhập password (PasswordInput)
  ├─ btnLogin (Button) - Bấm đăng nhập
  ├─ chkRemember (CheckBox) - Ghi nhớ tài khoản
  ├─ imgFingerprint (ImageView) - Icon vân tay
  └─ accountDAO (DAO) - Query database

---

## 2. HomeActivity (Trang Chủ)

Vị trí: com.example.hrm.activities.HomeActivity

### Luồng chính:
```
onCreate()
    ↓
initViews() → Liên kết UI elements
    ↓
setupToolbarAndDrawer() → Navigation Drawer setup
    ↓
setupAccountFooter() → Hiển thị tài khoản đã login
    ↓
loadDashboardData() → Load stats từ Database
    │  ├─ Tổng phòng ban
    │  ├─ Tổng nhân viên
    │  ├─ Nhân viên đang làm
    │  ├─ Chưa chấm công
    │  └─ Đang học
    ↓
setupClickEvents() → Gán click listener
    │  ├─ Card department
    │  ├─ Card employees
    │  └─ Menu items
    ↓
User click menu
    ↓
onNavigationItemSelected()
    └─ startActivity() đến màn hình tương ứng
```

### Chi tiết các hàm:

**onCreate()**
- Khởi tạo HomeActivity sau khi login thành công
- Gọi initViews() để liên kết UI
- Gọi setupToolbarAndDrawer() để setup navigation drawer
- Gọi setupAccountFooter() để hiển thị info user
- Gọi loadDashboardData() để load thống kê
- Gọi setupClickEvents() để gán click listener
- Setup back button behavior: nếu drawer mở → đóng drawer, nếu drawer đóng → exit app

**initViews()**
- Liên kết DrawerLayout từ layout
- Liên kết NavigationView (menu drawer)
- Liên kết Toolbar
- Liên kết các TextView hiển thị stats (tổng phòng ban, nhân viên, etc)
- Liên kết các Card (clickable container)
- Liên kết các LinearLayout menu items
- Lưu ý: 8 menu items cần liên kết hết

**setupToolbarAndDrawer()**
- Gọi setSupportActionBar(toolbar) để setup toolbar
- Tạo ActionBarDrawerToggle để handle drawer toggle button
- Thêm drawer listener vào DrawerLayout
- Gọi syncState() để sync toggle state với drawer
- Gán NavigationView.OnNavigationItemSelectedListener
- Set checked item mặc định = home

**setupAccountFooter()**
- Lấy SharedPreferences với key "SESSION"
- Lấy adminname từ prefs (key "adminname")
- Lấy username từ prefs (key "username")
- Gán giá trị vào tvAccountName và tvAccountRole
- Gán click listener cho btnLogout:
  - Xóa tất cả prefs (isLogin=false, remember=false, username="", adminname="")
  - Hiển thị Toast "Đã đăng xuất"
  - Chuyển sang LoginActivity
  - Gọi finish() để không quay lại

**loadDashboardData()**
- Tạo instance của các DAO: EmployeeDAO, DepartmentDAO, AttendanceDAO, TrainingDAO
- Gọi getAllDepartments() → gán vào tvTotalDepartments
- Gọi getAllEmployees() → gán vào tvTotalEmployees
- Gọi method đếm nhân viên đang làm (status=1) → gán vào tvHomeWorking
- Gọi method đếm chưa chấm công hôm nay → gán vào tvHomeNotCheckin
- Gọi method đếm đang học → gán vào tvHomeTraining
- Lưu ý: KHÔNG gọi trong onResume() vì sẽ load lại mỗi lần quay lại activity, gây chậm

**setupClickEvents()**
- Gán click listener cho cardDepartments → openDepartment()
- Gán click listener cho cardEmployees → openEmployee()
- Gán click listener cho 8 menu items → call hàm tương ứng:
  - menuDepartment → openDepartment()
  - menuEmployee → openEmployee()
  - menuAttendance → openAttendance()
  - menuSalary → openSalary()
  - menuReward → openReward()
  - menuDiscipline → openDiscipline()
  - menuTraining → openTraining()
  - menuSetting → openSettings()

**openDepartment() / openEmployee() / etc**
- Tạo Intent đến Activity tương ứng
- Gọi startActivity(intent)
- Đóng drawer: drawerLayout.closeDrawer(GravityCompat.START)
- KHÔNG gọi finish() vì HomeActivity là "home" của app

**onNavigationItemSelected(MenuItem item)**
- Được gọi khi user chọn item từ navigation drawer
- Kiểm tra item.getItemId() để xác định menu item nào
- Gọi hàm openXXX() tương ứng
- Đóng drawer
- Return true để indicate đã handle item

**onResume()**
- KHÔNG load lại dashboard data ở đây
- Chỉ dùng khi cần reload list sau khi trở lại từ activity khác (nếu cần)

### Điểm cần lưu ý:

- Back button: Nếu drawer mở → đóng drawer, nếu đóng → exit app
- Dashboard data: Load trong onCreate(), KHÔNG trong onResume()
- SharedPreferences: Phải dùng key "SESSION" để match LoginActivity
- Menu items: 8 items cần setup click listener riêng từng cái
- Drawer navigation: Phải đóng drawer sau khi bấm menu item

### Lỗi thường gặp:

SAI: Lấy giá trị từ prefs sai key
String name = prefs.getString("adminName", "");

ĐÚNG: Dùng đúng key
String name = prefs.getString("adminname", "Người dùng");

SAI: Không đóng drawer khi navigate
menuEmployee.setOnClickListener(v -> openEmployee());

ĐÚNG: Đóng drawer trước khi navigate
menuEmployee.setOnClickListener(v -> {
    startActivity(new Intent(this, EmployeeActivity.class));
    drawerLayout.closeDrawer(GravityCompat.START);
});

SAI: Load dashboard data trong onResume()
@Override
protected void onResume() {
    super.onResume();
    loadDashboardData(); // Gây chậm, không cần
}

ĐÚNG: Chỉ load trong onCreate()
Nếu cần reload, gọi riêng method khi cần

### Element Structure:

HomeActivity
  ├─ DrawerLayout - Container chứa content và drawer
  ├─ NavigationView - Menu drawer side
  ├─ Toolbar - Header bar
  ├─ Cards (Department, Employees) - Clickable cards
  ├─ TextViews (Stats) - Hiển thị số liệu
  ├─ 8 Menu Items:
  │  ├─ menuDepartment
  │  ├─ menuEmployee
  │  ├─ menuAttendance
  │  ├─ menuSalary
  │  ├─ menuReward
  │  ├─ menuDiscipline
  │  ├─ menuTraining
  │  └─ menuSetting
  └─ Footer - Account info + Logout button

---

## 3. EmployeeActivity (Quản Lý Nhân Viên)

Vị trí: com.example.hrm.activities.EmployeeActivity

### Luồng chính:
```
onCreate()
    ↓
initViews() & initData()
    └─ Load all employees từ DB
    ↓
Hiển thị list (RecyclerView)
    ├─ Avatar (từ drawable folder)
    ├─ Tên, Mã NV
    ├─ Phòng ban, Chức vụ
    └─ Buttons: Edit, Delete, View Detail
    ↓
User action:
    ├─ Click Add FAB → showEmployeeDialog(null, false)
    ├─ Click Edit → showEmployeeDialog(employee, true)
    └─ Click Delete → confirmDelete()
```

### Chi tiết các hàm:

**onCreate()**
- Khởi tạo EmployeeActivity
- Gọi initViews() để liên kết UI
- Gọi initData() để load dữ liệu
- Gọi initActions() để gán listener
- Gọi setupToolbar() để setup back button
- Lưu ý: Order của các hàm gọi rất quan trọng

**initViews()**
- Liên kết Toolbar từ layout
- Liên kết SearchView (EditText) để search nhân viên
- Liên kết RecyclerView để hiển thị list
- Liên kết FloatingActionButton (FAB) để add employee
- Tạo LayoutManager cho RecyclerView (LinearLayoutManager)
- Tạo EmployeeDAO instance

**initData()**
- Gọi employeeDAO.getAllEmployees() để load tất cả nhân viên
- Tạo EmployeeAdapter với danh sách nhân viên
- Gán adapter vào RecyclerView
- Lưu danh sách vào biến global để dùng cho filter

**initActions()**
- Gán click listener cho FAB:
  - Gọi showEmployeeDialog(null, false) để add new employee
- Gán TextWatcher cho SearchView:
  - Khi text thay đổi, gọi filterList(query)
  - filterList() sẽ filter danh sách dựa trên tên/mã nhân viên
  - Cập nhật adapter với danh sách lọc

**setupToolbar()**
- Gọi setSupportActionBar(toolbar) để setup toolbar
- Gọi getSupportActionBar().setDisplayHomeAsUpEnabled(true)
  - Hiển thị back arrow trên toolbar
- Gán click listener cho back arrow:
  - Gọi finish() để quay lại activity trước

**showEmployeeDialog(Employee emp, boolean isEdit)**
- Tạo AlertDialog.Builder
- Inflate layout dialog (dialog_add_employee)
- Liên kết các EditText trong dialog
- Nếu isEdit=true (edit mode):
  - Gán giá trị từ emp vào các EditText
  - Disable mã nhân viên (không đổi được)
  - Hiển thị title "Chỉnh sửa nhân viên"
- Nếu isEdit=false (add mode):
  - Clear tất cả EditText
  - Enable tất cả field
  - Hiển thị title "Thêm nhân viên mới"
- Gán click listener cho button Save:
  - Validate dữ liệu (check empty)
  - Gọi insertEmployee() hoặc updateEmployee()
  - Nếu thành công, reload list và close dialog
- Gán click listener cho button Cancel:
  - Close dialog
- Hiển thị dialog

**filterList(String query)**
- Duyệt danh sách nhân viên gốc
- Kiểm tra xem tên hoặc mã nhân viên có match query không
- Tạo danh sách mới chứa các employee match
- Gọi adapter.notifyDataSetChanged() để cập nhật UI

**insertEmployee(Employee emp)**
- Gọi employeeDAO.insertEmployee(emp)
- Nếu thành công:
  - Hiển thị Toast thành công
  - Reload list
  - Close dialog
- Nếu thất bại:
  - Hiển thị Toast lỗi

**updateEmployee(Employee emp)**
- Gọi employeeDAO.updateEmployee(emp)
- Nếu thành công:
  - Hiển thị Toast thành công
  - Reload list
  - Close dialog
- Nếu thất bại:
  - Hiển thị Toast lỗi

**deleteEmployee(Employee emp)**
- Hiển thị confirm dialog
- Nếu user confirm:
  - Gọi employeeDAO.deleteEmployee(emp)
  - Reload list
  - Hiển thị Toast thành công
- Nếu user cancel:
  - Close dialog

### Điểm cần lưu ý:

- Avatar: Lưu tên file trong DB, load từ `/drawable` folder
  - Ví dụ: `avatar_001.png` → Lấy drawable resource ID
  - Không load từ external storage
- Mã NV: Format cố định (NV001, NV002, ...), KHÔNG được đổi
- Ngày vào làm: Format `yyyy-MM-dd` trong DB
- Dialog Add/Edit: Dùng chung 1 dialog layout
- Disable mã NV khi edit: để tránh user đổi mã (vi phạm FK)

### Lỗi thường gặp:

SAI: Load avatar từ DB path trực tiếp
Picasso.get().load(employee.getAvatar()).into(imageView);

ĐÚNG: Convert drawable name thành resource ID
int resId = context.getResources()
    .getIdentifier(employee.getAvatar(), "drawable", context.getPackageName());
imageView.setImageResource(resId);

SAI: Không validate DatePicker
if (edtDOB.getText().toString().isEmpty()) {
    // Continue
}

ĐÚNG: Hiện error indicator
edtDOB.setError("Vui lòng chọn ngày sinh");
edtDOB.requestFocus();

SAI: Cho edit mã nhân viên
edtMaNV.setEnabled(true); // Sai trong edit mode

ĐÚNG: Disable mã NV trong edit mode
if (isEdit) {
    edtMaNV.setEnabled(false);
}

### Element Structure:

EmployeeActivity
  ├─ Toolbar (setDisplayHomeAsUpEnabled)
  ├─ SearchView (EditText) - Tìm kiếm nhân viên
  ├─ RecyclerView - List employees
  │  └─ Item Employee
  │     ├─ Avatar (ImageView)
  │     ├─ Info (TextViews) - Tên, mã, phòng ban, chức vụ
  │     ├─ Edit Button
  │     └─ Delete Button
  └─ FAB (Add) - Thêm nhân viên mới

---

## 📅 4. AttendanceActivity (Chấm Công)

### 📍 Vị trí: `com.example.hrm.activities.AttendanceActivity`

### 🔀 Luồng:
```
onCreate()
  ↓
initViews() & initData()
  └─ Load all employees (LEFT JOIN attendance hôm nay)
  ↓
Hiển thị list:
  ├─ Avatar nhân viên
  ├─ Tên, Mã NV, Phòng ban
  ├─ Giờ vào (nếu có)
  └─ Button Check-in / History
  ↓
User click "Check-in":
  ↓
TimePickerDialog (24h format)
  ├─ Default = Current time
  ├─ User select giờ/phút
  └─ Save time as "HH:mm:ss"
  ↓
markAttendance(idNv, gioVao)
  ├─ Get current date (yyyy-MM-dd)
  ├─ Get work_shift từ prefs (mặc định 08:00)
  ├─ Compare time
  │  ├─ gioVao > workShift → Status = 2 (Trễ)
  │  └─ gioVao <= workShift → Status = 1 (Đúng giờ)
  └─ Insert/Update DB
  ↓
Display update time trên list
```

### ⚠️ Điểm Cần Lưu Ý:
- **Work Shift**: Lấy từ `SharedPreferences` key `work_shift` (mặc định 08:00)
- **Current Date**: LUÔN dùng **hôm nay**, không dùng tham số
- **Time Format**: DB lưu `HH:mm:ss`, display format từ prefs
- **UNIQUE Constraint**: (id_nv, ngay_cc) → Chỉ 1 check-in/ngày

### ❌ Lỗi Phổ Biến:
```java
// ❌ SAI: Lấy date từ dialog (có thể sai ngày)
String date = selectedDate; // User có thể chọn ngày khác

// ✅ ĐÚNG: Lấy date hôm nay
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault());
String date = sdf.format(new Date());

// ❌ SAI: Không xét timezone
new SimpleDateFormat("yyyy-MM-dd").format(new Date())

// ✅ ĐÚNG: Set timezone trước
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault());
String date = sdf.format(new Date());

// ❌ SAI: So sánh giờ không xét :00
if (gioVao > workShift) // "09:15" > "08:00" ✗ String compare wrong

// ✅ ĐÚNG: Format chuẩn rồi compare
String timeForDb = String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute);
int compare = timeForDb.compareTo(workShift); // > 0 = Trễ
```

### 🏗️ Element Structure:
```
AttendanceActivity
  ├─ Toolbar
  ├─ SearchView
  ├─ RecyclerView
  │  └─ Item Attendance
  │     ├─ Avatar
  │     ├─ Info (Name, Code, Department)
  │     ├─ Check-in Time (hoặc "Chưa điểm danh")
  │     ├─ Status Indicator (Green/Red)
  │     ├─ Check-in Button
  │     └─ History Button
  └─ FAB (History)
```

---

## 5. SalaryActivity (Quản Lý Lương)

Vị trí: com.example.hrm.activities.SalaryActivity

### Luồng chính:
```
onCreate()
    ↓
initData()
    └─ Load salary records từ DB
    ↓
Hiển thị list:
    ├─ Nhân viên (ID + Tên)
    ├─ Tháng/Năm
    ├─ Số ngày công
    ├─ Tổng lương
    └─ Buttons: View, Edit, Delete
    ↓
User click item:
    ├─ View Detail (Read-only)
    └─ Edit/Delete dialog
```

### Chi tiết các hàm:

**onCreate()**
- Khởi tạo SalaryActivity
- Gọi initViews() để liên kết UI
- Gọi initData() để load dữ liệu
- Gọi initActions() để gán listener

**initViews()**
- Liên kết Toolbar
- Liên kết Spinner để filter theo nhân viên
- Liên kết RecyclerView để hiển thị list lương
- Liên kết FloatingActionButton để thêm lương mới
- Tạo SalaryDAO instance
- Tạo EmployeeDAO instance

**initData()**
- Gọi salaryDAO.getAllSalary() để load tất cả bản ghi lương
- Tạo SalaryAdapter với danh sách
- Gán adapter vào RecyclerView
- Lưu danh sách vào biến global để dùng cho filter
- Load danh sách employee để populate spinner:
  - Gọi employeeDAO.getAllEmployees()
  - Format spinner: "idNv - maNv - hoTen" (ví dụ: "1 - NV001 - Tên Nhân Viên")

**initActions()**
- Gán listener cho Spinner:
  - Khi user chọn employee, filter danh sách lương theo employee đó
  - Gọi filterSalaryByEmployee(selectedId)
- Gán click listener cho FAB:
  - Gọi showSalaryDialog(null, false) để add salary mới

**setupToolbar()**
- Giống các activity khác

**filterSalaryByEmployee(int employeeId)**
- Duyệt danh sách lương gốc
- Kiểm tra xem salary.idNv == employeeId
- Tạo danh sách mới chứa các bản ghi match
- Gọi adapter.notifyDataSetChanged() để cập nhật UI

**showSalaryDialog(SalaryDTO salary, boolean isEdit)**
- Tạo AlertDialog
- Inflate layout dialog (dialog_add_salary)
- Liên kết các EditText: thángNam, soNgayCong, phuCap, khauTru, etc
- Liên kết Spinner để chọn nhân viên
- Setup spinner:
  - Load danh sách employee từ DB
  - Format: "idNv - maNv - hoTen"
  - Gán adapter vào spinner
- Nếu isEdit=true (edit mode):
  - Gán giá trị từ salary vào các field
  - Find position của employee → setSelection()
  - Disable thángNam spinner (không đổi được)
  - Hiển thị title "Chỉnh sửa lương"
- Nếu isEdit=false (add mode):
  - Clear tất cả field
  - Hiển thị title "Thêm lương mới"
- Setup các field tính toán (READONLY):
  - tongThuong, tongPhat, tongLuong
  - setEnabled(false)
  - setFocusable(false)
- Gán click listener cho button Save:
  - Validate dữ liệu
  - Extract employee ID từ spinner:
    - String selected = spinner.getSelectedItem().toString();
    - int employeeId = Integer.parseInt(selected.split(" - ")[0]);
  - Gọi insertSalary() hoặc updateSalary()
  - Reload list
- Hiển thị dialog

**insertSalary(SalaryDTO salary)**
- Gọi salaryDAO.insertSalary(salary)
- Nếu thành công:
  - Toast "Thêm lương thành công"
  - Reload list
  - Close dialog
- Nếu thất bại:
  - Toast lỗi

**updateSalary(SalaryDTO salary)**
- Gọi salaryDAO.updateSalary(salary)
- Nếu thành công:
  - Toast "Cập nhật lương thành công"
  - Reload list
  - Close dialog
- Nếu thất bại:
  - Toast lỗi

**deleteSalary(SalaryDTO salary)**
- Hiển thị confirm dialog
- Nếu confirm:
  - Gọi salaryDAO.deleteSalary(salary)
  - Reload list
  - Toast thành công
- Nếu cancel:
  - Close dialog

### Điểm cần lưu ý:

- Spinner Nhân Viên: Format "idNv - maNv - hoTen"
  - Extract ID: split(" - ")[0] rồi parseInt
- Tháng/Năm: Format "MM/yyyy" (e.g., "04/2026")
  - Nên dùng MonthYearPicker hoặc custom dialog
- Calculation Fields: tongThuong, tongPhat, tongLuong
  - Chỉ TÍNH, không EDIT trực tiếp
  - PHẢI setEnabled(false) và setFocusable(false)
- Display Format: Số tiền hiển thị dạng "##,###" VND
  - Dùng NumberFormat hoặc custom format
- soNgayCong: Có thể auto-calculate từ attendance table (đếm ngày có chấm công)

### Lỗi thường gặp:

SAI: Không extract employee ID từ spinner
int idNv = Integer.parseInt(spinnerDisplay); // "1 - NV001 - Tên" → Exception!

ĐÚNG: Extract phần đầu
String displayText = spinner.getSelectedItem().toString();
int idNv = Integer.parseInt(displayText.split(" - ")[0]);

SAI: Cho edit cột tính toán
edtTongLuong.setEnabled(true); // Phải read-only!

ĐÚNG: Read-only các cột tính
edtTongLuong.setEnabled(false);
edtTongLuong.setFocusable(false);
edtTongLuong.setKeyListener(null);

SAI: Không setup spinner TRƯỚC hiển thị dialog
dialog.show();
setupSpinner(spinner);

ĐÚNG: Setup spinner TRƯỚC show dialog
setupSpinner(spinner);
dialog.show();

SAI: Lấy salary từ prefs
String salary = prefs.getString("salary", ""); // Sai!

ĐÚNG: Load từ DAO
List<SalaryDTO> list = salaryDAO.getAllSalary();

### Element Structure:

SalaryActivity
  ├─ Toolbar - Header bar
  ├─ Spinner - Filter by Employee (dropdown)
  ├─ RecyclerView - List salary records
  │  └─ Item Salary
  │     ├─ Employee display (ID - Code - Name)
  │     ├─ Month/Year
  │     ├─ Working days
  │     ├─ Allowances (read-only)
  │     ├─ Deductions (read-only)
  │     ├─ Bonuses (read-only)
  │     ├─ Penalties (read-only)
  │     ├─ Total Salary (read-only)
  │     ├─ Edit Button
  │     └─ Delete Button
  └─ FAB (Add) - Thêm lương mới

---

## 6. RewardActivity (Khen Thưởng) & DisciplineActivity (Kỷ Luật)

Vị trí: com.example.hrm.activities.RewardActivity & DisciplineActivity

### Luồng chính (giống nhau):
```
onCreate()
    ↓
initData()
    └─ Load records từ DB
    ├─ Load employee list (cho Spinner)
    └─ Setup adapter với callback (Edit, Delete, Click)
    ↓
Hiển thị list
    ├─ Employee (Spinner format)
    ├─ Ngày quyết định
    ├─ Hình thức (text)
    ├─ Số tiền
    ├─ Lý do (text)
    └─ Buttons: Edit, Delete
    ↓
User click Edit/Add:
    ↓
showDialog()
    ├─ Setup spinner (employee list)
    ├─ Bind existing data (nếu edit)
    ├─ Setup click listeners
    └─ Dialog show
    ↓
Save → Insert/Update DB
    └─ Reload list
```

### Chi tiết các hàm (RewardActivity và DisciplineActivity giống nhau):

**onCreate()**
- Khởi tạo activity
- Gọi initViews() để liên kết UI
- Gọi initData() để load dữ liệu
- Gọi initActions() để gán listener
- Gọi setupToolbar() để setup back button

**initViews()**
- Liên kết Toolbar
- Liên kết SearchView (EditText) để search
- Liên kết RecyclerView để hiển thị list
- Liên kết FloatingActionButton để add mới
- Tạo DAO instance (RewardDAO hoặc DisciplineDAO)
- Tạo EmployeeDAO instance

**initData()**
- Gọi rewardDAO.getAllKhenThuong() (hoặc disciplineDAO.getAllKyLuat())
- Tạo adapter (RewardAdapter hoặc DisciplineAdapter)
- Gán adapter vào RecyclerView
- Load danh sách employee từ EmployeeDAO:
  - Gọi employeeDAO.getAllEmployees()
  - Lưu vào biến employeeList để dùng trong dialog

**initActions()**
- Gán TextWatcher cho SearchView:
  - Khi text thay đổi, gọi filterList(query)
- Gán click listener cho FAB:
  - Gọi showDialog(null, false) để add mới
- Setup adapter callback:
  - onEdit(object) → gọi showDialog(object, true)
  - onDelete(object) → confirm delete
  - onItemClick(object) → view detail (optional)

**setupToolbar()**
- Giống các activity khác

**filterList(String query)**
- Duyệt danh sách gốc
- Kiểm tra tên nhân viên hoặc hình thức match query
- Tạo danh sách mới
- Cập nhật adapter

**showDialog(RewardDTO reward, boolean isEdit)** (hoặc DisciplineDTO)
- Tạo AlertDialog
- Inflate layout dialog
- Liên kết các EditText:
  - Ngày quyết định
  - Hình thức (text)
  - Số tiền (text, numeric)
  - Lý do (text)
- Liên kết Spinner để chọn nhân viên
- Setup spinner:
  - Load danh sách employee từ employeeList
  - Tạo adapter (format: hoTen hoặc "idNv - maNv - hoTen")
  - Gán adapter vào spinner
- Nếu isEdit=true (edit mode):
  - Gán giá trị từ reward vào các field
  - Find position của employee → setSelection(position)
  - Disable spinner (không đổi được employee)
  - Hiển thị title "Chỉnh sửa khen thưởng"
- Nếu isEdit=false (add mode):
  - Clear tất cả field
  - Enable spinner
  - Hiển thị title "Thêm khen thưởng mới"
- Gán click listener cho button Save:
  - Validate dữ liệu (check empty)
  - Lấy employee từ spinner
  - Gọi insertReward() hoặc updateReward()
  - Reload list
- Gán click listener cho button Cancel:
  - Close dialog
- Hiển thị dialog

**insertReward(RewardDTO reward)** (hoặc Discipline)
- Gọi rewardDAO.insertKhenThuong(reward)
- Nếu thành công:
  - Toast thành công
  - Reload list
  - Close dialog
- Nếu thất bại:
  - Toast lỗi

**updateReward(RewardDTO reward)** (hoặc Discipline)
- Gọi rewardDAO.updateKhenThuong(reward)
- Nếu thành công:
  - Toast thành công
  - Reload list
  - Close dialog
- Nếu thất bại:
  - Toast lỗi

**deleteReward(RewardDTO reward)** (hoặc Discipline)
- Hiển thị confirm dialog
- Nếu confirm:
  - Gọi rewardDAO.deleteKhenThuong(reward)
  - Reload list
  - Toast thành công
- Nếu cancel:
  - Close dialog

**findSelectedEmployeePosition(int idEmployee)**
- Duyệt danh sách employee
- Tìm employee có idNv == idEmployee
- Return vị trí trong list
- Lưu ý: Trả về -1 nếu không tìm thấy, PHẢI check trước khi setSelection()

### Điểm cần lưu ý:

- Spinner Position:
  - Load employee list TRƯỚC khi setup spinner
  - Edit: Find position của employee hiện tại → setSelection(position)
  - Add: Spinner sẽ select mục đầu tiên (default)
- Ngày quyết định: Format "yyyy-MM-dd"
  - Nên dùng DatePickerDialog hoặc custom date picker
- Validation: TẤT CẢ field đều required
  - Kiểm tra empty, kiểm tra số tiền hợp lệ
- Spinner setup PHẢI TRƯỚC inflate dialog
  - Nếu setup sau inflate, có thể crash
- Adapter callback:
  - onEdit() phải gọi showDialog với isEdit=true
  - onDelete() phải hiển thị confirm dialog trước delete

### Lỗi thường gặp:

SAI: Setup spinner TRƯỚC khi load employee list
spEmployee.setAdapter(adapter);
List<Employee> list = employeeDAO.getAllEmployees();
setupEmployeeSpinner(spEmployee, list);

ĐÚNG: Load list TRƯỚC, setup spinner
List<Employee> list = employeeDAO.getAllEmployees();
setupEmployeeSpinner(spEmployee, list);

SAI: Không kiểm tra adapter null
int position = findSelectedEmployeePosition(id);
spEmployee.setSelection(position); // Có thể NPE hoặc crash

ĐÚNG: Kiểm tra và handle
int position = findSelectedEmployeePosition(id);
if (position >= 0 && position < employeeList.size()) {
    spEmployee.setSelection(position);
}

SAI: Không disable spinner trong edit mode
if (isEdit) {
    // Không set enabled = false
}
// User có thể đổi employee (sai!)

ĐÚNG: Disable spinner trong edit mode
if (isEdit) {
    spEmployee.setEnabled(false);
}

SAI: Load employee list mỗi lần hiển thị dialog
showDialog() {
    employeeList = employeeDAO.getAllEmployees(); // Gây chậm
}

ĐÚNG: Load một lần trong initData
initData() {
    employeeList = employeeDAO.getAllEmployees();
}
showDialog() {
    // Dùng employeeList đã load
}

### Element Structure:

RewardActivity / DisciplineActivity
  ├─ Toolbar - Header bar
  ├─ SearchView - Tìm kiếm (optional)
  ├─ RecyclerView - List khen thưởng/kỷ luật
  │  └─ Item (Reward/Discipline)
  │     ├─ Employee name
  │     ├─ Decision date
  │     ├─ Form/Type
  │     ├─ Amount
  │     ├─ Reason
  │     ├─ Edit Button
  │     └─ Delete Button
  └─ FAB (Add) - Thêm mới

---

## 7. TrainingActivity (Quản Lý Đào Tạo)

Vị trí: com.example.hrm.activities.TrainingActivity

### Luồng chính:
```
onCreate()
    ↓
initData()
    └─ Load training courses JOIN employee
    ↓
Hiển thị list:
    ├─ Course name + Status badge
    ├─ Trainer name
    ├─ Date range (Start - End)
    ├─ Employee học viên
    ├─ Employee code (ID)
    └─ Buttons: Edit, Delete
    ↓
User click item:
    ├─ Edit → Load dialog với data cũ
    │  ├─ Course name (editable)
    │  ├─ Trainer (editable)
    │  ├─ Dates (DatePicker)
    │  ├─ Employee (read-only spinner)
    │  ├─ Status (editable spinner)
    │  └─ Save button
    └─ Delete → Confirm dialog
```

### Chi tiết các hàm:

**onCreate()**
- Khởi tạo TrainingActivity
- Gọi initViews() để liên kết UI
- Gọi initData() để load dữ liệu
- Gọi initActions() để gán listener
- Gọi setupToolbar() để setup back button

**initViews()**
- Liên kết Toolbar
- Liên kết SearchView (EditText) để search khóa học
- Liên kết RecyclerView để hiển thị list
- Liên kết FloatingActionButton để add khóa học mới
- Tạo TrainingDAO instance
- Tạo EmployeeDAO instance

**initData()**
- Gọi trainingDAO.getAllTrainingInfo()
  - Câu lệnh: SELECT từ KhoaHoc JOIN ChiTietDaoTao JOIN NhanVien
  - Kết quả: List<TrainingDTO> với đầy đủ thông tin
  - QUAN TRỌNG: Lấy COL_ID_NV (INT), không COL_MA_NV
- Tạo TrainingAdapter với danh sách
- Gán adapter vào RecyclerView với callback:
  - onEdit(dto) → showDialog(dto, true)
  - onDelete(dto) → confirmDelete(dto)
- Lưu danh sách vào biến global

**initActions()**
- Gán TextWatcher cho SearchView:
  - Khi text thay đổi, gọi filterList(query)
  - Cập nhật adapter với danh sách lọc
- Gán click listener cho FAB:
  - Gọi showAddDialog() để add khóa học mới

**setupToolbar()**
- Giống các activity khác

**filterList(String query)**
- Duyệt danh sách gốc
- Kiểm tra: courseName hoặc employeeName match query
- Tạo danh sách mới
- Cập nhật adapter

**showAddDialog()**
- Tạo AlertDialog
- Inflate layout dialog (dialog_add_training)
- Liên kết các EditText:
  - edtCourseName - Tên khóa học
  - edtTeacherName - Tên giảng viên
  - edtStartDate - Ngày bắt đầu
  - edtEndDate - Ngày kết thúc
- Liên kết các Spinner:
  - spStaff - Chọn nhân viên (học viên)
  - spResult - Chọn kết quả học tập
- Setup spinners:
  - Gọi setupSpinners(spStaff, spResult)
  - spStaff: Load employee list từ EmployeeDAO
  - spResult: Fixed array ["Đang học", "Đạt", "Xuất sắc", "Không đạt"]
- Gán click listener cho date EditText:
  - edtStartDate → showDatePicker(edtStartDate)
  - edtEndDate → showDatePicker(edtEndDate)
  - DatePickerDialog sẽ set date format "yyyy-MM-dd"
- Gán click listener cho button Save:
  - Validate dữ liệu (check empty)
  - Lấy employee từ spinner: emp.getIdNv()
  - Gọi saveTraining() hoặc updateTraining()
  - Reload list
- Hiển thị dialog

**showEditDialog(TrainingDTO dto)**
- Tạo AlertDialog
- Inflate layout dialog
- Liên kết UI element từ dialog
- Gán dữ liệu cũ từ dto:
  - edtCourseName.setText(dto.getCourseName())
  - edtTeacherName.setText(dto.getTeacher())
  - edtStartDate.setText(dto.getStartDate())
  - edtEndDate.setText(dto.getEndDate())
- Setup spinners (NHƯ ADD):
  - setupSpinners(spNV, spKQ)
- Disable employee spinner (QUAN TRỌNG):
  - spNV.setEnabled(false)
  - Không cho đổi employee trong edit mode
- Find position của employee hiện tại:
  - Gọi setSpinnerSelection(spNV, dto.getEmployeeCode())
  - Phải compare bằng ID (String.valueOf(emp.getIdNv()))
- Find position của status:
  - Gọi setSpinnerResultSelection(spKQ, dto.getStatus())
- Gán click listener cho date field:
  - edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate))
  - edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate))
  - PHẢI có listener, nếu không user không thể chọn date mới
- Gán click listener cho button Save:
  - Validate dữ liệu
  - Gọi trainingDAO.updateTraining(...)
  - Reload list
- Hiển thị dialog

**saveTraining()**
- Lấy dữ liệu từ EditText
- Validate (check empty)
- Lấy employee từ spinner: emp = (Employee) spStaff.getSelectedItem()
- Gọi trainingDAO.insertTraining(name, teacher, startDate, endDate, String.valueOf(emp.getIdNv()), status)
  - QUAN TRỌNG: Truyền String.valueOf(emp.getIdNv()), không emp.getMaNv()
- Nếu thành công:
  - Toast "Đã thêm khóa học cho " + emp.getHoTen()
  - Return true
- Nếu thất bại:
  - Return false

**showDatePicker(EditText editText)**
- Tạo Calendar instance
- Nếu editText đã có giá trị, parse để hiển thị ngày đó:
  - Parse "yyyy-MM-dd" format
  - Set vào calendar
- Tạo DatePickerDialog:
  - Callback: Format ngày rồi set vào editText ("yyyy-MM-dd")
  - Default: year, month, day từ calendar
  - Format: 24h (isShowMilliseconds=false)
- Show dialog

**setSpinnerSelection(Spinner spinner, String code)**
- Duyệt spinner items
- So sánh String.valueOf(emp.getIdNv()) == code
  - QUAN TRỌNG: phải compare ID, không phải maNv
- setSelection(i) khi tìm thấy
- Nếu không tìm thấy, mặc định select item đầu tiên

**setSpinnerResultSelection(Spinner spinner, String status)**
- Duyệt spinner items
- So sánh status string
- setSelection(i) khi tìm thấy

**setupSpinners(Spinner spNV, Spinner spKQ)**
- Load employee list:
  - Gọi employeeDAO.getAllEmployees()
  - Tạo ArrayAdapter với danh sách
  - Gán adapter vào spNV
- Load result list:
  - String[] results = {"Đang học", "Đạt", "Xuất sắc", "Không đạt"}
  - Tạo ArrayAdapter
  - Gán adapter vào spKQ

**deleteTraining(TrainingDTO dto)**
- Hiển thị confirm dialog:
  - Message: "Bạn có chắc chắn muốn xóa [tên employee] khỏi [tên khóa học]?"
- Nếu confirm:
  - Gọi trainingDAO.deleteTraining(dto.getCourseId(), dto.getEmployeeCode())
  - Nếu thành công: reload list, Toast "Đã xóa"
  - Nếu thất bại: Toast "Lỗi"

### Điểm cần lưu ý:

- Data Structure:
  - Table 1: KhoaHoc (course info)
  - Table 2: ChiTietDaoTao (training detail) - Liên kết nhiều-nhiều
  - Employee ID trong DB: id_nv (INTEGER), không phải ma_nv (STRING)
- Employee Selection:
  - Save: Truyền String.valueOf(emp.getIdNv()) (convert int to string)
  - Load: Lấy nv.COL_ID_NV từ cursor
  - Matching: String.valueOf(emp.getIdNv()).equals(code)
- Status: Fixed array ["Đang học", "Đạt", "Xuất sắc", "Không đạt"]
- Edit Mode: PHẢI disable employee spinner
- Date Format: "yyyy-MM-dd" cả lưu vào DB và hiển thị

### Lỗi thường gặp:

SAI: Dùng employee code (String) làm ID
trainingDAO.insertTraining(..., emp.getMaNv(), ...); // "NV001" ✗

ĐÚNG: Dùng integer ID
trainingDAO.insertTraining(..., String.valueOf(emp.getIdNv()), ...);

SAI: Load employee code từ cursor
cursor.getString(6) // COL_MA_NV ✗

ĐÚNG: Load employee ID từ cursor
cursor.getString(6) // COL_ID_NV → convert to String ✓

SAI: So sánh sai kiểu dữ liệu
if (e.getMaNv().equals(code)) { } // code là "1" ✗

ĐÚNG: Compare ID
if (String.valueOf(e.getIdNv()).equals(code)) { }

SAI: Không gán date picker listener trong edit
// Khi user bấm date field, không mở dialog

ĐÚNG: Gán listener cho cả add và edit
edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

SAI: Không disable employee spinner trong edit mode
if (isEdit) {
    // Quên setEnabled(false)
}

ĐÚNG: Disable spinner trong edit mode
if (isEdit) {
    spNV.setEnabled(false);
}

### Element Structure:

TrainingActivity
  ├─ Toolbar - Header bar
  ├─ SearchView - Tìm kiếm khóa học
  ├─ RecyclerView - List training
  │  └─ Item Training
  │     ├─ Course name (Bold + Status badge)
  │     ├─ Trainer name
  │     ├─ Date range (Icon + Text)
  │     ├─ Employee section
  │     │  ├─ Student name
  │     │  └─ Student code (ID)
  │     ├─ Edit Button
  │     └─ Delete Button
  └─ FAB (Add) - Thêm khóa học mới

---

## 8. SettingActivity (Cài Đặt)

Vị trí: com.example.hrm.activities.SettingActivity

### Luồng chính:
```
onCreate()
    ↓
initViews() → Liên kết UI elements
    ↓
loadSettings() → Load preferences từ SharedPreferences
    ├─ Dark mode status
    ├─ Biometric status
    ├─ Work shift time
    ├─ Date format
    └─ Time format
    ↓
setupToolbar() → Setup back button
    ↓
setupEvents() → Gán click listener
    ├─ Toggle Dark Mode
    ├─ Enable Biometric
    ├─ Set Work Shift
    ├─ Change Date Format
    ├─ Change Time Format
    ├─ Backup/Restore DB
    └─ Logout
    ↓
User interactions
    └─ Dialog hoặc activity mới
```

### Chi tiết các hàm:

**onCreate()**
- Khởi tạo SettingActivity
- Lấy SharedPreferences với key "SESSION"
- Gọi initViews() để liên kết UI
- Gọi loadSettings() để load cài đặt hiện tại
- Gọi setupToolbar() để setup back button
- Gọi setupEvents() để gán listener
- Lưu ý: Order của các hàm gọi rất quan trọng

**initViews()**
- Liên kết Toolbar
- Liên kết Switch controls:
  - swBiometric - Bật/tắt vân tay
  - swDarkMode - Bật/tắt chế độ tối
- Liên kết LinearLayout/Button controls:
  - btnWorkShift - Đặt giờ vào làm
  - btnChangePassword - Đổi mật khẩu
  - btnBackup - Backup database
  - btnRestore - Restore database
  - btnDateFormat - Chọn format ngày
  - btnTimeFormat - Chọn format giờ
  - btnAbout - Thông tin
  - btnLogout - Đăng xuất
- Liên kết TextView để hiển thị giá trị:
  - tvWorkShiftTime - Giờ vào làm hiện tại
  - tvDateFormat - Format ngày
  - tvTimeFormat - Format giờ
  - tvAccountName - Tên admin
  - tvAccountRole - Username

**loadSettings()**
- Lấy dark mode status từ prefs (key "isDarkMode") → set switch
- Lấy biometric status từ prefs (key "isBiometricEnabled") → set switch
- Lấy work shift từ prefs (key "work_shift", default "08:00") → set tvWorkShiftTime
- Lấy date format từ prefs (key "date_format", default "dd/MM/yyyy") → set tvDateFormat
- Lấy time format từ prefs (key "time_format", default "HH:mm") → set tvTimeFormat
- Setup account footer: lấy adminname và username từ prefs

**setupToolbar()**
- Giống các activity khác
- Gọi setSupportActionBar()
- Setup back button: finish()

**setupEvents()**
- Setup swBiometric listener:
  - isInternalChange flag để tránh trigger khi load settings
  - Nếu true: gọi confirmBiometricToEnable()
  - Nếu false: lưu isBiometricEnabled=false vào prefs
- Setup swDarkMode listener:
  - isInternalChange flag
  - Nếu thay đổi:
    - Hiển thị ProgressDialog "Đang thay đổi giao diện..."
    - Lưu vào prefs: isDarkMode=isChecked
    - Gọi AppCompatDelegate.setDefaultNightMode() tương ứng:
      - MODE_NIGHT_YES nếu true
      - MODE_NIGHT_NO nếu false
    - Delay 500ms rồi startActivity(HomeActivity) + finish()
      - Delay để user thấy progress dialog
- Setup btnWorkShift listener:
  - Lấy work_shift từ prefs → parse giờ/phút
  - Tạo TimePickerDialog (24h format):
    - Default = current work shift time
    - Callback: Lưu giờ vào prefs, update tvWorkShiftTime
- Setup btnDateFormat listener:
  - Gọi showDateFormatDialog()
- Setup btnTimeFormat listener:
  - Gọi showTimeFormatDialog()
- Setup btnChangePassword listener:
  - Gọi showChangePasswordDialog()
- Setup btnBackup listener:
  - Gọi showBackupOptions()
- Setup btnRestore listener:
  - Gọi showRestoreDialog()
- Setup btnAbout listener:
  - Gọi showAboutDialog()
- Setup btnLogout listener:
  - SharedPreferences.Editor editor = prefs.edit()
  - Clear tất cả: putBoolean("isLogin", false), putBoolean("remember", false), etc
  - editor.apply()
  - Toast "Đã đăng xuất"
  - startActivity(LoginActivity), finish()

**confirmBiometricToEnable()**
- Kiểm tra xem thiết bị có hỗ trợ biometric không:
  - Gọi BiometricManager.canAuthenticate()
  - So sánh với BiometricManager.BIOMETRIC_SUCCESS
- Nếu không hỗ trợ:
  - Toast "Thiết bị không hỗ trợ vân tay"
  - Reset switch về false (set isInternalChange=true)
- Nếu hỗ trợ:
  - Gọi showBiometricPrompt()

**showBiometricPrompt()**
- Tạo BiometricPrompt với callback:
  - onAuthenticationSucceeded():
    - Lưu isBiometricEnabled=true vào prefs
    - Toast "Vân tay đã kích hoạt"
    - Set switch isInternalChange=true (IMPORTANT)
  - onAuthenticationError(errorCode, errString):
    - Toast lỗi
    - Reset switch về false
  - onAuthenticationFailed():
    - Toast "Xác thực thất bại"
- Tạo PromptInfo:
  - Title: "Xác thực vân tay"
  - Subtitle: "Dùng vân tay để kích hoạt đăng nhập nhanh"
  - NegativeButtonText: "Hủy"
- Gọi authenticate(promptInfo)

**showDateFormatDialog()**
- String[] options = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"}
- Lấy current format từ prefs
- Tìm checked item tương ứng
- Tạo AlertDialog.Builder:
  - setTitle("Chọn định dạng ngày")
  - setSingleChoiceItems(options, checkedItem, callback):
    - Callback: Lưu format vào prefs, update tvDateFormat
    - Gọi notifyDataSetChanged() trên adapter nếu cần
    - dialog.dismiss()
  - Hiển thị dialog

**showTimeFormatDialog()**
- String[] options = {"12 giờ (07:00 PM)", "24 giờ (19:00)"}
- String[] values = {"hh:mm a", "HH:mm"}
- Lấy current format từ prefs
- Tìm checked item:
  - Nếu format là "HH:mm" → checkedItem = 1 (24h)
  - Else → checkedItem = 0 (12h)
- Tạo AlertDialog.Builder:
  - setTitle("Chọn định dạng giờ")
  - setSingleChoiceItems(options, checkedItem, callback):
    - Callback: 
      - String selectedVal = values[which]
      - prefs.edit().putString("time_format", selectedVal).apply()
      - Cập nhật tvTimeFormat
      - dialog.dismiss()

**showChangePasswordDialog()**
- Tạo AlertDialog
- Inflate layout dialog_change_password
- Liên kết 3 EditText:
  - edtCurrentPassword - Mật khẩu cũ
  - edtNewPassword - Mật khẩu mới
  - edtConfirmPassword - Xác nhận mật khẩu
- Gán click listener cho button Change:
  - Validate: check empty, check edtNewPassword == edtConfirmPassword
  - Gọi accountDAO.changePassword(username, currentPass, newPass)
    - DAO sẽ verify currentPass trước khi update
  - Nếu thành công:
    - Toast "Đã đổi mật khẩu"
    - dialog.dismiss()
  - Nếu thất bại:
    - Toast "Mật khẩu cũ sai"
- Hiển thị dialog

**showBackupOptions()**
- Tạo AlertDialog với 2 option:
  - "Backup to Internal Storage"
  - "Backup to Cloud" (optional)
- Nếu chọn Internal Storage:
  - Tạo file backup: /data/data/com.example.hrm/databases/backup.db
  - Copy database file → backup file
  - Hiển thị Toast "Backup thành công"
  - (Optional) Share file nếu cần

**showRestoreDialog()**
- Tạo file picker intent (type: "*/*" hoặc "application/*")
- startActivityForResult()
- onActivityResult():
  - Lấy file uri từ data.getData()
  - Kiểm tra file hợp lệ
  - Copy backup file → database file
  - Hiển thị Toast "Restore thành công"
  - Nên restart app để reload dữ liệu từ database mới

**showAboutDialog()**
- Tạo AlertDialog
- Hiển thị thông tin app:
  - App name: "HRM System"
  - Version: "1.0"
  - Build date: "2026-04-17"
  - Credits: "Developer team"
- Button OK để close dialog

**revertSwitch()**
- Gọi từ BiometricPrompt.onAuthenticationError()
- Set isInternalChange = true (tránh trigger listener)
- swBiometric.setChecked(false)
- Set isInternalChange = false

### Điểm cần lưu ý:

- Dark Mode:
  - KHÔNG support dynamic change, phải restart activity
  - Dùng AppCompatDelegate.setDefaultNightMode()
  - Sau đó startActivity(HomeActivity) + finish()
  - isInternalChange flag để tránh trigger listener khi programmatic change
- Work Shift:
  - Format: "HH:mm" (24-hour)
  - Mặc định: "08:00"
  - Key: "work_shift"
  - So sánh check-in: phải format thành "HH:mm:ss"
- Date Format: 3 options
  - dd/MM/yyyy (VN format, default)
  - MM/dd/yyyy (US)
  - yyyy-MM-dd (ISO)
- Time Format: 2 options
  - HH:mm (24-hour, default)
  - hh:mm a (12-hour with AM/PM)
- Biometric:
  - Phải check BiometricManager.canAuthenticate()
  - Phải xác thực bằng vân tay TRƯỚC lưu
  - isInternalChange flag để tránh callback khi programmatic set
- SharedPreferences key: "SESSION" (phải match với LoginActivity)

### Lỗi thường gặp:

SAI: Thay đổi dark mode không restart
prefs.edit().putBoolean("isDarkMode", isChecked).apply();
AppCompatDelegate.setDefaultNightMode(...);
// Activity vẫn ở chế độ cũ

ĐÚNG: Restart app/activity sau
AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
startActivity(new Intent(this, HomeActivity.class));
finish();

SAI: Không set isInternalChange flag
swBiometric.setChecked(false); // Sẽ trigger listener!

ĐÚNG: Set flag trước change
isInternalChange = true;
swBiometric.setChecked(false);
isInternalChange = false;

SAI: Không set timezone khi format giờ
SimpleDateFormat sdf = new SimpleDateFormat(pattern);

ĐÚNG: Set timezone
SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault());

SAI: Lưu time format sai
prefs.edit().putString("time_format", "24h").apply();

ĐÚNG: Lưu format pattern
prefs.edit().putString("time_format", "HH:mm").apply();

SAI: Enable biometric không check supported
if (isChecked) {
    prefs.edit().putBoolean("isBiometricEnabled", true).apply();
}

ĐÚNG: Check supported trước enable
if (BiometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
    confirmBiometricToEnable();
} else {
    swBiometric.setChecked(false);
}

### Element Structure:

SettingActivity
  ├─ Toolbar - Header bar
  ├─ Account Footer
  │  ├─ tvAccountName - Tên admin
  │  ├─ tvAccountRole - Username
  │  └─ btnLogout - Nút đăng xuất
  ├─ Preferences Section
  │  ├─ swDarkMode (Switch) - Chế độ tối
  │  ├─ swBiometric (Switch) - Vân tay
  │  ├─ btnWorkShift (LinearLayout) - Đặt giờ vào
  │  │  └─ tvWorkShiftTime - Hiển thị giờ
  │  ├─ btnDateFormat (LinearLayout) - Format ngày
  │  │  └─ tvDateFormat - Hiển thị format
  │  ├─ btnTimeFormat (LinearLayout) - Format giờ
  │  │  └─ tvTimeFormat - Hiển thị format
  │  ├─ btnChangePassword (LinearLayout) - Đổi mật khẩu
  │  ├─ btnBackup (LinearLayout) - Backup DB
  │  ├─ btnRestore (LinearLayout) - Restore DB
  │  └─ btnAbout (LinearLayout) - Thông tin

---

## 🗄️ Database Structure

### 📊 Bảng Chính:

```
┌──────────────────────┐
│   NhanVien (Employee)│
├──────────────────────┤
│ id_nv (INT) PK       │
│ ma_nv (TEXT) UNIQUE  │
│ ho_ten (TEXT)        │
│ ngay_sinh (TEXT)     │
│ gioi_tinh (TEXT)     │
│ so_dt (TEXT)         │
│ email (TEXT)         │
│ id_phong_ban (FK)    │
│ chuc_vu (TEXT)       │
│ ngay_vao_lam (TEXT)  │
│ he_so_luong (REAL)   │
│ trang_thai (INT)     │
│ avatar (TEXT)        │
└──────────────────────┘

┌──────────────────────┐
│  ChuyenCan (Attend)  │
├──────────────────────┤
│ id_cc (INT) PK       │
│ id_nv (FK)           │
│ ngay_cc (TEXT)       │
│ gio_vao (TEXT)       │
│ gio_ra (TEXT)        │
│ trang_thai (INT)     │
│ ghi_chu (TEXT)       │
└──────────────────────┘
  UNIQUE(id_nv, ngay_cc)

┌──────────────────────┐
│  KhoaHoc (Training)  │
├──────────────────────┤
│ id_kh (INT) PK       │
│ ten_kh (TEXT)        │
│ ngay_bat_dau (TEXT)  │
│ ngay_ket_thuc (TEXT) │
│ giang_vien (TEXT)    │
└──────────────────────┘

┌──────────────────────┐
│ ChiTietDaoTao (Many)│
├──────────────────────┤
│ id_kh (FK)           │
│ id_nv (FK)           │
│ ket_qua (TEXT)       │
│ PK (id_kh, id_nv)   │
└──────────────────────┘

┌──────────────────────┐
│   Luong (Salary)     │
├──────────────────────┤
│ id_luong (INT) PK    │
│ id_nv (FK)           │
│ ma_nv (TEXT)         │
│ ho_ten (TEXT)        │
│ thang_nam (TEXT)     │
│ so_ngay_cong (INT)   │
│ phu_cap (REAL)       │
│ khau_tru (REAL)      │
│ tong_thuong (REAL)   │
│ tong_phat (REAL)     │
│ tong_luong (REAL)    │
└──────────────────────┘

┌──────────────────────┐
│ KhenThuong (Reward)  │
├──────────────────────┤
│ id_kt (INT) PK       │
│ id_nv (FK)           │
│ ngay_quyet_dinh      │
│ hinh_thuc (TEXT)     │
│ so_tien (REAL)       │
│ ly_do (TEXT)         │
└──────────────────────┘

┌──────────────────────┐
│ KyLuat (Discipline)  │
├──────────────────────┤
│ id_kl (INT) PK       │
│ id_nv (FK)           │
│ ngay_quyet_dinh      │
│ hinh_thuc (TEXT)     │
│ so_tien (REAL)       │
│ ly_do (TEXT)         │
└──────────────────────┘
```

---

## 🔑 SharedPreferences Keys

```
SESSION (Context.MODE_PRIVATE)
├─ isLogin (BOOLEAN) - Trạng thái đăng nhập
├─ remember (BOOLEAN) - Nhớ tài khoản
├─ isBiometricEnabled (BOOLEAN) - Bật vân tay
├─ username (STRING) - Tên đăng nhập
├─ adminname (STRING) - Tên admin
├─ isDarkMode (BOOLEAN) - Chế độ tối
├─ work_shift (STRING) - Giờ vào làm (HH:mm, default 08:00)
├─ date_format (STRING) - Format ngày (default dd/MM/yyyy)
└─ time_format (STRING) - Format giờ (default HH:mm)
```

---

## 🎯 Pattern & Best Practices

### 🏗️ Activity Lifecycle Pattern:
```java
public class Activity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xxx);
        
        initViews();        // 1. Link views
        initData();         // 2. Load data
        initActions();      // 3. Setup listeners
        setupToolbar();     // 4. Configure toolbar
    }
}
```

### 📝 DAO Pattern:
```java
public class XxxDAO {
    private DBHelper dbHelper;
    
    public XxxDAO(Context context) {
        dbHelper = new DBHelper(context);
    }
    
    public List<Xxx> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Query logic
        return list;
    }
    
    public boolean insert(Xxx obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert logic
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }
}
```

### 🔄 Adapter Pattern:
```java
public class XxxAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<Xxx> list;
    private OnItemClickListener listener;
    
    public XxxAdapter(Context context, List<Xxx> list, 
                      OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Xxx item = list.get(position);
        holder.tvName.setText(item.getName());
        
        holder.btnEdit.setOnClickListener(v -> 
            listener.onEdit(item)
        );
        holder.btnDelete.setOnClickListener(v -> 
            listener.onDelete(item)
        );
    }
    
    public void updateList(List<Xxx> newList) {
        this.list = newList;
        notifyDataSetChanged(); // Hoặc dùng DiffUtil
    }
}
```

---

## FAQ - Câu Hỏi Thường Gặp (Thi Vấn Đáp)

**Q1: Employee ID và Mã NV khác nhau như thế nào?**

A: Employee ID (id_nv) là số nguyên tự tăng trong database, dùng để liên kết các bảng. Mã NV (ma_nv) là mã định danh do con người tạo (VD: NV001, NV002), dùng để hiển thị cho user. Khi làm việc với Training hoặc Attendance, PHẢI dùng id_nv, không phải ma_nv.

**Q2: Khi nào cần set TimeZone cho SimpleDateFormat?**

A: Luôn cần set TimeZone khi format/parse date hoặc time. Vì SimpleDateFormat mặc định dùng timezone của JVM, không phải timezone của hệ thống. Dùng: sdf.setTimeZone(TimeZone.getDefault()).

**Q3: Tại sao phải reload list bằng initData() thay vì notifyDataSetChanged()?**

A: notifyDataSetChanged() chỉ refresh UI của adapter, không reload dữ liệu từ database. Nếu có thay đổi dữ liệu (thêm, sửa, xóa), cần gọi initData() để load dữ liệu mới từ DB.

**Q4: Spinner select position sai là do gì?**

A: Thường là vì adapter chưa ready hoặc data không match. Luôn load employee list TRƯỚC, setup adapter, RỒI mới setSelection(). Khi setSelection(), phải so sánh đúng loại dữ liệu (ID vs Code vs Name).

**Q5: Dark mode toggle không có hiệu lực là sao?**

A: Android không hỗ trợ dynamic theme change. Sau khi set AppCompatDelegate.setDefaultNightMode(), phải restart activity bằng startActivity(new Intent) + finish().

**Q6: Check-in lưu sai ngày là do gì?**

A: Nếu lấy date từ user input (DatePickerDialog), user có thể select ngày khác hôm nay. Solution: Lấy date hôm nay bằng new Date(), format thành yyyy-MM-dd, dùng dó.

**Q7: Làm sao để so sánh giờ check-in với work shift?**

A: Cả hai phải format chuẩn "HH:mm:ss" rồi dùng compareTo(). VD: "09:15:00".compareTo("08:00:00") > 0 = Trễ.

**Q8: Avatar load từ drawable như thế nào?**

A: Lưu tên file trong DB (VD: avatar_001.png). Lúc load: getResources().getIdentifier(filename, "drawable", packageName) để lấy resource ID, rồi setImageResource(resId).

**Q9: Training thay đổi employee khi edit được không?**

A: KHÔNG. Employee spinner PHẢI disabled trong edit mode vì violation foreign key. Chỉ có thể sửa course name, trainer, dates, và status.

**Q10: Sao spinner không hiển thị employee?**

A: Thường là vì:
1. Employee list chưa load (gọi EmployeeDAO.getAllEmployees())
2. Adapter chưa setup trước hiển thị dialog
3. EmployeeDAO return null hoặc empty list

**Q11: SharedPreferences key phải match ở đâu?**

A: "SESSION" là key dùng chung cho tất cả activity (LoginActivity, HomeActivity, SettingActivity). Nếu dùng key khác sẽ không đọc được data lẫn nhau.

**Q12: Làm sao biết check-in có thành công không?**

A: Gọi markAttendance(), nó return long (row ID nếu success, -1 nếu fail). Check result, nếu > 0 thì thành công. Sau đó call loadLatestData() để update UI.

**Q13: Tại sao phải validate dữ liệu trước save?**

A: Vì database có NOT NULL constraints và unique constraints. Nếu dữ liệu sai, insert/update sẽ fail. Validate client-side vừa cho user feedback ngay, vừa tránh query vô ích.

**Q14: Date format trong database là gì?**

A: Format chuẩn là "yyyy-MM-dd" cho date (VD: 2026-04-17) và "HH:mm:ss" cho time (VD: 09:15:30). Display format thì khác, lấy từ prefs user setting.

**Q15: Làm sao đảm bảo data consistency khi edit?**

A: Dùng transaction (db.beginTransaction() ... db.setTransactionSuccessful()). Nếu có lỗi giữa chừng, toàn bộ transaction rollback, không bị dữ liệu nửa vời.

**Q16: Khi nào cần disable EditText/Spinner?**

A: Disable khi field không được phép sửa, VD:
- Mã NV trong EmployeeActivity edit mode
- Employee spinner trong TrainingActivity edit mode
- Calculated fields như tongLuong trong SalaryActivity
Lưu ý: cần set setFocusable(false) + setKeyListener(null) để hoàn toàn read-only.

**Q17: DAO return type nên là gì?**

A: Insert/Update/Delete → boolean (true/false). Select → List<Object> hoặc Object. Transaction operation nên return boolean để dễ check success/fail.

**Q18: Adapter callback (listener) làm gì?**

A: Để item trong list có thể communicate với Activity. VD: Click edit button → callback onEdit() → Activity hiển thị dialog sửa. Không callback thì item chỉ hiển thị, không interactive.

**Q19: AlertDialog.Builder dùng để làm gì?**

A: Tạo dialog (popup window). Thường dùng để:
- Add/Edit form (inflate layout)
- Confirm dialog trước delete
- Select dialog (radio/checkbox)
- Input dialog (EditText)

**Q20: initData() gọi từ đâu?**

A: Gọi từ onCreate() để load dữ liệu lần đầu. Sau đó, gọi lại trong các callback (Save success, Delete success, v.v) để reload list.

---

## Quick Reference - Công Thức Nhanh

**Set TimeZone cho date format:**
```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault());
String dateStr = sdf.format(new Date());
```

**Get current date (hôm nay):**
```java
String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
```

**Extract ID từ spinner "idNv - maNv - hoTen":**
```java
String displayText = spinner.getSelectedItem().toString();
int id = Integer.parseInt(displayText.split(" - ")[0]);
```

**Set spinner selection bằng ID:**
```java
for (int i = 0; i < spinner.getCount(); i++) {
    Employee e = (Employee) spinner.getItemAtPosition(i);
    if (String.valueOf(e.getIdNv()).equals(idString)) {
        spinner.setSelection(i);
        break;
    }
}
```

**Load drawable resource từ name:**
```java
int resId = getResources().getIdentifier(filename, "drawable", getPackageName());
imageView.setImageResource(resId);
```

**Get SharedPreferences:**
```java
SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
String value = prefs.getString("key", "default");
```

**Setup DAO transaction:**
```java
SQLiteDatabase db = dbHelper.getWritableDatabase();
db.beginTransaction();
try {
    // Insert/Update logic
    db.setTransactionSuccessful();
} catch (Exception e) {
    return false;
} finally {
    db.endTransaction();
}
```

**Disable EditText/Spinner (read-only):**
```java
editText.setEnabled(false);
editText.setFocusable(false);
editText.setKeyListener(null);
```

**Format TimePickerDialog output:**
```java
String time = String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute);
```

**Reload RecyclerView data:**
```java
List<Xxx> newList = dao.getAll();
adapter.updateList(newList);
adapter.notifyDataSetChanged();
```

**Setup AlertDialog with callback:**
```java
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("Title");
builder.setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
    // Handle selection
    dialog.dismiss();
});
builder.show();
```

**Compare time for check-in:**
```java
String gioVao = "09:15:00";
String workShift = "08:00:00";
int compare = gioVao.compareTo(workShift); // > 0 = late
```

---

## Flow Diagram Summary

```
LOGIN FLOW:
  LoginActivity
    ├─ Check isLogin in prefs
    ├─ Check isBiometric
    ├─ Validate username/password
    ├─ Save to SharedPreferences
    └─ → HomeActivity

HOME FLOW:
  HomeActivity
    ├─ Load stats (DAO queries)
    ├─ Setup drawer navigation
    ├─ User click menu
    └─ → EmployeeActivity/AttendanceActivity/etc

EMPLOYEE FLOW:
  EmployeeActivity
    ├─ Load all employees
    ├─ Display in RecyclerView
    ├─ User click Edit/Delete
    ├─ Dialog inflate & fill
    ├─ Validate & save
    └─ Reload list

ATTENDANCE FLOW:
  AttendanceActivity
    ├─ Load today employees
    ├─ Show list with check-in time
    ├─ User click "Check-in"
    ├─ TimePickerDialog
    ├─ Calculate status (on-time/late)
    ├─ Save with current date (not user-selected)
    └─ Update UI

SETTING FLOW:
  SettingActivity
    ├─ Load all settings
    ├─ User change setting
    ├─ Save to SharedPreferences
    ├─ Notify DateUtils/etc
    └─ Reload data if needed
```

---

## Quick Reference - Công Thức Nhanh

**Set TimeZone cho date format:**
```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault());
String dateStr = sdf.format(new Date());
```

**Get current date (hôm nay):**
```java
String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
```

**Extract ID từ spinner "idNv - maNv - hoTen":**
```java
String displayText = spinner.getSelectedItem().toString();
int id = Integer.parseInt(displayText.split(" - ")[0]);
```

**Set spinner selection bằng ID:**
```java
for (int i = 0; i < spinner.getCount(); i++) {
    Employee e = (Employee) spinner.getItemAtPosition(i);
    if (String.valueOf(e.getIdNv()).equals(idString)) {
        spinner.setSelection(i);
        break;
    }
}
```

**Load drawable resource từ name:**
```java
int resId = getResources().getIdentifier(filename, "drawable", getPackageName());
imageView.setImageResource(resId);
```

**Get SharedPreferences:**
```java
SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
String value = prefs.getString("key", "default");
```

**Setup DAO transaction:**
```java
SQLiteDatabase db = dbHelper.getWritableDatabase();
db.beginTransaction();
try {
    // Insert/Update logic
    db.setTransactionSuccessful();
} catch (Exception e) {
    return false;
} finally {
    db.endTransaction();
}
```

**Disable EditText/Spinner (read-only):**
```java
editText.setEnabled(false);
editText.setFocusable(false);
editText.setKeyListener(null);
```

**Format TimePickerDialog output:**
```java
String time = String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute);
```

**Reload RecyclerView data:**
```java
List<Xxx> newList = dao.getAll();
adapter.updateList(newList);
adapter.notifyDataSetChanged();
```

**Setup AlertDialog with callback:**
```java
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("Title");
builder.setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
    // Handle selection
    dialog.dismiss();
});
builder.show();
```

**Compare time for check-in:**
```java
String gioVao = "09:15:00";
String workShift = "08:00:00";
int compare = gioVao.compareTo(workShift); // > 0 = late
```

---

## 📚 Key Takeaways

1. **Luôn set TimeZone** khi format date/time
2. **Employee ID = id_nv (INT)**, không phải ma_nv
3. **Current date = hôm nay**, không lấy từ user
4. **Spinner setup** trước load data
5. **Dialog add/edit** dùng chung layout
6. **Dark mode** cần restart activity
7. **Avatar** từ drawable resources, không direct path
8. **Unique constraints** (id_nv, ngay_cc) cho attendance
9. **Work shift** từ prefs, so sánh string format
10. **Training** dùng foreign key với employee ID

---

**Version**: 1.0 | **Date**: 2026-04-17 | **Status**: Complete

