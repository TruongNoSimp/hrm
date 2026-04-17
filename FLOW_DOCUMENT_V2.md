# Tài Liệu Luồng Chạy & Lý Thuyết HRM Application

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

## PHẦN I: LÝ THUYẾT CORE ELEMENTS & LIBRARIES

### 1. Intent - Nắm Qua Activity

**Định nghĩa:**
- Intent là một bộ chỉ dẫn để thực hiện một hành động
- Dùng để khởi động Activity, Service, hoặc gửi Broadcast
- Có thể truyền dữ liệu giữa các Activity

**Explicit Intent (Intent rõ ràng):**
- Chỉ định chính xác Activity nào cần khởi động
```java
Intent intent = new Intent(CurrentActivity.this, TargetActivity.class);
intent.putExtra("key", value); // Truyền dữ liệu
startActivity(intent);
```

**Implicit Intent (Intent ẩn):**
- Không chỉ định Activity cụ thể, hệ thống tìm component phù hợp
```java
Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"));
startActivity(intent);
```

**Vòng đời Intent:**
- startActivity() → Target Activity onCreate() → onStart() → onResume()
- Khi finish() được gọi → onPause() → onStop() → onDestroy()

**Trong HRM:**
- `HomeActivity` dùng Intent để mở các Activity khác:
  ```java
  Intent intent = new Intent(this, EmployeeActivity.class);
  startActivity(intent);
  ```

---

### 2. AppCompatActivity - Activity Tương Thích

**Định nghĩa:**
- AppCompatActivity là base class cho Activity trong Android
- Hỗ trợ Action Bar, Material Design, Fragments
- Duy trì compatibility với các phiên bản Android cũ

**Extends AppCompatActivity:**
```java
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
```

**Lifecycle Methods:**
- `onCreate()` - Khởi tạo, load layout, khởi tạo biến
- `onStart()` - Activity trở nên visible
- `onResume()` - Activity lấy focus, sẵn sàng nhận input
- `onPause()` - Activity mất focus (user bấm back, mở app khác)
- `onStop()` - Activity không còn visible
- `onDestroy()` - Activity bị destroy, giải phóng resource

**Trong HRM:**
- Tất cả 8 Activity (LoginActivity, HomeActivity, EmployeeActivity, v.v) đều extend AppCompatActivity
- Trong onCreate(), luôn gọi initViews() → initData() → initActions() → setupToolbar()

---

### 3. Toolbar - Header Bar

**Định nghĩa:**
- Toolbar là phần header của Activity
- Dùng để hiển thị tiêu đề, back button, menu, v.v
- Thay thế ActionBar (cũ hơn, kém linh hoạt)

**Setup Toolbar:**
```java
private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện back arrow
    }
    toolbar.setNavigationOnClickListener(v -> finish()); // Back button listener
}
```

**Trong HRM:**
- Tất cả Activity (trừ LoginActivity, HomeActivity) có Toolbar
- Back arrow quay lại Activity trước bằng finish()
- Title hiển thị tên screen (EmployeeActivity, TrainingActivity, v.v)

---

### 4. RecyclerView & Adapter - Hiển Thị List

**RecyclerView:**
- Component để hiển thị danh sách dữ liệu lớn
- Tái sử dụng View (recycle) → hiệu năng tốt
- Cần LayoutManager + Adapter để hoạt động

**Setup RecyclerView:**
```java
rvEmployees.setLayoutManager(new LinearLayoutManager(this)); // Linear hoặc Grid
rvEmployees.setAdapter(adapter); // Gán adapter
```

**Adapter Pattern:**
- Adapter là cầu nối giữa Data (List<Object>) và RecyclerView
- Có 3 hàm chính:
  - `onCreateViewHolder()` - Tạo item view (gọi 1 lần per scroll)
  - `onBindViewHolder()` - Bind dữ liệu vào view (gọi mỗi khi scroll)
  - `getItemCount()` - Return số lượng item

**Adapter trong HRM:**
```java
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> {
    private List<Employee> list;
    private OnItemActionListener listener;
    
    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Employee emp = list.get(position);
        holder.tvName.setText(emp.getHoTen());
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(emp));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(emp));
    }
    
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
```

**ViewHolder Pattern:**
- Cache các view references để tránh findViewById() lặp lại
- Bảo vệ reference từ mọi bộ nhớ leak

**Trong HRM:**
- 8 Adapter: EmployeeAdapter, AttendanceAdapter, SalaryAdapter, TrainingAdapter, v.v
- Mỗi adapter có callback (listener) để handle item click (Edit, Delete)

---

### 5. AlertDialog & Dialog - Popup Windows

**Định nghĩa:**
- Dialog là cửa sổ popup tạm thời
- AlertDialog.Builder là helper để tạo dialog dễ dàng

**Loại Dialog:**
- Single choice (radio button): Select 1 item từ list
- Multiple choice (checkbox): Select nhiều items
- Edit form dialog: Chứa EditText, Spinner, Button
- Confirm dialog: Xác nhận trước delete

**Dialog trong HRM:**
```java
// Add/Edit Employee dialog
AlertDialog.Builder builder = new AlertDialog.Builder(this);
View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_employee, null);
builder.setView(dialogView);

EditText edtName = dialogView.findViewById(R.id.edtName);
Button btnSave = dialogView.findViewById(R.id.btnSave);

AlertDialog dialog = builder.create();

btnSave.setOnClickListener(v -> {
    if (employeeDAO.insertEmployee(...)) {
        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        initData(); // Reload list
    }
});

dialog.show();
```

---

### 6. DatePickerDialog & TimePickerDialog - Chọn Ngày/Giờ

**DatePickerDialog:**
- Popup chọn ngày
- Return: year, month, day

**TimePickerDialog:**
- Popup chọn giờ
- Return: hour, minute

**Trong HRM:**
```java
private void showDatePicker(EditText editText) {
    Calendar calendar = Calendar.getInstance();
    
    DatePickerDialog datePickerDialog = new DatePickerDialog(
        this,
        (view, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            editText.setText(date);
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    );
    datePickerDialog.show();
}
```

**Lưu ý:** month từ DatePickerDialog là 0-indexed (0=Jan, 1=Feb)

---

### 7. SharedPreferences - Lưu Dữ Liệu Nhỏ

**Định nghĩa:**
- Key-value store để lưu dữ liệu nhỏ (string, int, boolean)
- Lưu trong file XML
- Không nên dùng cho dữ liệu lớn (dùng Database)

