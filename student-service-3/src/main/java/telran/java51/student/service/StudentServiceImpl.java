package telran.java51.student.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java51.student.dao.StudentRepository;
import telran.java51.student.dto.ScoreDto;
import telran.java51.student.dto.StudentCreateDto;
import telran.java51.student.dto.StudentDto;
import telran.java51.student.dto.StudentUpdateDto;
import telran.java51.student.dto.exceptions.StudentNotFoundException;
import telran.java51.student.model.Student;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

	final StudentRepository studentRepository;
	
	@Override
	public Boolean addStudent(StudentCreateDto studentCreateDto) {
		if (studentRepository.existsById(studentCreateDto.getId())) {
			return false;
		}
		Student student = new Student(studentCreateDto.getId(), studentCreateDto.getName(),
				studentCreateDto.getPassword());
		studentRepository.save(student);
		return true;
	}

	@Override
	public StudentDto findStudent(Integer id) {
		Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
		return new StudentDto(id, student.getName(), student.getScores());
	}

	@Override
	public StudentDto removeStudent(Integer id) {
		Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
		studentRepository.deleteById(id);
		return new StudentDto(id, student.getName(), student.getScores());
	}

	@Override
	public StudentCreateDto updateStudent(Integer id, StudentUpdateDto studentUpdateDto) {
		Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
		if (studentUpdateDto.getName() != null) {
			student.setName(studentUpdateDto.getName());
		}
		if (studentUpdateDto.getPassword() != null) {
			student.setPassword(studentUpdateDto.getPassword());
		}
		student = studentRepository.save(student);
		return new StudentCreateDto(student.getId(), student.getName(), student.getPassword());
	}

	@Override
	public Boolean addScore(Integer id, ScoreDto scoreDto) {
		Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
		boolean res = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
		studentRepository.save(student);
		return res;
	}

	@Override
	public List<StudentDto> findStudentsByName(String name) {
		return studentRepository.findByNameIgnoreCase(name)
								.map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
								.collect(Collectors.toList());
	}

	@Override
	public Long getStudentsNamesQuantity(Set<String> names) {
		return studentRepository.countByNameInIgnoreCase(names);				
	}

	@Override
	public List<StudentDto> getStudentsByExamMinScore(String exam, Integer minScore) {
		return studentRepository.findByExamAndScoreGreaterThan(exam, minScore)
				.map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
				.collect(Collectors.toList());
	}

}
