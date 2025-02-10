package group_2.cursus.repository;

import group_2.cursus.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CourseReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @Test
    @Transactional
    @Rollback(false)
    void testFindByCourse_CourseId() {
        // Create and persist a Category
        Category category = new Category();
        category.setCategoryName("Lập trình");
        entityManager.persist(category);

        // Create and persist a SubCategory
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryName("Lập trình Java");
        subCategory.setCategory(category);
        entityManager.persist(subCategory);

        // Create and persist an Instructor
        Instructor instructor = new Instructor();
        instructor.setEmail("minh.nguyen@gmail.com");
        instructor.setPassword("password123");
        instructor.setFullName("Nguyễn Văn Minh");
        entityManager.persist(instructor);

        // Create and persist a Course
        Course course = new Course();
        course.setCourseName("Lập trình Java cơ bản");
        course.setDescription("Khóa học giới thiệu về lập trình Java");
        course.setThumb("java-course.jpg");
        course.setPrice(BigDecimal.valueOf(299000));
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);
        entityManager.persist(course);

        // Create and persist a Student
        Student student = new Student();
        student.setEmail("linh.pham@gmail.com");
        student.setPassword("password123");
        student.setFullName("Phạm Minh Linh");
        entityManager.persist(student);

        // Create and persist CourseReviews
        CourseReview review1 = new CourseReview();
        review1.setCourse(course);
        review1.setStudent(student);
        review1.setRating(5);
        review1.setComment("Khóa học rất hay, giảng viên dạy rất tốt!");
        entityManager.persist(review1);

        CourseReview review2 = new CourseReview();
        review2.setCourse(course);
        review2.setStudent(student);
        review2.setRating(4);
        review2.setComment("Nội dung khóa học rất bổ ích, mình rất thích!");
        entityManager.persist(review2);

        entityManager.flush();

        List<CourseReview> foundReviews = courseReviewRepository.findByCourse_CourseId(course.getCourseId());

        assertThat(foundReviews).hasSize(2);
    }

    @Test
    @Transactional
    @Rollback(false)
    void testFindByInstructorId() {
        // Create and persist a Category
        Category category = new Category();
        category.setCategoryName("Lập trình");
        entityManager.persist(category);

        // Create and persist a SubCategory
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryName("Lập trình Java");
        subCategory.setCategory(category);
        entityManager.persist(subCategory);

        // Create and persist an Instructor
        Instructor instructor = new Instructor();
        instructor.setEmail("an.tran@gmail.com");
        instructor.setPassword("password123");
        instructor.setFullName("Trần Văn An");
        entityManager.persist(instructor);

        // Create and persist a Course
        Course course = new Course();
        course.setCourseName("Lập trình Java nâng cao");
        course.setDescription("Khóa học về các tính năng nâng cao của Java");
        course.setThumb("java-advanced.jpg");
        course.setPrice(BigDecimal.valueOf(499000));
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);
        entityManager.persist(course);

        // Create and persist a Student
        Student student = new Student();
        student.setEmail("thuy.nguyen@gmail.com");
        student.setPassword("password123");
        student.setFullName("Nguyễn Thị Thủy");
        entityManager.persist(student);

        // Create and persist a CourseReview
        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(5);
        review.setComment("Khóa học rất chất lượng, giảng viên giảng dạy nhiệt tình!");
        entityManager.persist(review);

        entityManager.flush();

        List<CourseReview> foundReviews = courseReviewRepository.findByInstructorId(instructor.getId());

        assertThat(foundReviews).hasSize(1);
    }
}