# TalentSync — Revision Cheat Sheet

Quick interview revision for the Spring Boot backend (`/api/v1` context path).

---

## Stack & Architecture

| Item | Detail |
|---|---|
| Java | 17 |
| Framework | Spring Boot 3.3.5 |
| DB | PostgreSQL + JPA/Hibernate |
| Auth | JWT (stateless), Spring Security |
| Migrations | Flyway (`db/migration/`) |
| File storage | Cloudinary (PDF resumes) |
| API docs | Swagger `/api/v1/v3/api-docs` |

**Layers:** `Controller → Service → Repository → Entity`  
**Extras:** DTOs, Mapper (`ReferralMapper`), `GlobalExceptionHandler`

---

## Roles & Security

| Role | Path prefix | Access |
|---|---|---|
| Public | `/auth/**`, `/jobs/**` | No token |
| `ADMIN` | `/admin/**` | Admin JWT |
| `CANDIDATE` | `/candidate/**` | Candidate JWT |

**JWT header:** `Authorization: Bearer <token>`

| Status | Meaning |
|---|---|
| 401 `Authentication required` | Missing/invalid/expired token |
| 403 `Access denied` | Valid token, wrong role |

**Admin user:** Seeded from env (`ADMIN_EMAIL`, `ADMIN_PSWD`, `ADMIN_NAME`) — not via signup.

---

## Auth Endpoints

| Method | Path | Notes |
|---|---|---|
| POST | `/auth/signup` | Creates **CANDIDATE** only; no token returned |
| POST | `/auth/login` | Returns `{ token, role, message }` |

---

## Application & Referral Workflow

### Application status (`ApplicationStatus`)
`APPLIED → SCREENING → SHORTLISTED → REFERRED → INTERVIEW → SELECTED / REJECTED`

| Method | Path | Who sets it |
|---|---|---|
| PATCH | `/admin/applications/{id}/status` | Admin manually |
| POST | `/admin/applications/{id}/referrals` | Auto-sets application to `REFERRED` |

**Rule:** Admin **cannot** PATCH status to `REFERRED` — only referral creation does that.

### Referral status (`ReferralStatus`)
`REFERRED → CONTACT_ACKNOWLEDGED → INTERVIEW_SCHEDULED → … → JOINED / REJECTED / CLOSED`

| Method | Path | Purpose |
|---|---|---|
| GET | `/admin/referrals` | List all (summary DTO, JOIN FETCH) |
| GET | `/admin/referrals/{id}` | Detail DTO |
| PATCH | `/admin/referrals/{id}/status` | Update referral status only |

**Referral create body (required):** `companyName`, `contactName`, `contactEmail`  
Use `contactEmail` — not `email` or `recruiterEmail`.

---

## Key Entities

| Entity | Notes |
|---|---|
| `User` | `email`, `role`, `isActive`; implements `UserDetails` |
| `CandidateProfile` | 1:1 with User; holds resume metadata |
| `JobApplication` | Unique `(candidate_id, job_id)` |
| `Referral` | Linked to `JobApplication`; `contact_name/email` columns |
| `Job` | Belongs to `Company` |
| `AdminProfile` | No company relation |

---

## DB Migration (Referral columns)

**Problem:** Legacy `recruiter_name` / `recruiter_email` NOT NULL columns blocked inserts after rename to `contact_name` / `contact_email`.

**Fix:** `V1__fix_referral_contact_columns.sql` (Flyway) — copies data, drops old columns.

```sql
ALTER TABLE referral DROP COLUMN IF EXISTS recruiter_name;
ALTER TABLE referral DROP COLUMN IF EXISTS recruiter_email;
```

---

## Common Errors & Causes

| Error | Likely cause |
|---|---|
| `Authentication required` on POST referrals | Was controller mapping conflict (fixed: endpoints under `AdminApplicationController`) |
| `must be a well-formed email address` | Swagger placeholder `"string"`, wrong field name, or spaces in email |
| `null value in column "recruiter_email"` | DB migration not run |
| `Upload your resume before completing profile` | Resume not uploaded yet |

