package group_2.cursus.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Embeddable;

@Embeddable
public class CourseStudentId implements Serializable {

    private UUID studentId;
    private long courseId;

    public CourseStudentId() {}

    public CourseStudentId(UUID studentId, long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseStudentId scoreId = (CourseStudentId) o;
        return courseId == scoreId.courseId && Objects.equals(studentId, scoreId.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}
