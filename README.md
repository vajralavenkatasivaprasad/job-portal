# ◈ JobPortal – Job Portal Management System

A full-featured web application built with **Spring Boot 3**, **Spring MVC**, **Spring Security**, **Spring Data JPA**, and **Thymeleaf** that connects job seekers and employers.

---

## 🚀 Features

### 👨‍🎓 Student / Job Seeker
- Register, login, and maintain a detailed profile
- Upload resume (PDF/DOC)
- Browse, search, and filter jobs (by keyword, location, category, type, experience)
- Apply to jobs with optional cover letter
- Track application status (Applied → Shortlisted → Interview → Offered)
- Withdraw applications
- Get email notifications on status changes

### 🏢 Employer
- Post jobs with full details (title, description, skills, salary, type, deadline)
- Edit, deactivate, and delete job postings
- View all applicants per job
- Shortlist, reject, or update application status
- Dashboard analytics (total jobs, applications, shortlisted count)

### 🔐 Admin
- View all users, enable/disable accounts
- Remove inappropriate jobs
- System-wide analytics dashboard
- H2 console access for database inspection

---

## 🛠️ Tech Stack

| Layer        | Technology                         |
|--------------|------------------------------------|
| Backend      | Spring Boot 3.2, Spring MVC        |
| Security     | Spring Security 6, BCrypt          |
| Database     | H2 (dev) / MySQL (prod), JPA       |
| Frontend     | Thymeleaf, HTML5, CSS3, JavaScript |
| Build Tool   | Maven                              |
| Java         | Java 17+                           |
| Email        | Spring Mail (JavaMailSender)       |

---

## ⚡ Quick Start (No Setup Required)

### Prerequisites
- Java 17 or higher
- Maven 3.6+ (or use included `mvnw`)

### Run with H2 (In-Memory Database)
```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/job-portal.git
cd job-portal

# Run (H2 is default, no DB install needed)
./mvnw spring-boot:run

# OR on Windows
mvnw.cmd spring-boot:run
```

Open your browser: **http://localhost:8080**

---

## 🔑 Demo Login Credentials

| Role     | Email                       | Password      |
|----------|-----------------------------|---------------|
| Admin    | admin@jobportal.com         | admin123      |
| Employer | employer@techcorp.com       | employer123   |
| Student  | student@gmail.com           | student123    |

---

## 🗄️ Database Options

### Option 1: H2 In-Memory (Default – No Setup)
Already configured. Data resets on restart.  
H2 Console: http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:jobportaldb`  
Username: `sa` | Password: *(empty)*

### Option 2: MySQL (Persistent)
1. Install MySQL and create a database:
```sql
CREATE DATABASE jobportal;
```
2. Update `application.properties`:
```properties
spring.profiles.active=mysql
```
3. Update `application-mysql.properties` with your credentials:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 📁 Project Structure

```
job-portal/
├── src/main/java/com/jobportal/
│   ├── config/          # Security, JPA Auditing, Data Seeder
│   ├── controller/      # MVC Controllers (Home, Job, Student, Employer, Admin)
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA Entities (User, Job, Application)
│   ├── exception/       # Custom exceptions + GlobalExceptionHandler
│   ├── repository/      # Spring Data JPA Repositories
│   ├── security/        # CustomUserDetailsService
│   └── service/         # Business Logic (User, Job, Application, Email)
├── src/main/resources/
│   ├── templates/       # Thymeleaf HTML templates
│   │   ├── auth/        # Login, Register
│   │   ├── jobs/        # Public job list & detail
│   │   ├── student/     # Student dashboard, applications, profile
│   │   ├── employer/    # Employer dashboard, job management
│   │   └── admin/       # Admin panel
│   └── static/
│       ├── css/main.css # Complete design system
│       └── js/main.js   # Interactive features
└── pom.xml
```

---

## 🎯 Key Spring Boot Concepts Used

- `@RestController` / `@Controller` – MVC controllers
- `@Entity`, `@OneToMany`, `@ManyToOne` – JPA relationships
- `@NotBlank`, `@Size`, `@Email` – Bean Validation
- `@ControllerAdvice` – Global exception handling
- `@EnableJpaAuditing` – Auto createdAt/updatedAt
- `@EnableAsync` + `@Async` – Async email notifications
- Spring Security role-based access control
- Spring Data JPA custom JPQL queries
- Thymeleaf + Spring Security integration

---

## 📧 Email Configuration (Optional)

Edit `application.properties`:
```properties
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password
```
> Use Gmail App Passwords (not your regular password).  
> Email failures are caught gracefully — the app works without it.

---

## 🌐 Running on GitHub Pages / Deploying

For a production deployment:
1. Switch profile to `mysql` in `application.properties`
2. Set environment variables for DB credentials
3. Build JAR: `./mvnw clean package -DskipTests`
4. Run: `java -jar target/job-portal-1.0.0.jar`

---

## 📸 Application URLs

| URL | Description |
|-----|-------------|
| `/` | Home page with job listings |
| `/jobs` | Browse & filter all jobs |
| `/jobs/{id}` | Job detail + apply |
| `/register` | Student/Employer registration |
| `/login` | Login page |
| `/student/dashboard` | Student home |
| `/student/applications` | Track applications |
| `/student/profile` | Edit profile + upload resume |
| `/employer/dashboard` | Employer home |
| `/employer/jobs` | Manage job postings |
| `/employer/jobs/new` | Post new job |
| `/employer/jobs/{id}/applicants` | View & manage applicants |
| `/admin/dashboard` | Admin overview |
| `/admin/users` | Manage all users |
| `/admin/jobs` | Manage all jobs |
| `/h2-console` | H2 database console (dev only) |

---

## 🤝 Contributing

Pull requests welcome! Please open an issue first for major changes.

---

## 📄 License

MIT License – free to use for academic and personal projects.