---

## Env Vars (Production)

```
DB_URL, DB_USERNAME, DB_PASSWORD
JWT_SECRET, JWT_EXPIRATION
CLOUD_NAME, API_KEY, API_SECRET
ADMIN_EMAIL, ADMIN_PSWD, ADMIN_NAME
PORT
```

---

## File Upload Revision

### High-level idea

Files are **not** stored on the Spring Boot server or in PostgreSQL as blobs.

| Layer | Responsibility |
|---|---|
| **Cloudinary** | Stores the actual PDF file |
| **PostgreSQL** | Stores resume **metadata** only |
| **Spring Boot** | Validates, uploads to Cloudinary, saves metadata |

Only **candidate resume PDFs** are uploaded. No generic admin/job file upload.

---

### Architecture (Strategy pattern)

```
ResumeController
    → ResumeServiceImpl          (business logic + validation)
        → StorageService         (interface)
            → CloudinaryStorageService   (Cloudinary SDK)
        → CandidateProfileRepository
            → PostgreSQL
```

**Why `StorageService` interface?**
- Swap Cloudinary → S3/Azure without changing business logic
- Easy to mock in tests
- Keeps Cloudinary SDK out of `ResumeServiceImpl`

---

### Endpoints

Base: `/api/v1/candidate/resume`  
Auth: **CANDIDATE JWT only**

| Method | Path | Purpose |
|---|---|---|
| POST | `/candidate/resume` | Upload / replace resume |
| GET | `/candidate/resume` | Get metadata (URL, filename, size, date) |
| DELETE | `/candidate/resume` | Delete from Cloudinary + clear DB |

**Upload request:**
```http
POST /api/v1/candidate/resume
Authorization: Bearer <candidate-token>
Content-Type: multipart/form-data

resume=<pdf-file>
```

Field name must be **`resume`** (`@RequestParam("resume")`).

**Upload response:**
```json
{
  "secureUrl": "https://res.cloudinary.com/.../file.pdf",
  "publicId": "talentsync/resumes/<uuid>",
  "fileName": "my-resume.pdf",
  "fileSize": 245678
}
```

---

### Upload flow (step by step)

1. **Controller** receives `MultipartFile` via `multipart/form-data`
2. **ResumeServiceImpl** validates:
   - Not null / not empty
   - PDF only (`application/pdf` or `.pdf` extension)
   - Max **5 MB** (also enforced in `application.properties`)
3. **Auth check:** JWT → current user must be `CANDIDATE` → load `CandidateProfile`
4. **Replace old file:** if `resumePublicId` exists → `deleteResume()` on Cloudinary first
5. **CloudinaryStorageService.uploadResume():**
   - `resource_type: "raw"` — PDFs are documents, not images
   - `folder: "talentsync/resumes"`
   - `public_id: UUID` — unique, no filename collisions
   - `cloudinary.uploader().upload(file.getBytes(), params)`
6. **Save metadata** to `CandidateProfile`:
   - `resume_url` → HTTPS URL to open/download
   - `resume_public_id` → needed for delete/replace
   - `resume_file_name`, `resume_file_size`, `resume_uploaded_at`
7. Return `ResumeUploadResponse` to client

---

### Cloudinary config

```java
@Bean
public Cloudinary cloudinary(
    @Value("${cloudinary.cloud-name}") String cloudName,
    @Value("${cloudinary.api-key}") String apiKey,
    @Value("${cloudinary.api-secret}") String apiSecret)
```

| Env var | Property |
|---|---|
| `CLOUD_NAME` | `cloudinary.cloud-name` |
| `API_KEY` | `cloudinary.api-key` |
| `API_SECRET` | `cloudinary.api-secret` |

API secret stays on backend only — frontend never calls Cloudinary directly.

---

### Delete flow