**Get SharedPreferences:**
```java
SharedPreferences prefs = getSharedPreferences("SESSION", Context.MODE_PRIVATE);
String username = prefs.getString("username", "default");
boolean isLogin = prefs.getBoolean("isLogin", false);
```

**Save vào SharedPreferences:**
```java
SharedPreferences.Editor editor = prefs.edit();
editor.putString("username", "admin");
editor.putBoolean("isLogin", true);
editor.apply(); // Async save, hoặc commit() sync save
```

**Trong HRM:**
- Key "SESSION" dùng chung cho tất cả Activity
- Keys: isLogin, remember, isBiometricEnabled, username, adminname, isDarkMode, work_shift, date_format, time_format

---

### 8. DAO (Data Access Object) - Layer Truy Cập Database

**Định nghĩa:**
- DAO là pattern để tách logic database ra khỏi Activity
- Mỗi Entity (Employee, Attendance, v.v) có 1 DAO riêng

**DAO Methods:**
- `getAll()` → List<Object>
- `getById(id)` → Object
- `insert(object)` → boolean
- `update(object)` → boolean
- `delete(id)` → boolean

**DAO trong HRM:**
```java
public class EmployeeDAO {
    private DBHelper dbHelper;
    
    public EmployeeDAO(Context context) {
        dbHelper = new DBHelper(context);
    }
    
    public List<Employee> getAllEmployees() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NHANVIEN, null, null, null, null, null, null);
        List<Employee> list = new ArrayList<>();
        
        if (cursor.moveToFirst()) {
            do {
                Employee emp = new Employee();
                emp.setIdNv(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_ID_NV)));
                emp.setMaNv(cursor.getString(cursor.getColumnIndex(DBHelper.COL_MA_NV)));
                emp.setHoTen(cursor.getString(cursor.getColumnIndex(DBHelper.COL_HO_TEN)));
                list.add(emp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    
    public boolean insertEmployee(Employee emp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_MA_NV, emp.getMaNv());
        values.put(DBHelper.COL_HO_TEN, emp.getHoTen());
        // ... put other fields
        
        long result = db.insert(DBHelper.TABLE_NHANVIEN, null, values);
        return result != -1;
    }
}
```

**Transaction trong DAO:**
- Dùng khi insert/update/delete liên quan (phải tất cả thành công hoặc tất cả fail)
```java
public boolean updateTraining(Training training) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.beginTransaction();
    try {
        // Update course table
        db.update(TABLE_KHOAHOC, ...);
        // Update training detail table
        db.update(TABLE_CHITIET_DAOTAO, ...);
        db.setTransactionSuccessful();
        return true;
    } catch (Exception e) {
        return false;
    } finally {
        db.endTransaction();
    }
}
```

---

### 9. SQLiteDatabase & Cursor - Database Operations

**Database Operations:**
- `getReadableDatabase()` - Open để read (có thể block nếu write)
- `getWritableDatabase()` - Open để write (tạo file nếu chưa tồn tại)

**Query Operations:**
- `query()` - SELECT với nhiều options
- `rawQuery()` - SELECT với SQL string (nguy hiểm SQL injection)
- `insert()` - INSERT với ContentValues
- `update()` - UPDATE
- `delete()` - DELETE

**Cursor:**
- Kết quả của query
- Cần moveToFirst() / moveToNext() để duyệt
- Phải close() sau khi dùng

**Trong HRM:**
```java
Cursor cursor = db.query(
    DBHelper.TABLE_NHANVIEN,  // table
    null,                      // columns (null = all)
    DBHelper.COL_ID_NV + "=?", // where clause
    new String[]{String.valueOf(id)}, // where args
    null,                      // group by
    null,                      // having
    null                       // order by
);

if (cursor.moveToFirst()) {
    int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_ID_NV));
    String name = cursor.getString(cursor.getColumnIndex(DBHelper.COL_HO_TEN));
}
cursor.close();
```

---

### 10. ContentValues - Insert/Update Helper

**Định nghĩa:**
- Helper class để tạo key-value pairs cho insert/update
- Thay vì SQL string (dễ lỗi, SQL injection)

**Cú pháp:**
```java
ContentValues values = new ContentValues();
values.put("column_name", "value");
values.put("column_int", 123);
values.put("column_bool", true);

db.insert(TABLE_NAME, null, values); // Insert
db.update(TABLE_NAME, values, "id=?", new String[]{id}); // Update
```

**Trong HRM:**
```java
ContentValues values = new ContentValues();
values.put(DBHelper.COL_HO_TEN, "Nguyễn Văn A");
values.put(DBHelper.COL_NGAY_SINH, "1990-05-15");
values.put(DBHelper.COL_SO_DT, "0912345678");

db.insert(DBHelper.TABLE_NHANVIEN, null, values);
```

---

### 11. BiometricPrompt - Xác Thực Vân Tay

**Định nghĩa:**
- BiometricPrompt hiển thị dialog xác thực vân tay
- Hỗ trợ fingerprint, face recognition, iris

**Setup BiometricPrompt:**
```java
BiometricPrompt biometricPrompt = new BiometricPrompt(
    this,
    ContextCompat.getMainExecutor(this),
    new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            // Xác thực thành công
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
        }
    }
);

BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
    .setTitle("Xác thực vân tay")
    .setSubtitle("Dùng vân tay để đăng nhập")
    .setNegativeButtonText("Nhập mật khẩu")
    .build();

biometricPrompt.authenticate(promptInfo);
```

**Trong HRM:**
- LoginActivity có option đăng nhập bằng vân tay
- Kiểm tra BiometricManager.canAuthenticate() trước

---

### 12. Fragment & Navigation Drawer - Menu Sidebar

**Navigation Drawer:**
- Sidebar menu dùng trong material design
- Kéo từ left edge để hiện

**Setup Navigation Drawer:**
```java
// Layout: activity_home.xml
<DrawerLayout>
    <LinearLayout /> <!-- Main content -->
    <NavigationView
        android:id="@+id/navigation_view"
        app:menu="@menu/navigation_menu" />
</DrawerLayout>

// Activity
DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
NavigationView navigationView = findViewById(R.id.navigation_view);

ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
    this, drawerLayout, toolbar,
    R.string.open_drawer, R.string.close_drawer
);
drawerLayout.addDrawerListener(toggle);
toggle.syncState();

navigationView.setNavigationItemSelectedListener(item -> {
    int id = item.getItemId();
    if (id == R.id.nav_employee) {
        startActivity(new Intent(this, EmployeeActivity.class));
    }
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
});
```

