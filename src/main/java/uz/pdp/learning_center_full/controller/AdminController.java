package uz.pdp.learning_center_full.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.learning_center_full.dto.request.*;
import uz.pdp.learning_center_full.dto.response.*;
import uz.pdp.learning_center_full.entity.GroupEntity;
import uz.pdp.learning_center_full.entity.enums.ApplicationStatus;
import uz.pdp.learning_center_full.service.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {
    private final ApplicationService applicationService;
    private final AttendanceService attendanceService;
    private final CourseService courseService;
    private final GroupService groupService;
    private final LessonService lessonService;
    private final MentorService mentorService;
    private final StudentService studentService;

    //application

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get_application/{id}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable UUID id){
        return applicationService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/getAllApplication")
    public List<ApplicationResponse> getAllApplication(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return applicationService.getAll(page, size);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/getAllByStatus")
    public List<ApplicationResponse> getAllApplicationBYStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam ApplicationStatus status){
        return applicationService.getAllByStatus(page, size,status);
    }

    //attendance

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("getAllAttendancesWithLesson/{group_id}")
    public ResponseEntity<List<LessonAttendanceResponse>> getAllAttendancesWithLesson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID group_id){
        return ResponseEntity.ok(attendanceService.getAllAttendancesWithLesson(group_id, page,size ));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("getAttendanceWithLessonByModule")
    public ResponseEntity<List<LessonAttendanceResponse>> getAttendanceWithLessonByModule(@RequestParam UUID group_id,@RequestParam Integer module){
        return ResponseEntity.ok(attendanceService.getAttendanceWithLessonByModule(group_id,module));
    }

    // course

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/create-course")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody @Valid CourseCr courseCr) {
        return ResponseEntity.ok(courseService.create(courseCr));
    }

    // group

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/create-group")
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupCr groupCr) {
        return ResponseEntity.status(200).body(groupService.create(groupCr));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-group/{id}")
    public GroupResponse getGroup(@PathVariable UUID id){
        return groupService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')  or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-all-group")
    public List<GroupEntity> getAllGroup(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return groupService.getAll(page, size);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/mentor-groups/{mentor_id}")
    public List<GroupResponse> getByMentorID(@PathVariable UUID mentor_id){
        return groupService.getByMentorId(mentor_id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("get-by-course-id/{course_id}")
    public List<GroupResponse> getByCourseId(@PathVariable UUID course_id){
        return groupService.getByCourseId(course_id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("get-available-groups-by-course-id/{course_id}")
    public List<GroupResponse> getAvailableGroupsByCourseId(@PathVariable UUID course_id){
        return groupService.getAvailableGroupsByCourseId(course_id);
    }


    // lesson

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-lesson{id}")
    public ResponseEntity<LessonResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(lessonService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-lessons-by-group-id/{groupId}")
    public ResponseEntity<List<LessonResponse>> getAll(@PathVariable UUID groupId) {
        return ResponseEntity.status(200).body(lessonService.getLesson(groupId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/start_lesson{lessonId}/{groupId}")
    public ResponseEntity<LessonResponse>  startLesson(
            @PathVariable UUID lessonId, @PathVariable UUID groupId){
        return lessonService.startLesson(lessonId, groupId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/finish_lesson")
    public ResponseEntity<String> finishLesson(@RequestBody List<AttendanceCr> attendanceCrList){
        return lessonService.finishLesson(attendanceCrList);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-all-lesson")
    public ResponseEntity<List<LessonResponse>> getAllLesson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(200).body(lessonService.getAll(page, size));
    }

    // mentor

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-mentor")
    public ResponseEntity<MentorResponse> addMentor(@RequestBody MentorCr mentorCr) {
        return mentorService.addMentor(mentorCr);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_mentor_by_id/{mentor_id}")
    public ResponseEntity<MentorResponse> getByID(@PathVariable UUID mentor_id){
        return mentorService.getById(mentor_id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get_all-mentors")
    public ResponseEntity<List<MentorResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return mentorService.getAll(page,size);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-mentors-by-course/{course_id}")
    public ResponseEntity<List<MentorResponse>> getMentorByCourseId( @PathVariable UUID course_id){
        return mentorService.getMentorByCourseId(course_id);
    }

    //student

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/create-student")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentCR studentCR) {
        return ResponseEntity.status(200).body(studentService.create(studentCR));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')  or hasRole('SUPER_ADMIN')")
    @GetMapping("/get-group-students/{group_id}")
    public ResponseEntity<List<StudentResponse>> getGroupStudents(@PathVariable UUID group_id){
        return ResponseEntity.status(200).body(studentService.getByGroupId(group_id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/create_student_by_application")
    public ResponseEntity<StudentResponse> createByApplication(@RequestParam UUID application_id, UUID group_id){
        return studentService.createByApplication(application_id,group_id);
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/get_students_by_rating")
    public ResponseEntity<List<StudentResponse>> getStudentByRating(UUID groupId){
        return ResponseEntity.status(200).body(studentService.getStudentByRating(groupId));
    }

}
