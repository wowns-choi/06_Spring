package edu.kh.todo.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.kh.todo.model.dto.Todo;



public interface TodoService {

/**
 * 할 일 목록  +  완료된 할 일 개수 조회
 * @return map
 */
	public Map<String, Object> selectAll();

	/** 할 일 추가
	 * 
	 * @return result
	 */
	public int addTodo(Todo todo);

	/** 할 일 상세조회
	 * @param todoNo
	 * @return todo
	 */
	public Todo todoDetail(int todoNo);

	/** 완료 여부 변경
	 * @param todo
	 * @return result
	 */
	public int changeComplete(Todo todo);

	/** 할일 수정
	 * @param todo
	 * @return result
	 */
	public int todoUpdate(Todo todo);

	/** 할 일 삭제
	 * @param todoNo
	 * @return result
	 */
	public int todoDelete(int todoNo);

	/** 전체 할 일 개수 조회
	 * @return totalCount
	 */
	public int getTotalCount();

	/** 완료된 할 일 개수 조회
	 * @return completeCount
	 */
	public int getCompleteCount();

	/** 전체 할 일 목록 조회
	 * @return
	 */
	public List<Todo> selectList();

	/** change complete
	 * @param todoNo
	 * @return
	 */

}