**Trong HRM:**
- HomeActivity có NavigationView với 8 menu items
- Click item → open tương ứng Activity

---

### 13. LayoutInflater - Tạo View Từ XML

**Định nghĩa:**
- LayoutInflater convert file XML layout thành View objects
- Dùng trong Dialog, Adapter, Fragment, v.v

**Cách sử dụng:**
```java
// Cách 1: Từ Activity
View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_employee, null);

// Cách 2: Từ Context
LayoutInflater inflater = LayoutInflater.from(context);
View view = inflater.inflate(R.layout.item_employee, container, false);

// Cách 3: Từ parent ViewGroup
View view = LayoutInflater.from(context).inflate(
    R.layout.item_employee, 
    parent, // parent ViewGroup
    false   // attachToParent
);
```

**Trong HRM:**
- Dialog inflate: `getLayoutInflater().inflate(R.layout.dialog_add_employee, null)`
- Adapter item inflate: `LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false)`
- Thường return View sau đó findViewById() để liên kết components

---

### 14. View Binding - Liên Kết UI Dễ Dàng

**Định nghĩa:**
- View Binding là cách safe để access view mà không cần findViewById()
- Generate class tự động từ XML layout
- Giảm null reference errors

**Setup View Binding:**
```java
// Bước 1: Enable trong build.gradle
android {
    ...
    buildFeatures {
        viewBinding true
    }
}

// Bước 2: Dùng trong Activity
private ActivityLoginBinding binding;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityLoginBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    // Access view: binding.edtUsername.setText(...);
    binding.edtUsername.setText("username");
    binding.btnLogin.setOnClickListener(v -> handleLogin());
}

// Bước 3: Dùng trong Dialog
private void showDialog() {
    DialogAddEmployeeBinding dialogBinding = DialogAddEmployeeBinding.inflate(
        getLayoutInflater(), 
        null, 
        false
    );
    View dialogView = dialogBinding.getRoot();
    
    dialogBinding.edtName.setText(...);
    dialogBinding.btnSave.setOnClickListener(v -> {...});
}
```

**Trong HRM:**
- Có thể dùng View Binding thay cho findViewById()
- Safer: compile-time check thay vì runtime
- Hiệu suất tốt hơn

---

### 15. Listener & Callback - Event Handling

**Định nghĩa:**
- Listener là interface để handle user interactions
- Callback pattern: Activity implement listener → pass vào Adapter

**Listener trong HRM:**
```java
// Tạo interface (OnItemActionListener.java)
public interface OnItemActionListener<T> {
    void onEdit(T object);
    void onDelete(T object);
    void onItemClick(T object);
}

// Activity implement
public class EmployeeActivity extends AppCompatActivity implements OnItemActionListener<Employee> {
    @Override
    public void onEdit(Employee employee) {
        showEditDialog(employee);
    }
    
    @Override
    public void onDelete(Employee employee) {
        confirmDelete(employee);
    }
    
    @Override
    public void onItemClick(Employee employee) {
        // View detail
    }
}

// Pass vào Adapter
adapter = new EmployeeAdapter(this, employeeList, this); // Pass 'this'

// Adapter callback
public class EmployeeAdapter extends RecyclerView.Adapter<ViewHolder> {
    private OnItemActionListener listener;
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Employee emp = list.get(position);
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(emp)); // Call callback
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(emp));
    }
}
```

**Trong HRM:**
- 8 Activity có callback listeners (Employee, Attendance, Salary, Training, v.v)
- Adapter nhận listener trong constructor
- Click item → call listener method → Activity handle

---

### 16. TextWatcher - Xử Lý Input Real-Time

**Định nghĩa:**
- TextWatcher monitor sự thay đổi text trong EditText
- Dùng cho search, validation, auto-format

**Cách sử dụng:**
```java
EditText edtSearch = findViewById(R.id.edtSearch);

edtSearch.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Gọi trước khi text thay đổi
    }
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Gọi khi text đang thay đổi
        String query = s.toString().trim();
        filterList(query); // Real-time search
    }
    
    @Override
    public void afterTextChanged(Editable s) {
        // Gọi sau khi text thay đổi
    }
});
```

**Trong HRM:**
- EmployeeActivity search: `edtSearch.addTextChangedListener()`
- AttendanceActivity search: real-time filter nhân viên
- TrainingActivity search: filter khóa học

---

### 17. DTO & Mapper - Transfer Data Objects

**Định nghĩa:**
- DTO (Data Transfer Object): Object để transfer dữ liệu giữa các layer
- Mapper: Convert giữa DTO và Model

**DTO trong HRM:**
```java
// EmployeeAttendanceDTO.java
public class EmployeeAttendanceDTO {
    private Employee employee;
    private String gioVao;
    private int trangThaiChamCong;
    
    public EmployeeAttendanceDTO(Employee emp, String time, int status) {
        this.employee = emp;
        this.gioVao = time;
        this.trangThaiChamCong = status;
    }
    
    // Getters
    public String getEmployeeName() {
        return employee.getHoTen();
    }
    
    public String getCheckInTime() {
        return gioVao;
    }
    
    public int getStatus() {
        return trangThaiChamCong;
    }
}

// Mapper.java
public class AttendanceMapper {
    public static EmployeeAttendanceDTO fromCursor(Cursor c) {
        Employee emp = new Employee();
        emp.setIdNv(c.getInt(c.getColumnIndex("id_nv")));
        emp.setHoTen(c.getString(c.getColumnIndex("ho_ten")));
        
        String time = c.getString(c.getColumnIndex("gio_vao"));
        int status = c.getInt(c.getColumnIndex("trang_thai"));
        
        return new EmployeeAttendanceDTO(emp, time, status);
    }
}

// Dùng trong DAO
public List<EmployeeAttendanceDTO> getAllEmployeesWithAttendance() {
    List<EmployeeAttendanceDTO> list = new ArrayList<>();
    Cursor cursor = db.rawQuery("SELECT ... FROM employees LEFT JOIN attendance ...", null);
    
    if (cursor.moveToFirst()) {
        do {
            list.add(AttendanceMapper.fromCursor(cursor));
        } while (cursor.moveToNext());
    }
    cursor.close();
    return list;
}
```

