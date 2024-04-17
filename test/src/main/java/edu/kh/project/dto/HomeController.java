package edu.kh.project.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequestMapping("book")
@Slf4j
public class HomeController {
	@GetMapping("selectAllList")
	@ResponseBody
	public List<Book> selectAllList() {
		List<Book> list = new ArrayList<>();
		list.add(new Book(1, "제목1", "최재준1", 1000));
		list.add(new Book(2, "제목2", "최재준2", 2000));
		list.add(new Book(3, "제목3", "최재준3", 3000));
		list.add(new Book(4, "제목4", "최재준4", 4000));
		
		return list;}
	
}
