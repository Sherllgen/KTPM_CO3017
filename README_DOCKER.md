# Hướng Dẫn Docker - Microservices Project

## 📋 Mục Lục

- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Cài Đặt](#cài-đặt)
- [Cấu Hình](#cấu-hình)
- [Chạy Dự Án](#chạy-dự-án)
- [Kiểm Tra](#kiểm-tra)
- [Troubleshooting](#troubleshooting)
- [Các Lệnh Hữu Ích](#các-lệnh-hữu-ích)

## 🔧 Yêu Cầu Hệ Thống

### Phần Mềm Cần Thiết

1. **Docker Desktop** (Windows/Mac) hoặc **Docker Engine** (Linux)
   - Phiên bản: 20.10 trở lên
   - Download: https://www.docker.com/products/docker-desktop

2. **Docker Compose**
   - Thường đi kèm với Docker Desktop
   - Phiên bản: 2.0 trở lên

3. **Maven** (để build ứng dụng)
   - Phiên bản: 3.6 trở lên
   - Download: https://maven.apache.org/download.cgi
   - Hoặc sử dụng Maven Wrapper (nếu có)

4. **Java Development Kit (JDK)**
   - Phiên bản: Java 17
   - Download: https://adoptium.net/

### Yêu Cầu Tài Nguyên

- **RAM**: Tối thiểu 8GB (khuyến nghị 16GB)
- **Disk Space**: Tối thiểu 10GB trống
- **CPU**: Tối thiểu 2 cores

## 📁 Cấu Trúc Dự Án

```
KTPM_CO3017/
├── docker-compose.yml          # File cấu hình Docker Compose chính
├── pom.xml                      # Parent POM
│
├── discovery-service/           # Eureka Discovery Service
│   ├── dockerfile
│   ├── pom.xml
│   └── src/
│
├── api-gateway/                 # API Gateway (Spring Cloud Gateway)
│   ├── dockerfile
│   ├── .env                     # File biến môi trường
│   ├── pom.xml
│   └── src/
│
├── user-service/                # User Service
│   ├── dockerfile
│   ├── .env                     # File biến môi trường
│   ├── pom.xml
│   └── src/
│
└── subject-service/             # Subject Service
    ├── dockerfile
    ├── .env                     # File biến môi trường
    ├── pom.xml
    └── src/
```

## 🚀 Cài Đặt

### Bước 1: Clone Repository

```bash
git clone <repository-url>
cd KTPM_CO3017
```

### Bước 2: Kiểm Tra Docker

```bash
# Kiểm tra Docker đã cài đặt
docker --version
docker-compose --version
```

### Bước 3: Tạo Các File .env

Tạo các file `.env` cho từng service với nội dung sau:

#### `api-gateway/.env`

```env
# Gateway Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Eureka Configuration
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-service:8761/eureka/

# JWT Configuration
JWT_SECRET=c2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzQXRMZWFzdDMyQ2hhcmFjdGVycw==
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000
```

#### `user-service/.env`

```env
# Server Configuration
SERVER_PORT=8081
USER_SERVICE_PORT=8081
SPRING_PROFILES_ACTIVE=prod

# Database Configuration
DB_HOST=postgres-user
DB_NAME=user_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Eureka Configuration
EUREKA_CLIENT_ENABLED=true
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-service:8761/eureka/

# JWT Configuration
JWT_SECRET=c2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzQXRMZWFzdDMyQ2hhcmFjdGVycw==
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000
```

#### `subject-service/.env`

```env
# Server Configuration
SERVER_PORT=8082
SUBJECT_SERVICE_PORT=8082
SPRING_PROFILES_ACTIVE=prod

# Database Configuration
DB_HOST=postgres-subject
DB_NAME=subject_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_PORT=5432
POSTGRES_DB=subject_db
POSTGRES_PORT=5433
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Eureka Configuration
EUREKA_CLIENT_ENABLED=true
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-service:8761/eureka/

# JWT Configuration
JWT_SECRET=c2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzQXRMZWFzdDMyQ2hhcmFjdGVycw==
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000
```

**⚠️ Lưu Ý Quan Trọng:**
- Tất cả các service phải sử dụng cùng một `JWT_SECRET`
- File `.env` có thể không hiển thị trong một số editor, nhưng vẫn cần tạo

## ⚙️ Cấu Hình

### Ports Được Sử Dụng

| Service | Port | Mô Tả |
|---------|------|-------|
| API Gateway | 8080 | Cổng chính để truy cập tất cả API |
| User Service | 8081 | Service quản lý người dùng |
| Subject Service | 8082 | Service quản lý môn học |
| Discovery Service | 8761 | Eureka Dashboard |
| PostgreSQL User | 5432 | Database cho User Service |
| PostgreSQL Subject | 5433 | Database cho Subject Service |

### Networks

- `microservices-network`: Mạng chung cho tất cả các service
- `user-network`: Mạng riêng cho User Service và database
- `subject-network`: Mạng riêng cho Subject Service và database

## 🏃 Chạy Dự Án

```bash
# Build discovery-service
cd discovery-service
mvn clean package -DskipTests
cd ..

# Build api-gateway
cd api-gateway
mvn clean package -DskipTests
cd ..

# Build user-service
cd user-service
mvn clean package -DskipTests
cd ..

# Build subject-service
cd subject-service
mvn clean package -DskipTests
cd ..
```

### Bước 3: Dọn Dẹp Container Cũ (Nếu Có)

```bash
# Dừng và xóa tất cả container
docker-compose down

# Xóa volumes cũ (cẩn thận: sẽ mất dữ liệu)
docker-compose down -v

# Xóa container cũ nếu có xung đột tên
docker rm -f discovery-service api-gateway user-service subject-service postgres-user postgres-subject 2>/dev/null
```

### Bước 4: Build và Chạy Docker Containers

```bash
# Build và chạy tất cả containers
docker-compose up --build -d

# Xem logs real-time
docker-compose logs -f
```


## ✅ Kiểm Tra

### 1. Kiểm Tra Eureka Dashboard

Truy cập: **http://localhost:8761**

Bạn sẽ thấy:
- **api-gateway** đã đăng ký
- **user-service** đã đăng ký
- **subject-service** đã đăng ký

## 📝 Lưu Ý Quan Trọng

1. **Thứ Tự Khởi Động:**
   - Discovery Service phải khởi động đầu tiên
   - Database containers khởi động tiếp theo
   - Các microservices khởi động sau
   - API Gateway khởi động cuối cùng

2. **Thời Gian Khởi Động:**
   - Discovery Service: ~30 giây
   - Database: ~10-15 giây
   - Microservices: ~60-90 giây
   - Tổng thời gian: ~2-3 phút

3. **JWT Secret:**
   - Tất cả service phải sử dụng cùng một `JWT_SECRET`
   - Secret phải là Base64 encoded string
   - Trong production, sử dụng secret mạnh và bảo mật

4. **Dữ Liệu Database:**
   - Dữ liệu được lưu trong Docker volumes
   - Xóa volume sẽ mất tất cả dữ liệu
   - Backup dữ liệu trước khi xóa volumes

5. **File .env:**
   - Không commit file `.env` vào Git (đã có trong .gitignore)
   - Mỗi developer có thể có file `.env` riêng
   - File `.env` phải có đầy đủ các biến môi trường

**Chúc bạn thành công! 🚀**

