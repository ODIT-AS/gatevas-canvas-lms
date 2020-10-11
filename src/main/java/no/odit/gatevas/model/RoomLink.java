package no.odit.gatevas.model;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "Enrollment")
@Table(name = "enrollment")
public class RoomLink {

	@Id
	@GeneratedValue
	@Type(type="uuid-char")
	private UUID id;

	@Column(nullable = false)
	private boolean emailSent;

	@Column(nullable = false)
	private boolean textSent;

	@ManyToOne
	@JoinColumn(name="student_id", nullable=false)
	private Student student;

	@ManyToOne
	@JoinColumn(name="course_id", nullable=false)
	private Classroom course;

	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public boolean isEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public boolean isTextSent() {
		return textSent;
	}

	public void setTextSent(boolean textSent) {
		this.textSent = textSent;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Classroom getCourse() {
		return course;
	}

	public void setCourse(Classroom course) {
		this.course = course;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "RoomLink [id=" + id + ", emailSent=" + emailSent + ", textSent=" + textSent + ", student=" + student
				+ ", course=" + course + ", updatedAt=" + updatedAt + ", createdAt=" + createdAt + "]";
	}
}