# AI Developer Log - Ktor Workshop

บันทึกประวัติการพัฒนาและบทเรียนที่ได้รับจากการสร้างแอปพลิเคชัน Ktor Server ร่วมกับ AI Coding Assistant

---

## 1. สรุป Prompt ที่ใช้ (Prompts Summary)

1. **เริ่มต้นโครงการ**: ขอให้ติดตั้งเซ็ตอัปโปรเจกต์จาก `https://start.ktor.io/settings`
   - *การตั้งค่าที่เลือก*: Gradle Kotlin DSL, Netty Engine, HOCON Configuration (`application.conf`), ปลั๊กอินพื้นฐาน (Routing, Content Negotiation with Kotlinx Serialization, Call Logging)
2. **วางแผนการพัฒนา**: ใช้คำสั่ง `/plan` เพื่อร่วมวางแผนการทำ REST API สำหรับจัดการข้อมูล Task
3. **พัฒนา Tasks API**: พัฒนา CRUD API สำหรับจำลองระบบจัดการรายการงาน (Task Management) บนหน่วยความจำ (In-Memory Repository) ครอบคลุมพฤติกรรม:
   - `GET /tasks` (ดึงรายการทั้งหมด)
   - `GET /tasks/{id}` (ค้นหาตาม ID หรือตอบกลับ 404)
   - `POST /tasks` (สร้างข้อมูลใหม่และสุ่ม ID)
   - `PUT /tasks/{id}` (อัปเดตข้อมูล)
   - `DELETE /tasks/{id}` (ลบข้อมูล)
4. **เขียนชุดทดสอบ**: สั่ง `write full test` เพื่อพัฒนา Unit Test ครอบคลุมทุก Route และเคสที่เกิด Error
5. **บันทึกประวัติ**: สั่งบันทึกข้อมูล AI Log ลงใน GitHub

---

## 2. สิ่งที่ AI ตอบผิด / น่าสงสัย (Issues & Anomalies)

* **ความผิดพลาดเรื่องลำดับการทำงานของ Gradle (Order of Operations)**:
  - ในตอนเริ่มต้น AI พยายามรันคำสั่งเจเนอเรต Gradle Wrapper (`gradle wrapper`) ในไดเรกทอรีว่างเปล่าทันที ส่งผลให้บิลด์พังด้วยข้อผิดพลาด:
    `Directory '/home/natto/Projects/ktor-app' does not contain a Gradle build`
* **ปัญหา Path ของ Gradle Daemon ตกค้าง**:
  - เมื่อนำไดเรกทอรีติดตั้ง Gradle ตัวชั่วคราวใน `/tmp` ออก แต่ตัวเก่ารันค้างเป็นแบ็กกราวด์ (Daemon) เมื่อมีการรันงานใหม่ทำให้เกิด Error:
    `java.nio.file.NoSuchFileException: /tmp/gradle-8.10-extracted/.../gradle-diagnostics-8.10.jar`

---

## 3. การตัดสินใจแก้ปัญหา (Resolutions)

* **การเรียงลำดับการเขียนไฟล์ใหม่**:
  - ตัดสินใจเขียนไฟล์ `build.gradle.kts`, `settings.gradle.kts` และ `gradle/libs.versions.toml` ลงไปในโฟลเดอร์ก่อน เพื่อให้ระบุขอบเขตของ Gradle ชัดเจน แล้วจึงค่อยสั่งสร้าง Wrapper
* **การสั่งหยุดการทำงาน Daemon**:
  - รันคำสั่ง `./gradlew --stop` เพื่อฆ่า Daemon ที่ค้างอยู่ ซึ่งชี้ไปยังพาธชั่วคราวเดิมที่โดนลบไปแล้ว เพื่อให้บิลด์ถัดไปใช้ทรัพยากรตัวติดตั้ง Gradle Wrapper ของตัวโครงการเองแทน

---

## 4. สิ่งที่ได้เรียนรู้ (Lessons Learned)

1. **Gradle Build File Requirement**: คำสั่งเจเนอเรต `gradle wrapper` ต้องการโครงสร้างไฟล์คอนฟิกูเรชันพื้นฐานอย่างน้อยที่สุดก่อนที่จะดำเนินการได้
2. **พฤติกรรมของ Gradle Daemon**: Daemon ของ Gradle จะคงอยู่เบื้องหลังเพื่อเพิ่มความเร็วในการบิลด์ครั้งถัดไป แต่จะพบปัญหาได้หากสภาพแวดล้อมที่มันอ้างอิงถึงตอนเริ่ม (เช่น โฟลเดอร์ชั่วคราวใน `/tmp`) ถูกทำลาย การใช้ `./gradlew --stop` จึงเป็นวิธีรีเซ็ตที่ดีที่สุด
3. **ฟังก์ชันทดสอบของ Ktor 3.0**: การทำโมดูลทดสอบด้วย `testApplication` ใน Ktor 3.0 ช่วยให้สามารถตรวจสอบค่าตอบกลับ JSON (Serialization) และสเตตัสโค้ดผ่าน Client เสมือนได้อย่างรวดเร็วและเป็นสัดส่วน (Isolated State) โดยการเพิ่มฟังก์ชันเคลียร์สเตตใน Repository
