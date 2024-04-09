package edu.kh.todo.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;
import lombok.extern.slf4j.Slf4j;
// -----------------------------------------------------------
// @Transactional 
// - 트랜잭션 처리를 수행하라고 지시하는 어노테이션
// - 이렇게 애노테이션으로 트랜잭션관리하는 걸 "선언적 트랜잭션 처리* 라고 함. 
// - 정상 코드 수행 시 COMMIT;

// - 기본값 : Service 내부 코드 수행 중 RuntimeException 발생 시 rollback
// rollbackFor 속성 : 어떤 예외가 발생했을 때 rollback 할지 지정


// -----------------------------------------------------------


@Service // 비즈니스 로직(데이터를 가공, 트랜잭션 처리) 역할 명시 + Bean 등록
@Transactional (rollbackFor = Exception.class)
@Slf4j
//rollbackFor = Exception.class 는 모든 종류의 예외 발생시 rollback 수행하라고 알려주는 것. 

public class TodoServiceImpl implements TodoService {
	
	@Autowired
	private TodoMapper mapper;
	
	// 할 일 목록 + 완료된 할 일 개수 조회
	@Override
	public Map<String, Object> selectAll(){
		
		// 1. 할 일 목록 조회
		List<Todo> todoList = mapper.selectAll();
		
		// 2. 완료된 할 일 개수 조회
		Integer completeCount = mapper.getCompleteCount();
		
		
		// Map으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		
		return map;
	}

	@Override
	public int addTodo(Todo todo) {
		
		// Connection 생성 / 반환 X
		// 트랜잭션 제어 처리 -> @Transactional 어노테이션 추가.
		
		// 마이바티스에서 SQL에 전달할 수 있는 파라미터의 개수는
		// 오직 1개!!!
		
		int result = mapper.addTodo(todo);
		
		return result;
	}

	@Override
	public Todo todoDetail(int todoNo) {
		Todo todo = mapper.todoDetail(todoNo);
		return todo;
	}

	@Override
	public int changeComplete(Todo todo) {
		int result = mapper.changeComplete(todo);
		return result;
	}

	// 할 일 수정
	@Override
	public int todoUpdate(Todo todo) {
		
		int result = mapper.todoUpdate(todo);
		
		return result;
	}

	// 할 일 삭제
	@Override
	public int todoDelete(int todoNo) {
		return mapper.todoDelete(todoNo);
	}

	// 전체 할 일 개수 조회
	@Override
	public int getTotalCount() {
		return mapper.getTotalCount();
	}

	@Override
	public int getCompleteCount() {
		return mapper.getCompleteCount();
	}

	@Override
	public List<Todo> selectList() {
		return mapper.selectAll();
	}

	
	
	

	

}
