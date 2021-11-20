package no.odit.gatevas.service;

import lombok.extern.slf4j.Slf4j;
import no.odit.gatevas.dao.StudentRepo;
import no.odit.gatevas.misc.ExcelSheetsCSV;
import no.odit.gatevas.misc.GeneralUtil;
import no.odit.gatevas.model.Classroom;
import no.odit.gatevas.model.Phone;
import no.odit.gatevas.model.Student;
import no.odit.gatevas.type.CanvasStatus;
import no.odit.gatevas.type.StudentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private ExcelSheetsCSV excelSheetsCSV;

    @Autowired
    private CanvasService canvasService;

    // Creates a new student or get existing
    public Student createStudent(String email, String firstName, String lastName, Integer phoneNum) {

        // Return existing student (email)
        Optional<Student> existingEmail = getUserByEmail(email);
        if (existingEmail.isPresent()) {
            log.debug("EMAIL ALREADY EXIST -> " + existingEmail.get());
            return existingEmail.get();
        }

        // Return existing student (name)
        Optional<Student> existingName = getUserByName(firstName, lastName);
        if (existingName.isPresent()) {
            log.debug("NAME ALREADY EXIST -> " + existingName.get());
            return existingName.get();
        }

        // Create new student
        Phone phone = phoneService.createPhone(phoneNum);
        Student student = new Student();
        student.setEmail(email.trim());
        student.setFirstName(firstName.trim());
        student.setLastName(lastName.trim());
        student.setTmpPassword(GeneralUtil.generatePassword());
        student.setPhone(phone);
        student.setLoginInfoSent(false);
        student.setExportedToCSV(false);
        student.setCanvasStatus(CanvasStatus.UNKNOWN);
        student.setStudentStatus(StudentStatus.ALLOWED);
        student = studentRepo.saveAndFlush(student);
        log.debug("CREATED STUDENT -> " + student.toString());
        return student;
    }

    // Exports students in course to a CSV file
    public boolean exportStudentsToCSV(Classroom course, File file) {

        // Sync students
        canvasService.syncUsersReadOnly(course);

        // Filter out existing students
        List<Student> students = course.getStudents().stream()
                .filter(student -> !student.getExportedToCSV() && student.getCanvasStatus() == CanvasStatus.MISSING)
                .collect(Collectors.toList());
        if (students.size() <= 0) {
            log.debug("All students in '" + course.getShortName() + "' already exists in Canvas LMS.");
            return true;
        }

        // Update status
        students.forEach(student -> {
            student.setExportedToCSV(true);
            saveChanges(student);
        });

        // Create CSV file
        try {
            excelSheetsCSV.createCSVFile(file, students);
            return true;
        } catch (IOException e) {
            log.error("Failed to create CSV file.", e);
            return false;
        }
    }

    // Save student to storage
    public void saveChanges(Student student) {
        studentRepo.saveAndFlush(student);
    }

    // Get student from storage by names
    public Optional<Student> getUserByName(String firstName, String lastName) {
        return studentRepo.findByFirstNameAndLastName(firstName.trim(), lastName.trim());
    }

    // Get student from storage by email
    public Optional<Student> getUserByEmail(String email) {
        return studentRepo.findByEmail(email.trim());
    }

    // Get student from storage by full name
    public Optional<Student> getUserByFullName(String fullName) {
        return studentRepo.findByFullname(fullName.trim());
    }

}