**Lợi ích DTO:**
- Transfer multiple related objects
- Tránh N+1 query problem
- Lazy loading: không load toàn bộ relationships

**Trong HRM:**
- EmployeeAttendanceDTO: employee + attendance ngày hôm nay
- TrainingDTO: training + employee + status
- SalaryDTO: salary + employee info

---

### 18. SimpleDateFormat - Format Ngày/Giờ

**Định nghĩa:**
- SimpleDateFormat parse string → Date hoặc format Date → String
- Pattern: "yyyy-MM-dd", "HH:mm:ss", v.v

**Cách sử dụng:**
```java
// Parse string → Date
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault());
Date date = sdf.parse("2026-04-17"); // String → Date

// Format Date → String
String dateStr = sdf.format(new Date()); // Date → String

// Khác format
SimpleDateFormat sdf24h = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
String time24h = sdf24h.format(new Date()); // "09:15:30"

SimpleDateFormat sdf12h = new SimpleDateFormat("hh:mm a", Locale.getDefault());
String time12h = sdf12h.format(new Date()); // "09:15 AM"
```

**QUAN TRỌNG: Luôn set TimeZone**
```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
sdf.setTimeZone(TimeZone.getDefault()); // Bắt buộc!
String date = sdf.format(new Date());
```

**Trong HRM:**
- DB lưu: "yyyy-MM-dd" & "HH:mm:ss"
- Display: tuỳ user setting (date_format, time_format prefs)
- DateUtils helper để convert

---

### 19. Toast & Log - Feedback & Debug

**Toast:**
- Hiển thị message ngắn cho user
- Self-dismiss sau timeout

```java
Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show(); // 2s
Toast.makeText(context, "Message", Toast.LENGTH_LONG).show(); // 3.5s
```

**Log:**
- Debug tool, không hiển thị cho user
- Mở Logcat để xem

```java
Log.d("TAG", "Debug message");
Log.i("TAG", "Info message");
Log.w("TAG", "Warning");
Log.e("TAG", "Error");
```

**Trong HRM:**
- Success: Toast "Thêm thành công"
- Error: Toast "Lỗi: ..." + Log.e()

---

### 20. Transaction & Cursor Management - Database Safety

**Transaction:**
- Atomic: tất cả thành công hoặc tất cả fail
- Avoid data inconsistency

```java
SQLiteDatabase db = dbHelper.getWritableDatabase();
db.beginTransaction();
try {
    // Multiple operations
    db.insert(TABLE_1, null, values1);
    db.update(TABLE_2, values2, ...);
    db.setTransactionSuccessful(); // Commit
    return true;
} catch (Exception e) {
    return false; // Rollback
} finally {
    db.endTransaction();
}
```

**Cursor Management:**
- Always close() sau dùng
- Avoid memory leak

```java
Cursor cursor = db.query(...);
try {
    if (cursor.moveToFirst()) {
        do {
            // Process
        } while (cursor.moveToNext());
    }
} finally {
    cursor.close(); // Bắt buộc
}
```

**Trong HRM:**
- Training insert: update 2 tables (KhoaHoc + ChiTietDaoTao) → transaction
- DAO methods wrap Cursor trong try-finally

---



### 1. LoginActivity - Chi Tiết Từng Hàm

**onCreate() - Khởi Tạo Activity**

Tác vụ: Khởi tạo LoginActivity khi app mở

Bước chi tiết:
1. Gọi super.onCreate(savedInstanceState) - Khôi phục trạng thái trước (nếu có)
2. setContentView(R.layout.activity_login) - Load layout XML
3. Gọi initViews() - Liên kết UI components
4. Lấy SharedPreferences("SESSION") - Kiểm tra trạng thái login
5. Kiểm tra isLogin:
   - Nếu true: startActivity(HomeActivity) → finish()
   - Nếu false: Hiển thị form login
6. Kiểm tra remember checkbox:
   - Nếu true: Load username từ prefs
   - Nếu false: Clear EditText
7. Kiểm tra isBiometricEnabled:
   - Nếu true + remember = true: Gọi checkBiometric()
8. setOnClickListener cho btnLogin → handleLogin()
9. setOnClickListener cho imgFingerprint → checkBiometric()

