package uz.pdp.learning_center_full.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.learning_center_full.dto.request.AttendanceCr;
import uz.pdp.learning_center_full.dto.response.AttendanceResponse;
import uz.pdp.learning_center_full.dto.response.LessonAttendanceResponse;
import uz.pdp.learning_center_full.dto.response.LessonResponse;
import uz.pdp.learning_center_full.entity.AttendanceEntity;
import uz.pdp.learning_center_full.entity.LessonEntity;
import uz.pdp.learning_center_full.entity.StudentInfo;
import uz.pdp.learning_center_full.entity.enums.LessonStatus;
import uz.pdp.learning_center_full.exception.DataNotFoundException;
import uz.pdp.learning_center_full.exception.DuplicateValueException;
import uz.pdp.learning_center_full.repository.AttendanceRepository;
import uz.pdp.learning_center_full.repository.LessonRepository;
import uz.pdp.learning_center_full.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final ModelMapper modelMapper;
    private final AttendanceRepository attendanceRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    public AttendanceResponse create(AttendanceCr attendanceDto) {
        checkLesson(attendanceDto.getLessonId());
        checkStudent(attendanceDto.getStudentId());
        AttendanceEntity attendance = attendanceRepository.save(modelMapper.map(attendanceDto,AttendanceEntity.class));
        return modelMapper.map(attendance,AttendanceResponse.class);
    }
//dofnvjd
    public List<LessonAttendanceResponse> getStudentAttendances(UUID id) {
        List<AttendanceEntity> attendanceEntityList = attendanceRepository.findAllByStudentId(id);
        return modelMapper.map(attendanceEntityList, new TypeToken<List<AttendanceResponse>>() {}.getType());

    }
    public ResponseEntity<String> createAttendances(List<AttendanceCr> attendanceDtoList) {
        for (AttendanceCr attendanceDto : attendanceDtoList) {
            checkLesson(attendanceDto.getLessonId());
            checkStudent(attendanceDto.getStudentId());
            checkAttendance(attendanceDto);
        }
        for (AttendanceCr attendanceDto : attendanceDtoList) {
            attendanceRepository.save(modelMapper.map(attendanceDto,AttendanceEntity.class));
        }
        return ResponseEntity.ok("Saved");
    }

    private void checkAttendance(AttendanceCr attendanceDto) {
        List<AttendanceEntity> allStudentAttendance = attendanceRepository.findAllByStudentId(attendanceDto.getStudentId());
        for (AttendanceEntity attendance : allStudentAttendance) {
            if(Objects.equals(attendance.getLessonId(),attendanceDto.getLessonId()) &&
                Objects.equals(attendance.getStudentId(),attendanceDto.getStudentId())){
                throw new DuplicateValueException("Duplicate data!");
            }
        }
    }

    private LessonEntity checkLesson(UUID lessonId){
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found by this Id " + lessonId));

    }

    private StudentInfo checkStudent(UUID studentId){
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found by this Id " +  studentId));

    }



    public List<AttendanceResponse> getAttendancesByLessonId(UUID lessonId){
        List<AttendanceEntity> attendanceEntityList = attendanceRepository.findAllByLessonId(lessonId);
        return modelMapper.map(attendanceEntityList, new TypeToken<List<AttendanceResponse>>() {}.getType());

    }
    public List<LessonAttendanceResponse> getAttendanceWithLessonByModule(UUID groupId){
        int module = 0;
        List<LessonEntity> lessonEntityList = lessonRepository.findLessonsByGroupId(groupId);
        for (LessonEntity lesson : lessonEntityList){
            if(lesson.getModule() > module){
                module = lesson.getModule();
            }
        }
        List<LessonEntity> lessonEntitiesByModule = lessonRepository.findLessonsByModule(module);
        List<LessonAttendanceResponse> lessonAttendanceResponseList = new ArrayList<>();

        System.out.println("lessonEntitiesByModule = " + lessonEntitiesByModule);
        for (LessonEntity lesson : lessonEntitiesByModule) {
            if(lesson.getLessonStatus() == LessonStatus.FINISHED){
                LessonAttendanceResponse lessonAttendanceResponse = new LessonAttendanceResponse();
                lessonAttendanceResponse.setLessonResponse(modelMapper.map(lesson, LessonResponse.class));
                lessonAttendanceResponse.setAttendanceResponseList(getAttendancesByLessonId(lesson.getId()));
                lessonAttendanceResponseList.add(lessonAttendanceResponse);
            }

        }
        return lessonAttendanceResponseList;
    }
    public List<LessonAttendanceResponse> getAllAttendancesWithLesson(UUID groupId) {
        List<LessonEntity> lessonEntityList = lessonRepository.findLessonsByGroupId(groupId);

        List<LessonAttendanceResponse> lessonAttendanceResponseList = new ArrayList<>();
        for (LessonEntity lesson : lessonEntityList) {
            LessonAttendanceResponse lessonAttendanceResponse = new LessonAttendanceResponse();
            lessonAttendanceResponse.setLessonResponse(modelMapper.map(lesson, LessonResponse.class));
            lessonAttendanceResponse.setAttendanceResponseList(getAttendancesByLessonId(lesson.getId()));
            lessonAttendanceResponseList.add(lessonAttendanceResponse);
        }
        return lessonAttendanceResponseList;
    }
}