1. `DELETE /candidate/resume`
2. Read `resumePublicId` from profile
3. `cloudinary.uploader().destroy(publicId, resource_type=raw)`
4. Null out all resume fields in DB

---

### Multipart limits (`application.properties`)

```properties
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

---

### Resume in business workflow

Resume upload is **separate** from profile update:

| Step | Endpoint | Format |
|---|---|---|
| 1. Upload resume | `POST /candidate/resume` | `multipart/form-data` |
| 2. Update profile | `PUT /candidate/profile` | JSON only |
| 3. Apply for job | `POST /candidate/jobs/{jobId}/apply` | No body |

- Profile update **blocked** if `resumeUrl` is null
- Job apply **blocked** if profile incomplete (includes resume)

Admin views resume via `resumeUrl` on candidate detail APIs — no admin upload.

---

### File upload — security

| Concern | Handling |
|---|---|
| Who can upload? | `CANDIDATE` role only |
| File type | PDF only |
| Max size | 5 MB (service + servlet) |
| CSRF | Disabled (stateless JWT API) |
| Cloudinary credentials | Backend env vars only |

---

### File upload — errors

| Error | HTTP | Cause |
|---|---|---|
| `Only PDF files are allowed` | 400 | Wrong file type |
| `Resume file size must not exceed 5 MB` | 400 | Too large |
| `Resume not found` | 404 | No resume on GET/DELETE |
| `Only candidates can manage resumes` | 403 | Admin token used |
| `Failed to upload resume` | 400 | Cloudinary API failure |

---

### Frontend upload snippet

```javascript
const formData = new FormData();
formData.append('resume', pdfFile);

await fetch(`${API_URL}/candidate/resume`, {
  method: 'POST',
  headers: { Authorization: `Bearer ${token}` },
  // Do NOT set Content-Type — browser sets multipart boundary
  body: formData,
});
```

---

### 30-second interview answer (file upload)

> "Resume upload uses a dedicated multipart endpoint. The controller passes the file to `ResumeService`, which validates PDF and 5 MB limit, deletes any old Cloudinary file, then uploads via `CloudinaryStorageService` with `resource_type=raw`. Cloudinary returns `secure_url` and `public_id`; we store only metadata in `CandidateProfile`. Files never touch local disk or PostgreSQL. A `StorageService` interface allows swapping providers. Only candidates with JWT can upload; resume is required before profile completion and job application."

---

### File upload — follow-up Q&A

| Question | Answer |
|---|---|
| Why Cloudinary? | CDN, HTTPS delivery, no server disk, cloud-native |
| Why `publicId`? | Required to delete/replace files later |
| Why `resource_type=raw`? | PDF is not image/video |
| What if Cloudinary succeeds but DB fails? | Orphan file in Cloudinary; needs cleanup/compensation |
| Can admin upload? | No — view-only via `resumeUrl` |
| Where is file bytes stored? | Cloudinary CDN only |

---

## Quick Endpoint Map

### Candidate
- `GET/PUT /candidate/profile`
- `POST/GET/DELETE /candidate/resume`
- `POST /candidate/jobs/{jobId}/apply`
- `GET /candidate/applicationStatus`

### Admin
- `GET/POST /admin/companies`
- `POST/PUT/PATCH /admin/jobs`
- `GET /admin/jobs/{id}/applications`
- `PATCH /admin/applications/{id}/status`
- `POST/GET /admin/applications/{id}/referrals`
- `GET /admin/referrals`, `GET /admin/referrals/{id}`
- `PATCH /admin/referrals/{id}/status`
- `GET /admin/candidates`, `GET /admin/candidates/{id}`

### Public
- `POST /auth/signup`, `POST /auth/login`
- `GET /jobs/**` (search/list)

---

## Build & Deploy

```bash
# Requires JDK 17 (Lombok breaks on JDK 26)
export JAVA_HOME=<path-to-jdk-17>
./mvnw clean package -DskipTests
```

Docker: `mvn clean package -DskipTests` → run JAR on Railway with env vars above.