Công thức code:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    // 1. Liên kết views
    initViews();
    
    // 2. Lấy SharedPreferences
    SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
    boolean isLogin = prefs.getBoolean("isLogin", false);
    boolean isRemember = prefs.getBoolean("remember", false);
    boolean isBiometric = prefs.getBoolean("isBiometricEnabled", false);
    
    // 3. Kiểm tra login
    if (isLogin) {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        return;
    }
    
    // 4. Load remember
    if (isRemember) {
        edtUsername.setText(prefs.getString("username", ""));
        chkRemember.setChecked(true);
    }
    
    // 5. Auto login with biometric
    if (isRemember && isBiometric) {
        checkBiometric();
    }
    
    // 6. Button listeners
    btnLogin.setOnClickListener(v -> handleLogin());
    imgFingerprint.setOnClickListener(v -> {
        if (isBiometric) {
            checkBiometric();
        } else {
            Toast.makeText(this, "Vân tay chưa kích hoạt", Toast.LENGTH_SHORT).show();
        }
    });
}
```

**handleLogin() - Xử Lý Đăng Nhập**

Tác vụ: Kiểm tra username/password, xác thực, save session

Bước chi tiết:
1. Lấy username từ edtUsername.getText()
2. Lấy password từ edtPassword.getText()
3. Trim whitespace: .toString().trim()
4. Kiểm tra empty:
   - Nếu empty: Toast + return false
   - Nếu valid: Continue
5. Gọi accountDAO.checkLogin(username, password)
   - DAO return Account object nếu match, null nếu sai
6. Kiểm tra result:
   - Nếu != null (match):
     a. Lấy adminName từ result
     b. Lấy remember checkbox status
     c. Save prefs: isLogin=true, username, adminname
     d. Nếu remember: save username
     e. Toast "Chào mừng [adminName]"
     f. startActivity(HomeActivity) → finish()
   - Nếu null (sai):
     a. Toast "Sai tài khoản hoặc mật khẩu"
     b. Clear password
     c. Request focus edtPassword

Công thức code:
```java
private void handleLogin() {
    String username = edtUsername.getText().toString().trim();
    String password = edtPassword.getText().toString().trim();
    
    // Validate
    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
        Toast.makeText(this, "Nhập đầy đủ username và password", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // Query DAO
    Account account = accountDAO.checkLogin(username, password);
    
    if (account != null) {
        // Save session
        SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLogin", true);
        editor.putString("username", username);
        editor.putString("adminname", account.getAdminName());
        
        if (chkRemember.isChecked()) {
            editor.putBoolean("remember", true);
        }
        editor.apply();
        
        Toast.makeText(this, "Chào mừng " + account.getAdminName(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    } else {
        Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        edtPassword.setText("");
        edtPassword.requestFocus();
    }
}
```

**checkBiometric() - Kiểm Tra Biometric Support**

Tác vụ: Kiểm tra device có hỗ trợ biometric không

Bước chi tiết:
1. Tạo BiometricManager instance
2. Gọi canAuthenticate():
   - Trả về BIOMETRIC_SUCCESS (hỗ trợ) hoặc lỗi
3. Kiểm tra result:
   - Nếu BIOMETRIC_SUCCESS: Gọi showBiometricPrompt()
   - Nếu fail: Toast "Device không hỗ trợ vân tay"

Công thức code:
```java
private void checkBiometric() {
    BiometricManager biometricManager = BiometricManager.from(this);
    int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
                         | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
    
    if (biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
        showBiometricPrompt();
    } else {
        Toast.makeText(this, "Vân tay không khả dụng", Toast.LENGTH_SHORT).show();
    }
}
```

**showBiometricPrompt() - Hiển Thị Vân Tay Dialog**

Tác vụ: Hiển thị dialog xác thực vân tay

Bước chi tiết:
1. Tạo BiometricPrompt.AuthenticationCallback():
   - onAuthenticationSucceeded(): Xác thực thành công
     a. startActivity(HomeActivity) → finish()
   - onAuthenticationError(code, msg): Xác thực fail
     a. Toast lỗi
   - onAuthenticationFailed(): User scan sai
     a. Toast "Scan lại"
2. Tạo BiometricPrompt instance
3. Tạo PromptInfo (title, subtitle, negative button)
4. Gọi authenticate(promptInfo)

Công thức code:
```java
private void showBiometricPrompt() {
    BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(LoginActivity.this, "Scan lại vân tay", Toast.LENGTH_SHORT).show();
        }
    };
    
    BiometricPrompt prompt = new BiometricPrompt(
        this,
        ContextCompat.getMainExecutor(this),
        callback
    );
    
    BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
        .setTitle("Xác thực vân tay")
        .setSubtitle("Quét vân tay để đăng nhập")
        .setNegativeButtonText("Hủy")
        .build();
    
    prompt.authenticate(info);
}
```

---

### 2. HomeActivity - Chi Tiết Từng Hàm

**onCreate() - Khởi Tạo Dashboard**

Tác vụ: Khởi tạo màn hình chính, load stats, setup drawer

Bước chi tiết:
1. setContentView(R.layout.activity_home)
2. initViews() - Liên kết tất cả UI
3. setupToolbarAndDrawer() - Setup toolbar + navigation drawer
4. setupAccountFooter() - Hiển thị thông tin user + logout button
5. loadDashboardData() - Load stats từ DB
6. setupClickEvents() - Setup click listener cho menus
7. Setup back button behavior (OnBackPressedDispatcher)

Công thức code:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    
    initViews();
    setupToolbarAndDrawer();
    setupAccountFooter();
    loadDashboardData();
    setupClickEvents();
    
    // Back button handler
    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        }
    });
}
```

**loadDashboardData() - Load Thống Kê**

Tác vụ: Load dữ liệu stats từ database, hiển thị trên dashboard

Bước chi tiết:
1. Tạo các DAO instances
2. Gọi DAO methods để lấy dữ liệu:
   - departmentDAO.getAllDepartments().size()
   - employeeDAO.getAllEmployees().size()
   - employeeDAO.getWorkingEmployees().size()
   - attendanceDAO.getNotCheckedInToday().size()
   - trainingDAO.getOngoingTrainings().size()
3. Set text cho các TextView:
   - tvTotalDepartments.setText(String.valueOf(count))
   - tvTotalEmployees.setText(...)
   - v.v

Công thức code:
```java
private void loadDashboardData() {
    // Tạo DAO instances
    EmployeeDAO employeeDAO = new EmployeeDAO(this);
    DepartmentDAO departmentDAO = new DepartmentDAO(this);
    AttendanceDAO attendanceDAO = new AttendanceDAO(this);
    TrainingDAO trainingDAO = new TrainingDAO(this);
    
    // Load data
    int totalDepts = departmentDAO.getAllDepartments().size();
    int totalEmps = employeeDAO.getAllEmployees().size();
    int workingEmps = employeeDAO.getWorkingEmployees().size();
    int notCheckedIn = attendanceDAO.getNotCheckedInToday().size();
    int ongoingTrainings = trainingDAO.getOngoingTrainings().size();
    
    // Set text
    tvTotalDepartments.setText(String.valueOf(totalDepts));
    tvTotalEmployees.setText(String.valueOf(totalEmps));
    tvHomeWorking.setText(String.valueOf(workingEmps));
    tvHomeNotCheckin.setText(String.valueOf(notCheckedIn));
    tvHomeTraining.setText(String.valueOf(ongoingTrainings));
}
```

**setupClickEvents() - Setup Menu Click Listeners**

Tác vụ: Setup click listener cho 8 menu items + cards

Bước chi tiết:
1. Card clicks:
   - cardDepartments → openDepartment()
   - cardEmployees → openEmployee()
2. Menu clicks (8 items):
   - menuDepartment → openDepartment()
   - menuEmployee → openEmployee()
   - menuAttendance → openAttendance()
   - menuSalary → openSalary()
   - menuReward → openReward()
   - menuDiscipline → openDiscipline()
   - menuTraining → openTraining()
   - menuSetting → openSettings()

Công thức code:
```java
private void setupClickEvents() {
    // Card clicks
    cardDepartments.setOnClickListener(v -> openDepartment());
    cardEmployees.setOnClickListener(v -> openEmployee());
    
    // Menu clicks
    menuDepartment.setOnClickListener(v -> openDepartment());
    menuEmployee.setOnClickListener(v -> openEmployee());
    menuAttendance.setOnClickListener(v -> openAttendance());
    menuSalary.setOnClickListener(v -> openSalary());
    menuReward.setOnClickListener(v -> openReward());
    menuDiscipline.setOnClickListener(v -> openDiscipline());
    menuTraining.setOnClickListener(v -> openTraining());
    menuSetting.setOnClickListener(v -> openSettings());
}

private void openEmployee() {
    startActivity(new Intent(this, EmployeeActivity.class));
}

private void openDepartment() {
    startActivity(new Intent(this, DepartmentActivity.class));
}

// ... Similar for other methods
```

---

### 3. EmployeeActivity - Chi Tiết Từng Hàm

**initData() - Load Danh Sách Nhân Viên**

Tác vụ: Load tất cả nhân viên từ DB, setup adapter, hiển thị trên RecyclerView

Bước chi tiết:
1. Tạo EmployeeDAO instance
2. Gọi employeeDAO.getAllEmployees() → List<Employee>
3. Tạo EmployeeAdapter:
   - Pass context, list, + callback listener
   - Callback onEdit() → showEditDialog()
   - Callback onDelete() → confirmDelete()
4. Gán adapter vào RecyclerView
5. Lưu list vào biến global (dùng cho search filter)

Công thức code:
```java
private void initData() {
    employeeDAO = new EmployeeDAO(this);
    employeeList = employeeDAO.getAllEmployees();
    
    adapter = new EmployeeAdapter(this, employeeList, new OnItemActionListener() {
        @Override
        public void onEdit(Employee employee) {
            showEditDialog(employee);
        }
        
        @Override
        public void onDelete(Employee employee) {
            confirmDelete(employee);
        }
    });
    
    rvEmployees.setAdapter(adapter);
}
```

**showEditDialog(Employee emp) - Hiển Thị Dialog Sửa**

Tác vụ: Hiển thị dialog chứa form sửa nhân viên

Bước chi tiết:
1. Tạo AlertDialog.Builder
2. Inflate dialog layout (dialog_add_employee.xml)
3. Liên kết các EditText từ dialog:
   - edtMaNV, edtName, edtDOB, edtPhone, edtEmail, v.v
4. Populate dữ liệu cũ:
   - edtName.setText(emp.getHoTen())
   - edtDOB.setText(emp.getNgaySinh())
   - v.v
5. Disable mã NV (không đổi được):
   - edtMaNV.setEnabled(false)
6. Setup spinner cho phòng ban:
   - Load danh sách departments
   - Select department của employee
7. Setup date picker:
   - edtDOB.setOnClickListener() → showDatePicker()
8. Setup button Save:
   - Validate dữ liệu
   - Gọi employeeDAO.updateEmployee()
   - Reload list: initData()
   - Close dialog

Công thức code:
```java
private void showEditDialog(Employee emp) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_employee, null);
    builder.setView(dialogView);
    
    EditText edtMaNV = dialogView.findViewById(R.id.edtMaNV);
    EditText edtName = dialogView.findViewById(R.id.edtName);
    EditText edtDOB = dialogView.findViewById(R.id.edtDOB);
    EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
    Button btnSave = dialogView.findViewById(R.id.btnSave);
    
    // Disable mã NV
    edtMaNV.setEnabled(false);
    
    // Populate dữ liệu cũ
    edtMaNV.setText(emp.getMaNv());
    edtName.setText(emp.getHoTen());
    edtDOB.setText(emp.getNgaySinh());
    edtPhone.setText(emp.getSoDt());
    
    // Date picker
    edtDOB.setOnClickListener(v -> showDatePicker(edtDOB));
    
    AlertDialog dialog = builder.create();
    
    btnSave.setOnClickListener(v -> {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Nhập tên");
            return;
        }
        
        emp.setHoTen(edtName.getText().toString().trim());
        emp.setNgaySinh(edtDOB.getText().toString().trim());
        emp.setSoDt(edtPhone.getText().toString().trim());
        
        if (employeeDAO.updateEmployee(emp)) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            initData();
            dialog.dismiss();
        }
    });
    
    dialog.show();
}
```

---

### 4. AttendanceActivity - Chi Tiết Từng Hàm

**markAttendance(int idNv, String gioVao) - Lưu Check-in**

Tác vụ: Lưu thời gian check-in cho nhân viên

Bước chi tiết:
1. Lấy ngày hôm nay:
   ```java
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
   sdf.setTimeZone(TimeZone.getDefault());
   String ngay = sdf.format(new Date());
   ```
2. Lấy work_shift từ prefs (default "08:00")
3. So sánh gioVao vs workShift:
   - Nếu gioVao > workShift → trangThai = 2 (Trễ)
   - Nếu gioVao <= workShift → trangThai = 1 (Đúng giờ)
4. Gọi attendanceDAO.insertAttendance(idNv, ngay, gioVao, trangThai)
   - DAO sẽ handle UNIQUE constraint (chỉ 1 check-in/ngày)
5. Nếu success:
   - Toast "Chấm công thành công"
   - Gọi loadLatestData() reload UI

Công thức code:
```java
private void markAttendance(int idNv, String gioVao) {
    // Get today's date
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    sdf.setTimeZone(TimeZone.getDefault());
    String ngay = sdf.format(new Date());
    
    // Get work shift
    SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
    String workShift = prefs.getString("work_shift", "08:00") + ":00"; // "08:00:00"
    
    // Format time: "09:15:00"
    String timeForDb = gioVao.endsWith(":00") ? gioVao : gioVao + ":00";
    
    // Compare
    int trangThai = timeForDb.compareTo(workShift) > 0 ? 2 : 1; // 2=Late, 1=OnTime
    
    // Insert to DB
    long result = attendanceDAO.markAttendance(idNv, ngay, timeForDb, trangThai);
    
    if (result > 0) {
        Toast.makeText(this, "Chấm công thành công", Toast.LENGTH_SHORT).show();
        loadLatestData();
    }
}
```

---

### 5. SalaryActivity - Chi Tiết Từng Hàm

**showDialog(SalaryDTO salary, boolean isEdit) - Dialog Thêm/Sửa Lương**

Tác vụ: Hiển thị dialog thêm/sửa bản ghi lương

Bước chi tiết:
1. Inflate dialog layout
2. Liên kết EditText:
   - edtThangNam, edtSoNgayCong, edtPhuCap, edtKhauTru, edtTongLuong
3. Liên kết Spinner:
   - spEmployee - Chọn nhân viên
4. Setup spinner:
   - Load employee list: employeeDAO.getAllEmployees()
   - Format: "idNv - maNv - hoTen"
   - Gán adapter
5. Nếu isEdit:
   - Populate dữ liệu cũ
   - Disable thángNam spinner (không đổi được)
   - Select employee hiện tại
6. Disable calculated fields:
   - edtTongLuong.setEnabled(false)
   - edtTongLuong.setFocusable(false)
7. Setup button Save:
   - Extract employee ID từ spinner
   - Validate dữ liệu
   - Gọi salaryDAO.insertSalary() hoặc updateSalary()
   - Reload list

Công thức code:
```java
private void showDialog(SalaryDTO salary, boolean isEdit) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_salary, null);
    builder.setView(dialogView);
    
    EditText edtThangNam = dialogView.findViewById(R.id.edtThangNam);
    EditText edtSoNgayCong = dialogView.findViewById(R.id.edtSoNgayCong);
    EditText edtPhuCap = dialogView.findViewById(R.id.edtPhuCap);
    EditText edtKhauTru = dialogView.findViewById(R.id.edtKhauTru);
    EditText edtTongLuong = dialogView.findViewById(R.id.edtTongLuong);
    Spinner spEmployee = dialogView.findViewById(R.id.spEmployee);
    Button btnSave = dialogView.findViewById(R.id.btnSave);
    
    // Load employees
    List<Employee> employees = employeeDAO.getAllEmployees();
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
        formatEmployeeList(employees) // "1 - NV001 - Tên"
    );
    spEmployee.setAdapter(adapter);
    
    // Read-only calculated fields
    edtTongLuong.setEnabled(false);
    edtTongLuong.setFocusable(false);
    
    // Populate nếu edit
    if (isEdit && salary != null) {
        edtThangNam.setText(salary.getThangNam());
        edtSoNgayCong.setText(String.valueOf(salary.getSoNgayCong()));
        edtPhuCap.setText(String.valueOf(salary.getPhuCapRaw()));
        edtKhauTru.setText(String.valueOf(salary.getKhauTruRaw()));
        // Select employee
        spEmployee.setSelection(findEmployeePosition(salary.getIdNv()));
        spEmployee.setEnabled(false); // Disable
    }
    
    AlertDialog dialog = builder.create();
    
    btnSave.setOnClickListener(v -> {
        // Extract employee ID
        String selectedText = spEmployee.getSelectedItem().toString();
        int employeeId = Integer.parseInt(selectedText.split(" - ")[0]);
        
        // Validate
        if (edtThangNam.getText().toString().isEmpty()) {
            edtThangNam.setError("Nhập tháng/năm");
            return;
        }
        
        // Create/Update
        SalaryDTO newSalary = new SalaryDTO();
        newSalary.setIdNv(employeeId);
        newSalary.setThangNam(edtThangNam.getText().toString());
        newSalary.setSoNgayCong(Integer.parseInt(edtSoNgayCong.getText().toString()));
        
        if (isEdit) {
            if (salaryDAO.updateSalary(newSalary)) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                initData();
                dialog.dismiss();
            }
        } else {
            if (salaryDAO.insertSalary(newSalary)) {
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                initData();
                dialog.dismiss();
            }
        }
    });
    
    dialog.show();
}
```

---

## PHẦN I.B: DESIGN PATTERNS & ADVANCED ELEMENTS

### 21. MVC/MVVM Architecture - Code Structure

**MVC (Model-View-Controller):**
- Model: Entity + DAO (Database layer)
- View: Activity + Fragment (UI layer)
- Controller: Activity (Business logic)

**HRM Structure:**
```
Models/
  ├─ Employee.java
  ├─ Attendance.java
  └─ Training.java

DAOs/
  ├─ EmployeeDAO.java
  ├─ AttendanceDAO.java
  └─ TrainingDAO.java

Activities (View + Controller)/
  ├─ LoginActivity.java
  ├─ HomeActivity.java
  └─ EmployeeActivity.java

Adapters/
  ├─ EmployeeAdapter.java
  └─ AttendanceAdapter.java

DTOs/
  ├─ EmployeeAttendanceDTO.java
  └─ TrainingDTO.java
```

**Flow:**
- Activity (Controller) → call DAO → get Model → pass to View
- User input → Activity → DAO update Model → refresh View

---

### 22. Observer Pattern (Listener/Callback)

**Giải thích:**
- Subject (Observable): Object phát emit event
- Observer: Object nhận listen event
- Decoupling: Subject không cần biết Observer

**Trong HRM:**
```java
// Observer interface
public interface OnItemActionListener<T> {
    void onEdit(T item);
    void onDelete(T item);
}

// Subject (Adapter)
public class EmployeeAdapter extends RecyclerView.Adapter {
    private OnItemActionListener listener;
    
    public EmployeeAdapter(List<Employee> list, OnItemActionListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(list.get(position)));
    }
}

// Observer (Activity)
public class EmployeeActivity extends AppCompatActivity implements OnItemActionListener {
    @Override
    public void onEdit(Employee emp) {
        showEditDialog(emp);
    }
}

// Setup
adapter = new EmployeeAdapter(list, this); // 'this' = observer
```

---

### 23. Builder Pattern - Complex Object Creation

**Giải thích:**
- Construct object step-by-step
- Readable, flexible

**AlertDialog.Builder trong HRM:**
```java
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("Add Employee");
builder.setView(dialogView);
builder.setPositiveButton("Save", (d, w) -> {...});
builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
AlertDialog dialog = builder.create(); // Build
dialog.show();
```

---

### 24. Adapter Pattern (RecyclerView)

**Giải thích:**
- Bridge giữa data source (List) và view (RecyclerView)
- 3 methods: onCreateViewHolder, onBindViewHolder, getItemCount

**Lifecycle:**
1. onCreateViewHolder(): Inflate layout (1 lần per scroll)
2. onBindViewHolder(): Bind data (mỗi scroll)
3. getItemCount(): Total items

**ViewHolder caching:**
```java
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> {
    
    // Cache reference lần đầu
    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public Button btnEdit;
        
        public EmployeeViewHolder(View itemView) {
            super(itemView);
            // Chỉ findViewById 1 lần
            tvName = itemView.findViewById(R.id.tvName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
    
    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Employee emp = list.get(position);
        // Reuse cached references
        holder.tvName.setText(emp.getHoTen());
    }
}
```

---

### 25. RecyclerView - Chi Tiết Đầy Đủ

**Định nghĩa:**
- RecyclerView là component để hiển thị danh sách dữ liệu với scroll
- "Recycle" = tái sử dụng View để tiết kiệm memory
- Cần 3 thành phần: LayoutManager, Adapter, ViewHolder

**LayoutManager:**
- Quyết định cách sắp xếp items (Linear, Grid, Staggered)
```java
// Linear (danh sách dọc)
rvEmployees.setLayoutManager(new LinearLayoutManager(this));

// Grid (lưới)
rvEmployees.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

// Staggered Grid
rvEmployees.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
```

**Adapter:**
- Bridge giữa Data (List<T>) và RecyclerView
- 3 methods bắt buộc:
  - onCreateViewHolder(): Tạo ViewHolder (1 lần per scroll)
  - onBindViewHolder(): Bind data vào view (mỗi khi scroll)
  - getItemCount(): Tổng số items

**ViewHolder:**
- Cache references của item view
- Tránh findViewById() lặp lại (chậm)
- Tránh memory leak

**Adapter Full Code:**
```java
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    private List<Employee> list;
    private Context context;
    private OnItemActionListener listener;
    
    // Constructor
    public EmployeeAdapter(Context context, List<Employee> list, OnItemActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }
    
    // 1. Tạo ViewHolder (inflate layout item)
    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
            R.layout.item_employee, // Layout file của 1 item
            parent,
            false
        );
        return new EmployeeViewHolder(itemView);
    }
    
    // 2. Bind data vào view
    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Employee emp = list.get(position);
        
        // Set dữ liệu
        holder.tvName.setText(emp.getHoTen());
        holder.tvCode.setText(emp.getMaNv());
        holder.tvDepartment.setText(emp.getPhongBan());
        
        // Load avatar
        int resId = context.getResources().getIdentifier(
            emp.getAvatar(),
            "drawable",
            context.getPackageName()
        );
        holder.imgAvatar.setImageResource(resId);
        
        // Setup button listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(emp);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(emp);
            }
        });
    }
    
    // 3. Return số lượng items
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
    
    // ViewHolder class
    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        // Cache các view references
        ImageView imgAvatar;
        TextView tvName;
        TextView tvCode;
        TextView tvDepartment;
        Button btnEdit;
        Button btnDelete;
        
        public EmployeeViewHolder(View itemView) {
            super(itemView);
            // findViewById chỉ 1 lần duy nhất
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
    
    // Method để update list (reload data)
    public void updateList(List<Employee> newList) {
        this.list = newList;
        notifyDataSetChanged(); // Thông báo adapter data thay đổi
    }
}
```

**Setup RecyclerView trong Activity:**
```java
private void initData() {
    // Load data từ DAO
    employeeDAO = new EmployeeDAO(this);
    List<Employee> employees = employeeDAO.getAllEmployees();
    
    // Setup LayoutManager
    RecyclerView rvEmployees = findViewById(R.id.rvEmployees);
    rvEmployees.setLayoutManager(new LinearLayoutManager(this));
    
    // Tạo adapter
    adapter = new EmployeeAdapter(this, employees, new OnItemActionListener<Employee>() {
        @Override
        public void onEdit(Employee employee) {
            showEditDialog(employee);
        }
        
        @Override
        public void onDelete(Employee employee) {
            confirmDelete(employee);
        }
    });
    
    // Gán adapter vào RecyclerView
    rvEmployees.setAdapter(adapter);
}

// Khi update data, gọi adapter.updateList()
private void reloadData() {
    List<Employee> newList = employeeDAO.getAllEmployees();
    adapter.updateList(newList); // Cập nhật adapter
}
```

**Lifecycle RecyclerView:**
1. onCreateViewHolder() - Tạo ViewHolder (gọi khi scroll xuất hiện item mới)
2. onBindViewHolder() - Bind dữ liệu (gọi mỗi khi scroll)
3. Khi scroll: ViewHolder cũ được "recycle" (reuse) cho dữ liệu mới
4. getItemCount() - Dùng để biết bao nhiêu item cần tạo

**Trong HRM:**
- 8 Adapter: EmployeeAdapter, AttendanceAdapter, TrainingAdapter, SalaryAdapter, v.v
- Mỗi adapter xử lý edit/delete click callback
- Update list: adapter.updateList(newList) sau khi insert/update/delete từ DB

---

### 26. Context - Android Context

**Định nghĩa:**
- Context: thông tin về environment của app
- 2 loại: Activity context, Application context

**Cách sử dụng:**
```java
// Activity context
Context context = this; // Activity

// Application context
Context context = getApplicationContext(); // Survives activity destroy

// Dùng context
Intent intent = new Intent(context, TargetActivity.class);
Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show();
SharedPreferences prefs = context.getSharedPreferences("SESSION", MODE_PRIVATE);
```

**Trong HRM:**
- Activity context: tạo Dialog, Toast, Intent
- Application context: DAO, utility, resources

---

### 30. Bundle - Pass Data Giữa Activities

**Bundle:**
- Key-value pairs để transfer dữ liệu
- Dùng với Intent hoặc SavedInstanceState

**Cách sử dụng:**
```java
// Gửi
Bundle bundle = new Bundle();
bundle.putString("name", "Nguyen Van A");
bundle.putInt("age", 25);
bundle.putSerializable("employee", employeeObject);

Intent intent = new Intent(this, DetailsActivity.class);
intent.putExtras(bundle);
startActivity(intent);

// Nhận
Bundle bundle = getIntent().getExtras();
if (bundle != null) {
    String name = bundle.getString("name");
    int age = bundle.getInt("age");
    Employee emp = (Employee) bundle.getSerializable("employee");
}
```

**Trong HRM:**
- Hiện tại không dùng Bundle (activity không pass data via intent)
- Có thể dùng khi cần view employee detail từ list

---

### 31. Parcelable & Serializable - Object Transfer

**Serializable:**
- Slower, reflection-based
- Dễ implement (implements Serializable)

```java
public class Employee implements Serializable {
    private int idNv;
    private String hoTen;
    // ...
}

// Dùng
bundle.putSerializable("employee", employee);
Employee emp = (Employee) bundle.getSerializable("employee");
```

**Parcelable:**
- Faster, bytecode generation
- Phức tạp implement nhưng efficient

```java
public class Employee implements Parcelable {
    // Auto-generate with Android Studio
}

// Dùng
bundle.putParcelable("employee", employee);
Employee emp = bundle.getParcelable("employee");
```

**Trong HRM:**
- Employee implement Serializable (dễ, hiệu suất ok)
- Bundle.putSerializable/getSerializable

---


