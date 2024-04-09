package edu.kh.todo.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.todo.model.dto.Todo;

/*@Mapper
 * - Mybatis 에서 제공하는 어노테이션
 * - 해당 어노테이션이 작성된 인터페이스는 
 *    namespace 에 해당 인터페이스가 작성된 
 *   mapper.xml 파일과 연결되어 SQL 호출/수행/결과
 *   반환 가능
 * 
 * - Mybatis 에서 제공하는 Mapper 상속 객체가 Bean 으로 등록됨.
 *  근데, 이건 인터페이스인데?
 *  인터페이스는 인스턴스화 될 수 없잖아.
 *  마이바티스가 @Mapper 붙은 인터페이스를 보고 내부적으로 동작해서
 *  인스턴스를 만들어내서 스프링 컨테이너에 빈으로 등록함.
 * */

@Mapper
public interface TodoMapper {
	
	/* Mapper의 메서드명 == mapper.xml 파일 내 태그의 id
	 * 
	 * 메서드명과 id가 같은 태그가 서로 연결됨
	 * 
	 * */
	
	public List<Todo> selectAll();
	
	public int getCompleteCount();

	public int addTodo(Todo todo);

	public Todo todoDetail(int todoNo);

	public int changeComplete(Todo todo);

	public int todoUpdate(Todo todo);

	/** 할 일 삭제
	 * @param todoNo
	 * @return result
	 */
	public int todoDelete(int todoNo);

	/** 전체 할 일 조회
	 * @return
	 */
	public int getTotalCount();



	public String findCompleteByTodoNo(int todoNo);



	
	
	
}
